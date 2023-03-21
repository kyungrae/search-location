# Search Location Service

## 서비스 테스트 하는 방법

1. 서비스 실행

```bash
docker-compose up -d

./gradlew clean build

chmod +x ./build/libs/search-location-0.0.1-SNAPSHOT.jar

java -jar ./build/libs/search-location-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local \
  --kakao.api.key={kakao-api-key} \
  --naver.client.id={naver-client-key} \
  --naver.client.secret={naver-client-secret} 
```

2. command line에서 api 실행

```bash
# 장소 검색
curl -X GET "http://localhost:8080/v1/locations?keyword=cafe" -H "accept: application/json"

# 검색 키워드 목록
curl -X GET "http://localhost:8080/v1/keywords/popular" -H "accept: application/json"
```

3. `./http.http` 파일로 api 실행

## 기술 스택

- Kotlin
- Spring MVC
- JUnit5, TestContainers
- Kafka, MySQL

## 코드 설계

- 장소 검색 API 호출 결과에 따라 검색 키워드 검색 횟수 리소스의 변경 필요
  - 문제: GET 요청에 ***부수 효과*** 발생 & 같은 키워드 검색 횟수*을 갱신하면 ***경합*** 가능성 높아짐
  - 해결책: GET 요청에서 검색 이벤트를 발행하고 비동기적으로 comsumer에서 키워드 검색 횟수 리소스를 갱신
    - 장소 검색 요청하면 record의 `key` 값을 키워드로 설정하고 이벤트 발행
    - 같은 키워드의 이벤트는 동일한 파티션에 적재
    - consumer가 같은 파티션에 할당되지 않기 때문에 동일한 키워드 검색 횟수 갱신 과정에 경합 가능성 없음
    - 키워드 검색 횟수 정합성이 real time 지원이 필요 없기 때문에 batch consumer로 구현
    - 많이 검색한 키워드 순으로 키워드 검색 횟수 정렬 필요
      - PK는 `keyword` 값으로 natural key 사용
      - 정렬을 위해 `search_count` 필드에 desc index 생성
      - 로그성 데이터를 단순히 내려주기 때문에 JDBC API 사용
- 서비스 가용성 확보
  - 문제: 외부 장소 검색 시스템 장애에 취약함
  - 해결책: 검색 이벤트 기반으로 fallback 저장소 구축
    - fallback 저장소로 redis string 자료구조 사용(리스트 연산 필요 없음)
    - `key` : `value` = `fallback:location:keyword` : `["location1", "location2", ...]`
    - ttl 값은 없으면 동일한 키워드가 들어오면 기존 데이터를 덮어씀
    - circuit breaker 패턴을 이용해 fallback 저장소에 데이터가 존재하면 fallback 저장소에서 데이터를 가져오도록 구현
- 서비스 응답 속도 개선
  - 문제: 장소 검색 API의 경우 외부 시스템에 의존하고 있어 응답 시간이 느림
  - 해결책: 시스템에 redis cache 적용
    - fallback 저장소로 redis string 자료구조 사용(리스트 연산 필요 없음)
    - `key` : `value` = `location:cache:keyword` : `["location1", "location2", ...]`
    - 장소 검색 결과 갱신을 위해 ttl 설정
- Testable & 변경하기 쉬운 코드 작성
  - TestContainers와 mock server를 이용해 외부 시스템에 의존하지 않도록 테스트 코드 작성
  - 장소 검색 `LocationQueryClient` 기능을 interface로 분리해 새로운 검색 API 추가 시 변경 영역 최소화(OCP,DIP)

## 서비스 요구사항

- [X] 장소 검색
    - [X] 카카오 검색 API 연동
    - [X] 네이버 검색 API 연동
    - [X] 검색 결과를 통합하여 반환
        - [X] 동일 업체 판단 기준
        - [X] 검색 결과 정렬
        - [X] 검색 결과 크기 엣지 케이스 처리
    - [X] 키워드 검색 이벤트 producer 구현
- [X] 검색 키워드 목록
    - [X] 키워드 검색 consumer 구현
    - [X] consumer에서 keyword_search_log 데이터 적재
    - [X] 검색 키워드 목록 API 구현
- [X] readme 업데이트
    - [X] 서비스 테스트 하는 방법
    - [X] 기술 스택
    - [X] 코드 작성 의도
- [ ] 외부 시스템 장애 fallback 전략 추가
    - [X] 키워드 검색 이벤트를 이용해 fallback 저장소 consumer 구현
    - [ ] 서킷브레이커 구현
- [ ] 검색 결과 Redis 캐싱

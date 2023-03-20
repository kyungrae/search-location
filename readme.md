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

- 장소 검색 API 호출 결과에 따라 검색 *키워드 검색 횟수* 리소스의 변경 필요
  - 문제: GET 요청에 **부수 효과** 발생 & 같은 *키워드 검색 횟수*을 갱신하면 **경합** 가능성 높아짐
  - 해결책: GET 요청에서 검색 이벤트를 발행해 비동기적으로 *키워드 검색 횟수* 리소스를 갱신
    - Consumer가 같은 *키워드 검색 횟수* 레코드를 갱신하는 것을 막기 위해 이벤트 발행시 record의 `key` 값을 키워드로 설정
    - 같은 키워드의 이벤트는 동일한 파티션에 적재
    - consumer가 같은 파티션에 할당되지 않기 때문에 동일한 *키워드 검색 횟수* 갱신 과정에 경합 가능성 없음
    - *키워드 검색 횟수* 정합성이 real time 지원이 필요 없기 때문에 batch consumer로 구현
- 많이 검색한 키워드 순으로 *키워드 검색 횟수* 정렬
  - PK는 `keyword` 값으로 natural key 사용
  - 정렬을 위해 `search_count` 필드에 desc index 생성
  - 로그성 데이터를 단순히 내려주기 때문에 JDBC API 사용
- Testable & 변경하기 쉬운 코드 작성
  - *TestContainers*를 이용한 통합 테스트 구축
  - 외부 시스템에 의존하지 않도록 mock server 구축
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
    - [ ] 키워드 검색 이벤트를 이용해 fallback 저장소 consumer 구현
    - [ ] 서킷브레이커 구현
    - [ ] MySQL, Redis failover 설정 추가
- [ ] 검색 결과 Redis 캐싱트

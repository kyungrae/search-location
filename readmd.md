# Search Location Service

## 서비스 테스트 하는 방법

1. command line테스트 코드 실행 방법

```bash
./gradlew clean test \
  -Dkakao.api.key={kakao-api-key} \
  -Dnaver.client.id={naver-client-key} \
  -Dnaver.client.secret={naver-client-secret} 
```
 
2. intellij에서 테스트 코드 실행
   1. ./src/test/resources/application-test.yml 파일 생성

```yaml
kakao:
  api:
    key: {kakao-api-key}

naver:
  client:
    id: {naver-client-key}
    secret: {naver-client-secret}
```

3. 서비스 실행

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

4. command line에서 api 실행

```bash
curl -X GET "http://localhost:8080/v1/locations?keyword=cafe" -H "accept: application/json"

curl -X GET "http://localhost:8080/v1/keywords/popular" -H "accept: application/json"
```

5. `./http.http` 파일로 api 실행

## 기술 스택

- Kotlin
- Spring MVC
- JUnit5, TestContainers
- Kafka, MySQL

## 코드 설계

- 동시성 이슈가 발생할 수 있는 부분을 염두에 둔 설계 및 구현 (예시. 키워드 별로 검색된 횟수)
  - 여러 프로세스에서 같은 keyword_search_log 레코드 갱신을 막기 위해 키워드 검색 이벤트 발행 시 레코드 키를 키워드로 지정
  - 하나의 Consumer가 같은 키워드의 검색 기록 갱신하기 때문에 keyword_search_log 레코드 갱신할 가 경합이 발생하지 않음
- 테스트 코드를 통한 프로그램 검증 및 테스트 용이성(Testability)을 높이기 위한 코드 설계
  - TestContainers를 이용한 통합 테스트 구축
- 구글 장소 검색 등 새로운 검색 API 제공자의 추가 시 변경 영역 최소화에 대한 고려
  - 장소 검색 `LocationQueryClient` 기능 interface 분리를 통해 새로운 검색 API 제공자 추가 시 변경 영역 최소화(OCP,DIP)

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
- [ ] 검색 결과 Redis 캐싱

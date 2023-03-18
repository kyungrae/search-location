# Search Location Service

## 서비스 테스트 하는 방법

```bash
curl -X GET "http://localhost:8080"
```

## 기술 스택

- Spring MVC

## 서비스 요구사항

- [ ] 장소 검색
  - [X] 카카오 검색 API 연동
  - [X] 네이버 검색 API 연동
  - [X] 검색 결과를 통합하여 반환
    - [X] 동일 업체 판단 기준
    - [X] 검색 결과 정렬
    - [ ] 검색 결과 크기 엣지 케이스 처리
  - [ ] 검색 결과 Redis 캐싱
  - [ ] 키워드 검색 이벤트 producer 구현
  - [ ] 외부 시스템 장애 fallback 전략 추가
    - [ ] 키워드 검색 이벤트를 이용해 fallback 저장소 consumer 구현
    - [ ] 서킷브레이커 구현
    - [ ] MySQL, Redis failover 설정 추가
- [ ] 검색 키워드 목록
  - [ ] 키워드 검색 이벤트를 이용해 keyword_search_history consumer 구현
  - [ ] 검색 키워드 목록 API 구현
- [ ] 서비스 테스트 스크립트 작성
- [ ] 표준 입출력 로깅 설정

## 기술 요구사항

- 프로그램의 지속적 유지 보수 및 확장에 용이한 아키텍처에 대한 설계
- 동시성 이슈가 발생할 수 있는 부분을 염두에 둔 설계 및 구현 (예시. 키워드 별로 검색된 횟수)
  - 여러 프로세스에서 같은 keyword_search_history 레코드 갱신을 막기 위해 키워드 검색 이벤트 발행 시 레코드 키를 키워드로 지정
  - 하나의 Consumer가 같은 키워드의 검색 기록 갱신하기 때문에 keyword_search_history 레코드 갱신 경합이 발생하지 않음
- 카카오, 네이버 등 검색 API 제공자의 “다양한” 장애 및 연동 오류 발생 상황에 대한 고려
  - 외부 시스템 장애를 대비해 장소 검색 fallback 저장소 구축
  - 서킷브레이커를 통해 외부 시스템 장애 발생 시 fallback 저장소를 이용해 검색 결과 반환
- 대용량 트래픽 처리를 위한 반응성(Low Latency), 확장성(Scalability), 가용성(Availability)을 높이기 위한 고려
  - 검색 기록 캐싱을 통해 응답 속도 향상
  - MySQL, Redis failover 설정을 통해 가용성 확보
- 테스트 코드를 통한 프로그램 검증 및 테스트 용이성(Testability)을 높이기 위한 코드 설계
  - TestContainers를 이용한 통합 테스트 구축
- 구글 장소 검색 등 새로운 검색 API 제공자의 추가 시 변경 영역 최소화에 대한 고려
  - 장소 검색 API 기능 interface 분리를 통해 새로운 검색 API 제공자 추가 시 변경 영역 최소화(OCP,DIP)
- 이 외에도 본인의 기술 역량을 잘 드러 낼 수 있는 부분을 과제 코드내에서 강조

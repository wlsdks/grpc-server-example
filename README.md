# gRPC vs HTTP 성능 비교 예제 프로젝트

이 프로젝트는 Spring Boot 3.x 기반의 **HTTP REST API**와 **gRPC** 방식을 동시에 제공하여, 회원(Member) 생성 로직을 통해 성능 차이를 간단히 비교할 수 있도록 구성되었습니다.

## **스프링 서버간 요청 테스트: gRPC 클라이언트 코드 (Github)**
- 클라이언트 서버의 README를 읽어보신후 api 요청을 보내서 MSA 서버간 gRPC와 HTTP 요청의 성능차이를 확인해보세요.
- 지금 읽고계신 Server를 실행하고 Client 서버도 실행하신 후 요청을 보내시면 테스트가 가능합니다.
- 하단에서 하게될 hey, ghz 명령어보다 더 확실하게 테스트가 가능합니다. (실제 서버간 통신에 가까운 상황을 만들어 테스트 가능)
- https://github.com/wlsdks/grpc-client-example

## 1. 프로젝트 개요

- **기술 스택**:
    - Java 21
    - Spring Boot 3.x
    - Spring Data JPA (H2 DB)
    - gRPC (net.devh grpc-spring-boot-starter)
    - MapStruct (DTO-Entity 변환)

- **주요 기능**:
    - HTTP(REST) 방식으로 `/api/members` 호출 시 회원 생성
    - gRPC(Protocol Buffers) 방식으로 `MemberService.CreateMember` 호출 시 회원 생성
    - 대용량 필드(`profileImageBase64`, `etcInfo`)를 포함해 직렬화/역직렬화 비용을 높여, 실제 트래픽 상황을 모사


## 2. 빌드 & 실행

```bash
# 1) 소스 다운로드
git clone https://github.com/your-repo/grpc-vs-http-demo.git
cd grpc-vs-http-demo

# 2) Gradle 빌드
./gradlew clean build

# 3) Spring Boot 실행
#   - 서버포트: 8090 (HTTP)
#   - gRPC포트: 50051
java -jar build/libs/grpc-vs-http-demo-0.0.1-SNAPSHOT.jar
````

#### H2 콘솔 접속
- 서버 실행 후 http://localhost:8090/h2-console로 접속
- JDBC URL: jdbc:h2:mem:testdb (또는 application.yml에서 지정한 값)
- User Name: sa / Password: (빈값)

## 3. gRPC & HTTP 성능 테스트 방법
### 중요사항 
- macOS를 기준으로 ```brew install ghz ``` 명령어로 ghz를 설치해주셔야합니다.
- grpc 요청 테스트를 위해서는 이 프로젝트의 root 디렉로리에서 ghz로 요청을 보내주세요.
- hey도 설치해주셔야합니다. ```brew install hey``` 명령어로 설치해주세요.
  - https://curiousjinan.tistory.com/entry/test-http-load-performance-with-hey

#### (1) HTTP 요청 예시: CLI 툴 (hey)
```bash
hey \
  -n 100 \
  -c 10 \
  -m POST \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bigload@http.com",
    "password": "test123!",
    "name": "LargeDataTester",
    "profileImageBase64": "QkFTRTY0X1RoaXNJc0EgU2FtcGxlIEJhc2U2NCBTdHJpbmcgVXNlZCBmb3IgVGVzdCBQdXJwb3NlcyAuLi4gLSBDYW4gQmUgRXZlbiBMb25nZXIgT25lCg==",
    "etcInfo": "이곳에는 정말 길고 긴 텍스트나 JSON, 혹은 대규모 문자열을 넣어서 직렬화 부담을 높일 수 있습니다. 예: {\"key\":\"value\",\"nested\":{\"key2\":\"value2\"}}"
  }' \
  http://localhost:8090/api/members
```
- -n: 총 요청 횟수. 여기서는 100번
- -c: 동시 접속자(동시에 요청을 보낼 개수). 여기서는 10
- -d: POST 바디에 들어갈 JSON 데이터
- profileImageBase64: 실제로 1MB 이상의 파일을 Base64로 변환해 넣으면 네트워크 트래픽과 직렬화 비용이 훨씬 커집니다.
- etcInfo: 마찬가지로 장문 문자열, JSON, XML 등을 넣어 로딩 시간을 늘릴 수 있습니다.
- 출력 결과: 초당 처리량(RPS), 평균 응답 시간, 에러율 등을 확인 가능

#### (2) gRPC 요청 예시: CLI 툴 (ghz)
```bash
ghz --insecure \
  --proto ./src/main/proto/member.proto \
  --call MemberService.CreateMember \
  -d '{
    "email": "bigload@grpc.com",
    "password": "test123!",
    "name": "LargeDataTesterGRPC",
    "profileImageBase64": "QkFTRTY0X1RoaXNJc0EgU2FtcGxlIEJhc2U2NCBTdHJpbmcgVXNlZCBmb3IgVGVzdCBQdXJwb3NlcyAuLi4gLSBDYW4gQmUgRXZlbiBMb25nZXIgT25lCg==",
    "etcInfo": "gRPC에서도 긴 문자열을 추가할 수 있습니다. base64 인코딩된 파일을 1MB 이상 넣어서 테스트해보세요."
  }' \
  -n 100 -c 10 \
  127.0.0.1:50051
```
- --proto: gRPC 통신에 사용할 .proto 파일 경로
- --call: 호출할 서비스/메서드 (예: MemberService.CreateMember)
- -d: JSON 형태로 gRPC 요청 파라미터를 넣을 수 있음
- -n, -c: 총 요청 횟수와 동시 접속자 수 (각각 100, 10)
- 출력 결과: gRPC 호출 성공/실패 수, p95/p99 지연 시간, TPS 등

## 4. 대용량 Base64 문자열 생성 팁
실제 이미지를 사용하시려면, Base64로 인코딩한 뒤 그 결과 문자열을 profileImageBase64에 그대로 붙여넣으면 됩니다.
```bash
# 예시) Linux/Mac 터미널에서 이미지 파일을 Base64 변환
base64 myImage.jpg > myImage.base64
# myImage.base64 파일 안에 있는 내용을 전부 복사해 profileImageBase64에 붙여넣기
```
- 문자열이 매우 길기 때문에, IDE나 터미널에서 다루기 불편할 수 있습니다. 실제 테스트 시에는 파일 리다이렉션이나 스크립트로 관리하시는 편이 좋습니다.

## 5. 결과 예시

- 동시성 10, 요청 100건으로 테스트했을 때는 Rest API가 더 빠른 수치를 보여주었습니다. RPS(716 vs 533)
- 동시성 100, 요청 1만 건으로 부하 테스트했을 때, gRPC가 훨씬 빠른 수치를 보여주고 있습니다. RPS(9435 vs 14583)
- 즉, 요청수가 증가할수록 gRPC가 더 빠른 처리 속도를 보여줍니다.

#### **참고사항 (성능 테스트의 정확도)** 
- localhost 환경에서의 HTTP와 gRPC 성능 차이는 몇 가지 중요한 요소들이 복합적으로 작용합니다.
1. JVM 웜업 효과
   - 첫 번째로, Spring Boot 애플리케이션에서 발생하는 JVM 웜업 효과가 큰 영향을 미칩니다. 초기 요청들은 JIT 컴파일러가 코드를 최적화하기 전이라 상대적으로 느리고, 이후 요청들은 최적화된 코드를 실행하므로 더 빠릅니다. 이는 HTTP와 gRPC 모두에 영향을 주지만, Spring MVC(HTTP)가 더 많은 최적화 기회를 가질 수 있습니다.
2. 연결 관리의 차이
   - localhost 환경에서도 gRPC의 HTTP/2 기반 연결 관리와 멀티플렉싱은 여전히 작동합니다. 다만, 네트워크 지연이 거의 없는 환경이다 보니 이러한 최적화의 이점이 크게 드러나지 않습니다. 실제 네트워크 환경에서는 이 차이가 더 명확해질 것입니다.
3. 프로토콜 오버헤드
   - gRPC는 Protocol Buffers를 사용한 바이너리 직렬화로 더 작은 메시지 크기를 가집니다. 하지만 localhost 환경에서는 네트워크 전송 시간이 매우 짧아서 이 장점이 크게 부각되지 않습니다.
4. 테스트 도구의 차이
   - hey와 ghz의 차이는 성능 측정에 영향을 줄 수 있지만, 이것이 주된 이유는 아닙니다. 더 정확한 비교를 위해서는 동일한 클라이언트 구현(예: 지금 만든 것처럼)을 사용하는 것이 좋습니다.

- 따라서 보다 정확한 성능 비교를 위해서는 실제 운영 환경과 유사한 조건에서 테스트를 수행하는 것이 권장됩니다.

#### 전체 결과 요약 
- HTTP(hey)
  - 총 소요 시간: 약 1.06초 
  - 평균 응답 시간: 약 10.1ms 
  - 처리량(RPS): 약 9,435건/초 
- gRPC(ghz)
  - 총 소요 시간: 약 0.69초 
  - 평균 응답 시간: 약 6.04ms 
  - 처리량(RPS): 약 14,583건/초 
  - 단순히 수치를 비교해보면, gRPC가 HTTP 대비 1.5배 이상 빠른 처리량(RPS)을 보여주고 있습니다.


#### 핵심 요약:
- 작은 규모 테스트 (RPS: REST > gRPC)
  - 초기 연결 오버헤드, 낮은 동시성에서 REST가 더 효율적일 수 있음.
- 대규모 테스트 (RPS: gRPC > REST)
  - HTTP/2 기반 멀티플렉싱, 효율적인 Protobuf 직렬화, 비동기 처리 덕분에 gRPC가 높은 처리량과 낮은 응답 시간을 보임.
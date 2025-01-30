# Spring Boot gRPC Server

이 프로젝트는 **Spring Boot 3.x** 기반으로, HTTP(REST)와 **gRPC** 방식 모두를 지원하는 서버 예시입니다.  
회원(Member) 관련 기능을 HTTP 엔드포인트 또는 gRPC 서비스로 제공하며, **대용량(Base64) 필드** 등을 활용하여 실제 트래픽 상황과 유사한 환경을 구성할 수 있습니다.

---
## 1. 주요 기능 설명

1. **HTTP(REST) 방식**
  - `POST /api/members` : 회원 생성
  - `GET /api/members/{id}` : 회원 조회
  - PostgreSQL을 사용, docker-compose로 실행 가능

2. **gRPC 방식**
- **MemberService** :
    - `rpc StreamCreateMember (stream MemberRequest) returns (stream MemberCreateResponse)` : (스트리밍 기반) 회원 생성
    - `rpc GetMemberById (MemberIdRequest) returns (MemberResponse)` : 단일 회원 조회
    - `rpc GetMemberByEmail (MemberEmailRequest) returns (MemberResponse)` : 이메일 기반 조회
- **Protocol Buffers** 기반 직렬화/역직렬화

3. **대용량 필드 처리**
  - `profileImageBase64`, `etcInfo` 같은 문자열 필드로 실제로 큰 사이즈의 데이터를 전송할 수 있음
  - 직렬화/역직렬화 비용을 체감해볼 수 있도록 구성

---

## 2. 클라이언트 서버 예시

이 서버와 통신하기 위한 **Spring Boot 기반 gRPC 클라이언트 예시**는 아래 저장소를 참고하세요:
- [gRPC Client Example (GitHub)](https://github.com/wlsdks/grpc-client-example)

해당 클라이언트를 실행하면,
- **HTTP(Feign)** 또는 **gRPC**로 이 서버에 요청을 보내고,
- 실제 MSA 서버 간 통신 상황을 모사하여 성능을 비교하거나 기능을 검증할 수 있습니다.

---

## 3. 기술 스택

- **Java 21**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **gRPC** (using [net.devh grpc-spring-boot-starter](https://github.com/yidongnan/grpc-spring-boot-starter))
- **MapStruct** (DTO ↔ Entity 변환)

---

## 4. 프로젝트 빌드 & 실행

```bash
# 1) 소스 다운로드
git clone https://github.com/<your-repo>/grpc-server-example.git
cd grpc-server-example

# 2) Gradle 빌드
./gradlew clean build

# 3) Spring Boot 실행
#   - HTTP 서버포트: 8090
#   - gRPC 서버포트: 50051
java -jar build/libs/grpc-0.0.1-SNAPSHOT.jar
```

### H2 콘솔 접속

- 서버 실행 후 브라우저에서 <http://localhost:8090/h2-console> 로 접속
- JDBC URL: `jdbc:h2:mem:testdb` (또는 `application.yml`에서 지정한 값 확인)
- User Name: `sa`, Password: (빈값)

---

## 5. HTTP & gRPC 테스트

### 5.1 HTTP (REST) 테스트

#### (1) 회원 생성
```bash
POST http://localhost:8090/api/members

request_body
{
 "email": "test@test.com",
 "password": "password",
 "name": "test",
 "profileImageBase64": "base64EncodedLongString...",
 "address": {
   "street": "123 Test St",
   "city": "Test City",
   "country": "Test Country",
   "postalCode": "12345",
   "additionalInfo": {
     "building": "A",
     "floor": "5",
     "doorCode": "1234"
   }
 },
 "contact": {
   "phone": "123-456-7890",
   "mobile": "098-765-4321",
   "workPhone": "111-222-3333",
   "emails": ["work@test.com", "personal@test.com"],
   "socialMedia": {
     "twitter": "@testuser",
     "linkedin": "linkedin.com/testuser",
     "facebook": "fb.com/testuser"
   }
 },
 "interests": ["coding", "testing", "debugging"],
 "skills": ["java", "spring", "grpc"],
 "metadata": "{\"lastLogin\":\"2024-01-23\",\"status\":\"active\",\"rank\":\"senior\"}"
}
```

#### (2) 회원 조회 (간단 cURL)
```bash
curl http://localhost:8090/api/members/1
```
- ID=1인 회원이 DB에 존재하면 JSON 형태로 응답

<br/>

### 5.2 gRPC 테스트

#### (1) ghz를 이용한 부하 테스트
```bash
ghz --insecure \
  --proto ./src/main/proto/member.proto \
  --call com.test.member.grpc.MemberService.GetMemberById \
  -d '{"id":1}' \
  -n 100 \
  -c 10 \
  127.0.0.1:50051
```
- --call com.test.member.grpc.MemberService.GetMemberById: 패키지명.서비스명.메서드명
- -d '{"id":1}' : JSON 형태의 요청 데이터 (gRPC는 내부적으로 Protobuf 직렬화)
- -n 100, -c 10 : 총 100번 요청, 동시 10개
- 127.0.0.1:50051 : gRPC 서버 주소(포트)
 
#### 2) 스트리밍 호출 (예: StreamCreateMember)
- ghz에서 스트리밍 테스트를 할 때는 --stream-call 옵션과 입력 데이터 파일을 사용하는 방법이 대표적입니다.
- 예: stream_data.json (스트리밍할 요청들을 여러 줄로 기재)
``` json
{"id":1, "email":"test1@stream.com", "password":"pwd1", "name":"Name1", "profileImageBase64":"Base64_1", "etcInfo":"Etc_1"}
{"id":2, "email":"test2@stream.com", "password":"pwd2", "name":"Name2", "profileImageBase64":"Base64_2", "etcInfo":"Etc_2"}
{"id":3, "email":"test3@stream.com", "password":"pwd3", "name":"Name3", "profileImageBase64":"Base64_3", "etcInfo":"Etc_3"}
```
각 줄이 **한 번의 MemberRequest**를 의미합니다.
- 그 후 ghz 명령어로 스트리밍 호출을 실행합니다.
```bash
ghz --insecure \
  --proto ./src/main/proto/member.proto \
  --call com.test.member.grpc.MemberService.StreamCreateMember \
  --stream-call \
  --data-file=stream_data.json \
  -n 50 \
  -c 5 \
  127.0.0.1:50051
```
- --stream-call : ghz에 양방향 스트리밍 호출임을 알려주는 옵션
- --data-file=stream_data.json : 스트리밍으로 전송할 요청들을 담은 파일
- -n 50, -c 5 : (한 번의 스트리밍 세션을 몇 번 반복할지 등 세부 동작은 ghz 버전에 따라 달라질 수 있음)

참고사항: 스트리밍은 검증이 필요하니 검증후 다시 업데이트 예정입니다.

<br/>

---
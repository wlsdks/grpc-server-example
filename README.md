# Spring Boot gRPC Server

이 프로젝트는 **Spring Boot 3.x** 기반으로 구현된 **HTTP(REST API)** 및 **gRPC** 프로토콜을 동시에 지원하는 멀티 프로토콜 서버입니다.

## 🌟 프로젝트 개요

회원(Member) 관련 기능을 REST API와 gRPC 서비스 형태로 모두 제공하며, 대용량 필드(Base64 인코딩 이미지 등)를 활용하여 실제 운영 환경과 유사한 트래픽 시뮬레이션이 가능하도록 설계되었습니다.

## 📚 기술 스택

- **Java 21**
- **Spring Boot 3.3.7**
- **Spring Security**
- **Spring Data JPA**
- **gRPC 1.65.1**
- **Protocol Buffers 4.28.2**
- **PostgreSQL**/H2 Database
- **JWT 인증**
- **Docker** (개발환경)

## 🏗️ 시스템 아키텍처

```
┌────────────────────────────────────────────────────────────────────┐
│                       Spring Boot Application                      │
│                                                                    │
│  ┌─────────────────┐          ┌────────────────────────────────┐   │
│  │                 │          │                                │   │
│  │    REST API     │          │         gRPC Service           │   │
│  │  (Port: 8090)   │◄────────►│         (Port: 50051)          │   │
│  │                 │          │                                │   │
│  └─────────────────┘          └────────────────────────────────┘   │
│           │                                    │                   │
│           │                                    │                   │
│           ▼                                    ▼                   │
│  ┌─────────────────┐          ┌────────────────────────────────┐   │
│  │                 │          │                                │   │
│  │  Controller     │          │  gRPC Service Implementation   │   │
│  │  Layer          │          │  Layer                         │   │
│  │                 │          │                                │   │
│  └─────────────────┘          └────────────────────────────────┘   │
│           │                                    │                   │
│           │                                    │                   │
│           ▼                                    ▼                   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                                                             │   │
│  │                 Service Layer (Business Logic)              │   │
│  │                                                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                 │
│                                  │                                 │
│                                  ▼                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                                                             │   │
│  │              Repository Layer (Spring Data JPA)             │   │
│  │                                                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                 │
│                                  │                                 │
│                                  ▼                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                                                             │   │
│  │                    Database (PostgreSQL/H2)                 │   │
│  │                                                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

### 시스템 흐름도

```mermaid
sequenceDiagram
    participant Client as 클라이언트
    participant REST as REST API (8090)
    participant Service as 서비스 레이어
    participant GRPC as gRPC 서비스 (50051)
    participant Security as 보안 레이어
    participant Repository as 리포지토리
    participant DB as 데이터베이스
    
    alt HTTP 요청 흐름
        Client->>+REST: HTTP 요청 (POST /api/members)
        REST->>Security: JWT 토큰 검증 (필요시)
        Security-->>REST: 인증 결과
        REST->>+Service: 회원 생성 요청
        Service->>+Repository: 회원 엔티티 저장 요청
        Repository->>+DB: SQL 쿼리 실행
        DB-->>-Repository: 결과 반환
        Repository-->>-Service: 저장된 엔티티 반환
        Service-->>-REST: 응답 DTO 반환
        REST-->>-Client: HTTP 응답 (JSON)
    else gRPC 요청 흐름
        Client->>+GRPC: gRPC 요청 (GetMemberById)
        GRPC->>Security: gRPC 인터셉터 (토큰 검증)
        Security-->>GRPC: 인증 결과
        GRPC->>+Service: 회원 조회 요청
        Service->>+Repository: 회원 엔티티 조회 요청
        Repository->>+DB: SQL 쿼리 실행
        DB-->>-Repository: 결과 반환
        Repository-->>-Service: 조회된 엔티티 반환
        Service-->>-GRPC: Protocol Buffer 메시지 변환
        GRPC-->>-Client: gRPC 응답 (Protobuf)
    end
```

## 🚀 주요 기능

### 1. HTTP REST API (8090 포트)

| 메서드 | 경로 | 설명 | 인증 필요 |
|--------|------|------|-----------|
| POST | `/api/members` | 회원 생성 | 아니오 |
| GET | `/api/members/{id}` | 회원 조회 | 예 |
| POST | `/api/auth/login` | 로그인 (JWT 토큰 발급) | 아니오 |
| POST | `/api/auth/refresh` | 토큰 갱신 | 예 |

### 2. gRPC 서비스 (50051 포트)

#### 주요 서비스 정의

```protobuf
service MemberService {
  // 스트리밍 기반 회원 생성 (양방향 스트리밍)
  rpc StreamCreateMember (stream MemberRequest) returns (stream MemberCreateResponse);
  
  // 회원 ID로 조회 (단일 요청-응답)
  rpc GetMemberById (MemberIdRequest) returns (MemberResponse);
  
  // 이메일로 회원 조회 (단일 요청-응답)
  rpc GetMemberByEmail (MemberEmailRequest) returns (MemberResponse);
}
```

#### 메시지 구조

- **MemberRequest**: 회원 생성 요청 데이터
- **MemberResponse**: 회원 정보 응답 데이터
- **MemberIdRequest**: ID 기준 조회 요청
- **MemberEmailRequest**: 이메일 기준 조회 요청

### 3. 보안 및 인증

#### JWT 기반 인증

- 사용자 인증을 위한 JWT 토큰 발급 및 검증
- REST API 및 gRPC 모두 동일한 JWT 토큰 활용
- Spring Security와 gRPC 인터셉터 연동

#### 서버 간 인증

- 마이크로서비스 간 통신을 위한 서버 토큰 구현
- 클라이언트 애플리케이션과의 보안 통신을 위한 인증 계층

### 4. 데이터 모델

#### 핵심 엔티티

- **MemberEntity**: 회원 기본 정보
  - id, email, password, name, profileImageBase64
- **Address**: 회원 주소 정보 (임베디드)
- **Contact**: 회원 연락처 정보 (임베디드)
- **EtcInfo**: 부가 정보 (JSON 타입 포함)

## 💾 데이터베이스 스키마

```mermaid
erDiagram
    MEMBER {
        bigint id PK
        string email
        string password
        string name
        text profile_image_base64
    }
```

## 🔄 gRPC와 REST API 비교

| 특성 | gRPC | REST API |
|------|------|----------|
| 프로토콜 | HTTP/2 | HTTP/1.1 |
| 데이터 포맷 | Protocol Buffers (바이너리) | JSON (텍스트) |
| 통신 방식 | 단방향, 서버/클라이언트 스트리밍, 양방향 스트리밍 | 요청-응답 |
| 코드 생성 | 자동 생성 (IDL 기반) | 수동 구현 |
| 타입 안전성 | 강한 타입 | 약한 타입 |
| 사용 환경 | 주로 내부 마이크로서비스 간 통신 | 외부 API 노출 |

## 📈 성능 특성

이 서버의 주요 성능 특성:

1. **낮은 지연 시간**: gRPC의 HTTP/2 기반 통신으로 인한 빠른 응답
2. **효율적인 직렬화**: Protocol Buffers를 통한 데이터 크기 최소화
3. **스트리밍 지원**: 대용량 데이터 처리를 위한 양방향 스트리밍
4. **다중 연결 처리**: 효율적인 스레드 풀 및 비동기 처리

## 🛠️ 설치 및 실행

### 사전 요구사항

- Java 21 이상
- Docker 및 Docker Compose (선택사항)
- Gradle

### Docker로 PostgreSQL 실행

```bash
# docker-compose.yml이 있는 디렉토리에서 실행
docker-compose up -d
```

### 애플리케이션 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew clean build

# 애플리케이션 실행
java -jar build/libs/grpc-0.0.1-SNAPSHOT.jar
```

## 🔍 API 테스트

### REST API 테스트

```bash
# 회원 생성
curl -X POST http://localhost:8090/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "Test User"
  }'

# 로그인 및 토큰 발급
curl -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# 회원 조회 (발급받은 토큰 사용)
curl http://localhost:8090/api/members/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### gRPC 테스트 (ghz 사용)

```bash
# 회원 조회 성능 테스트
ghz --insecure \
  --proto ./src/main/proto/member.proto \
  --call com.test.member.grpc.MemberService.GetMemberById \
  -d '{"id":1}' \
  -n 1000 \
  -c 50 \
  127.0.0.1:50051
```

## 🔗 연계 프로젝트

이 서버와 함께 사용할 수 있는 클라이언트 예제:
- [gRPC Client Example](https://github.com/wlsdks/grpc-client-example) - REST와 gRPC 동시 지원 클라이언트
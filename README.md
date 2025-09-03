# JWT Auth Tester (이메일 인증 확장)

**Spring Security + JWT(Json Web Token)** 기반의 인증/인가 방식을 학습하고 테스트하기 위한 실습용 애플리케이션입니다.  
기존 **spring-auth-prac2** 토큰 구조를 그대로 유지하면서, **이메일 인증 회원가입 절차**를 추가하여 보안을 강화했습니다.  
브라우저 UI에서 회원가입, 이메일 인증, 로그인, 토큰 재발급, 권한 검증 요청 등을 간단히 실행해볼 수 있습니다.

---

## 실행 환경
- **Backend**: Spring Boot, Spring Security, JWT  
- **Database**: MySQL  
- **Frontend(UI)**: 단순 HTML/JS 페이지 (토큰/이메일 인증 테스트용)  
- **Email Service**: JavaMailSender + Thymeleaf (MailHog 등 로컬 SMTP 연동)

---

## 기능 개요

### 화면
- 통합 테스트 UI를 통해 회원가입, 로그인, 토큰 관리, 권한 검증을 손쉽게 수행  
- 이메일 인증을 위해 코드 발송/검증 절차 포함  

<img width="1496" height="778" alt="image" src="https://github.com/user-attachments/assets/20f05933-61ad-415a-800b-607890148fa0" />
<img width="469" height="176" alt="image" src="https://github.com/user-attachments/assets/7c0c0da1-9ecf-47f8-888a-3c047d7e1375" />
<img width="490" height="568" alt="image" src="https://github.com/user-attachments/assets/0b07c0b2-59ca-4555-b49f-a08b31e1cecd" />


---

### 회원가입 / 이메일 인증
- Username, Password, Role(USER/ADMIN), Email 입력 후 진행  
- 가입 절차:
  1. **코드 발송** → 입력한 이메일로 6자리 인증 코드 전송  
  2. **코드 확인** → 입력값과 일치 시 이메일 검증 완료  
  3. **회원가입(Signup)** 버튼으로 최종 가입 확정  
- 이메일 도메인 화이트리스트 기능 제공 (`innogrid.com`, `example.com` 등)

---

### 로그인
- 로그인 시 **Access Token**과 **Refresh Token** 발급  
- Access Token → 클라이언트 메모리에 저장 후 요청 시 `Authorization` 헤더에 포함  
- Refresh Token → HttpOnly 쿠키에 저장되어 필요 시 토큰 재발급에 사용  

---

### 토큰 관리
- Access Token: 짧은 만료시간 (예: 15초)  
- Refresh Token: 긴 만료시간 (예: 1분), HttpOnly 쿠키에 저장  
- Access Token 만료 시 `/auth/refresh` 요청으로 새 토큰 발급  

---

### 권한 검증 요청
- `/role/user` → USER 권한 이상 접근 가능  
- `/role/admin` → ADMIN 권한 필요  
- 토큰의 유효성과 권한을 확인할 수 있음  

---

### 로그(Log) 출력
- 모든 요청/응답 내역이 하단 로그 창에 표시되어 디버깅 및 동작 확인 용이  

---

## 사용 흐름
1. **코드 발송** → 입력한 이메일로 인증 코드 전송  
2. **코드 확인** → 이메일 인증 완료  
3. **회원가입(Signup)** → Username/Password/Role 입력 후 가입 확정  
4. **로그인(Login)** → Access Token + Refresh Token 발급  
5. **권한 검증 요청** → `role/user`, `role/admin` 버튼 실행  
6. Access Token 만료 시 **auth/refresh** 버튼으로 새 토큰 발급  
7. **로그(Log) 영역**에서 전체 요청/응답 내역 확인  

---

## 목적
- JWT 기반 인증/인가 구조 이해  
- Access Token과 Refresh Token의 역할 차이 학습  
- 이메일 인증을 통한 안전한 회원가입 경험  
- 권한별 API 접근 제어 실습  
- HttpOnly 쿠키 기반 Refresh Token 관리 방식 체험  

---

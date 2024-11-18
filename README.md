# 💻 Git And Run (8조) 프로젝트
: AI 검증 비즈니스 배달 플랫폼
|분류|내용|
|---|---|
|WBS|[LINK](http://hexagonal-taleggio-d78.notion.site/Git-and-Run-1363121c7bfd80f1839ccd358e83756d?pvs=73)|
|ERD|[LINK](https://www.erdcloud.com/d/zwwsd7gtExGqthiYH)|
|API|[LINK](https://hexagonal-taleggio-d78.notion.site/API-1363121c7bfd80e2bce1ee1295028abc?pvs=74)|

## 🤔 프로젝트 개요
가게 관리 및 사용자 중심 서비스를 제공하는 배달 플랫폼입니다. 이 프로젝트는 가게 소유자, 관리자, 고객을 대상으로 각각의 역할에 적합한 기능을 제공하며, 특히 생성형 인공지능 서비스(API)와 연동하여 메뉴 설명을 도와 사용자 친화적인 플랫폼을 목표로 합니다.

### 기획 배경
- 효율적이고 자동화된 온라인 주문 관리 시스템 요구

### 프로젝트 목표
- 추후 확장성을 고려한 유연한 데이터 설계
- 효율적인 가게 관리 시스템 구축
- 사용자 친화적인 메뉴 관리
- 검색을 통한 고객 맞춤형 서비스 제공
- 역할 기반의 권한 제어
- 지역 기반 서비스

### ● 기대 효과
AI를 통해 AI가 생성한 자연스러운 메뉴 설명으로 고객에게 메뉴의 매력을 효과적으로 전달.

---

## ⏳ 개발 기간
24.11.06 ~ 24.11.18

## 🧑‍🤝‍🧑 멤버 구성
팀장 : [정광호](https://github.com/jabberwocker04) - 메뉴 도메인, AI 연동

부팀장 : [채수원](https://github.com/soo1e) - 가게 도메인, 카테고리 도메인, 지역 도메인

팀원 1 : [오세창](https://github.com/sepang-pang) - 주문 도메인, 결제 도메인

팀원 2 : [이현민](https://github.com/seven2762) - 유저 도메인, 인증 / 인가

팀원 3 : [홍유진](https://github.com/Hujin0322) - 리뷰 도메인

---

---

## 💻 개발 환경

![example workflow](https://github.com/jabberwocker04/gitandrun/actions/workflows/gradle.yml/badge.svg)

### ● 데이터베이스
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white"> <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=flat&logo=amazonrds&logoColor=white"> <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=Redis&logoColor=white"> 

### ● 백엔드
<img src="https://img.shields.io/badge/java-007396?style=flat&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/Gradle 8.10.2-02303A?style=flat&logo=gradle&logoColor=white"> <img src="https://img.shields.io/badge/Spring Boot 3.3.5-6DB33F?style=flat&logo=Spring Boot&logoColor=yellow"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=Spring Security&logoColor=green"> <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat"> <img src="https://img.shields.io/badge/Hibernate-59666C?style=flat&logo=Hibernate&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=Docker&logoColor=white"> <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=flat&logo=GitHub Actions&logoColor=white">

### ● API
<img src="https://img.shields.io/badge/Google Gemini-8E75B2?style=flat&logo=Google Gemini&logoColor=white"> <img src="https://img.shields.io/badge/Json Web Tokens-80247B?style=flat&logo=jsonwebtokens&logoColor=white">

### ● Tools
<img src="https://img.shields.io/badge/GitHub-181717?style=flat&logo=GitHub&logoColor=white"> <img src="https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white"> <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=Postman&logoColor=white"> <img src="https://img.shields.io/badge/Intellij IDEA-3B00B9?style=flat&logo=Intellij IDEA&logoColor=white"> <img src="https://img.shields.io/badge/ZEP-0049D7?style=flat"> 

---
## ▶️ 실행 방법

## 서비스 구성 및 실행 방법

### 1. 레포지토리 클론
```bash
git clone https://github.com/jabberwocker04/gitandrun.git
```

### 2. 의존성 설치 및 빌드
```bash
./gradlew build
```

### 3. 개발 서버 실행
```bash
./gradlew bootRun
```

---

## 📚주요 기능

● 회원 관리
- 회원 가입, 조회, 수정, 삭제(Soft-Delete) 기능
- 고객, 소유자(Owner), 관리자(Admin, Manager) 등 역할 기반 접근 제어
- Spring Security와 JWT를 활용하여 보안 인증 및 권한 관리

● 가게 관리
- 가게 생성, 조회, 수정, 삭제(Soft-Delete) 기능
- 역할별 정보 접근 제한:
  - 관리자: 모든 가게 조회 및 관리
  - 소유자: 본인 가게 수정 및 삭제
- 카테고리별 검색 및 키워드 검색 기능
- 지역 정보(Region)를 계층적으로 관리
  
● 메뉴 관리
- 가계별 메뉴 생성, 조회, 수정, 삭제(Soft-Delete) 기능
- 메뉴 정보와 가게 정보 간의 연관 관계 관리
- 키워드로 가게명 검색 후 메뉴 반환

● AI
- 메뉴 설명 생성 요청
  - 입력 : 메뉴 이름, 주요 재료 기입
  - 출력 : 생성형 AI가 작성한 매력적인 메뉴 설명
- 설명 자동 등록
- 다국어 지원

● 주문 관리

● 리뷰 관리
- 리뷰 작성, 조회, 수정, 삭제(Soft-Delete), 복구 기능
- 주요 리뷰 작성 제한
  - 본인 주문에 대해 리뷰 1개만 작성
  - 주문 완료 후 3일 이내 작성
- 역할별 정보 접근 제한
  - 관리자: 모든 리뷰 조회, 수정, 삭제, 복구
  - 소유자: 본인 가게 리뷰 조회 및 타 가게 리뷰 조회
  - 고객: 본인 주문 리뷰 작성 및 조회, 수정, 삭제 및 가게별 리뷰 조회
- 조건 검색 및 정렬
  - 최근 생성 순, 평점 높은 순, 평점 낮은 순, 오래된 순, 업데이트 순(관리자)







---
인프라 설계도
---
<img width="698" alt="image" src="https://github.com/user-attachments/assets/514c3044-4ced-4786-a094-ca8c1c6f55c2">


# 🌱 Spring Boot Frameworks 3.x + Spring Data JPA 실습

> **Java 17 기반 실습 코드**  
> Spring Boot 3.x는 Java 8을 지원하지 않으므로, 실습 환경은 Java 17 기반입니다.

---

### 📚 **작성자**
**한국폴리텍대학 서울강서캠퍼스 빅데이터과**  
**이협건 교수**  
✉️ [hglee67@kopo.ac.kr](mailto:hglee67@kopo.ac.kr)  

🔗 **빅데이터학과 입학 상담 오픈채팅방**: [입학 상담 바로가기](https://open.kakao.com/o/gEd0JIad)

---

## 🚀 주요 실습 내용

1. **MariaDB 기반 Spring Data JPA 서비스 구현**
2. **JPA를 활용한 공지사항 구현**
3. **Ehcache를 활용한 JPA 2차 캐시 실습**
4. **JPA 조인 실습**
   - `@JoinColumn` 어노테이션 방식  
   - Entity 조회 방식  
   - NativeQuery 방식  
5. **JPQL을 활용한 FETCH Join 및 QueryDSL 실습**
6. **JPA를 활용한 회원가입 및 로그인 구현**
7. **웹 크롤링 데이터를 MongoDB에 저장 및 CRUD 프로그래밍**
8. **RedisDB를 활용한 캐싱 프로그래밍**
9. **음성 명령으로 CGV 영화 순위 정보를 웹 크롤링한 뒤 RedisDB에 저장하는 프로그래밍**
10. **LogBack과 fluentd 연동하여 Open Search에 로그 수집하기**
11. **Open Search 대시보스를 활용한 로그 분석**

---

## 🛠️ 주요 적용 프레임워크

- **Spring Boot Frameworks**
- **Spring Data JPA**
- **QueryDSL**

---

## 🛠️ 로그 수집 환경

- **Fluentd**
- **Open Search**
- **Open Search Dashboard**
- **도커 컴포즈를 활용한 환경 구성**

---

## 📩 문의 및 입학 상담

📧 **이메일**: [hglee67@kopo.ac.kr](mailto:hglee67@kopo.ac.kr)  

💬 **입학 상담 오픈채팅방**: [바로가기](https://open.kakao.com/o/gEd0JIad)

---

## 💡 **우리 학과 소개**
한국폴리텍대학 서울강서캠퍼스 빅데이터과는 **클라우드 컴퓨팅, 인공지능, 빅데이터 기술**을 활용하여 소프트웨어 개발자를 양성하는 학과입니다.  
더 자세한 정보는 [학과홈페이지](https://www.kopo.ac.kr/kangseo/content.do?menu=1547))를 참고하세요.

---

## 📦 설치 및 실행 방법**

### 1. 레포지토리 클론**
   ```bash
   git clone https://github.com/Hyeopgeon-Lee/SpringJpaJdk17Template.git
   cd SpringJpaJdk17Template
   ```

### 2. MariaDB 설정
application.yml 또는 application.properties 파일에서 데이터베이스 설정 정보를 업데이트합니다.

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/your_database
    username: your_username
    password: your_password
```

아래는 의존성 설치부터 시작하는 설치 및 실행 방법입니다.

### 3. 의존성 설치
아래 명령어를 실행하여 Maven 의존성을 설치합니다:
```bash
mvn clean install
```

### 4. 애플리케이션 실행
```bash
mvn spring-boot:run
```


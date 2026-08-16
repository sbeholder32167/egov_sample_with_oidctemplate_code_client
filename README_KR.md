## OIDC Template Code - Client가 적용된 전자정부 프레임워크 3.7.0 통합 예제 Application.

[한국어](./README_KR.md) | [English](./README.md)

이 프로젝트는 기존 전자정부 프레임워크 3.5.0 통합 예제를 전자정부 프레임워크 버젼 3.7.0으로 Migration하고,
그 Application에 OIDC Template Code가 적용되어 Keycloak 인증 연동이 가능하도록 작성된 Application입니다.

OIDC Template Code - Client는 아래 Repository에서 받으실 수 있습니다
https://github.com/sbeholder32167/oidctemplate_code_client

### 설명
- OIDC Template Code - Client를 Legacy Spring / Egov. Application에 적용하실 때 예제로서 참조하실수 있도록 공개하였으며,
JDK 1.8 (Eclipse Adoptium), Apache Tomcat 8.0.36 으로 동작하는 IntelliJ IDE에서 테스트 되었습니다.
- 이 Application은 "Keycloak과 Legacy Egov. framework Application의 RBAC 연동 시연" 영상 제작에 사용된 Application입니다.

### 레거시 코드 침습 상세 내역

- egovframework.rte.tex.mbr.service.EgovMemberService : DB에서 사용자 정보/권한/코드 로드
- egovframework.rte.tex.mbr.service.impl.MemberDAO : DB에서 사용자 정보/권한/코드 로드
- egovframework/sqlmap/rte/tex/query/mysql/EgovMember_SQL_mysql.xml : DB에서 사용자 정보/권한/코드 로드
- egovframework/rte/tex/mbr/egovLogin.jsp : Egov. framework 3.7.0 Migration.
- egovframework/rte/tex/com/leftmenu.jsp : Egov. framework 3.7.0 Migration.
- egovframework/rte/tex/com/header.jsp : Frontend Screen refresh.
- 기타 : pom.xml에 com.auth0.java-jwt 3.19.3 의존성 추가 등.

### 라이선스
- 전자정부 프레임워크 : Apache 2.0
- OIDC Template Code - Client : Apache 2.0

### 유료 기술 자문(지원) 문의
- sbeholder6684@gmail.com
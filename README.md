## Egov.Framework (in South Korea) 3.7.0 Integrated Sample Application with OIDC Template Code - Client Applied

[English](./README.md) | [한국어](./README_KR.md)

This project is an application written to enable Keycloak authentication integration by migrating the existing Egov. Framework 3.5.0 integration sample to Egov. Framework version 3.7.0 and applying 'OIDC Template Code - Client' to it.

You can download 'OIDC Template Code - Client' from the repository below:
https://github.com/sbeholder32167/oidctemplate_code_client

### Description

- 'OIDC Template Code - Client' has been released so that you can refer to it as an example when applying it to Legacy Spring / Egov. framework Applications.
- It has been tested in an IntelliJ IDE running JDK 1.8 and Tomcat 8.

### Legacy code intrusion details.

- egovframework.rte.tex.mbr.service.EgovMemberService: Load user information/authorities/code from DB
- egovframework.rte.tex.mbr.service.impl.MemberDAO: Load user information/authorities/code from DB
- egovframework/sqlmap/rte/tex/query/mysql/EgovMember_SQL_mysql.xml : Load user information/authorities/code from DB
- egovframework/rte/tex/mbr/egovLogin.jsp: Egov.framework 3.7.0 Migration.
- egovframework/rte/tex/com/leftmenu.jsp: Egov.framework 3.7.0 Migration.
- egovframework/rte/tex/com/header.jsp: Frontend Screen refresh.
- Others: Added com.auth0.java-jwt 3.19.3 dependency to pom.xml, etc.

### License

- Egov. Framework (in South Korea) : Apache 2.0
- OIDC Template Code - Client : Apache 2.0

### Inquiry for Paid Technical Consulting (Support)
- sbeholder6684@gmail.com (in South Korea only.)
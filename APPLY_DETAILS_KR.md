## OIDC Template Code-Client 적용 상세

[한국어](./APPLY_DETAILS_KR.md) | [English](./APPLY_DETAILS.md)

- 본 문서는 OIDC Template Code-Client를 전자정부 프레임워크 통합 예제에 적용시킨 절차에 대해 기술합니다.
- OIDC Template Code-Client의 적용과 무관한 Version Up Migration에 대해서는 언급하지 않습니다.
- 본 예제는 전자정부 프레임워크 통합 예제 v3.5.0을 v3.7.0으로 Migration 한 Application입니다.

### 절차

#### 1. 파일 복사 및 의존성 추가

 - OIDC Template Code-Client의 파일들을 전자정부 통합 예제(Egov. Sample)의 src/main/java 디렉토리로 복사합니다.
 - 전자정부 통합예제 Application의 경우 Spring Security가 포함되어 있으므로, client.security 패키지와 egov 패키지를 삭제하지 않습니다.
   - Spring Security 패키지가 포함되지 않은 Application의 경우에는 의존성 문제로 인하여 OIDC Template Code-Client가 빌드 오류가 발생하므로, 반드시 OIDC Template Code-Client의 client.security와 egov 패키지를 삭제해야 합니다.
 - com.auth0.java-jwt 패키지를 pom.xml에 추가합니다.

#### 2. 인터페이스 구현

 - OIDC Template Code-Client와 연동하기 위해 5종의 Adapter Interface를 구현해야 합니다.

###### (1). ClientAuthConvertAdapter
 
 - IDP에서 받은 Token들을 이용하여 Legacy 세션을 생성하는 단계입니다.
 - 이 예제에서는 egovframework.rte.tex.adapter.EgovAuthConvertAdapterImpl 클래스입니다.
 - 토큰을 받아 사용자 ID를 확인하고, 그 ID를 이용하여 Legacy DB를 조회하여 사용자 정보를 얻은 후, 그것을 이용하여 Legacy 세션을 생성하고 리턴하도록 구현했습니다.
   - 여기서 Legacy 코드의 침습이 발생할 수 있습니다.
   - 이 예제의 경우, 사용자 ID만으로 사용자 정보를 DB에서 추출하는 로직등을 추가하였습니다.
   - 이 예제에서, 권한 코드를 가져오는 등의 부차적인 작업을 위해 추가적인 침습이 발생했으나, 기존 코드의 수정은 발생하지 않았습니다.
 - 신규 사용자 / 기존 사용자의 처리 방침 및 토큰 정보의 반영등에 대한 논의가 필수적인 단계입니다.

###### (2). ClientLoginAdapter

 - 인증 성공 또는 실패 시 단계의 동작을 정의합니다.
 - 이 예제에서는 egovframework.rte.tex.adapter.EgovLoginAdapterImpl 클래스입니다.
 - 성공 또는 실패에 따라 적절한 페이지로 Redirect하도록 구현하였습니다.
 - 이 예제에서는 인증 성공 또는 실패 시 사실상 같은 페이지로 Redirect됩니다.

###### (3). ClientLogoutAdapter

 - IDP 통합 로그아웃 동작 시 로그아웃 직전 및 직후 단계의 동작을 정의합니다.
 - 이 예제에서는 egovframework.rte.tex.adapter.EgovLogoutAdapterImpl 클래스입니다.
 - 로그아웃 동작에 대해서만 알수 있도록 Log만 간략하게 출력하게끔 구현하였습니다.
 - Inbound Logout / Outbound Logout 모두 동일하게 이 클래스의 메서드가 호출됩니다.

###### (4). ClientLegacySessionAdapter

 - OIDC Login Filter, OIDC Session Manager에서 Client의 Legacy 세션 취급 시 사용합니다.
 - 이 예제에서는 egovframework.rte.tex.adapter.EgovSessionAdapterImpl 클래스입니다.
 - 전자정부 통합 예제는 Legacy Session으로서 Spring Security 세션을 사용중이므로, Spring Security 세션에 접근하도록 구현했습니다.

###### (5). OIDCExceptionHandler

 - OIDC 인증 단계 중 예외가 발생할 경우, 그에대한 처리 동작을 정의합니다.
 - 이 예제에서는 egovframework.rte.tex.adapter.EgovOIDCExceptionHandlerImpl 클래스입니다.
 - IDP 인증 페이지로 Redirect하는 동작 중 예외가 발생하거나, Redirect 후 Parameter가 없거나 State가 일치하지 않을 시에만 특정 페이지로 Redirect 되도록 구현하였습니다.
 - 이 예제와는 달리, 실무에서는 각 단계별 예외 동작을 모두 구현해야 할 것으로 생각합니다.

#### 3. OIDC 설정 파일 복사

 - setting_sample/security 디렉토리의 sample_oidc-config.xml을 전자정부 통합예제 설정 파일 디렉토리로 복사합니다
 - 이 예제에서는 resources/egovframework/spring/context-oidc.xml 입니다
 - 전자정부 통합 예제 어플리케이션 설정 파일 네이밍 규칙에 맞추기 위해, 파일명을 context-oidc.xml로 변경했습니다.

#### 4. 위에서 구현한 5종의 클래스를 설정 파일에 적용

 - 위 2항에서 작성한 클래스들을 설정파일의 CUSTOMIZING AREA 에 적용합니다.
 - 이 예제의 resources/egovframework/spring/context-oidc.xml 을 참조하시기 바랍니다.

#### 5. web.xml 파일에 WAS Session Listener 적용

 - WAS Session 만료 이벤트를 OIDC Session에 반영하도록 리스너를 적용합니다.
   - OIDC Session 또한 자체적인 Session timeout을 보유하고 있으나, Timeout에 따른 만료처리를 위해서는 사용자 입력이 필요합니다.
   - WAS Session 만료 이벤트를 OIDC Session과 동기화하여 메모리 누수를 방지하는 역할을 합니다.
 - Redis 등의 외부 통합 세션 구현체를 OIDC Session으로서 사용하는 경우, 이 리스너의 사용을 권장하지 않습니다.
   - 한곳의 WAS Session 만료로 인해 Redis의 OIDC Session이 파기되면 안되기 때문입니다.
 - webapp/WEB-INF/web.xml 파일을 참조하시기 바랍니다.

#### 6. Spring security 설정

 - http Tag의 접근 권한을 설정하고, Filter를 등록하는 절차입니다.
 - 이 예제에서는 진행하지 않고, 대신 egov 패키지의 Injector 설정이 추가되었습니다.
   - 이 예제에서는 OIDC Login / Logout Filter 등록 및 OIDC Authentication Provider의 등록을 egov패키지의 Injector Bean이 대신 수행합니다.
   - context-oidc.xml 파일의 for Egov Security Tag 섹션을 참조하시기 바랍니다.
 - 표준 Spring Security Tag의 경우 아래와 같은 설정이 필요합니다.
   - OIDC Login URL에 대해 permitAll로 세팅
   - OIDC Logout URL (Inbound)에 대해 CSRF 우회 처리 적용
   - OIDC Authentication Provider를 Authentication Manager에 추가
 - 표준 Spring Security 설정을 변경하는 예제는, setting_sample/security 디렉토리의 sample_security-config.xml 파일을 참조하시기 바랍니다.
   - OIDC Template Code에 있습니다.

#### 7. IDP 설정값을 OIDC Config bean에 적용

 - IDP (Keycloak, Google ..)의 설정값을 반영합니다
 - Client ID와 Client Secret을 IDP 에서 받아서 적용합니다.
 - redirectUri, postLogoutUri를 IDP 에서 설정한 값에 일치하게 적용합니다.
   - postLogoutUri의 경우, Logout 이후 Redirect 할 URL로 이용됩니다.
 - authenticationEndpoint 및 jwksUri 등 다른 IDP Endpoint 또한 설정합니다.
   - Keycloak의 경우 Realm에 맞게 설정합니다.
   - 이 예제의 경우, 시연 영상에서와 같이 Demonstration Realm으로 세팅되어 있습니다.

#### 8. OIDC Login / Logout URL 설정

 - OIDC Login 및 Logout을 위한 URL을 Bean 설정 파일인 context-oidc.xml 파일에서 설정합니다.
   - OIDC Login Filter Bean 설정에서 OIDC 인증 URL과 OIDC Redirect URL을 설정합니다.
     - 본 예제에서는 OIDC 인증 URI로서, /keycloak/oidc.do로 설정하였습니다.
     - 본 예제에서는 OIDC Redirect URI로서, /keycloak/oidc_code.do로 설정하였습니다.
     - OIDC Redirect URI는 IDP에 등록되어야 합니다.
   - OIDC Logout Filter Bean 설정에서 OIDC 통합 Logout을 위한 Outbound URL과 Inbound URL을 설정해야 합니다.
     - manualLogoutUri은 Outbound Logout URI를 나타냅니다.
     - idpLogoutUri은 Inbound Logout URI를 나타냅니다.
     - Keycloak의 경우 Inbound Logout URI가 IDP에 등록되어야 합니다. 
 - OIDCAuthSuccessHandler의 로그인 성공 URI를 설정합니다.
   - 인증 성공 시 이동할 URI를 설정합니다.
   - 본 예제에서는, /com/egovMain.do로 설정되어 있습니다.
 - OIDCAuthFailureHandler의 로그인 실패 URI를 설정합니다.
   - 인증 실패 시 이동할 URI를 설정합니다.
   - 본 예제에서는, /index.jsp로 설정되어 있습니다.

#### 9. 테스트

 - OIDC 인증 및 로그아웃 동작을 테스트합니다.
   - 브라우저에서 OIDC Login Filter Bean 설정 시 세팅한 OIDC 인증 URL로 진입합니다.
     - 이 예제에서는 /keycloak/oidc.do 입니다.
   - 정상적으로 설정되었다면, IDP의 인증 화면이 출력됩니다.
   - 인증 후에는 로그인이 완료되어 초기 페이지가 출력됩니다.

#### 시연 영상
 - 본 예제의 동작 시연 영상입니다.
   - https://www.youtube.com/watch?v=9FGOm5OUjVU
 - Keycloak의 인가 기능을 이용한 RBAC 동작을 본 예제로 시연한 영상입니다.(OIDC Template Code-Authz 필요)
   - https://www.youtube.com/watch?v=mgiVfoCt6gc


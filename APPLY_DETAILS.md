## OIDC Template Code - Client Integration Details

[English](./APPLY_DETAILS.md) | [한국어](./APPLY_DETAILS_KR.md)

- This document describes the procedure for applying the OIDC Template Code-Client to Egov. Framework (in South Korea) integration example.
- Version Up Migration, which is unrelated to the application of the OIDC Template Code-Client, is not discussed.
- This example is an application that has migrated the Egov. Framework (in South Korea)  integration sample v3.5.0 to v3.7.0.

### Procedure

#### 1. Copy files and add dependencies.

- Copy the files from 'OIDC Template Code-Client' to the src/main/java directory of Egov. Integration Sample (Egov. Sample).
- Since Egov. Integration Sample Application includes Spring Security, do not delete the i.g.s.o.client.security and egov packages in 'OIDC Template Code-Client'.
  - For Applications that do not include the Spring Security package, build errors will occur in OIDC Template Code-Client due to dependency issues; therefore, you must delete the i.g.s.o.client.security and egov packages from OIDC Template Code-Client.
- Add the com.auth0.java-jwt package to pom.xml.

#### 2. Implement adapter interfaces.

- To integrate with the OIDC Template Code-Client, you must implement 5 types of Adapter Interfaces.

###### (1). ClientAuthConvertAdapter

- This is the step where a Legacy session is created using the Tokens received from the IDP.
- In this example, the class is egovframework.rte.tex.adapter.EgovAuthConvertAdapterImpl.
- The implementation receives the token, verifies the User ID, queries the Legacy DB using that ID to obtain user information, and then uses that information to create and return a Legacy session.
  - Legacy code intrusion may occur at this stage.
  - In this example, logic was added to extract user information from the DB using only the User ID.
  - In this example, additional intrusion occurred for secondary tasks such as retrieving authorization codes, but no modifications were made to the existing code.
- This is a step where discussions regarding handling policies for new and existing users, as well as the reflection of token information, are essential.

###### (2). ClientLoginAdapter

- Defines the behavior of the step upon authentication success or failure.
- In this example, it is the egovframework.rte.tex.adapter.EgovLoginAdapterImpl class.
- It is implemented to redirect to the appropriate page depending on success or failure.
- In this example, it redirects to virtually the same page upon authentication success or failure.

###### (3). ClientLogoutAdapter

- Defines the actions for the phases immediately before and after logout during the IDP integrated logout operation.
- In this example, it is the egovframework.rte.tex.adapter.EgovLogoutAdapterImpl class.
- It is implemented to briefly output only logs so that only the logout operation is known.
- The methods of this class are called identically for both Inbound Logout and Outbound Logout.

###### (4). ClientLegacySessionAdapter

- Used when handling client legacy sessions in the OIDC Login Filter and OIDC Session Manager.
- In this example, it is the egovframework.rte.tex.adapter.EgovSessionAdapterImpl class.
- Since this Egov. Integration sample application uses a Spring Security session as the legacy session, it is implemented to access the Spring Security session.

###### (5). OIDCExceptionHandler

- Defines the handling behavior for exceptions that occur during the OIDC authentication phase.
- In this example, it is the egovframework.rte.tex.adapter.EgovOIDCExceptionHandlerImpl class.
- In this example, I implemented a redirection to a specific page only when an exception occurs during the redirection to the IDP authentication page, or when parameters are missing or the State does not match after the redirection.
- Unlike this example, in a real-world scenario, it is expected that exception handling behaviors for each stage must be implemented.

#### 3. Copy OIDC Configuration files.

- Copy sample_oidc-config_with_security.xml from the sample directory to Egov. Integrated sample application configuration file directory.
- In this example, it is resources/egovframework/spring/context-oidc.xml.
- To comply with the naming conventions for Egov. Integrated sample application configuration files, the filename has been changed to context-oidc.xml.

#### 4. Apply the 5 classes implemented above to the configuration file.

- Apply the classes created in Section 2 above to the CUSTOMIZING AREA of the configuration file.
- Please refer to resources/egovframework/spring/context-oidc.xml in this example.

#### 5. Applying WAS Session Listener to web.xml.

- Apply a listener to reflect WAS Session expiration events in the OIDC Session.
  - Although the OIDC Session also has its own session timeout, user input is required to handle expiration based on the timeout.
  - It serves to prevent memory leaks by synchronizing WAS Session expiration events with the OIDC Session.
- The use of this listener is not recommended when using external integration session implementations, such as Redis, as the OIDC Session.
  - This is because the Redis OIDC Session should not be destroyed due to the expiration of a single WAS Session.
- Please refer to the webapp/WEB-INF/web.xml file in this example.

#### 6. Spring security Configuration

- This is the procedure for setting access permissions for HTTP Tags and registering Filters.
- In this example, this step is omitted; instead, the Injector configuration from the egov package has been added.
  - In this example, the Injector Bean from the egov package handles the registration of OIDC Login/Logout Filters and the OIDC Authentication Provider.
  - Please refer to the 'for Egov Security Tag' section in the context-oidc.xml file.
- For standard Spring Security Tags, the following configurations are required:
  - Set the OIDC Login URL to permitAll
  - Apply CSRF bypass processing to the OIDC Logout URL (Inbound)
  - Add the OIDC Authentication Provider to the Authentication Manager
- For an example of modifying standard Spring Security configurations, please refer to the sample_security-config_with_security.xml file in the sample directory.
  - It is located in the OIDC Template Code.

#### 7. Applying IDP Configuration to OIDC Config bean.

- apply the configuration values of the IDP (Keycloak, Google, etc.)
- Receive the Client ID and Client Secret from the IDP and apply them.
- Apply redirectUri and postLogoutUri to match the values set in the IDP.
  - postLogoutUri is used as the URL to redirect to after logging out.
- Configure other IDP Endpoints such as authenticationEndpoint and jwksUri as well.
  - For Keycloak, configure it according to Realm.
  - In this example, it is set up with the Demonstration Realm, as shown in the demonstration video.

#### 8. OIDC Login / Logout URL Configuration

- Configure the URLs for OIDC Login and Logout in the context-oidc.xml file, which is the Bean configuration file.
  - Configure the OIDC Authentication URL and OIDC Redirect URL in the OIDC Login Filter Bean configuration.
    - In this example, the OIDC Authentication URI is set to /keycloak/oidc.do.
    - In this example, the OIDC Redirect URI is set to /keycloak/oidc_code.do.
    - The OIDC Redirect URI must be registered with the IDP.
  - In the OIDC Logout Filter Bean configuration, you must configure the Outbound URL and Inbound URL for OIDC Integrated Logout.
    - manualLogoutUri represents the Outbound Logout URI.
    - idpLogoutUri represents the Inbound Logout URI.
    - For Keycloak, the Inbound Logout URI must be registered with the IDP.
- Configure the login success URI for the OIDCAuthSuccessHandler.
  - Set the URI to navigate to upon successful authentication.
  - In this example, it is set to /com/egovMain.do.
- Set the login failure URI for OIDCAuthFailureHandler.
  - Set the URI to navigate to upon authentication failure.
  - In this example, it is set to /index.jsp.

#### 9. Test

- Test OIDC authentication and logout operations.
  - Access the OIDC authentication URL configured in the browser when setting the OIDC Login Filter Bean.
    - In this example, it is /keycloak/oidc.do.
  - If configured correctly, the IDP authentication screen will be displayed.
  - After authentication, the login is complete and the initial page is displayed.


#### Demonstration Videos.
- This is a video demonstrating the operation of this example.
    - https://www.youtube.com/watch?v=9FGOm5OUjVU
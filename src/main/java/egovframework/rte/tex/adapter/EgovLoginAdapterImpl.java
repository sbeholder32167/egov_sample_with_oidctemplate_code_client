package egovframework.rte.tex.adapter;

import io.github.sbeholder32167.oidctemplate.adapter.ClientLoginAdapter;
import io.github.sbeholder32167.oidctemplate.exception.OIDCException;
import io.github.sbeholder32167.oidctemplate.client.tokens.OIDCTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 로그인 후 성공 실패에 따른 처리를 구현한 Class.<br>
 *
 * <p>OIDC 연동을 위해 고객사에서 작성되어야 하는 비지니스 로직이 포함된 예제 클래스<br>
 * OIDCAuthSuccessHandler에서 호출 <br>
 * Example: 인증 후 사용자 정보 처리를 담당한다<br>비지니스 로직.<br></p>
 * @author sbeholder6684
 * @version 1.0.0
 * @since 2026-07-03
 */
//-- XML Bean 등록
public class EgovLoginAdapterImpl implements ClientLoginAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EgovLoginAdapterImpl.class);

    @Override
    public void doJobPostLogin(HttpServletRequest request, HttpServletResponse response, OIDCTokens oidcTokens, Object legacySessionObj) {
        //-- Example : 인증 후의 동작 작성
        LOGGER.debug("OIDC Authentication has been finished.");
        try {
            response.sendRedirect(request.getContextPath() + "/com/egovMain.do");
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void doJobFailedLogin(HttpServletRequest request, HttpServletResponse response, OIDCException cause) {
        //-- Example : 세션 중복등으로 인하여 레거시 인증이 실패한 경우의 동작 작성.
        LOGGER.debug("OIDC Authentication has been failed.");
        try {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }
}

package egovframework.rte.tex.adapter;

import io.github.sbeholder32167.oidctemplate.exception.OIDCExceptionEnum;
import io.github.sbeholder32167.oidctemplate.exception.OIDCExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * OIDC 인증 도중 예외 발생 시 예외 처리 구현 클래스<br>
 * Example..<br>
 *
 * @author sbeholder6684
 * @version 1.0.0
 * @since 2026-07-03
 */
//-- XML Bean으로 등록됨
public class EgovOIDCExceptionHandlerImpl implements OIDCExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EgovOIDCExceptionHandlerImpl.class);

    @Override
    public void handleException(OIDCExceptionEnum step, Exception e, HttpServletRequest request, HttpServletResponse response) {
        LOGGER.error("{} -- {}", step.name(), e.getLocalizedMessage());
        if (step == OIDCExceptionEnum.IDP_AUTH_REDIRECT || step == OIDCExceptionEnum.CHECK_PARAMETERS ||
                step == OIDCExceptionEnum.CHECK_STATE){
            //-- OIDCFilter에서 인증 Flow 도중 예외 발생. param은 HttpServlet Response.
            try {
                response.sendRedirect("./mbr/loginView.do?login_error=1" + step.name());
            } catch (IOException ex) {
                LOGGER.error(ex.getLocalizedMessage());
            }
        }
    }
}

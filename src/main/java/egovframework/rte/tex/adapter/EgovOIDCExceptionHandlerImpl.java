package egovframework.rte.tex.adapter;

import io.github.sbeholder32167.oidctemplate.adapter.ClientOIDCExceptionAdapter;
import io.github.sbeholder32167.oidctemplate.client.exception.AuthenticationCodeFlowException;
import io.github.sbeholder32167.oidctemplate.client.exception.OIDCException;
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
public class EgovOIDCExceptionHandlerImpl implements ClientOIDCExceptionAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EgovOIDCExceptionHandlerImpl.class);

    @Override
    public void handleException(OIDCException oe, HttpServletRequest request, HttpServletResponse response) {
        LOGGER.error("{} -- {}", oe.getCategory().name(), oe.getLocalizedMessage());
        if (oe instanceof AuthenticationCodeFlowException){
            AuthenticationCodeFlowException acfe = (AuthenticationCodeFlowException)oe;
            if (acfe.getStep() == AuthenticationCodeFlowException.Step.IDP_AUTH_REDIRECT ||
                acfe.getStep() == AuthenticationCodeFlowException.Step.CHECK_PARAMETERS ||
                acfe.getStep() == AuthenticationCodeFlowException.Step.CHECK_STATE){
                //-- OIDCFilter에서 인증 Flow 도중 예외 발생.
                try {
                    response.sendRedirect("./mbr/loginView.do?login_error=1&category=" + oe.getCategory().name());
                } catch (IOException ex) {
                    LOGGER.error(ex.getLocalizedMessage());
                }
            }
        }
    }
}

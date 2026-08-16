package egovframework.rte.tex.adapter;

import io.github.sbeholder32167.oidctemplate.adapter.ClientLegacySessionAdapter;
import io.github.sbeholder32167.oidctemplate.client.security.OIDCContextUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Client의 Legacy Session Adapter 구현체.<br>
 *
 * <p>Legacy Session Adapter를 OIDC Session Manager등에서 이용할 때 사용<br>
 * Example.<br></p>
 *
 * @author sbeholder6684
 * @version 1.0.0
 * @since 2026-07-03
 */
//-- XML Bean 등록
public class EgovSessionAdapterImpl implements ClientLegacySessionAdapter {

    @Override
    public Object getSession(HttpServletRequest request, HttpServletResponse response, Object param) {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void setSession(HttpServletRequest request, HttpServletResponse response, Object legacySessionObj, boolean created, Object param) {
        if (created){
            OIDCContextUtil.generateOIDCSecurityContext((Authentication) legacySessionObj, request, response);
        }else{
            OIDCContextUtil.refreshOIDCSecurityContext((Authentication)legacySessionObj, request, response);
        }
    }

    @Override
    public void invalidateSession(HttpServletRequest request, HttpServletResponse response, Object param) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session!= null){
            session.invalidate();
        }
    }
}

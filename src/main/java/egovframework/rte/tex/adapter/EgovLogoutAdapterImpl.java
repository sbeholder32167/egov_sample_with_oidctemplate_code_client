package egovframework.rte.tex.adapter;

import io.github.sbeholder32167.oidctemplate.adapter.ClientLogoutAdapter;
import io.github.sbeholder32167.oidctemplate.client.tokens.OIDCTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OIDC Logout 이후 동작시킬 비지니스 로직을 구현한 Class.<br>
 * Example..<br>
 *
 * @author sbeholder6684
 * @version 1.0.0
 * @since 2026-07-03
 */
//-- XML에서 bean으로 등록됨
public class EgovLogoutAdapterImpl implements ClientLogoutAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EgovLogoutAdapterImpl.class);

    @Override
    public void doJobBeforeLogout(HttpServletRequest request, HttpServletResponse response, OIDCTokens oidcTokens) {
        LOGGER.info("Logout begin.");
    }

    @Override
    public void doJobPostLogout(HttpServletResponse response, OIDCTokens oidcTokens) {
        LOGGER.info("Logout complete.");
    }
}

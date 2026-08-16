package egovframework.rte.tex.adapter;

import com.auth0.jwt.interfaces.Claim;
import egovframework.rte.fdl.security.userdetails.EgovUserDetails;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.tex.mbr.service.EgovMemberService;
import egovframework.rte.tex.mbr.service.MemberVO;
import io.github.sbeholder32167.oidctemplate.adapter.ClientAuthConvertAdapter;
import io.github.sbeholder32167.oidctemplate.client.OIDCConfig;
import io.github.sbeholder32167.oidctemplate.client.OIDCTokenTransferObject;
import io.github.sbeholder32167.oidctemplate.client.exception.RBACException;
import io.github.sbeholder32167.oidctemplate.util.KeycloakUtil;
import io.github.sbeholder32167.oidctemplate.util.OIDCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.annotation.Resource;
import java.util.*;

/**
 * Legacy Session을 생성.<br>
 *
 * <p>OIDC Token의 내용으로 Legacy Session을 생성한다.<br>
 * 생성하는 Legacy Session은 Legacy Client에서 사용되는 객체를 그대로 구현한다.<br>
 * Example Class.</p>
 * @author sbeholder6684
 * @version 1.0.0
 * @since 2026-07-03
 */
//-- XML Bean 등록..
public class EgovAuthConvertAdapterImpl implements ClientAuthConvertAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EgovAuthConvertAdapterImpl.class);

    @Autowired
    private OIDCConfig oidcConfig;

    @Autowired
    private RoleHierarchy roleHierarchy;

    /**MemberService */
    @Resource(name="memberService")
    private EgovMemberService memberService;

    @Override
    public Object buildAuthenticationUsingToken(OIDCTokenTransferObject tto) throws RBACException {
        //-- extract user id.
        Map<String, Claim> idTokenMap = OIDCUtil.parseJwtPayload(tto.getIdToken());
        if (idTokenMap == null || idTokenMap.isEmpty()){
            LOGGER.error("Parsing id token has failed.");
            throw new RBACException("Parsing id Token has failed.");
        }
        String userId = idTokenMap.get("preferred_username").asString();
        //-- check user info in legacy DB.
        MemberVO paramVo = new MemberVO();
        paramVo.setId(userId);
        try {
            paramVo = this.memberService.getMemberOIDC(paramVo);
        } catch (Exception e) {
            LOGGER.error("failed to load member information in legacy.");
            throw new RBACException("loading legacy member info has failed.");
        }

        //-- extract legacy authority code using Client Role from IDP.
        Map<String, Claim> accessTokenMap = OIDCUtil.parseJwtPayload(tto.getAccessToken());
        if (accessTokenMap == null){
            LOGGER.error("Parsing access token has failed.");
            throw new RBACException("Parsing access Token has failed.");
        }
        List<String> clientRoleLst = KeycloakUtil.extractClientRolesFromClaims(this.oidcConfig.getClientId(), accessTokenMap);
        if (clientRoleLst == null || clientRoleLst.isEmpty()){
            //-- Example : default role list.
            clientRoleLst = new ArrayList<String>();
            clientRoleLst.add("ROLE_USER");
        }
        //-- Example : 본 레거시 클라이언트는 하나의 계정에 하나의 Role만 대응하기로 DB Scheme이 작성되어 있다. (실무 협의 및 정의가 필요한 사항)
        String authCode;
        try {
            authCode = this.memberService.getCode(clientRoleLst.get(0));
        } catch (Exception e) {
            LOGGER.error("failed to load code in legacy.");
            throw new RBACException("loading legacy code has failed.");
        }
        if (paramVo == null || paramVo.getId().isEmpty()){
            //-- 신규 IDP 인증 사용자 : 토큰 속성 정보를 레거시 DB에 삽입하는 것으로 정의되었다고 가정. (실무 협의 및 정의가 필요한 사항)
            LOGGER.info("new user insert:{}", userId);
            paramVo = new MemberVO();
            paramVo.setId(userId);
            paramVo.setEmail(idTokenMap.get("email").asString());
            paramVo.setPassword(UUID.randomUUID().toString().replace("-", ""));
            paramVo.setName(userId);
            if (idTokenMap.containsKey("mobile_no")){
                paramVo.setMobile(idTokenMap.get("mobile_no").asString());
            }
            if (idTokenMap.containsKey("tel_no")){
                paramVo.setTelno(idTokenMap.get("tel_no").asString());
            }
            paramVo.setMngrSe(authCode);
            try {
                this.memberService.insertMember(paramVo);
            } catch (Exception e) {
                LOGGER.error("failed to insert user info. in legacy.");
                throw new RBACException("create user info. has failed.");
            }
        }else{
            //-- Example : 기존 사용자의 경우 IDP의 권한으로만 업데이트 하기로 정의한 것으로 가정. (실무 협의 및 정의가 필요한 사항)
            paramVo.setMngrSe(authCode);
            try {
                this.memberService.updateMember(paramVo);
            } catch (Exception e) {
                LOGGER.error("failed to update user info. in legacy.");
                throw new RBACException("update user info. has failed.");
            }
        }
        //-- extract user authorities in legacy session.
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        List<EgovMap> authorityLst;
        try {
            authorityLst = this.memberService.getAuthority(paramVo);
            for (EgovMap m : authorityLst){
                authorities.add(new SimpleGrantedAuthority(String.valueOf(m.get("authority"))));
            }
        } catch (Exception e) {
            LOGGER.error("failed to load member authorities in legacy.");
            throw new RBACException("loading legacy member authorities has failed.");
        }

        //-- ROLE_ADMIN을 넣으면 ROLE_USER, ROLE_RESTRICTED 등을 추출하여 리턴
        //-- Egov framework는 최 상위 권한뿐만 아니라 모든 권한을 세션에 넣고 동작한다.
        //-- 이는 레거시 어플리케이션의 세션 구조마다 다르다. (실무 협의 및 정의가 필요한 사항)
        Collection<? extends GrantedAuthority> reachableAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        EgovUserDetails userDetails = new EgovUserDetails(userId, paramVo.getPassword(), true, paramVo);
        return new UsernamePasswordAuthenticationToken(userDetails, tto, reachableAuthorities);
    }
}

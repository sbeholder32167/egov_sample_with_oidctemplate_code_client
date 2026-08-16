<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script>
    //-- Backend Session 만료에 따른 화면 갱신 Logic.
    //-- OIDC Template Code와는 별도 Logic.
    //-- @author : sbeholder6684
    //-- @since : 2026-08-04
    const isLoggedIn = ${not empty loginVO};

    class SessionHeartbeat {
      constructor(options) {
        this.checkUrl = options.checkUrl || '/sample/com/heartbeat.do';
        this.intervalMs = options.intervalMs || 3000;
        this.baseUrl = options.baseUrl || '/sample/index.jsp';
        this.loginUrl = options.loginUrl || '/sample/mbr/loginView.do';
        this.timer = null;
        this.isChecking = false;
      }

      start() {
        if (this.timer) return;

        this.timer = setInterval(async () => {
          if (this.isChecking) return;
          this.isChecking = true;

          try {
            const response = await fetch(this.checkUrl, {
              method: 'HEAD',
              credentials: 'include'
            });

            if (response.status === 401 || response.status === 403) {
              this.handleSessionExpired('Unauthorized / Forbidden');
              return;
            }

            if (response.status === 302 || response.status === 307){
              this.handleSessionExpired('Redirected because no session.');
              return;
            }

            if (response.url.includes(this.loginUrl)) {
              this.handleSessionExpired('Redirected to login Uri.');
              return;
            }

          } catch (error) {
            console.error('세션 체크 중 네트워크 오류:', error);
          } finally {
            this.isChecking = false;
          }
        }, this.intervalMs);
      }

      stop() {
        if (this.timer) {
          clearInterval(this.timer);
          this.timer = null;
        }
      }

      handleSessionExpired(reason) {
        console.warn('Legacy Session expired [' + reason + ']');
        this.stop();
        // alert('The session has been expired. Retry login plz.');
        window.location.href = this.baseUrl;
      }
    }


    const heartbeat = new SessionHeartbeat({
      checkUrl: '/sample/brd/egovBoardList.do',
      intervalMs: 3000,
      baseUrl: '/sample/index.jsp',
      loginUrl: '/sample/mbr/loginView.do'
    });
    if (isLoggedIn){
        heartbeat.start();
    }else{
        console.debug("no need to session.");
    }
</script>

<!-- 행정안전부 로고 및 타이틀 시작 -->
<div id="logoarea" style="padding-top: 10px;">
	<h1><a href="<c:url value='/com/egovMain.do'/>"><font color=black><img src="<c:url value='/images/egovframework/header/img_egovframe_logo.gif'/>"  alt="go main" /></font></a></h1>
</div>
<div id="project_title" style="padding-top: 10px;"><span class="maintitle"><spring:message code="main.egov" /></span> <strong><spring:message code="main.rte" /></strong>
</div>

<div id = "languagearea">
	<div id="langImg">
		<span><a href="<c:url value='/com/egovMain.do?locale=kr'/>" ><img src="<c:url value='/images/egovframework/header/kr.gif'/>"  alt="language_Korean"  title="language_Korean"/></a></span>
		<span><a href="<c:url value='/com/egovMain.do?locale=en'/>" ><img src="<c:url value='/images/egovframework/header/en.gif'/>"  alt="language_English"   title="language_English"/></a></span>
		<span><a href="<c:url value='/com/egovMain.do?locale=jp'/>" ><img src="<c:url value='/images/egovframework/header/jp.gif'/>"  alt="language_Japanese"   title="language_Japanese"/></a></span>
	</div>
</div>


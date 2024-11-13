<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Insert title here</title>
  </head>
  <body>
  <sec:authorize access="isAuthenticated()">
  	<sec:authentication property="principal.email" var="email"/>
  </sec:authorize>
  
    <div class="right-align">
      <ul
        class="horizontal-list member-menu"
        data-email="${email}"
      >
      	  <sec:authorize access="!isAuthenticated()">
            <li>
              <a href="/member/regist">회원가입</a>
            </li>
            <li>
              <a href="/member/login">로그인</a>
            </li>
          </sec:authorize>
          <sec:authorize access="isAuthenticated()">
            <li>
              <sec:authentication property="principal.name"/>
              (<sec:authentication property="principal.email"/>)
            </li>
            <li>
              <a href="/member/logout">로그아웃</a>
            </li>
            <li>
              <a href="/member/delete-me">탈퇴</a>
            </li>
          </sec:authorize>
      </ul>
    </div>
  </body>
</html>

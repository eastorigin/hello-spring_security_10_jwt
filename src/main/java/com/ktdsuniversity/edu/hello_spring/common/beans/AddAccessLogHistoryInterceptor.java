package com.ktdsuniversity.edu.hello_spring.common.beans;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ktdsuniversity.edu.hello_spring.access.dao.AccessLogDao;
import com.ktdsuniversity.edu.hello_spring.access.vo.AccessLogVO;
import com.ktdsuniversity.edu.hello_spring.member.vo.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AddAccessLogHistoryInterceptor implements HandlerInterceptor{

	private AccessLogDao accessLogDao;
	
	public AddAccessLogHistoryInterceptor(AccessLogDao accessLogDao) {
		this.accessLogDao = accessLogDao;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		// Class Cast Exception (String -> MemberVO)
		// 인증되기 전의 Authentication에는 "anonymousUser" 값이 할당되어있음
		// "anonymousUser"을 MemberVO로 형변환 할 수 없는 문제
		
		boolean isAuthenticated = authentication != null && authentication.getPrincipal() instanceof MemberVO;
		MemberVO memberVO = isAuthenticated ? (MemberVO) authentication.getPrincipal() : null;
		
		String email = memberVO == null ? null : memberVO.getEmail();
		
		String controller = handler.toString();
		String packageName = controller.replace("com.ktdsuniversity.edu.hello_spring.", "");
		packageName = packageName.substring(0, packageName.indexOf(".")).toUpperCase();
		
		AccessLogVO accessLogVO = new AccessLogVO();
		accessLogVO.setAccessType(packageName);
		accessLogVO.setAccessEmail(email);
		accessLogVO.setAccessUrl(request.getRequestURI());
		accessLogVO.setAccessMethod(request.getMethod().toUpperCase());
		accessLogVO.setAccessIp(request.getRemoteAddr());
		accessLogVO.setLoginSuccessYn(memberVO == null ? "N" : "Y");
		
		this.accessLogDao.insertNewAccessLog(accessLogVO);
		
		return true;
	}
}

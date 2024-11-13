package com.ktdsuniversity.edu.hello_spring.common.beans.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;
import io.jsonwebtoken.security.SignatureException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
import com.ktdsuniversity.edu.hello_spring.common.beans.security.SecurityUser;
import com.ktdsuniversity.edu.hello_spring.common.vo.ApiResponse;
import com.ktdsuniversity.edu.hello_spring.member.vo.MemberVO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 시큐리티 필터 진행 중 JWT가 전달되었을 경우
 * JWT 검증 & 시큐리티 인증을 진행
 */
@Component
public class JsonWebTokenAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JsonWebTokenProvider jsonWebTokenProvider;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// API일 때만 동작
		// 1. 사용자가 요청한 URL이 무엇인지 확인
		String url = request.getServletPath();
		
		// 2. URL이 /api/로 시작하는 경우는 API 호출을 한 것
		if(url.startsWith("/api/")) {
			
			// 3. HttpRequest에서 header에 있는 Authorization 값을 읽어온다 -> JWT
			String jwt = request.getHeader("Authorization");
			if(jwt == null || jwt.trim().length() == 0) {
				// 403 에러를 전송
				response.sendError(HttpStatus.FORBIDDEN.value());
				return;
			}
			
			// 4. JWT 검증 진행 -> MemberVO를 얻는다 (jsonWebTokenProvider
			MemberVO memberVO = null;
			try {
				memberVO = this.jsonWebTokenProvider.getMemberFromJwt(jwt);
			}
			catch(ExpiredJwtException eje) {
				// 토큰이 만료된 경우
				ApiResponse errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED);
				errorResponse.setErrors(List.of("인증 토큰이 만료되었습니다. 다시 로그인 해주세요."));
				
				// errorResponse -> JSON
				Gson gson = new Gson();
				String errorJson = gson.toJson(errorResponse);
				
				// JSON -> Response
				response.setCharacterEncoding("UTF-8");
				response.setContentType(MediaType.APPLICATION_JSON.toString());
				
				PrintWriter out = response.getWriter();
				out.write(errorJson);
				
				return;
			}
			catch(MalformedJwtException | SignatureException mje) {
				// 토큰이 변조된 경우
				ApiResponse errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED);
				errorResponse.setErrors(List.of("인증 토큰이 변조되었습니다. 다시 로그인 해주세요."));
				
				// errorResponse -> JSON
				Gson gson = new Gson();
				String errorJson = gson.toJson(errorResponse);
				
				// JSON -> Response
				response.setCharacterEncoding("UTF-8");
				response.setContentType(MediaType.APPLICATION_JSON.toString());
				
				PrintWriter out = response.getWriter();
				out.write(errorJson);
				
				return;
			}
			
			// 5. MeberVO를 이용해서 인증 절차를 거친다
			SecurityUser securityUser = new SecurityUser(memberVO);
			Authentication authentication = new UsernamePasswordAuthenticationToken(memberVO,
														"jwtAuthenticatedUser",
														securityUser.getAuthorities());
			
			// 6. SecurityContext에 인증 토큰을 등록처리
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		// 다음 필터가 있을 경우, 제어권(실행권)을 넘긴다
		filterChain.doFilter(request, response);
	}
}

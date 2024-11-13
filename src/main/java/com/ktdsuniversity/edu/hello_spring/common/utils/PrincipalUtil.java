package com.ktdsuniversity.edu.hello_spring.common.utils;

import org.springframework.security.core.Authentication;

import com.ktdsuniversity.edu.hello_spring.member.vo.MemberVO;

public class PrincipalUtil {

	public static MemberVO getPrincipal(Authentication authentication) {
		return (MemberVO) authentication.getPrincipal();
	}
	
	public static String email(Authentication authentication) {
		return getPrincipal(authentication).getEmail();
	}
}

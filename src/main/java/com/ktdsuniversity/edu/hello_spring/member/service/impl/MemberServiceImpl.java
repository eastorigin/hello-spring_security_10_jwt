package com.ktdsuniversity.edu.hello_spring.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ktdsuniversity.edu.hello_spring.access.dao.AccessLogDao;
import com.ktdsuniversity.edu.hello_spring.access.vo.AccessLogVO;
import com.ktdsuniversity.edu.hello_spring.common.beans.Sha;
import com.ktdsuniversity.edu.hello_spring.common.exceptions.AlreadyUseException;
import com.ktdsuniversity.edu.hello_spring.common.utils.RequestUtil;
import com.ktdsuniversity.edu.hello_spring.member.dao.MemberDao;
import com.ktdsuniversity.edu.hello_spring.member.service.MemberService;
import com.ktdsuniversity.edu.hello_spring.member.vo.LoginMemberVO;
import com.ktdsuniversity.edu.hello_spring.member.vo.MemberRegistVO;
import com.ktdsuniversity.edu.hello_spring.member.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService{
	
	@Autowired
	private MemberDao memberDao;

	@Autowired
	private Sha sha;
	
	@Transactional
	@Override
	public boolean insertNewMember(MemberRegistVO memberRegistVO) {
		int emailCount = memberDao.selectEmailCount(memberRegistVO.getEmail());
		if(emailCount > 0) {
			throw new AlreadyUseException(memberRegistVO, "Email이 이미 사용 중입니다");
		}
		
		// 1. Salt 발급
		String salt = sha.generateSalt();
		
		// 2. 사용자의 비밀번호 암호화
		String password = memberRegistVO.getPassword();
		password = sha.getEncrypt(password, salt);
		
		memberRegistVO.setPassword(password);
		memberRegistVO.setSalt(salt);
		
		int insertCount = memberDao.insertNewMember(memberRegistVO);
		return insertCount > 0;
	}
	
	@Override
	public boolean checkAvailableEmail(String email) {
		return this.memberDao.selectEmailCount(email) == 0;
	}
	
	// 탈퇴를 할 때 회원이 작성한 게시글과 댓글을 다 지워주겠다는 코드가 있다면
	// rollback이 없으면 회원만 탈퇴하는 경우가 생김
	@Transactional
	@Override
	public boolean deleteMe(String email) {
		int deleteCount = memberDao.deleteMe(email);
		return deleteCount > 0;
	}
}

package com.ktdsuniversity.edu.hello_spring.bbs.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.hello_spring.bbs.service.BoardService;
import com.ktdsuniversity.edu.hello_spring.bbs.vo.BoardListVO;
import com.ktdsuniversity.edu.hello_spring.bbs.vo.SearchBoardVO;
import com.ktdsuniversity.edu.hello_spring.common.vo.ApiResponse;

@RestController
@RequestMapping("/api/v1")
public class BoardApiController {

	@Autowired
	private BoardService boardService;
	
	/*
	 * JSP : GET / POST
	 * API : GET -> API 데이터 조회 (@GetMapping) -> QueryString Parameter (@RequestParam, CommandObject)
	 * 		 POST -> API 데이터 생성 (@PostMapping) -> JSON Request -> @RequestBody
	 * 		 PUT(FETCH) -> API 데이터 수정 (@PutMapping) -> JSON Request -> @RequestBody
	 * 		 DELETE -> API 데이터 삭제 (@DeleteMapping) -> QueryString Parameter (@RequestParam, CommandObject)
	 */
	@GetMapping("/board/list") // http://localhost:8080/api/v1/board/list?pageNo=1&listSize=10
	public ApiResponse viewBoardList(SearchBoardVO searchBoardVO) {
		
		BoardListVO boardListVO = this.boardService.getAllBoard(searchBoardVO);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setBody(boardListVO.getBoardList());
		
		return apiResponse;
	}
}

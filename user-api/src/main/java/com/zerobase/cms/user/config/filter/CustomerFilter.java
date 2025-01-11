package com.zerobase.cms.user.config.filter;

import com.zerobase.cms.user.service.CustomerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserVo;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/customer/*")
@RequiredArgsConstructor
public class CustomerFilter implements Filter {
	// 특정 URL 패턴 (/customer/*) 에 대한 요청을 가로채고, JWT 토큰을 검증한 후 사용자 정보를 확인하는 역할

	private final JwtAuthenticationProvider jwtAuthenticationProvider;
	private final CustomerService customerService;


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String token = req.getHeader("X-AUTH-TOKEN");
		if(!jwtAuthenticationProvider.validateToken(token)){
			throw new ServletException("Invalid Access");
		}
		UserVo vo = jwtAuthenticationProvider.getUserVo(token);
		customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
				() -> new ServletException("Invalid Access")
		);
		chain.doFilter(request, response);
	}
}

/*
동작 흐름
1. /customer/* 경로로 들어오는 요청을 필터링한다.
2. 요청 헤더에서 X-AUTH-TOKEN 값은 가져온다.
3. JWT 토큰이 유효한지 확인
4. 토큰에서 사용자 정보를 추출하고, 데이터베이스에서 해당 사용자가 존재하는지 확인
5. 검증이 모두 통과되면 요청을 다음 필터나 컨트롤러로 전달한다.
6. 검증 실패 시 예외를 발생시켜 요청을 차단
 */
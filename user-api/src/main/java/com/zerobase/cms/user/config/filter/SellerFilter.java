package com.zerobase.cms.user.config.filter;

import com.zerobase.cms.user.service.seller.SellerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserVo;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/seller/*")
@RequiredArgsConstructor
public class SellerFilter implements Filter {
	// 특정 URL 패턴 (/seller/*) 에 대한 요청을 가로채고, JWT 토큰을 검증한 후 사용자 정보를 확인하는 역할

	private final JwtAuthenticationProvider jwtAuthenticationProvider;
	private final SellerService sellerService;


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String token = req.getHeader("X-AUTH-TOKEN");
		if(!jwtAuthenticationProvider.validateToken(token)){
			throw new ServletException("Invalid Access");
		}
		UserVo vo = jwtAuthenticationProvider.getUserVo(token);
		sellerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
				() -> new ServletException("Invalid Access")
		);
		chain.doFilter(request, response);
	}
}
package com.zerobase.cms.user.application;

import com.zerobase.cms.user.domain.SignInForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.service.customer.CustomerService;
import com.zerobase.cms.user.service.seller.SellerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.zerobase.cms.user.exception.ErrorCode.LOGIN_CHECK_FAIL;

@Service
@RequiredArgsConstructor
public class SignInApplication {

	private final CustomerService customerService;
	private final SellerService sellerService;
	private final JwtAuthenticationProvider provider;

	public String customerLoginToken(SignInForm form) {
		// 1. 로그인 가능 여부
		Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
						.orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

		// 2. 토큰 발행하고

		// 3. 토큰을 response 한다.
		return provider.crateToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
	}

	public String sellerLoginToken(SignInForm form) {
		Seller s = sellerService.findValidSeller(form.getEmail(), form.getPassword())
						.orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

		return provider.crateToken(s.getEmail(), s.getId(), UserType.SELLER);
	}
}

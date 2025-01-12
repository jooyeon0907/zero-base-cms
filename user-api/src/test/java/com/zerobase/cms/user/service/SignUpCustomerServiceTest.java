package com.zerobase.cms.user.service;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.service.customer.SignUpCustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SignUpCustomerServiceTest {

	@Autowired
	private SignUpCustomerService service;

	@Test
	void signUp() {
		SignUpForm form = SignUpForm.builder()
				.name("name")
				.birth(LocalDate.now())
				.email("abc@gmail.com")
				.password("1")
				.phone("010-0000-1111")
				.build();

		Customer c = service.signUp(form);

		assertNotNull(c.getId());
		assertNotNull(c.getCratedAt());
	}


}
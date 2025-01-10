package com.zerobase.cms.user.client.service;

import com.zerobase.cms.user.config.FeignConfig;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailSendServiceTest {

	@Autowired
	private EmailSendService emailSendService;

	@Test
	public void EmailTest() {
		Response response = emailSendService.sendEmail();
		System.out.println(response);

	}

}
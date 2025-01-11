package com.zerobase.cms.user.client.service;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {
	private final MailgunClient mailgunClient;

	public Response sendEmail() {
		SendMailForm form = SendMailForm.builder()
				.from("jooooyeon0907@naver.com")
				.to("jooyeon0907@naver.com")
				.subject("Test email from zero base")
				.text("my text")
				.build();

		return mailgunClient.sendEmail(form);
	}
}

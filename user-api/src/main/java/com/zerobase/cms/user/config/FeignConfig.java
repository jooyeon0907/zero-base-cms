package com.zerobase.cms.user.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

	@Qualifier(value = "mailgun")
	@Bean
	public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor("api", "e03562a87eceaa2ede66c88377c04821-7113c52e-3d5809ff");
	}
}
// privateKey
// 2dfc8ca9e322d9681fe33a1a29f0f88b-7113c52e-c23f1657

// tetmail
// e03562a87eceaa2ede66c88377c04821-7113c52e-3d5809ff
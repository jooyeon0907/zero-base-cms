package com.zerobase.cms.user.config;

import com.zerobase.domain.config.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
	@Bean
	public JwtAuthenticationProvider jwtAuthenticationProvider() {
		return new JwtAuthenticationProvider();
	}
}

/*
 Jwt 는 다른 모듈(zerobase-domain) 에서 가져와 사용하는 것이기 때문에
 Spring 이 해당 객체를 자동으로 bean 으로 등록하지 않을 수 있음.
 그래서 이를 명시적으로 등록하기 위해 @Configuration 과 @Bean 을 사용
 */

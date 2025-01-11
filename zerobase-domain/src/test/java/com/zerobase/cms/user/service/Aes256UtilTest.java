package com.zerobase.cms.user.service;

import com.zerobase.domain.util.Aes256Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Aes256UtilTest {


	@Test
	void encrypt() {
		String encrypt = Aes256Util.encrypt("Hello world");
		assertEquals(Aes256Util.decrypt(encrypt), "Hello world");
	}

}
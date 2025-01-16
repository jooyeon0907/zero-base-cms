package com.zerobase.cms.order.client;

import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-api", url = "${fein.client.url.user-api}")
public interface UserClient {

	@GetMapping("/customer/getInfo")
	ResponseEntity<CustomerDto> getCustomerInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token);

	@PostMapping("/customer/balance")
	ResponseEntity<Integer> changeBalance(
			@RequestHeader(name = "X-AUTH-TOKEN") String token,
		 	@RequestBody ChangeBalanceForm form);

}

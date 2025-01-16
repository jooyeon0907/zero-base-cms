package com.zerobase.cms.order.appliation;

import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.model.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

@Service
@RequiredArgsConstructor
public class OrderApplication {

	private final CartApplication cartApplication;
	private final UserClient userClient;
	private final ProductItemService productItemService;

	@Transactional
	public void order(String token, Cart cart) {
		// case
		// 1. 주문 시 기존 카트 버림 // 강의는 이 로직으로
		// 2. 선택 주문 : 내가 사지 않은 아이템을 살려야 함

		Cart orderCart = cartApplication.refreshCart(cart);
		if (orderCart.getMessages().size() > 0) { // 문제가 있는 경우
			throw new CustomException(ORDER_FAIL_CHECK_CART);
		}

		CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

		int totalPrice = getTotalPrice(cart);
		if (customerDto.getBalance() < totalPrice) {
			throw new CustomException(ORDER_FAIL_NO_MONEY);
		}

		// 해당 시점에 유저의 잔액 변동되거나, 상품 수량 및 가격 변동에 대한 롤백 필요

		userClient.changeBalance(token,
				ChangeBalanceForm.builder()
						.from("USER")
						.message("Order")
						.money(-totalPrice)
						.build());

		for(Cart.Product product : orderCart.getProducts()) {
			for(Cart.ProductItem cartItem : product.getItems()) {
				ProductItem productItem = productItemService.getProductItem(cartItem.getId());
				productItem.setCount(productItem.getCount() - cartItem.getCount());
			}
		}


	}

	private Integer getTotalPrice(Cart cart) {
		return cart.getProducts().stream().flatMapToInt(
 				product -> product.getItems().stream().flatMapToInt(
						productItem -> IntStream.of(productItem.getPrice() * productItem.getCount())
				)
		).sum();
	}


	// 결제를 위해 필요한 것
	// 1. 물건들이 전부 주문 가능한 상태인지 확인
	// 2. 가격 변동이 있었는지에 대해 확인
	// 3. 고객의 돈이 충분한지
	// 4. 결제 & 상품의 재고 관리
}

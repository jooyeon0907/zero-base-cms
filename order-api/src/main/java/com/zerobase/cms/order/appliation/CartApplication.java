package com.zerobase.cms.order.appliation;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.model.redis.Cart;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zerobase.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.zerobase.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class CartApplication {
	private final ProductSearchService productSearchService;
	private final CartService cartService;

	public Cart addCart(Long customerId, AddProductCartForm form) {
		Product product = productSearchService.getByProductId(form.getId());
		if (product == null) {
			throw new CustomException(NOT_FOUND_PRODUCT);
		}
		Cart cart = cartService.getCart(customerId);

		if (cart != null && !addAle(cart, product, form)) {
			throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
		}

		return cartService.addCart(customerId, form);

	}

	/**
	 * 엣지 케이스
	 *
	 * @param customerId
	 * @param cart
	 * @return
	 */
	public Cart updateCart(Long customerId, Cart cart) {
		// 실질적으로 변하는 데이터 -> 상품 삭제, 수량 변경
		cartService.putCart(customerId, cart);
		return getCart(customerId);
	}

	// 1. 장바구니에 상품을 추가한다.
	// 2. 상품의 가격이나 수량이 변동 된다.
	public Cart getCart(Long customerId) {
		Cart cart = refreshCart(cartService.getCart(customerId));
		cartService.putCart(cart.getCustomerId(), cart);
		Cart returnCart = new Cart();
		returnCart.setCustomerId(customerId);
		returnCart.setProducts(cart.getProducts());
		returnCart.setMessages(cart.getMessages());
		cart.setMessages(new ArrayList<>()); // 메세지 비우기
		cartService.putCart(customerId, cart); // 비운 메시지 카트 업데이트

		return returnCart;
		// 2. 메세지를 보고난 다음에는, 이미 본 메세지는 스팸이 되기 때문에 제거한다.
	}

	public void clearCart(Long customerId){
		cartService.putCart(customerId, null);
	}

	protected Cart refreshCart(Cart cart) {
		/// 1. 상품이나 상품의 아이템의 정보, 가격, 수량이 변경되었는지 체크하고
			// 그에 맞는 알람을 제공해ㅐ준다.
		// 2. 상품의 수량, 가겨을 우리가 임의로 변경한다.

		Map<Long, Product> productMap = productSearchService.getListByProductIds(
				cart.getProducts().stream().map(Cart.Product::getId).collect(Collectors.toList()))
				.stream().collect(Collectors.toMap(Product::getId, product -> product));

//		for(Cart.Product cartProduct: cart.getProducts()) {
		for (int i = 0; i < cart.getProducts().size(); i++) {
			Cart.Product cartProduct = cart.getProducts().get(i);

			Product p = productMap.get(cartProduct.getId());
			if(p == null) {
				cart.getProducts().remove(cartProduct);
				i--;
				cart.addMessage(cartProduct.getName() + " 상품이 삭제되었습니다.");
				continue;
			}

			Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
					.collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

			List<String> tmpMessage = new ArrayList<>();
			for (int j = 0; j < cartProduct.getItems().size(); j++) {
				Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
				Long cartProductItemId = cartProductItem.getId();

				ProductItem pi = productItemMap.get(cartProductItemId);
				if(pi == null) {
					cartProduct.getItems().remove(cartProductItem);
					j--;
					tmpMessage.add(cartProductItem.getName() +  " 옵션이 삭제되었습니다.");
					continue;
				}

				boolean isPriceChanged = false, isCountEnough = false;

				if (!cartProductItem.getPrice().equals(pi.getPrice())){
					isPriceChanged = true;
					cartProductItem.setPrice(pi.getPrice());
				}
				if (cartProductItem.getCount() > pi.getCount()) {
					isCountEnough = true;
					cartProductItem.setCount(pi.getCount());
				}

				if (isPriceChanged && isCountEnough) {
					tmpMessage.add(cartProductItem.getName() +  " 가격과 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
				} else if (isPriceChanged) {
					tmpMessage.add(cartProductItem.getName() +  " 가격이 변동되었습니다.");
				} else if (isCountEnough) {
					tmpMessage.add(cartProductItem.getName() +  " 가격과 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
				}
			}

			if (cartProduct.getItems().size() == 0 ){
				cart.getProducts().remove(cartProduct);
				i--;
				cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
				continue;
			} else if (tmpMessage.size() > 0) {
				StringBuilder builder = new StringBuilder();
				builder.append(cartProduct.getName() + "상품의 변동 사항 : ");
				for (String message: tmpMessage) {
					builder.append(message);
					builder.append(", ");
				}
				cart.addMessage(builder.toString());
			}
		}

		return cart;
	}

	private boolean addAle(Cart cart, Product product, AddProductCartForm form) {
		Cart.Product cartProduct = cart.getProducts().stream()
				.filter(p -> p.getId().equals(form.getId()))
				.findFirst().orElse(Cart.Product.builder()
						.id(product.getId())
						.items(Collections.emptyList())
						.build());

		Map<Long, Integer> cartItemContMap = cartProduct.getItems().stream()
				.collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

		Map<Long, Integer> currentItemContMap = product.getProductItems().stream()
				.collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

		return form.getItems().stream().noneMatch(
				formItem -> {
					Integer cartCount = cartItemContMap.get(formItem.getId());
					if (cartCount == null){
						cartCount = 0;
					}
					Integer currentCount = currentItemContMap.get(formItem.getId());
					return formItem.getCount() + cartCount > currentCount;
				});

	}
}

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

	private boolean addAle(Cart cart, Product product, AddProductCartForm form) {
		Cart.Product cartProduct = cart.getProducts().stream()
				.filter(p -> p.getId().equals(form.getId()))
				.findFirst().orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		Map<Long, Integer> cartItemContMap = cartProduct.getItems().stream()
				.collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

		Map<Long, Integer> currentItemContMap = product.getProductItems().stream()
				.collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

		return form.getItems().stream().noneMatch(
				formItem -> {
					Integer cartCount = cartItemContMap.get(formItem.getId());
					Integer currentCount = currentItemContMap.get(formItem.getId());
					return formItem.getCount() + cartCount > currentCount;
				});

	}
}

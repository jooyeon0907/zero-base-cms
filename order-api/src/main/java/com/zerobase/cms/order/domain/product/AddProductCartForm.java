package com.zerobase.cms.order.domain.product;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductCartForm {
	private Long id;
	private Long sellerId;
	private String name;
	private String description;
	private List<ProductItem> items;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProductItem {
		private Long id;
		private String name;
		private Integer count;
		private Integer price;
	}


}

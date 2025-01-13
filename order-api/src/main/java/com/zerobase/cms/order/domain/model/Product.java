package com.zerobase.cms.order.domain.model;

import com.zerobase.cms.order.domain.product.AddProductForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited // Product 객체의 생성, 수정, 삭제 등이 감사 테이블에 기록
@AuditOverride(forClass = BaseEntity.class)
public class Product extends BaseEntity{
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long sellerId;

	private String name;

	private String description;

	// ProductIdem 과 양방향 join
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "product_id")
	private List<ProductItem> productItems = new ArrayList<>();

	public static Product of(Long sellerId, AddProductForm form) {
		return Product.builder()
				.sellerId(sellerId)
				.name(form.getName())
				.description(form.getDescription())
				.productItems(form.getItems()
						.stream().map(piForm -> ProductItem.of(sellerId, piForm)).collect(Collectors.toList()))
				.build();
	}

}

/*
Product : ProductItem = 1 : N 관계
하나의 Product 는 여러 개의 ProductItem 을 가질 수 있으며, ProductItem 은 하나의 Product 에 속한다.
 */
package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    public void shouldGenerate() {
        OrderItem.brandNew()
                .product(Product.builder()
                        .id(new ProductId())
                        .name(new ProductName("Mouse pad"))
                        .price(new Money("100"))
                        .inStock(true)
                        .build())
                .quantity(new Quantity(1))
                .orderId(new OrderId())
                .build();
    }

}
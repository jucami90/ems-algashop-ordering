package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "order_item")
@Data
@ToString(of= "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemPersistenceEntity  {
    @Id
    @EqualsAndHashCode.Include
    private Long id;
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;

    @JoinColumn
    @ManyToOne(optional = false)
    private OrderPersistenceEntity order;

    public Long getOrderId() {
        if (getOrder() == null) {
            return null;
        }

        return getOrder().getId();
    }

}
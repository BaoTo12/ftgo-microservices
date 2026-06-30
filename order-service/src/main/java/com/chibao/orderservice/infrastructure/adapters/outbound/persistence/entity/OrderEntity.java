package com.chibao.orderservice.infrastructure.adapters.outbound.persistence.entity;

import com.chibao.orderservice.domain.model.OrderState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private String id;

    @Column(name = "consumer_id")
    private String consumerId;

    @Column(name = "restaurant_id")
    private String restaurantId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @Version
    private Long version;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderLineItemEntity> lineItems;

    public OrderEntity() {
    }

}

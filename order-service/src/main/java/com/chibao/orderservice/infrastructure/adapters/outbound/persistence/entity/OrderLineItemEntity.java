package com.chibao.orderservice.infrastructure.adapters.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "order_line_items")
public class OrderLineItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_item_id")
    private String menuItemId;

    private String name;
    private BigDecimal price;
    private int quantity;

    public OrderLineItemEntity() {}

    public OrderLineItemEntity(String menuItemId, String name, BigDecimal price, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

}

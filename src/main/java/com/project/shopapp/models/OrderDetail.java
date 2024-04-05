package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private com.project.shopapp.models.Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private com.project.shopapp.models.Order order;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "number_of_products", nullable = false)
    private int numberOfProduct;

    @Column(name = "total_money", nullable = false)
    private float totalMoney;

}

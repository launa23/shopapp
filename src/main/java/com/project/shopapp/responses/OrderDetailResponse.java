package com.project.shopapp.responses;

import com.project.shopapp.models.OrderDetail;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class OrderDetailResponse {
    private long id;
    private long orderId;
    private String productName;
    private float price;
    private int numberOfProduct;
    private float totalMoney;
    private String color;

    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail){
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productName(orderDetail.getProduct().getName())
                .price(orderDetail.getPrice())
                .totalMoney(orderDetail.getTotalMoney())
                .numberOfProduct(orderDetail.getNumberOfProduct())
                .color(orderDetail.getColor())
                .build();
        return orderDetailResponse;
    }
}

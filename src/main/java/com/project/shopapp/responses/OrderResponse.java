package com.project.shopapp.responses;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@Setter
@Getter
public class OrderResponse {
    private Long id;

    private long userId;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String address;

    private String note;

    private Date orderDate;

    private String status;

    private float totalMoney;

    private String shippingMethod;

    private String shippingAdress;

    private LocalDate shippingDate;

    private String trackingNumber;

    private String paymentMethod;
}

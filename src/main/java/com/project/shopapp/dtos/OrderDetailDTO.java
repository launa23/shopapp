package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @Min(value = 1, message = "Order's ID must be > 0")
    @JsonProperty("order_id")
    private Long orderId;

    @Min(value = 1, message = "Product's ID must be > 0")
    @JsonProperty("product_id")
    private Long productId;

    @Min(value = 0, message = "Price must be >= 0")
    private float price;

    @Min(value = 1, message = "Number of product ID must be >= 1")
    @JsonProperty("number_of_product")
    private int numberOfProduct;

    @Min(value = 0, message = "Total money must be >= 0")
    @JsonProperty("total_money")
    private float totalMoney;


}

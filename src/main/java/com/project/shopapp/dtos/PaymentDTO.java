package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    @JsonProperty("money")
    private String money;
    @JsonProperty("message")
    private String message;
//    private String URL;
}

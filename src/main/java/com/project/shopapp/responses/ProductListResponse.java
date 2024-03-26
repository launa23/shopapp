package com.project.shopapp.responses;

import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class ProductListResponse {
    private List<ProductResponse> productResponses;
    private int totalPage;
}

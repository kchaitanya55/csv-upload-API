package com.csv.upload.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedProductResponse {
    private List<Product> productList;
    private Long numberOfItems;
    private int numberOfPages;
}
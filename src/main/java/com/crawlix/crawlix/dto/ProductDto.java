package com.crawlix.crawlix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String name;
    private String price;
    private String url;
    private String rating;
    private String reviewCount;

    // 생성자: 동일한 변수명 사용
    public ProductDto(String name, String price, String url) {
        this.name = name;
        this.price = price;
        this.url = url;
    }
}

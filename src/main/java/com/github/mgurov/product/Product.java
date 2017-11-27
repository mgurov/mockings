package com.github.mgurov.product;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Product {
    private String identifier;
    private String title;
    @Builder.Default
    private List<String> eans = new ArrayList<String>();
}

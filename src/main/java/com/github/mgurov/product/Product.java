package com.github.mgurov.product;

import lombok.Data;

import java.util.List;

@Data
public class Product {
    private String identifier;
    private String title;
    private List<String> eans;
}

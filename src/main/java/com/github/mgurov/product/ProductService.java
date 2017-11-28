package com.github.mgurov.product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductConnector productConnector;

    public ProductService(ProductConnector productConnector) {
        this.productConnector = productConnector;
    }

    public List<Product> fetchAwesomeProducts() {
        return productConnector.listProducts().stream().filter(p -> p.getTitle().contains("a")).collect(Collectors.toList());
    }

    public List<Product> findProductsWithTitle(String title) {
        return productConnector.listProducts().stream().filter(p -> p.getTitle().equals(title)).collect(Collectors.toList());
    }
}

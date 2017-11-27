package com.github.mgurov.product;

import java.util.List;

public class ProductService {

    private final ProductConnector productConnector;

    public ProductService(ProductConnector productConnector) {
        this.productConnector = productConnector;
    }

    public List<Product> fetchAwesomeProducts() {
        return productConnector.listProducts();
    }
}

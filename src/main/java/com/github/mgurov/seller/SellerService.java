package com.github.mgurov.seller;

public class SellerService {

    private final SellerConnector sellerConnector;

    public SellerService(SellerConnector sellerConnector) {
        this.sellerConnector = sellerConnector;
    }

    public Seller fetchSellerById(String sellerId) {
        return sellerConnector.fetchSellerById(sellerId);
    }
}

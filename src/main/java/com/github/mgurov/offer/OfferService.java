package com.github.mgurov.offer;

import java.util.List;

public class OfferService {

    private final OfferConnector offerConnector;

    public OfferService(OfferConnector offerConnector) {
        this.offerConnector = offerConnector;
    }

    public List<Offer> fetchOffers(String productIdentifier) {
        return offerConnector.fetchOffers(productIdentifier);
    }
}

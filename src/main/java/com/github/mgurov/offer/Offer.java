package com.github.mgurov.offer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Offer {
    private String identifier;
    private String productIdentifier;
    private String sellerId;
    private int priceCents;
    private int daysToDeliver;
}

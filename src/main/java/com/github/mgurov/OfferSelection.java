package com.github.mgurov;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferSelection {
    private String offerId;
    private int priceCents;
    private String sellerName;
}

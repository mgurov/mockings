package com.github.mgurov.seller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Seller {
    public String id;
    public String name;
    @Builder.Default
    public boolean trustWorthy = true;
}

package com.github.mgurov.seller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Seller {
    public String id;
    public String name;
    public boolean trustWorthy;
}

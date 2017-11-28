package com.github.mgurov;

import com.github.mgurov.offer.Offer;
import com.github.mgurov.offer.OfferService;
import com.github.mgurov.product.Product;
import com.github.mgurov.product.ProductService;
import com.github.mgurov.seller.Seller;
import com.github.mgurov.seller.SellerService;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BusinessLogic {

    private final OfferService offerService;
    private final ProductService productService;
    private final SellerService sellerService;

    public BusinessLogic(OfferService offerService, ProductService productService, com.github.mgurov.seller.SellerService sellerService) {
        this.offerService = offerService;
        this.productService = productService;
        this.sellerService = sellerService;
    }

    public Optional<OfferSelection> selectBestOffer(String title) {
        List<Product> productsWithTitle = productService.findProductsWithTitle(title);

        List<Offer> offers = productsWithTitle.stream()
                .flatMap(p -> offerService.fetchOffers(p.getIdentifier()).stream()).collect(Collectors.toList());

        List<OfferAndSeller> offersWithSeller = offers.stream()
                .map(o -> new OfferAndSeller(o, sellerService.fetchSellerById(o.getSellerId()))).collect(Collectors.toList());

        List<OfferAndSeller> reliableOffers = offersWithSeller.stream().filter(os -> os.seller.trustWorthy).collect(Collectors.toList());

        Optional<OfferAndSeller> offerWithMinPrice = reliableOffers.stream().min(Comparator.comparingInt(os -> os.offer.getPriceCents()));

        return offerWithMinPrice.map(OfferAndSeller::toOfferSelection);
    }

    @Data
    private static class OfferAndSeller {

        private final Offer offer;
        private final Seller seller;

        private OfferSelection toOfferSelection() {
            return OfferSelection.builder().offerId(offer.getId()).priceCents(offer.getPriceCents()).sellerName(seller.name).build();
        }
    }
}

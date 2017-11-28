package com.github.mgurov;

import com.github.mgurov.offer.Offer;
import com.github.mgurov.offer.OfferConnector;
import com.github.mgurov.offer.OfferService;
import com.github.mgurov.product.Product;
import com.github.mgurov.product.ProductConnector;
import com.github.mgurov.product.ProductService;
import com.github.mgurov.seller.Seller;
import com.github.mgurov.seller.SellerConnector;
import com.github.mgurov.seller.SellerService;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class _2_BusinessLogicTest {

    @Test
    public void shouldTakeCheapestAsBestOffer() {
        //given

        Product blahProduct = Product.builder().title("blah").build();

        Mockito.when(productConnector.listProducts())
                .thenReturn(Collections.singletonList(blahProduct));

        Offer offer1 = Offer.builder().priceCents(100).id("expensive").build();
        Offer offer2 = Offer.builder().priceCents(10).id("cheap").build();

        Mockito.when(offerConnector.fetchOffers(Mockito.any()))
                .thenReturn(Arrays.asList(offer1, offer2));

        Seller seller = Seller.builder().name("Продавець").build();
        Mockito.when(sellerConnector.fetchSellerById(Mockito.any()))
                .thenReturn(seller);

        //when
        Optional<OfferSelection> actual = cut.selectBestOffer("blah");
        //then
        Optional<OfferSelection> expected = Optional.of(OfferSelection.builder().offerId("cheap").priceCents(10).sellerName(seller.name).build());
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotTakeCheapestIfSellerUnreliable() {
        //given

        Product blahProduct = Product.builder().title("blah").build();

        Mockito.when(productConnector.listProducts())
                .thenReturn(Collections.singletonList(blahProduct));

        Offer offer1 = Offer.builder().priceCents(100).id("expensive").sellerId("1").build();
        Offer offer2 = Offer.builder().priceCents(10).id("cheap").sellerId("2").build();

        Mockito.when(offerConnector.fetchOffers(Mockito.any()))
                .thenReturn(Arrays.asList(offer1, offer2));

        Seller goodSeller = Seller.builder().name("Продавець").build();
        Mockito.when(sellerConnector.fetchSellerById("1")).thenReturn(goodSeller);
        Seller badSeller = Seller.builder().name("Шахрай").trustWorthy(false).build();
        Mockito.when(sellerConnector.fetchSellerById("2")).thenReturn(badSeller);

        //when
        Optional<OfferSelection> actual = cut.selectBestOffer("blah");
        //then
        Optional<OfferSelection> expected = Optional.of(OfferSelection.builder().offerId("expensive").priceCents(100).sellerName(goodSeller.name).build());
        assertEquals(expected, actual);
    }

    ProductConnector productConnector = Mockito.mock(ProductConnector.class);
    OfferConnector offerConnector = Mockito.mock(OfferConnector.class);
    SellerConnector sellerConnector = Mockito.mock(SellerConnector.class);

    BusinessLogic cut = new BusinessLogic(new OfferService(offerConnector), new ProductService(productConnector), new SellerService(sellerConnector));
}
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class _3_MeetExternalDomainTest {

    @Test
    public void shouldTakeCheapestAsBestOffer() {
        //given

        ExternalProduct blahProduct = given.aProduct();
        blahProduct.title = "blah";

        ExternalSeller seller = given.aSeller();
        seller.name = "Продавець";

        ExternalOffer offer1 = seller.hasOffer(blahProduct);
        offer1.price = 100;
        offer1.id = "expensive";
        ExternalOffer offer2 = seller.hasOffer(blahProduct);
        offer2.price = 10;
        offer2.id = "cheap";

        //when
        Optional<OfferSelection> actual = cut.selectBestOffer("blah");
        //then
        Optional<OfferSelection> expected = Optional.of(OfferSelection.builder().offerId("cheap").priceCents(10).sellerName(seller.name).build());
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotTakeCheapestIfSellerUnreliable() {
        //given
        ExternalProduct blahProduct = given.aProduct();

        ExternalSeller goodSeller = given.aSeller();
        goodSeller.good = true;
        ExternalOffer offer1 = goodSeller.hasOffer(blahProduct);
        offer1.price = 100;

        ExternalSeller badSeller = given.aSeller();
        badSeller.good = false;
        ExternalOffer offer2 = badSeller.hasOffer(blahProduct);
        offer2.price = 10;

        //when
        Optional<String> actual = cut.selectBestOffer(blahProduct.title).map(OfferSelection::getSellerName);
        //then
        assertEquals(Optional.of(goodSeller.name), actual);
    }

    @Test
    public void lookHowEasyToInjectExceptions() {
        //given
        ExternalProduct blahProduct = given.aProduct();

        ExternalSeller goodSeller = given.aSeller();
        ExternalOffer offer1 = goodSeller.hasOffer(blahProduct);
        offer1.price = 100;

        ExternalSeller cheaper = given.aSeller();
        ExternalOffer offer2 = cheaper.hasOffer(blahProduct);
        offer2.price = 10;

        //when
        Optional<String> actual = cut.selectBestOffer(blahProduct.title).map(OfferSelection::getSellerName);
        //then
        assertEquals(Optional.of(goodSeller.name), actual);
    }

    ProductConnector productConnector = Mockito.mock(ProductConnector.class);
    OfferConnector offerConnector = Mockito.mock(OfferConnector.class);
    SellerConnector sellerConnector = Mockito.mock(SellerConnector.class);

    BusinessLogic cut = new BusinessLogic(new OfferService(offerConnector), new ProductService(productConnector), new SellerService(sellerConnector));

    private ExternalContext given = new ExternalContext();

    @Before
    public void setupDynamicMockery() {
        Mockito.when(productConnector.listProducts())
                .thenAnswer((invocation) -> given.products.stream().map(ExternalProduct::toTransportable).collect(Collectors.toList()));

        Mockito.when(offerConnector.fetchOffers(Mockito.any()))
                .thenAnswer((invocation) ->{
                    String productId = invocation.getArgumentAt(0, String.class);
                    return given.sellers.stream()
                            .flatMap(s -> s.offers.stream())
                            .filter(o -> o.product.id.equals(productId))
                            .map(ExternalOffer::toTransportable)
                            .collect(Collectors.toList());
                    }
                );

        Mockito.when(sellerConnector.fetchSellerById(Mockito.any()))
                .thenAnswer(invocation -> {
                    String sellerId = invocation.getArgumentAt(0, String.class);
                    return given.sellers.stream()
                            .filter(s -> s.id.equals(sellerId))
                            .findAny()
                            .map(ExternalSeller::toTransportable)
                            .orElseThrow(() -> new RuntimeException("Could not find seller with id " + sellerId));
                });
    }

    public static class ExternalContext {

        public static final AtomicLong seq = new AtomicLong(0L);

        private List<ExternalProduct> products = new ArrayList<>();
        private List<ExternalSeller> sellers = new ArrayList<>();

        public ExternalProduct aProduct() {
            ExternalProduct p = new ExternalProduct();
            products.add(p);
            return p;
        }

        public ExternalSeller aSeller() {
            ExternalSeller s = new ExternalSeller();
            sellers.add(s);
            return s;
        }

    }

    public static class ExternalProduct {
        public final String id = "#" + ExternalContext.seq.incrementAndGet();
        public String title = "product" + id;

        public Product toTransportable() {
            return Product.builder().title(title).identifier(id).build();
        }

    }

    public static class ExternalSeller {
        public String id = "#" + ExternalContext.seq.incrementAndGet();
        public String name = "seller" + id;
        private List<ExternalOffer> offers = new ArrayList<>();
        public boolean good = true;

        public Seller toTransportable() {
            return Seller.builder().name(name).trustWorthy(good).build();
        }

        public ExternalOffer hasOffer(ExternalProduct product) {
            ExternalOffer externalOffer = new ExternalOffer(product, this);
            offers.add(externalOffer);
            return externalOffer;
        }
    }

    private static class ExternalOffer {
        private final ExternalProduct product;
        private final ExternalSeller seller;
        public int price = 1;

        public String id = "offer" + ExternalContext.seq.incrementAndGet();

        public ExternalOffer(ExternalProduct product, ExternalSeller seller) {
            this.product = product;
            this.seller = seller;
        }

        public Offer toTransportable() {
            return Offer.builder()
                    .id(id)
                    .priceCents(price)
                    .sellerId(seller.id)
                    .productIdentifier(product.id)
                    .build();
        }
    }
}
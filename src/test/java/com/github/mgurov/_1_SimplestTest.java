package com.github.mgurov;

import com.github.mgurov.product.Product;
import com.github.mgurov.product.ProductConnector;
import com.github.mgurov.product.ProductService;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class _1_SimplestTest {

    @Test
    public void shouldInclude_A_product_awesomeProductsList() {
        //given
        Product awesome = Product.builder().title("blah").build();
        Product notSoMuch = Product.builder().title("fooe").build();
        Mockito.when(productConnector.listProducts()).thenReturn(
                Arrays.asList(
                        awesome,
                        notSoMuch
                )
        );
        //when
        List<Product> actual = cut.fetchAwesomeProducts();
        //then
        assertEquals(Collections.singletonList(awesome), actual);
    }

    ProductConnector productConnector = Mockito.mock(ProductConnector.class);
    ProductService cut = new ProductService(productConnector);
}
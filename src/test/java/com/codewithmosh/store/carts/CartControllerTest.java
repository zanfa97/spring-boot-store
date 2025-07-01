package com.codewithmosh.store.carts;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.codewithmosh.store.auth.JwtAuthenticationFilter;
import com.codewithmosh.store.auth.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false) // This disables all security filters
class CartControllerTest {

    private CartDto testCart;
    private ProductDto testProduct;
    private CartItemDto testCartItem;

    @BeforeEach
    void setUp() {
        testCart = new CartDto();
        testCart.setId(UUID.randomUUID());
        testCart.setCartItems(new ArrayList<>());
        testCart.setTotalPrice(BigDecimal.ZERO);

        testProduct = new ProductDto();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(99.99));
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreateEmptyCart() throws Exception {

        when(cartService.createCart())
                .thenReturn(testCart);

        mockMvc.perform(post("/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty()))) 
                .andExpect(jsonPath("$.id", is(testCart.getId().toString())))
                .andExpect(jsonPath("$.cartItems", hasSize(0)))
                .andExpect(jsonPath("$.totalPrice", is(0)));

        verify(cartService).createCart();
    }

    @Test
    void shouldAddItemToCart() throws Exception {

        testCartItem = new CartItemDto();
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(1);
        testCartItem.setTotalPrice(testProduct.getPrice());

        var request = new AddItemToCartRequest();
        request.setProductId(1L);

        given(cartService.addToCart(testCart.getId(), 1L))
                .willReturn(testCartItem);

        testCart.getCartItems().add(testCartItem);
        testCart.setTotalPrice(testCartItem.getTotalPrice());

        given(cartService.getCart(testCart.getId()))
        .willReturn(testCart);

        mockMvc.perform(post("/carts/{cartId}/items", testCart.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.product.id", is(testCartItem.getProduct().getId().intValue())))
            .andExpect(jsonPath("$.product.name", is(testCartItem.getProduct().getName())))
            .andExpect(jsonPath("$.product.price", is(testCartItem.getProduct().getPrice().doubleValue())))
            .andExpect(jsonPath("$.quantity", is(testCartItem.getQuantity())))
            .andExpect(jsonPath("$.totalPrice", is(testCartItem.getTotalPrice().doubleValue())));

        mockMvc.perform(get("/carts/{cartId}", testCart.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(testCart.getId().toString())))
            .andExpect(jsonPath("$.cartItems", hasSize(1)))
            .andExpect(jsonPath("$.totalPrice", is(testCartItem.getTotalPrice().doubleValue())));

        then(cartService).should().addToCart(testCart.getId(), 1L);
    }

}

package com.codewithmosh.store.carts;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithmosh.store.products.ProductNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RequestMapping("/carts")
@RestController
public class CartController {

    private final CartService cartService;

    @PostMapping
    @Operation(summary = "Create a new cart")
    @ApiResponse(responseCode = "201",
                 description = "Cart created successfully")
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder) {

        var cartDto = cartService.createCart();

        var uri = uriBuilder
                .path("/carts/{id}")
                .buildAndExpand(cartDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addItem(
            @RequestBody AddItemToCartRequest request,
            @PathVariable("cartId") UUID cartId) {

        var cartItemDto = cartService.addToCart(cartId, request.getProductId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartItemDto);
    
    }

    @ApiResponse(responseCode = "200",
                 description = "Cart retrieved successfully")
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable("cartId") UUID cartId) {
        var cart = cartService.getCart(cartId);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartItemDto> updateCartItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        
        var cartItemDto = cartService.updateCartItem(cartId, productId, request.getQuantity());

        return ResponseEntity.ok(cartItemDto);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId) {

        cartService.removeCartItem(cartId, productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(@PathVariable("cartId") UUID cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound(CartNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Cart not found"));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Product not found in the cart"));
    }
}

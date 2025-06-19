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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Cart", description = "Shopping cart management endpoints")
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
    }    @Operation(summary = "Add item to cart", description = "Adds a new product to the specified shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item added successfully", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItemDto.class))),
        @ApiResponse(responseCode = "404", description = "Cart or product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addItem(
            @RequestBody AddItemToCartRequest request,
            @PathVariable("cartId") UUID cartId) {

        var cartItemDto = cartService.addToCart(cartId, request.getProductId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartItemDto);
    
    }    @Operation(summary = "Get cart details", description = "Retrieves the details of a specific shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartDto.class))),
        @ApiResponse(responseCode = "404", description = "Cart not found",
            content = @Content)
    })
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable("cartId") UUID cartId) {
        var cart = cartService.getCart(cartId);
        return ResponseEntity.ok(cart);
    }@Operation(summary = "Update cart item", description = "Updates the quantity of a product in the cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItemDto.class))),
        @ApiResponse(responseCode = "404", description = "Cart or product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartItemDto> updateCartItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        var cartItemDto = cartService.updateCartItem(cartId, productId, request.getQuantity());

        return ResponseEntity.ok(cartItemDto);
    }    @Operation(summary = "Remove item from cart", description = "Removes a product from the shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Item removed successfully"),
        @ApiResponse(responseCode = "404", description = "Cart or product not found")
    })
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId) {

        cartService.removeCartItem(cartId, productId);

        return ResponseEntity.noContent().build();
    }    @Operation(summary = "Clear cart", description = "Removes all items from the specified shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
        @ApiResponse(responseCode = "404", description = "Cart not found",
            content = @Content)
    })
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

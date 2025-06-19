package com.codewithmosh.store.products;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Products", description = "Product management endpoints")
@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Operation(summary = "Get all products", description = "Retrieves all products with optional category filter")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully",
                content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(name = "StoreProduct", implementation = ProductDto.class))))
    @GetMapping
    public List<ProductDto> getAllProducts(
        @Parameter(description = "Category ID to filter products by")
        @RequestParam(name = "categoryId", required = false) Byte categoryId) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAllWithCategory();
        }
        return products.stream().map(productMapper::toDto).toList();
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID")
    @ApiResponses(value = {        @ApiResponse(responseCode = "200", description = "Product found",
            content = @Content(mediaType = "application/json",
            schema = @Schema(name = "StoreProduct", implementation = ProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(
        @Parameter(description = "ID of the product to retrieve", required = true)
        @PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @Operation(summary = "Create new product", description = "Creates a new product in the store")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(mediaType = "application/json",
            schema = @Schema(name = "StoreProduct", implementation = ProductDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or category not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product details to create",
            required = true,
            content = @Content(schema = @Schema(name = "StoreProduct", implementation = ProductDto.class)))
        @RequestBody ProductDto productDto,
        UriComponentsBuilder uriBuilder) {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }
        
        var product = productMapper.toEntity(productDto);
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());

        var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();

        return ResponseEntity.created(uri).body(productMapper.toDto(product));
    }

    @Operation(summary = "Update product", description = "Updates an existing product's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully",
            content = @Content(mediaType = "application/json",
            schema = @Schema(name = "StoreProduct", implementation = ProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
        @Parameter(description = "ID of the product to update", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated product details",
            required = true,
            content = @Content(schema = @Schema(name = "StoreProduct", implementation = ProductDto.class)))
        @RequestBody ProductDto productDto) {
        var product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        productMapper.update(productDto, product);
        productRepository.save(product);

        productDto.setId(product.getId());

        return ResponseEntity.ok(productDto);
    }

    @Operation(summary = "Delete product", description = "Deletes a product from the store")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "ID of the product to delete", required = true)
        @PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);

        return ResponseEntity.noContent().build();
    }
}

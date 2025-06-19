package com.codewithmosh.store.admin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin", description = "Administrative endpoints")
@RestController
@RequestMapping("/admin")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    
    @Operation(
        summary = "Admin test endpoint", 
        description = "Simple endpoint to test admin access"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully accessed admin endpoint"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized - Requires ADMIN role")
    })
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Admin!";
    }
}

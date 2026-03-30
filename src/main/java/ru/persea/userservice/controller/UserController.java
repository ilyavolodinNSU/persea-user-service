package ru.persea.userservice.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.FactorDto;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.service.UserService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

// TODO: заменить UserDetails на Jwt

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me/viewed-products")
    public ResponseEntity<List<ProductDto>> getViewedProducts(
        @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(userService.getViewedProducts(UUID.fromString(user.getUsername())));
    }

    @GetMapping("/me/favorites")
    public ResponseEntity<List<ProductDto>> getFavoriteProducts(
        @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(userService.getFavoriteProducts(UUID.fromString(user.getUsername())));
    }

    @PostMapping("/me/favorites/{productId}")
    public ResponseEntity<Void> addFavoriteProduct(
        @AuthenticationPrincipal UserDetails user, 
        @PathVariable Long productId
    ) {
        userService.addFavoriteProduct(UUID.fromString(user.getUsername()), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/favorites/{productId}")
    public ResponseEntity<Void> deleteFavoriteProduct(
        @AuthenticationPrincipal UserDetails user,
        @PathVariable Long productId
    ) {
        userService.deleteFavoriteProduct(UUID.fromString(user.getUsername()), productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/allergens")
    public ResponseEntity<List<FactorDto>> getAllergens(
        @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(userService.getAllergens(UUID.fromString(user.getUsername())));
    }

    @PostMapping("/me/allergens/{factorId}")
    public ResponseEntity<Void> addAllergen(
        @AuthenticationPrincipal UserDetails user, 
        @PathVariable Long factorId
    ) {
        userService.addAllergen(UUID.fromString(user.getUsername()), factorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/allergens/{factorId}")
    public ResponseEntity<Void> deleteAllergen(
        @AuthenticationPrincipal UserDetails user,
        @PathVariable Long factorId
    ) {
        userService.deleteAllergen(UUID.fromString(user.getUsername()), factorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/viewed-products/{productId}")
    public ResponseEntity<Void> addViewedProduct(
        @PathVariable String userId, 
        @PathVariable Long productId
    ) {
        userService.addViewedProduct(UUID.fromString(userId), productId);
        return ResponseEntity.noContent().build();
    }

}

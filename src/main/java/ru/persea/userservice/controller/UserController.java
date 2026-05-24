package ru.persea.userservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.FactorDto;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me/viewed-products")
    public ResponseEntity<List<ProductDto>> getViewedProducts(JwtAuthenticationToken jwt) {
        return ResponseEntity.ok(userService.getViewedProducts(UUID.fromString(jwt.getToken().getSubject())));
    }

    @GetMapping("/me/favorites")
    public ResponseEntity<List<ProductDto>> getFavoriteProducts(JwtAuthenticationToken jwt) {
        return ResponseEntity.ok(userService.getFavoriteProducts(UUID.fromString(jwt.getToken().getSubject())));
    }

    @PostMapping("/me/favorites/{productId}")
    public ResponseEntity<Void> addFavoriteProduct(JwtAuthenticationToken jwt, @PathVariable Long productId) {
        userService.addFavoriteProduct(UUID.fromString(jwt.getToken().getSubject()), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/favorites/{productId}")
    public ResponseEntity<Void> deleteFavoriteProduct(JwtAuthenticationToken jwt, @PathVariable Long productId) {
        userService.deleteFavoriteProduct(UUID.fromString(jwt.getToken().getSubject()), productId);
        return ResponseEntity.noContent().build();
    }
}
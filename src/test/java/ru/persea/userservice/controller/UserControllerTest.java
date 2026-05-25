package ru.persea.userservice.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.test.context.ActiveProfiles;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private JwtAuthenticationToken jwtAuthToken;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        Jwt jwt = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("sub", userId.toString())
        );

        jwtAuthToken = new JwtAuthenticationToken(jwt, List.of());
    }

    // -------------------------------------------------------------------------
    // GET /users/me/scanned-products
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getScannedProducts — возвращает 200 и список продуктов")
    void getScannedProducts_returnsOkWithList() {
        List<ProductDto> products = List.of(
                new ProductDto(1L, "Product A", null, null, 5, "uri-a"),
                new ProductDto(2L, "Product B", null, null, 4, "uri-b")
        );
        when(userService.getScannedProducts(userId)).thenReturn(products);

        ResponseEntity<List<ProductDto>> response = userController.getScannedProducts(jwtAuthToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).id()).isEqualTo(1L);
        assertThat(response.getBody().get(0).name()).isEqualTo("Product A");
        assertThat(response.getBody().get(1).id()).isEqualTo(2L);
        verify(userService).getScannedProducts(userId);
    }

    @Test
    @DisplayName("getScannedProducts — возвращает пустой список")
    void getScannedProducts_returnsEmptyList() {
        when(userService.getScannedProducts(userId)).thenReturn(List.of());

        ResponseEntity<List<ProductDto>> response = userController.getScannedProducts(jwtAuthToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("getScannedProducts — userId корректно извлекается из JWT")
    void getScannedProducts_extractsUserIdFromJwt() {
        when(userService.getScannedProducts(userId)).thenReturn(List.of());

        userController.getScannedProducts(jwtAuthToken);

        verify(userService).getScannedProducts(userId);
    }

    // -------------------------------------------------------------------------
    // GET /users/me/viewed-products
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getViewedProducts — возвращает 200 и список продуктов")
    void getViewedProducts_returnsOkWithList() {
        List<ProductDto> products = List.of(
                new ProductDto(10L, "Viewed Product", null, null, 3, "uri-v")
        );
        when(userService.getViewedProducts(userId)).thenReturn(products);

        ResponseEntity<List<ProductDto>> response = userController.getViewedProducts(jwtAuthToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).name()).isEqualTo("Viewed Product");
        verify(userService).getViewedProducts(userId);
    }

    @Test
    @DisplayName("getViewedProducts — возвращает пустой список")
    void getViewedProducts_returnsEmptyList() {
        when(userService.getViewedProducts(userId)).thenReturn(List.of());

        ResponseEntity<List<ProductDto>> response = userController.getViewedProducts(jwtAuthToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // GET /users/me/favorites
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getFavoriteProducts — возвращает 200 и список избранных")
    void getFavoriteProducts_returnsOkWithList() {
        List<ProductDto> favorites = List.of(
                new ProductDto(5L, "Favorite A", null, null, 5, "uri-fa"),
                new ProductDto(6L, "Favorite B", null, null, 4, "uri-fb"),
                new ProductDto(7L, "Favorite C", null, null, 3, "uri-fc")
        );
        when(userService.getFavoriteProducts(userId)).thenReturn(favorites);

        ResponseEntity<List<ProductDto>> response = userController.getFavoriteProducts(jwtAuthToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody().get(2).name()).isEqualTo("Favorite C");
        verify(userService).getFavoriteProducts(userId);
    }

    @Test
    @DisplayName("getFavoriteProducts — возвращает пустой список")
    void getFavoriteProducts_returnsEmptyList() {
        when(userService.getFavoriteProducts(userId)).thenReturn(List.of());

        ResponseEntity<List<ProductDto>> response = userController.getFavoriteProducts(jwtAuthToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // POST /users/me/favorites/{productId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("addFavoriteProduct — возвращает 204 No Content")
    void addFavoriteProduct_returnsNoContent() {
        Long productId = 42L;
        doNothing().when(userService).addFavoriteProduct(userId, productId);

        ResponseEntity<Void> response = userController.addFavoriteProduct(jwtAuthToken, productId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(userService).addFavoriteProduct(userId, productId);
    }

    @Test
    @DisplayName("addFavoriteProduct — userId и productId передаются корректно")
    void addFavoriteProduct_passesCorrectIds() {
        Long productId = 99L;
        doNothing().when(userService).addFavoriteProduct(userId, productId);

        userController.addFavoriteProduct(jwtAuthToken, productId);

        verify(userService).addFavoriteProduct(userId, 99L);
    }

    // -------------------------------------------------------------------------
    // DELETE /users/me/favorites/{productId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteFavoriteProduct — возвращает 204 No Content")
    void deleteFavoriteProduct_returnsNoContent() {
        Long productId = 42L;
        doNothing().when(userService).deleteFavoriteProduct(userId, productId);

        ResponseEntity<Void> response = userController.deleteFavoriteProduct(jwtAuthToken, productId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(userService).deleteFavoriteProduct(userId, productId);
    }

    @Test
    @DisplayName("deleteFavoriteProduct — userId и productId передаются корректно")
    void deleteFavoriteProduct_passesCorrectIds() {
        Long productId = 7L;
        doNothing().when(userService).deleteFavoriteProduct(userId, productId);

        userController.deleteFavoriteProduct(jwtAuthToken, productId);

        verify(userService).deleteFavoriteProduct(userId, 7L);
    }
}
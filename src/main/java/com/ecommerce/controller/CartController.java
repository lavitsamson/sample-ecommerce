package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.model.User;
import com.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cartService.getCartByUser(user.getEmail()));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cartService.addToCart(user.getEmail(), productId, quantity));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Cart> removeFromCart(
            Authentication authentication,
            @PathVariable Long itemId) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cartService.removeFromCart(user.getEmail(), itemId));
    }
}

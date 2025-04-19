package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public CartService(CartRepository cartRepo, CartItemRepository cartItemRepo,
                       ProductRepository productRepo, UserRepository userRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }


    public Cart getCartByUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return cartRepo.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepo.save(cart);
        });
    }

    public Cart addToCart(String email, Long productId, int quantity) {
        Cart cart = getCartByUser(email);
        Product product = productRepo.findById(productId).orElseThrow();

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setCart(cart);

        cart.getItems().add(item);
        cartItemRepo.save(item);
        return cartRepo.save(cart);
    }

    public Cart removeFromCart(String email, Long itemId) {
        Cart cart = getCartByUser(email);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartItemRepo.deleteById(itemId);
        return cartRepo.save(cart);
    }
}

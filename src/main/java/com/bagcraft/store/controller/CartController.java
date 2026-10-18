package com.bagcraft.store.controller;

import com.bagcraft.store.dto.CartItemRequest;
import com.bagcraft.store.exception.NotFoundException;
import com.bagcraft.store.model.*;
import com.bagcraft.store.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User currentUser(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @GetMapping
    public List<CartItem> getCart(@AuthenticationPrincipal UserDetails principal) {
        User user = currentUser(principal);
        return cartItemRepository.findByUserId(user.getId());
    }

    @PostMapping
    public CartItem addToCart(@AuthenticationPrincipal UserDetails principal,
                               @Valid @RequestBody CartItemRequest req) {
        User user = currentUser(principal);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());
        item.setQuantity(item.getQuantity() + req.getQuantity());
        return cartItemRepository.save(item);
    }

    @PutMapping("/{itemId}")
    public CartItem updateQuantity(@PathVariable Long itemId, @Valid @RequestBody CartItemRequest req) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        item.setQuantity(req.getQuantity());
        return cartItemRepository.save(item);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        cartItemRepository.deleteById(itemId);
    }
}

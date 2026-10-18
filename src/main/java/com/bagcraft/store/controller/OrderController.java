package com.bagcraft.store.controller;

import com.bagcraft.store.dto.CheckoutRequest;
import com.bagcraft.store.exception.BadRequestException;
import com.bagcraft.store.exception.NotFoundException;
import com.bagcraft.store.model.*;
import com.bagcraft.store.repository.*;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    private User currentUser(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    // Step 1: create order + Stripe PaymentIntent from current cart
    @PostMapping("/checkout")
    public Map<String, Object> checkout(@AuthenticationPrincipal UserDetails principal,
                                         @Valid @RequestBody CheckoutRequest req) throws Exception {
        User user = currentUser(principal);
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        BigDecimal total = BigDecimal.ZERO;
        Order order = Order.builder().user(user).shippingAddress(req.getShippingAddress()).build();

        for (CartItem ci : cartItems) {
            BigDecimal lineTotal = ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(lineTotal);
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .productNameSnapshot(ci.getProduct().getName())
                    .priceAtPurchase(ci.getProduct().getPrice())
                    .quantity(ci.getQuantity())
                    .build();
            order.getItems().add(oi);
        }
        order.setTotalAmount(total);

        // Stripe works in the smallest currency unit (e.g. cents)
        long amountInCents = total.multiply(BigDecimal.valueOf(100)).longValue();
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .build();
        PaymentIntent intent = PaymentIntent.create(params);
        order.setStripePaymentIntentId(intent.getId());

        orderRepository.save(order);
        cartItemRepository.deleteByUserId(user.getId());

        return Map.of(
                "orderId", order.getId(),
                "clientSecret", intent.getClientSecret(),
                "totalAmount", total
        );
    }

    // Step 2: frontend calls this after Stripe confirms payment client-side
    @PostMapping("/{orderId}/confirm")
    public Order confirmPayment(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    @GetMapping("/my")
    public List<Order> myOrders(@AuthenticationPrincipal UserDetails principal) {
        User user = currentUser(principal);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
}

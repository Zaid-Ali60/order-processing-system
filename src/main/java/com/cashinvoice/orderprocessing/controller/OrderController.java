package com.cashinvoice.orderprocessing.controller;

import com.cashinvoice.orderprocessing.dto.CreateOrderRequest;
import com.cashinvoice.orderprocessing.dto.CreateOrderResponse;
import com.cashinvoice.orderprocessing.model.Order;
import com.cashinvoice.orderprocessing.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("Received create order request: customerId={}, product={}, amount={}", 
                request.getCustomerId(), request.getProduct(), request.getAmount());
        CreateOrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Order> getOrderById(
            @PathVariable String orderId,
            Authentication authentication) {
        log.info("Received get order request: orderId={}", orderId);
        Order order = orderService.getOrderById(orderId);
        
        // USER can only view their own orders
        if (authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            String username = authentication.getName();
            if (!order.getCustomerId().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        return ResponseEntity.ok(order);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(
            @RequestParam String customerId,
            Authentication authentication) {
        log.info("Received list orders request: customerId={}", customerId);
        
        // USER can only view their own orders
        if (authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            String username = authentication.getName();
            if (!customerId.equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }
}

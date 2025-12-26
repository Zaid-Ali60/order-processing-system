package com.cashinvoice.orderprocessing.service;

import com.cashinvoice.orderprocessing.dto.CreateOrderRequest;
import com.cashinvoice.orderprocessing.dto.CreateOrderResponse;
import com.cashinvoice.orderprocessing.exception.OrderNotFoundException;
import com.cashinvoice.orderprocessing.model.Order;
import com.cashinvoice.orderprocessing.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    
    private static final String FILE_OUTPUT_DIR = "input/orders/";
    
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        
        Order order = Order.builder()
                .orderId(orderId)
                .customerId(request.getCustomerId())
                .product(request.getProduct())
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .status("CREATED")
                .build();
        
        orderRepository.save(order);
        log.info("Order created successfully: OrderId={}, CustomerId={}, Amount={}", 
                orderId, request.getCustomerId(), request.getAmount());
        
        // BONUS: Write to file
        writeOrderToFile(order);
        
        return CreateOrderResponse.builder()
                .orderId(orderId)
                .status("CREATED")
                .build();
    }
    
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }
    
    public List<Order> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    private void writeOrderToFile(Order order) {
        try {
            File directory = new File(FILE_OUTPUT_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String fileName = FILE_OUTPUT_DIR + "order-" + order.getOrderId() + ".json";
            objectMapper.writeValue(new File(fileName), order);
            log.info("Order written to file: {}", fileName);
        } catch (IOException e) {
            log.error("Failed to write order to file: OrderId={}", order.getOrderId(), e);
        }
    }
}

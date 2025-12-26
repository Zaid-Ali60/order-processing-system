package com.cashinvoice.orderprocessing.repository;

import com.cashinvoice.orderprocessing.model.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {
    
    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();
    
    public Order save(Order order) {
        orderStore.put(order.getOrderId(), order);
        return order;
    }
    
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orderStore.get(orderId));
    }
    
    public List<Order> findByCustomerId(String customerId) {
        return orderStore.values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }
    
    public List<Order> findAll() {
        return new ArrayList<>(orderStore.values());
    }
}

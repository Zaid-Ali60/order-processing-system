package com.cashinvoice.orderprocessing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotBlank(message = "Customer ID must not be null or empty")
    private String customerId;
    
    @NotBlank(message = "Product must not be null or empty")
    private String product;
    
    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;
}

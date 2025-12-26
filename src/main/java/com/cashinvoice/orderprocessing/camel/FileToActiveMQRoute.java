package com.cashinvoice.orderprocessing.camel;

import com.cashinvoice.orderprocessing.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileToActiveMQRoute extends RouteBuilder {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public void configure() throws Exception {
        
        // Error handler
        onException(Exception.class)
                .handled(true)
                .log("Error processing file: ${file:name} - ${exception.message}")
                .to("file:error/orders?fileName=${file:name}");
        
        // Main route: File to ActiveMQ
        from("file:input/orders?delete=true&initialDelay=3000&delay=5000")
                .routeId("file-to-activemq-route")
                .log("Picked up file: ${file:name}")
                .unmarshal().json(Order.class)
                .process(exchange -> {
                    Order order = exchange.getIn().getBody(Order.class);
                    String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
                    
                    // Validation
                    if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                        throw new IllegalArgumentException("OrderId cannot be null or empty");
                    }
                    if (order.getCustomerId() == null || order.getCustomerId().isEmpty()) {
                        throw new IllegalArgumentException("CustomerId cannot be null or empty");
                    }
                    if (order.getAmount() == null || order.getAmount() <= 0) {
                        throw new IllegalArgumentException("Amount must be greater than 0");
                    }
                    
                    log.info("File validated successfully | FileName={} | OrderId={}", 
                            fileName, order.getOrderId());
                })
                .marshal().json()
                .to("activemq:queue:ORDER.CREATED.QUEUE")
                .log("Message sent to ActiveMQ | OrderId=${body.orderId}");
    }
}

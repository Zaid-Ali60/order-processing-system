package com.cashinvoice.orderprocessing.camel;

import com.cashinvoice.orderprocessing.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ActiveMQConsumerRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        from("activemq:queue:ORDER.CREATED.QUEUE")
                .routeId("activemq-consumer-route")
                .unmarshal().json(Order.class)
                .process(exchange -> {
                    Order order = exchange.getIn().getBody(Order.class);
                    
                    log.info("Order processed | OrderId={} | CustomerId={} | Amount={}", 
                            order.getOrderId(), 
                            order.getCustomerId(), 
                            order.getAmount());
                })
                .log("Message acknowledged from ActiveMQ");
    }
}

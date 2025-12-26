package com.cashinvoice.orderprocessing.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import org.apache.camel.component.activemq.ActiveMQComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

@Configuration
public class ActiveMQConfig {
    
    @Bean
    public BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistent(false);
        return broker;
    }
    
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://localhost:61616");
        factory.setTrustAllPackages(true);
        return factory;
    }
    
    @Bean(name = "activemq")
    public ActiveMQComponent activeMQComponent(ConnectionFactory connectionFactory) {
        ActiveMQComponent component = new ActiveMQComponent();
        component.setConnectionFactory(connectionFactory);
        return component;
    }
}

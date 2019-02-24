package com.example.cartservice.Configuration;

import com.example.cartservice.Exceptions.ProductNotFoundException;
import com.example.cartservice.Model.Cart;
import com.example.cartservice.Model.ProductInventory;
import com.example.cartservice.Service.CartService;
import com.example.cartservice.Service.InventoryServiceFeignClient;
import common.KafkaMessageModel.CartMessageModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaConsumerConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Value("${catalog-cart.topic-name}")
    private String topicName;

    @Value("${spring.kafka.consumergroup-id}")
    private String groupId;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private InventoryServiceFeignClient inventoryServiceFeignClient;

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties());

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return properties;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        final JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    @KafkaListener(topics = "catalog-cart-updateProduct", containerFactory = "kafkaListenerContainerFactory")
    public void addCartListener(ConsumerRecord<String, CartMessageModel> consumerRecord, @Payload CartMessageModel cartMessageModel){

        LOGGER.info("Received Message, UserId : " + cartMessageModel.getUserId());
        Cart cart = new Cart();
        cart.setUserId(cartMessageModel.getUserId());
        cart.addProductAndQuantity(cartMessageModel.getProductId(), cartMessageModel.getQuantity());
        List<ProductInventory> productInventories = inventoryServiceFeignClient.getInventoryLevels();
        ResponseEntity<ProductInventory> productInventoryResponseEntity = cartService.getProductFromInventory(cartMessageModel.getProductId());
        if(productInventoryResponseEntity.getStatusCode() == HttpStatus.OK){
            Double price = productInventoryResponseEntity.getBody().getPrice();
            Double totalPrice = price * cartMessageModel.getQuantity();
            cart.setTotalPrice(totalPrice);
            cartService.addOrUpdateCart(cart);
        }
        else {
            throw new ProductNotFoundException("Exception while fetching productId : " + cartMessageModel.getProductId() +
                    " with HttpStatus : " + productInventoryResponseEntity.getStatusCode());
        }
    }
}
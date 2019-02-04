package com.example.inventoryservice.Configuration;

import common.Model.KafkaMessageModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaConsumerConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Value("${inventory.topic-name}")
    private String topicName;

    @Value("${spring.kafka.consumergroup-id}")
    private String groupId;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties());

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return properties;
    }

    @Bean
    public ConsumerFactory<String, KafkaMessageModel> consumerFactory() {
        final JsonDeserializer<KafkaMessageModel> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessageModel> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessageModel> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    @KafkaListener(topics = "product-inventory-update", containerFactory = "kafkaListenerContainerFactory")
    public void productConsumerListener(ConsumerRecord<String, KafkaMessageModel> consumerRecord, @Payload KafkaMessageModel kafkaMessageModel){

        LOGGER.info("Received Message, VendorId : " + kafkaMessageModel.getVendorId());
    }

}
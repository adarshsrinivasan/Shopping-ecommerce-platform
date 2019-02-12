package com.example.vendorservice.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaProducerConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Value("${vendor-inventory-updateInventory.topic-name}")
    private String updateInventorytopicName;

    @Value("${vendor-inventory-vendorRemoved.topic-name}")
    private String vendorRemovedtopicName;

    @Bean
    public Map<String, Object> producerConfig(){
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties());

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return properties;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic advanceUpdateInventoryTopic(){
        return new NewTopic(updateInventorytopicName, 3, (short) 1);
    }

    @Bean
    public NewTopic advanceVendorRemovedTopic(){
        return new NewTopic(vendorRemovedtopicName, 3, (short) 1);
    }
}

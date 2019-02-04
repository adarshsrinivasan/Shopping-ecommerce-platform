package com.example.vendorservice.Service;

import com.example.vendorservice.Model.ProductInventory;
import common.Model.KafkaMessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VendorService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${vendor.topic-name}")
    private String topicName;

    public void addProduct(String vendorId, List<ProductInventory> productInventories) {
        KafkaMessageModel kafkaMessageModel = new KafkaMessageModel(vendorId, productInventories);

        kafkaTemplate.send(topicName, kafkaMessageModel);
    }
}

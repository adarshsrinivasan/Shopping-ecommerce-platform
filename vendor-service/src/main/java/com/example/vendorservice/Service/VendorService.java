package com.example.vendorservice.Service;

import com.example.vendorservice.Model.ProductInventory;
import common.KafkaMessageModel.VendorMessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VendorService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${vendor-inventory.topic-name}")
    private String topicName;

    public void addProduct(String vendorId, List<ProductInventory> productInventories) {
        VendorMessageModel vendorMessageModel = new VendorMessageModel(vendorId, productInventories);

        kafkaTemplate.send(topicName, vendorMessageModel);
    }
}

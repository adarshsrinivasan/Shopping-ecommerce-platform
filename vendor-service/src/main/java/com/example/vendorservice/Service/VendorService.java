package com.example.vendorservice.Service;

import com.example.vendorservice.Exception.VendorNotFoundException;
import com.example.vendorservice.Model.ProductInventory;
import com.example.vendorservice.Model.User;
import com.example.vendorservice.Model.VendorProductInventory;
import common.KafkaMessageModel.VendorMessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VendorService {

    private final static Logger LOGGER = LoggerFactory.getLogger(VendorService.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private MongoTemplate mongoTemplate;

    @Value("${vendor-inventory-updateInventory.topic-name}")
    private String updateInventorytopicName;

    @Value("${vendor-inventory-vendorRemoved.topic-name}")
    private String vendorRemovedtopicName;

    private Map<String, User> vendorsCache = new HashMap<>();

    @Autowired
    public VendorService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        populateCache();
    }

    private void populateCache() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isVendor").is(true));

        List<User> vendors = mongoTemplate.find(query, User.class);
        for(User vendor : vendors){
            vendorsCache.put(vendor.getUserId(), vendor);
        }
    }

    public void addProduct(String vendorId, List<ProductInventory> productInventories) {
        Query query = new Query();
        query.addCriteria(Criteria.where("vendorId").is(vendorId));
        VendorProductInventory vendorProductInventory = mongoTemplate.findOne(query, VendorProductInventory.class);

        if(vendorProductInventory == null){
            throw new VendorNotFoundException("No vendor found with vendorId : " + vendorId);
        }
        vendorProductInventory.addProduct(productInventories);
        mongoTemplate.save(vendorProductInventory);
        VendorMessageModel vendorMessageModel = new VendorMessageModel(vendorId, productInventories);

        kafkaTemplate.send(updateInventorytopicName, vendorMessageModel);
    }

    public void addVendor(User vendor){
        if(vendorsCache.get(vendor.getUserId()) != null){
            LOGGER.info("Vendor : " + vendor.getUserId() + " Already Exists");
        }
        else {
            LOGGER.info("Adding Vendor : " + vendor.getUserId());
            VendorProductInventory vendorProductInventory; vendorProductInventory = new VendorProductInventory();
            vendorProductInventory.setVendorId(vendor.getUserId());
            mongoTemplate.save(vendorProductInventory);
            vendorsCache.put(vendor.getUserId(), vendor);
        }
    }

    public void removeVendor(User vendor){
        String vendorId = vendor.getUserId();
        Query query = new Query();
        query.addCriteria(Criteria.where("vendorId").is(vendorId));
        VendorProductInventory vendorProductInventory = mongoTemplate.findOne(query, VendorProductInventory.class);
        if(vendorProductInventory == null){
            LOGGER.info("Vendor : " + vendor.getUserId() + " doesn't Exists, nothing to delete");
        }
        else {
            mongoTemplate.remove(query, VendorProductInventory.class);
            vendorsCache.remove(vendorId);
            VendorMessageModel vendorMessageModel = new VendorMessageModel();
            vendorMessageModel.setVendorId(vendorId);
            vendorMessageModel.setProductInventories(vendorProductInventory.getProducts());
            kafkaTemplate.send(vendorRemovedtopicName, vendorMessageModel);
            LOGGER.info("Deleted vendor with vendorId : " + vendorId);
        }
    }
}

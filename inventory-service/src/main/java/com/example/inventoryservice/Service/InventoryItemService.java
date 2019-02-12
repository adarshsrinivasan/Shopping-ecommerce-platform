package com.example.inventoryservice.Service;

import com.example.inventoryservice.Model.InventoryItem;
import com.example.inventoryservice.Model.Product;
import com.example.inventoryservice.Model.ProductInventory;
import com.mongodb.client.result.DeleteResult;
import common.KafkaMessageModel.InventoryMessageModel;
import common.KafkaMessageModel.VendorMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InventoryItemService {

    private final static Logger LOGGER = LoggerFactory.getLogger(InventoryItemService.class);
    private final MongoTemplate mongoTemplate;
    private KafkaTemplate<String, Object> kafkaTemplate;


    @Value("${inventory-catalog.topic-name}")
    private String topicName;

    @Autowired
    public InventoryItemService(MongoTemplate mongoTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Optional<InventoryItem> findInventoryByProductCode(String productCode){
        LOGGER.info("Finding product with productCode : " + productCode);
        Query query = new Query();

        query.addCriteria(Criteria.where("productCode").is(productCode));

        InventoryItem inventoryItem = mongoTemplate.findOne(query, InventoryItem.class);
        if( inventoryItem != null){
            return Optional.of(inventoryItem);
        }
        LOGGER.info("Product with productCode : " + productCode + " NOT found");
        return Optional.empty();
    }

    public List<InventoryItem> findAllInventoryItems() {
        LOGGER.debug("Returning all product");
        return mongoTemplate.findAll(InventoryItem.class);
    }

    public Optional<InventoryItem> updateInventory(InventoryItem inventoryItem){
        Optional<InventoryItem> optionalInventoryItem = findInventoryByProductCode(inventoryItem.getProductCode());

        if(optionalInventoryItem.isPresent()){
            InventoryItem resultInventoryItem = optionalInventoryItem.get();
            inventoryItem.setId(resultInventoryItem.getId());
        }
        LOGGER.info("Updating product with productCode : " + inventoryItem.getProductCode());
        mongoTemplate.save(inventoryItem);
        return optionalInventoryItem;
    }

    public DeleteResult deleteInventoryByProductCode(String productCode){
        Query query = new Query();

        query.addCriteria(Criteria.where("productCode").is(productCode));
        LOGGER.info("Deleting product with productCode : " + productCode);
        return mongoTemplate.remove(query, InventoryItem.class);
    }

    public String addInventory(InventoryItem inventoryItem){
        mongoTemplate.save(inventoryItem);

        Query query = new Query();
        query.addCriteria(Criteria.where("productCode").is(inventoryItem.getProductCode()));
        LOGGER.info("Added product with productCode : " + inventoryItem.getProductCode());
        return mongoTemplate.findOne(query, InventoryItem.class).getId();
    }

    public void addOrUpdateInventoryByVendor(String vendorId, List<ProductInventory> productInventories){
        for(ProductInventory productInventory : productInventories){
            InventoryItem inventoryItem;
            Optional<InventoryItem> optionalInventoryItem = findInventoryByProductCode(productInventory.getProductCode());
            if(optionalInventoryItem.isPresent()){
                inventoryItem = optionalInventoryItem.get();
                inventoryItem.addVendor(vendorId, productInventory);
            }
            else {
                inventoryItem = addNewProductToInventory(vendorId, productInventory);
            }

            addInventory(inventoryItem);
            notifyProductUpdate(productInventory, inventoryItem);
        }

    }

    private void notifyProductUpdate(ProductInventory productInventory, InventoryItem inventoryItem) {
        Product product = new Product();
        product.setCode(productInventory.getProductCode());
        product.setName(productInventory.getName());
        product.setDescription(productInventory.getDescription());
        product.setPrice(productInventory.getPrice());
        product.setStock(inventoryItem.getAvailableQuantity());

        InventoryMessageModel inventoryMessageModel = new InventoryMessageModel(product);
        LOGGER.info("Notifying Product Service about " + productInventory.getProductCode());
        kafkaTemplate.send(topicName, inventoryMessageModel);
    }

    private InventoryItem addNewProductToInventory(String vendorId, ProductInventory productInventory){
        LOGGER.info("Product with productCode : " + productInventory.getProductCode() + " not found, updating Inventory Repository");
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setProductCode(productInventory.getProductCode());
        inventoryItem.setPrice(productInventory.getPrice());
        inventoryItem.addVendor(vendorId, productInventory);

        return inventoryItem;
    }

    public void handleVendorRemoved(VendorMessageModel vendorMessageModel){
        List<ProductInventory> productInventories = vendorMessageModel.getProductInventories();

        for (ProductInventory productInventory : productInventories){
            Optional<InventoryItem> optionalInventoryItem = findInventoryByProductCode
                    (productInventory.getProductCode());
            InventoryItem inventoryItem = optionalInventoryItem.get();
            inventoryItem.removeVendor(vendorMessageModel.getVendorId());
            mongoTemplate.save(inventoryItem);
            notifyProductUpdate(productInventory, inventoryItem);
        }
    }

}

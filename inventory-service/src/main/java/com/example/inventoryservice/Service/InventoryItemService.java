package com.example.inventoryservice.Service;

import com.example.inventoryservice.Model.InventoryItem;
import com.example.inventoryservice.Model.ProductInventory;
import com.mongodb.client.result.DeleteResult;
import common.KafkaMessageModel.InventoryMessageModel;
import lombok.extern.slf4j.Slf4j;
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

    private final MongoTemplate mongoTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${inventory-catalog.topic-name}")
    private String topicName;

    @Autowired
    public InventoryItemService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<InventoryItem> findInventoryByProductCode(String productCode){
        Query query = new Query();

        query.addCriteria(Criteria.where("productCode").is(productCode));

        InventoryItem inventoryItem = mongoTemplate.findOne(query, InventoryItem.class);
        if( inventoryItem != null){
            return Optional.of(inventoryItem);
        }
        return Optional.empty();
    }

    public List<InventoryItem> findAllInventoryItems() {

        return mongoTemplate.findAll(InventoryItem.class);
    }

    public Optional<InventoryItem> undateInventory(InventoryItem inventoryItem){
        Optional<InventoryItem> optionalInventoryItem = findInventoryByProductCode(inventoryItem.getProductCode());

        if(optionalInventoryItem.isPresent()){
            InventoryItem resultInventoryItem = optionalInventoryItem.get();
            inventoryItem.setId(resultInventoryItem.getId());

            mongoTemplate.save(inventoryItem);
        }
        return optionalInventoryItem;
    }

    public DeleteResult deleteInventoryByProductCode(String productCode){
        Query query = new Query();

        query.addCriteria(Criteria.where("productCode").is(productCode));

        return mongoTemplate.remove(query, InventoryItem.class);
    }

    public void addInventory(InventoryItem inventoryItem){
        mongoTemplate.save(inventoryItem);
    }

    public void initMongoData() {
        InventoryItem inventoryItem1 = new InventoryItem();
        InventoryItem inventoryItem2 = new InventoryItem();
        InventoryItem inventoryItem3 = new InventoryItem();

        inventoryItem1.setProductCode("P001");
        inventoryItem1.setAvailableQuantity(250);
        inventoryItem1.setPrice(25.0);
        mongoTemplate.save(inventoryItem1);

        inventoryItem2.setProductCode("P002");
        inventoryItem2.setAvailableQuantity(132);
        inventoryItem2.setPrice(32.0);
        mongoTemplate.save(inventoryItem2);

        inventoryItem3.setProductCode("P003");
        inventoryItem3.setAvailableQuantity(0);
        inventoryItem3.setPrice(50.0);
        mongoTemplate.save(inventoryItem3);
    }

    public void notifyAddOrUpdateProduct(List<ProductInventory> productInventories){
        InventoryMessageModel inventoryMessageModel = new InventoryMessageModel(productInventories);

        kafkaTemplate.send(topicName, inventoryMessageModel);
    }
}

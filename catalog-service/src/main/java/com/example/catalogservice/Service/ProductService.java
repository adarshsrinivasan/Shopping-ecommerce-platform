package com.example.catalogservice.Service;


import com.example.catalogservice.Model.ProductInventory;
import com.example.catalogservice.Model.Product;
import com.mongodb.client.result.DeleteResult;
import common.KafkaMessageModel.CartMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ProductService {
    private final MongoTemplate mongoTemplate;
    private final InventoryServiceFeignClient inventoryServiceFeignClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private KafkaTemplate<String, Object> kafkaTemplate;
    private CartServiceFeignClient cartServiceFeignClient;


    @Value("${catalog-cart.topic-name}")
    private String topicName;

    @Autowired
    public ProductService(MongoTemplate mongoTemplate, InventoryServiceFeignClient inventoryServiceFeignClient,
                          KafkaTemplate<String, Object> kafkaTemplate, CartServiceFeignClient cartServiceFeignClient){
        this.mongoTemplate = mongoTemplate;
        this.inventoryServiceFeignClient = inventoryServiceFeignClient;
        this.kafkaTemplate = kafkaTemplate;
        this.cartServiceFeignClient = cartServiceFeignClient;
    }

    public List<Product> findAllProduct(){

        List<Product> products =  mongoTemplate.findAll(Product.class);
        Map<String, Integer> productInventory = getInventoryLevel();
        List<Product> availableProducts = products.stream()
                .filter(p -> productInventory.get(p.getCode()) != null && productInventory.get(p.getCode()) > 0)
                .collect(Collectors.toList());
        return availableProducts;
    }

    private Map<String, Integer> getInventoryLevel(){
        LOGGER.debug("Contacting inventory-service to get all inventory");
        List<ProductInventory> productInventories = inventoryServiceFeignClient.getInventoryLevels();
        LOGGER.debug("Received response from inventory-service to get all inventory");
        Map<String, Integer> inventoryLevel = new HashMap<>();

        for (ProductInventory pir : productInventories){
            inventoryLevel.put(pir.getProductCode(), pir.getAvailableQuantity());
        }

        return inventoryLevel;
    }

    public Optional<Product> findByCode(String code){
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(code));

        Product resultProduct = mongoTemplate.findOne(query, Product.class);
        Optional<Product> optionalProduct;
        if (resultProduct != null){
            optionalProduct = Optional.of(resultProduct);
        }
        else {
            optionalProduct = Optional.empty();
        }

        if(optionalProduct.isPresent()){
            LOGGER.info("Fetching inventory level for product_code: "+code);
            ResponseEntity<ProductInventory> productInventoryResponseResponseEntity = inventoryServiceFeignClient.getInventoryByProductCode(code);
            if(productInventoryResponseResponseEntity.getStatusCode() == HttpStatus.OK){
                int quantity = productInventoryResponseResponseEntity.getBody().getAvailableQuantity();
                double price = productInventoryResponseResponseEntity.getBody().getPrice();
                LOGGER.info("Available quantity: "+quantity);
                optionalProduct.get().setStock(quantity);
                optionalProduct.get().setPrice(price);

            }else {
                LOGGER.error("Unable to get inventory level for product_code: "+code +", StatusCode: "
                        +productInventoryResponseResponseEntity.getStatusCode());
            }
        }
        return optionalProduct;
    }

    public void addOrUpdateProduct(Product product){
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(product.getCode()));
        Product resultProduct = mongoTemplate.findOne(query, Product.class);
        if(resultProduct != null) {
            product.setId(resultProduct.getId());
        }
        mongoTemplate.save(product);
        LOGGER.info("Added data in MongoDB code = {}", product.getCode());
    }


    public DeleteResult deleteProductByCode(String code){
        Query query = new Query();

        query.addCriteria(Criteria.where("code").is(code));

        return mongoTemplate.remove(query, Product.class);
    }

    public void initMongoData() {
        LOGGER.info("Initiliazing data in MongoDB");
        Product product1 = new Product();
        Product product2 = new Product();
        Product product3 = new Product();

        product1.setCode("P001");
        product1.setName("Product 1");
        product1.setDescription("Product 1 description");
        product1.setPrice(25);
        addOrUpdateProduct(product1);

        product2.setCode("P002");
        product2.setName("Product 2");
        product2.setDescription("Product 2 description");
        product2.setPrice(32);
        addOrUpdateProduct(product2);

        product3.setCode("P003");
        product3.setName("Product 3");
        product3.setDescription("Product 3 description");
        product3.setPrice(50);
        addOrUpdateProduct(product3);

    }

    public void addToCart(CartMessageModel cartMessageModel) {
        LOGGER.info("Adding new Cart items to kafka");
        kafkaTemplate.send(topicName, cartMessageModel);
    }

    public Cart getUserCart(String userId){
        return cartServiceFeignClient.getUserCart(userId);
    }

}

package com.example.catalogservice.Service;

import com.example.catalogservice.Model.Product;
import com.example.catalogservice.Model.ProductInventoryResponse;
import com.example.catalogservice.Repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ProductRepository productRepository;
    private final InventoryServiceFeignClient inventoryServiceFeignClient;
    private final RestTemplate restTemplate;
    private final InventoryServiceClient inventoryServiceClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductService(ProductRepository productRepository, InventoryServiceFeignClient inventoryServiceFeignClient,
                          RestTemplate restTemplate, InventoryServiceClient inventoryServiceClient){
        this.productRepository = productRepository;
        this.inventoryServiceFeignClient = inventoryServiceFeignClient;
        this.restTemplate = restTemplate;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public List<Product> findAllProduct(){

        List<Product> products =  productRepository.findAll();
        Map<String, Integer> productInventory = getInventoryLevel();
        List<Product> availableProducts = products.stream()
                .filter(p -> productInventory.get(p.getCode()) != null && productInventory.get(p.getCode()) > 0)
                .collect(Collectors.toList());
        return availableProducts;
    }

    private Map<String, Integer> getInventoryLevel(){
        LOGGER.debug("Contacting inventory-service to get all inventory");
        List<ProductInventoryResponse> productInventoryResponses = inventoryServiceFeignClient.getInventoryLevels();
        LOGGER.debug("Received response from inventory-service to get all inventory");
        Map<String, Integer> inventoryLevel = new HashMap<>();

        for (ProductInventoryResponse pir : productInventoryResponses){
            inventoryLevel.put(pir.getProductCode(), pir.getAvailableQuantity());
        }

        return inventoryLevel;
    }

    public Optional<Product> findByCode(String code){
        Optional<Product> optionalProduct = productRepository.findByCode(code);

        if(optionalProduct.isPresent()){
            LOGGER.info("Fetching inventory level for product_code: "+code);
            ResponseEntity<ProductInventoryResponse> productInventoryResponseResponseEntity = inventoryServiceClient.getInventoryByProductCode(code);
            if(productInventoryResponseResponseEntity.getStatusCode() == HttpStatus.OK){
                int quantity = productInventoryResponseResponseEntity.getBody().getAvailableQuantity();
                LOGGER.info("Available quantity: "+quantity);
                optionalProduct.get().setInStock(quantity > 0);
            }else {
                LOGGER.error("Unable to get inventory level for product_code: "+code +", StatusCode: "+productInventoryResponseResponseEntity.getStatusCode());
            }
        }
        return optionalProduct;
    }

}

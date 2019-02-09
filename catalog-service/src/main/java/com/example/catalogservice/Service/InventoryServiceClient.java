package com.example.catalogservice.Service;

import com.example.catalogservice.Model.ProductInventory;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class InventoryServiceClient {
    private final RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceClient.class);

    @Autowired
    public InventoryServiceClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getDefaultProductInventoryByCode", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")
    })
    public ResponseEntity<ProductInventory> getInventoryByProductCode(String productCode){
        LOGGER.info("Contacting inventory-service to get inventory of : {}", productCode);
        ResponseEntity<ProductInventory> productInventoryResponseResponseEntity =
                    restTemplate.getForEntity("http://inventory-service/inventory/{productCode}", ProductInventory.class, productCode);
        if(productInventoryResponseResponseEntity.getStatusCode() == HttpStatus.OK){
            LOGGER.debug("Received response from inventory-service for inventory of : {}", productCode);
        }
        else {
            LOGGER.error("Error response from inventory-service for inventory of : {}", productCode);
        }

        return productInventoryResponseResponseEntity;
    }

    @SuppressWarnings("unused")
    public ResponseEntity<ProductInventory> getDefaultProductInventoryByCode(String productCode){
        LOGGER.info("Falling back to getDefaultProductInventoryByCode for product code : [{}], returning default value 50", productCode);

        ProductInventory productInventory = new ProductInventory();
        productInventory.setProductCode(productCode);
        productInventory.setAvailableQuantity(50);

        return new ResponseEntity<ProductInventory>(productInventory, HttpStatus.OK);
    }

}

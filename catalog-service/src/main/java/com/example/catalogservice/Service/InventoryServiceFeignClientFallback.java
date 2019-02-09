package com.example.catalogservice.Service;

import com.example.catalogservice.Model.ProductInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class InventoryServiceFeignClientFallback implements InventoryServiceFeignClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceFeignClientFallback.class);
    @Override
    public List<ProductInventory> getInventoryLevels() {
        LOGGER.info("Falling back getInventoryLevels returning default product code \"P001\" and quantity = 50");

        List<ProductInventory> productInventories = new ArrayList<>();

        ProductInventory productInventory = new ProductInventory();
        productInventory.setProductCode("P001");
        productInventory.setAvailableQuantity(50);

        productInventories.add(productInventory);

        return productInventories;
    }

    @Override
    public ResponseEntity<ProductInventory> getInventoryByProductCode(String productCode) {
        LOGGER.info("Falling back getInventoryByProductCode for product code : [{}], returning default value 50", productCode);

        ProductInventory productInventory = new ProductInventory();
        productInventory.setProductCode(productCode);
        productInventory.setAvailableQuantity(50);

        return new ResponseEntity<ProductInventory>(productInventory, HttpStatus.OK);
    }

}

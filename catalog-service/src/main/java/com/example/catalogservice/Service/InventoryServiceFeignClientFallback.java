package com.example.catalogservice.Service;

import com.example.catalogservice.Model.ProductInventoryResponse;
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
    public List<ProductInventoryResponse> getInventoryLevels() {
        LOGGER.info("Falling back getInventoryLevels returning default product code \"P001\" and quantity = 50");

        List<ProductInventoryResponse> productInventoryResponses = new ArrayList<>();

        ProductInventoryResponse productInventoryResponse = new ProductInventoryResponse();
        productInventoryResponse.setProductCode("P001");
        productInventoryResponse.setAvailableQuantity(50);

        productInventoryResponses.add(productInventoryResponse);

        return productInventoryResponses;
    }

    @Override
    public ResponseEntity<ProductInventoryResponse> getInventoryByProductCode(String productCode) {
        LOGGER.info("Falling back getInventoryByProductCode for product code : [{}], returning default value 50", productCode);

        ProductInventoryResponse productInventoryResponse = new ProductInventoryResponse();
        productInventoryResponse.setProductCode(productCode);
        productInventoryResponse.setAvailableQuantity(50);

        return new ResponseEntity<ProductInventoryResponse>(productInventoryResponse, HttpStatus.OK);
    }

}

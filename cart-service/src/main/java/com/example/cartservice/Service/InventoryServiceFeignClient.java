package com.example.cartservice.Service;

import com.example.cartservice.Model.ProductInventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "inventory-service", fallback = InventoryServiceFeignClientFallback.class)
public interface InventoryServiceFeignClient {

    @GetMapping("/inventory")
    List<ProductInventory> getInventoryLevels();

    @GetMapping("/inventory/{productCode}")
    ResponseEntity<ProductInventory> getInventoryByProductCode(@PathVariable String productCode);
}

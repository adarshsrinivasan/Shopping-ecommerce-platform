package com.example.catalogservice.Service;

import com.example.catalogservice.Model.ProductInventoryResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@FeignClient(name = "inventory-service", fallback = InventoryServiceFeignClientFallback.class)
public interface InventoryServiceFeignClient {

    @GetMapping("/inventory")
    List<ProductInventoryResponse> getInventoryLevels();

    @GetMapping("/inventory/{productCode}")
    ResponseEntity<ProductInventoryResponse> getInventoryByProductCode(@PathVariable String productCode);
}

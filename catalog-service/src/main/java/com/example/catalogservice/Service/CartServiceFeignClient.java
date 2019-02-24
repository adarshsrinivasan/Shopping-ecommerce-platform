package com.example.catalogservice.Service;

import com.example.catalogservice.Model.ProductInventory;
import feign.hystrix.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cart-service", fallback = CartServiceFeignClientFallBack.class)
public interface CartServiceFeignClient {

    @GetMapping("cart/{userId}/get-cart")
    public Cart getUserCart(@PathVariable String userId);

}

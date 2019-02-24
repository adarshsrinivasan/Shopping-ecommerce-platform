package com.example.cartservice.Service;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceFeignClientFallbackFactory implements FallbackFactory<InventoryServiceFeignClient> {
    @Override
    public InventoryServiceFeignClient create(Throwable throwable) {
        return new InventoryServiceFeignClientFallback(throwable);
    }
}

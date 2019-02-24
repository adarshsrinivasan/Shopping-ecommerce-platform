package com.example.catalogservice.Service;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class CartServiceFeignClientFallBackFactory implements FallbackFactory<CartServiceFeignClient> {
    @Override
    public CartServiceFeignClient create(Throwable throwable) {
        return new CartServiceFeignClientFallBack(throwable);
    }
}

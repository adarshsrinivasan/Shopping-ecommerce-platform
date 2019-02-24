package com.example.catalogservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class CartServiceFeignClientFallBack implements CartServiceFeignClient{

    private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceFeignClientFallBack.class);
    private Throwable cause;

    public CartServiceFeignClientFallBack() {
    }

    public CartServiceFeignClientFallBack(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public Cart getUserCart(@PathVariable String userId){
        LOGGER.info("Falling back for getUserCart for userId : " + userId + " returning empty cart");
        LOGGER.info("Fallback reason : " + cause.toString());
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cart;
    }

}

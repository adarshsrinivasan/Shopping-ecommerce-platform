package com.example.cartservice.Controller;

import com.example.cartservice.Model.Cart;
import com.example.cartservice.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/{userId}/get-cart")
    public Cart getUserCart(@PathVariable String userId){
        return cartService.getUserCart(userId);
    }
}

package com.example.cartservice.Model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Document
public class Cart {
    @Id
    private String id;
    private String userId;
    private Map<String, Integer> productAndQuantity = new HashMap<>();
    private Double totalPrice = 0.0;

    public Cart() {
    }

    public Cart(String id, String userId, Map<String, Integer> productAndQuantity, Double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.productAndQuantity = productAndQuantity;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Integer> getProductAndQuantity() {
        return productAndQuantity;
    }

    public void setProductAndQuantity(Map<String, Integer> productAndQuantity) {
        Set<String> keys = productAndQuantity.keySet();
        for(String key : keys){
            addProductAndQuantity(key, productAndQuantity.get(key));
        }
    }

    public void addProductAndQuantity(String productId, Integer quantity) {
        if(productAndQuantity == null){
            productAndQuantity = new HashMap<>();
        }
        productAndQuantity.put(productId, quantity);
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

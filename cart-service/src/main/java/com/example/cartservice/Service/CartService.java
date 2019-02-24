package com.example.cartservice.Service;

import com.example.cartservice.Model.Cart;
import com.example.cartservice.Model.ProductInventory;
import common.KafkaMessageModel.CartMessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Component
public class CartService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private InventoryServiceFeignClient inventoryServiceFeignClient;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(CartService.class);

    public void addOrUpdateCart(Cart cart){
        String userId = cart.getUserId();

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        Cart resultCart = mongoTemplate.findOne(query, Cart.class);
        if(resultCart != null){
            LOGGER.info("Cart for user : " + userId + " already present. Adding items to existing cart");
            Map<String, Integer> presentCartItems = resultCart.getProductAndQuantity();
            Map<String, Integer> newCartItems = cart.getProductAndQuantity();
            Set<String> productIds = newCartItems.keySet();
            Double totalPrice = resultCart.getTotalPrice();
            for(String productId : productIds){
                ResponseEntity<ProductInventory> productInventoryResponseEntity = getProductFromInventory(productId);
                if(productInventoryResponseEntity.getStatusCode() == HttpStatus.OK){
                    ProductInventory productInventory = productInventoryResponseEntity.getBody();
                    Double price = productInventory.getPrice();
                    Double totalItemPrice = price * newCartItems.get(productId);
                    Integer totalItem = ((presentCartItems.get(productId) != null)? presentCartItems.get(productId) : 0) + newCartItems.get(productId);
                    presentCartItems.put(productId, totalItem);
                    totalPrice += totalItemPrice;
                }
                else {
                    LOGGER.info("No such product with productId : " + productId + " present in the Inventory");
                }
            }
            resultCart.setProductAndQuantity(presentCartItems);
            resultCart.setTotalPrice(totalPrice);
            mongoTemplate.save(resultCart);
        }
        else {
            LOGGER.info("Creating new cart for user : " + userId);
            mongoTemplate.save(cart);
        }
    }

    public ResponseEntity<ProductInventory> getProductFromInventory(String productId){
        return inventoryServiceFeignClient.getInventoryByProductCode(productId);
    }

    public Cart getUserCart(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        Cart resultCart = mongoTemplate.findOne(query, Cart.class);
        if(resultCart == null){
            LOGGER.info("User : " + userId +" doesn't have a cart. Creating a new empty cart.");
            resultCart = new Cart();
            resultCart.setUserId(userId);
            resultCart.setProductAndQuantity(new HashMap<>());
            mongoTemplate.save(resultCart);
            resultCart = mongoTemplate.findOne(query, Cart.class);
        }
        return resultCart;
    }
}

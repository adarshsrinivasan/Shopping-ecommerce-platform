package com.example.catalogservice.Controller;

import com.example.catalogservice.Exception.ProductNotFoundException;
import com.example.catalogservice.Model.Product;
import com.example.catalogservice.Service.Cart;
import com.example.catalogservice.Service.ProductService;
import com.mongodb.client.result.DeleteResult;
import common.KafkaMessageModel.CartMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RefreshScope
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public List<Product> getAllProduct(){
        return productService.findAllProduct();
    }

    @GetMapping("/{code}")
    public Product getByCode(@PathVariable String code){
        return productService.findByCode(code).orElseThrow(() -> new ProductNotFoundException("Product with [" + code + "] not found"));
    }

    @DeleteMapping("/{code}")
    public DeleteResult deleteByCode(@PathVariable String code){
        return productService.deleteProductByCode(code);
    }

    @GetMapping("/init")
    public void initilizeMongo(){
        productService.initMongoData();
    }

    @PostMapping("/add-to-cart")
    public void addToCart(@RequestBody CartMessageModel cartMessageModel){
        productService.addToCart(cartMessageModel);
    }

    @GetMapping("/{userId}/get-cart")
    public Cart getUserCart(@PathVariable String userId){
        return productService.getUserCart(userId);
    }
}

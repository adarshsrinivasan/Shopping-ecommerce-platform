package com.example.catalogservice.Controller;

import com.example.catalogservice.Exception.ProductNotFoundException;
import com.example.catalogservice.Model.Product;
import com.example.catalogservice.Service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

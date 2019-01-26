package com.example.catalogservice.Service;

import com.example.catalogservice.Model.Product;
import com.example.catalogservice.Repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ProductService(ProductRepository productRepository, RestTemplate restTemplate){
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }

    public List<Product> findAllProduct(){
        return productRepository.findAll();
    }

    public Optional<Product> findByCode(String code){
        Optional<Product> optionalProduct = productRepository.findByCode(code);

        if(optionalProduct.isPresent()){
            //log.info("Fetching inventory level for product_code: "+code);
            ResponseEntity<ProductInventoryResponse> productInventoryResponseResponseEntity =
                    restTemplate.getForEntity("http://localhost:8282/api/inventory/{code}", ProductInventoryResponse.class, code);
            if(productInventoryResponseResponseEntity.getStatusCode() == HttpStatus.OK){
                int quantity = productInventoryResponseResponseEntity.getBody().getAvailableQuantity();
                //log.info("Available quantity: "+quantity);
                optionalProduct.get().setInStock(quantity > 0);
            }else {
                //log.error("Unable to get inventory level for product_code: "+code +", StatusCode: "+productInventoryResponseResponseEntity.getStatusCode());
            }
        }
        return optionalProduct;
    }

}

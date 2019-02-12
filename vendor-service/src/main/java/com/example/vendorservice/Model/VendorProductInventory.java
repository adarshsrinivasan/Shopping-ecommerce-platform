package com.example.vendorservice.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
public class VendorProductInventory {
    @Id
    private String Id;
    private String vendorId;
    private Map<String, ProductInventory> products = new HashMap<>();

    public VendorProductInventory() {
    }

    public VendorProductInventory(String id, String vendorId, Map<String, ProductInventory> products) {
        Id = id;
        this.vendorId = vendorId;
        this.products = products;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public void addProduct(ProductInventory productInventory){
        products.put(productInventory.getProductCode(), productInventory);
    }

    public void addProduct(List<ProductInventory> productInventories){
        for(ProductInventory productInventory : productInventories){
            addProduct(productInventory);
        }
    }

    public List<ProductInventory> getProducts(){
        List<ProductInventory> productInventories = new ArrayList<>();
        Set<String> keys = products.keySet();

        for(String key : keys){
            productInventories.add(products.get(key));
        }
        return productInventories;
    }


    private ProductInventory getProductByProductCode(String productCode){
        return products.get(productCode);
    }
}

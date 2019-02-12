package com.example.inventoryservice.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Document
public class InventoryItem {
    @Id
    private String id;
    private String productCode;
    private Map<String, ProductInventory> vendorsProducts = new HashMap<>();
    private Integer availableQuantity = 0;
    private Double price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void addVendor(String vendorId, ProductInventory productInventory){
        ProductInventory productInventory1 = vendorsProducts.get(vendorId);
        if(productInventory1 != null && availableQuantity != 0){
            availableQuantity -= productInventory1.getAvailableQuantity();
        }
        vendorsProducts.put(vendorId, productInventory);
        availableQuantity += productInventory.getAvailableQuantity();
    }

    public void removeVendor(String vendorId){
        ProductInventory productInventory = vendorsProducts.get(vendorId);
        if(productInventory != null){
            availableQuantity -= productInventory.getAvailableQuantity();
            vendorsProducts.remove(vendorId);
        }
    }
}

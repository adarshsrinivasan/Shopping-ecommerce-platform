package com.example.catalogservice.Model;

import java.util.List;

public class ProductInventoryList {

    List<ProductInventory> productInventories;

    public List<ProductInventory> getProductInventories() {
        return productInventories;
    }

    public void setProductInventories(List<ProductInventory> productInventories) {
        this.productInventories = productInventories;
    }
}

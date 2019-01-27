package com.example.catalogservice.Model;

import java.util.List;

public class ProductInventoryResponseList {

    List<ProductInventoryResponse> productInventoryResponses;

    public List<ProductInventoryResponse> getProductInventoryResponses() {
        return productInventoryResponses;
    }

    public void setProductInventoryResponses(List<ProductInventoryResponse> productInventoryResponses) {
        this.productInventoryResponses = productInventoryResponses;
    }
}

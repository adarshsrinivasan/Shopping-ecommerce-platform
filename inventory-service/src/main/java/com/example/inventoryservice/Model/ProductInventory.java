package com.example.inventoryservice.Model;

import lombok.Data;

@Data
public class ProductInventory {
    private String productCode;
    private int availableQuantity = 0;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String toString() {
        return "{" +
                "productCode='" + productCode + '\'' +
                ", availableQuantity=" + availableQuantity +
                '}';
    }
}

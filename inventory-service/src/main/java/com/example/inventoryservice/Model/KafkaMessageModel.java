package com.example.vendorservice.Model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "VendorId",
        "ProductInventory"
})
public class KafkaMessageModel {

    @JsonProperty("VendorId")
    private String vendorId;

    @JsonProperty("ProductInventory")
    private List<ProductInventory> productInventories;
}

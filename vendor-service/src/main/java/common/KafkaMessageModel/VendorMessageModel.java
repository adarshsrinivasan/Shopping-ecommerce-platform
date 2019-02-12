package common.KafkaMessageModel;


import com.example.vendorservice.Model.ProductInventory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "VendorId",
        "ProductInventory"
})
public class VendorMessageModel {

    @JsonProperty("VendorId")
    private String vendorId;

    @JsonProperty("ProductInventory")
    private List<ProductInventory> productInventories;

    public VendorMessageModel() {
    }

    public VendorMessageModel(String vendorId, List<ProductInventory> productInventories) {
        this.vendorId = vendorId;
        this.productInventories = productInventories;
    }


    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public List<ProductInventory> getProductInventories() {
        return productInventories;
    }

    public void setProductInventories(List<ProductInventory> productInventories) {
        this.productInventories = productInventories;
    }
}

package common.KafkaMessageModel;

import com.example.inventoryservice.Model.ProductInventory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;


@JsonPropertyOrder({
        "ProductInventory"
})
public class InventoryMessageModel {

    @JsonProperty("ProductInventory")
    private List<ProductInventory> productInventories;

    public InventoryMessageModel() {
    }

    public InventoryMessageModel(List<ProductInventory> productInventories) {
        this.productInventories = productInventories;
    }

    public List<ProductInventory> getProductInventories() {
        return productInventories;
    }

    public void setProductInventories(List<ProductInventory> productInventories) {
        this.productInventories = productInventories;
    }
}


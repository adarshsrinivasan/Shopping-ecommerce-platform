package common.KafkaMessageModel;

import com.example.catalogservice.Model.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;


@JsonPropertyOrder({
        "Product"
})
public class InventoryMessageModel {

    @JsonProperty("Product")
    private Product product;

    public InventoryMessageModel() {
    }

    public InventoryMessageModel(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

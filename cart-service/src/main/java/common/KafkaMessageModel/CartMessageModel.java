package common.KafkaMessageModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "UserId",
        "ProductId",
        "Quantity"
})
public class CartMessageModel {
    @JsonProperty("UserId")
    private String userId;

    @JsonProperty("ProductId")
    private String productId;

    @JsonProperty("Quantity")
    private Integer quantity;

    public CartMessageModel() {
    }

    public CartMessageModel(String userId, String productId, Integer quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

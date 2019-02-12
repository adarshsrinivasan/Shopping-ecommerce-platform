package common.KafkaMessageModel;

import com.example.userservice.Model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "User"
})
public class UserMessageModel {
    @JsonProperty("User")
    private User user;

    public UserMessageModel() {
    }

    public UserMessageModel(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

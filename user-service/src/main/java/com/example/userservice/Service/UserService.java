package com.example.userservice.Service;

import common.KafkaMessageModel.UserMessageModel;
import com.example.userservice.Exception.UserAlreadyExistsException;
import com.example.userservice.Exception.UserNotFoundException;
import com.example.userservice.Model.Address;
import com.example.userservice.Model.User;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${user-vendor-add.topic-name}")
    private String addTopicName;

    @Value("${user-vendor-delete.topic-name}")
    private String deleteTopicName;

    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public User addUser(User user){

        Optional<User> optionalUser = getUserByUserId(user.getUserId());
        if(optionalUser.isPresent()){
            LOGGER.info("User with userId : " + user.getUserId() + " already exists");
            throw new UserAlreadyExistsException("UserId : " + user.getUserId() + " already exists");
        }

        if(user.isVendor()){
            UserMessageModel userMessageModel = new UserMessageModel();
            userMessageModel.setUser(user);
            kafkaTemplate.send(addTopicName, userMessageModel);
        }
        LOGGER.info("Adding user with userId : " + user.getUserId());
        return mongoTemplate.save(user);
    }

    public Optional<User> getUserByUserId(String userId){

        Optional<User> optionalUser = Optional.empty();
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        User resultUser = mongoTemplate.findOne(query, User.class);
        if(resultUser != null){
            LOGGER.info("Returning user with userId : " + userId);
            optionalUser = Optional.of(resultUser);
        }

        return optionalUser;
    }

    public User updateUserByUserId(String userId, User user){
        Optional<User> optionalUser = getUserByUserId(userId);
        optionalUser.orElseThrow(() -> new UserNotFoundException("Unable to find a user this userId : "+ userId));
        User resultUser = optionalUser.get();
        user.setId(resultUser.getId());
        LOGGER.info("Updating user with userId : " + user.getUserId());
        return mongoTemplate.save(user);
    }

    public DeleteResult deleteUserByUserId(String userId){
        Optional<User> optionalUser = getUserByUserId(userId);
        optionalUser.orElseThrow(() -> new UserNotFoundException("Unable to find a user this userId : "+ userId));
        User user = optionalUser.get();
        if(user.isVendor()){
            UserMessageModel userMessageModel = new UserMessageModel();
            userMessageModel.setUser(user);
            kafkaTemplate.send(deleteTopicName, userMessageModel);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        LOGGER.info("Deleted user with userId : " + userId);
        return mongoTemplate.remove(query, User.class);
    }

    public void initiMongoData(){
        Address address1 = new Address("Address Line1", "Address Line2", "Bangalore", "Karnataka", "India", 20);
        User user1 = new User();
        user1.setMailId("example@example.com");
        user1.setUserAddress(address1);
        user1.setPhoneNumber("0123456789");
        user1.setUserId("Customer-1");
        user1.setUserName("Customer-1");
        addUser(user1);

        Address address2 = new Address("Address Line1", "Address Line2", "Chennai", "TamilNadu", "India", 40);
        User user2 = new User();
        user2.setMailId("example@example.com");
        user2.setUserAddress(address2);
        user2.setPhoneNumber("0123456789");
        user2.setUserId("Customer-2");
        user2.setUserName("Customer-2");
        addUser(user2);

        Address address3 = new Address("Address Line1", "Address Line2", "Coimbatore", "TamilNadu", "India", 60);
        User user3 = new User();
        user3.setMailId("example@example.com");
        user3.setUserAddress(address3);
        user3.setPhoneNumber("0123456789");
        user3.setUserId("Vendor-1");
        user3.setUserName("Vendor-1");
        user3.setVendor(true);
        addUser(user3);

        Address address4 = new Address("Address Line1", "Address Line2", "Mysore", "Karnataka", "India", 10);
        User user4 = new User();
        user4.setMailId("example@example.com");
        user4.setUserAddress(address4);
        user4.setPhoneNumber("0123456789");
        user4.setUserId("Vendor-2");
        user4.setUserName("Vendor-2");
        user4.setVendor(true);
        addUser(user4);
    }

    public List<User> getAllUsers(){
        LOGGER.info("Returning all Users");
        return mongoTemplate.findAll(User.class);
    }

    public List<User> getAllCustomers(){
        Query query = new Query();
        query.addCriteria(Criteria.where("isVendor").is(false));
        LOGGER.info("Returning all Customers");
        return mongoTemplate.find(query, User.class);
    }

    public List<User> getAllVendors(){
        Query query = new Query();
        query.addCriteria(Criteria.where("isVendor").is(true));
        LOGGER.info("Returning all Vendors");
        return mongoTemplate.find(query, User.class);
    }
}

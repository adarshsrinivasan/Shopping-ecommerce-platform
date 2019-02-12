package com.example.userservice.Controller;

import com.example.userservice.Exception.UserNotFoundException;
import com.example.userservice.Model.User;
import com.example.userservice.Service.UserService;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void addUser(@RequestBody User user){
        userService.addUser(user);
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserByUserId(@PathVariable String userId){
        Optional<User> optionalUser = userService.getUserByUserId(userId);
        optionalUser.orElseThrow(() -> new UserNotFoundException("Unable to find a user this userId : "+ userId));

        return optionalUser;
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/customers")
    public List<User> getAllCustomers(){
        return userService.getAllCustomers();
    }

    @GetMapping("/vendors")
    public List<User> getAllVendors(){
        return userService.getAllVendors();
    }

    @PutMapping("/{userId}")
    public void updateUserByUserId(@PathVariable String userId, @RequestBody User user){
        userService.updateUserByUserId(userId, user);
    }

    @DeleteMapping("/{userId}")
    public DeleteResult deleteUserByUserId(@PathVariable String userId){
        return userService.deleteUserByUserId(userId);
    }

    @GetMapping("/init")
    public void initMongoData(){
        userService.initiMongoData();
    }
}

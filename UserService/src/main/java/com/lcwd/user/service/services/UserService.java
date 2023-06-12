package com.lcwd.user.service.services;

import java.util.*;
import com.lcwd.user.service.entities.User;

public interface UserService {
    //user operatioons

    //create
    User saveUser(User user);

    //get all user
    List<User> getAllUser();

    //get single user of given userId
    User getUser(String userId);


}

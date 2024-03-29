package com.lcwd.user.service.services.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        //generate unique userId
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        //implement RATING SERVICE CALL : USING REST TEMPLATE
        return userRepository.findAll();
    }

    //get single user
    @Override
    public User getUser(String userId) {
        //get user from database with help of user repository
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Use given id is not found on server !! : " + userId));

        //fetch rating of the above user from rating service
        //http://localhost:8083/ratings/users/12acf8b0-b8d6-452e-b5c9-19def36caa0e

        Rating[] ratingsOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
       logger.info("{} ",ratingsOfUser);

       List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();

      List<Rating> ratingList = ratings.stream().map(rating -> {
           //api call to hotel service to get the hotel
          //http://localhost:8082/hotels/894f97e6-8d61-4ce5-86f5-e8c3ef677246
//         ResponseEntity<Hotel> forEntity =  restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);

          Hotel hotel = hotelService.getHotel(rating.getHotelId());
//          logger.info("response status code: {}",forEntity.getStatusCode());


           //set hotel to rating
          rating.setHotel(hotel);

           //return the rating
           return rating;
       }).collect(Collectors.toList());

       user.setRatings(ratingList);
        return user;
    }
}

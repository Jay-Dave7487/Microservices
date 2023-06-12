package com.lcwd.rating.repository;

import com.lcwd.rating.entities.Rating;
import com.lcwd.rating.impl.RatingServiceImpl;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RatingRepository extends MongoRepository<Rating,String> {
    //custom finders methods
    List<Rating> findByUserId(String userId);
    List<Rating> findByHotelId(String hotelId);
}

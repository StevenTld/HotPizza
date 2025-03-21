package com.repositories;

import com.entities.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    // Find comments by pizza ID
    List<Comment> findByPizzaId(Long pizzaId);

    // Find comments by user ID
    List<Comment> findByUserId(String userId);

    // Find comments by pizza ID and sort by creation date (newest first)
    List<Comment> findByPizzaIdOrderByCreatedAtDesc(Long pizzaId);
}
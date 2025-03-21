package com.repositories;

import com.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPizzaId(Long pizzaId);
    List<Comment> findByUserId(Long userId);
    List<Comment> findByPizzaIdOrderByCreatedAtDesc(Long pizzaId);
}
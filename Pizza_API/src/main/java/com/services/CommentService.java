package com.services;

import com.dtos.CommentDto;
import java.util.List;

public interface CommentService {

    // Create a new comment
    CommentDto addComment(CommentDto commentDto);

    // Get comments for a specific pizza
    List<CommentDto> getCommentsByPizzaId(Long pizzaId);

    // Get comments by a specific user
    List<CommentDto> getCommentsByUserId(String userId);

    // Update an existing comment
    CommentDto updateComment(String commentId, CommentDto commentDto);

    // Delete a comment
    void deleteComment(String commentId);

    // Get average rating for a pizza
    Double getAverageRatingForPizza(Long pizzaId);
}
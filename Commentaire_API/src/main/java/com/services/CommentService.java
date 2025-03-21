package com.services;

import com.dtos.CommentDto;
import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto);
    CommentDto getCommentById(Long id);
    List<CommentDto> getCommentsByPizzaId(Long pizzaId);
    List<CommentDto> getCommentsByUserId(Long userId);
    CommentDto updateComment(Long id, CommentDto commentDto, Long requestUserId, boolean isAdmin);
    boolean deleteComment(Long id, Long requestUserId, boolean isAdmin);
    Double getAverageRatingForPizza(Long pizzaId);
}
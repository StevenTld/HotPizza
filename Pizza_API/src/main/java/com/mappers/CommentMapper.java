package com.mappers;

import com.dtos.CommentDto;
import com.entities.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setPizzaId(comment.getPizzaId());
        commentDto.setUserId(comment.getUserId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setRating(comment.getRating());
        return commentDto;
    }

    public Comment toEntity(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setPizzaId(commentDto.getPizzaId());
        comment.setUserId(commentDto.getUserId());
        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(commentDto.getCreatedAt());
        comment.setRating(commentDto.getRating());
        return comment;
    }
}
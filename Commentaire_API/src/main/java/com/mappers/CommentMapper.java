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

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setPizzaId(comment.getPizzaId());
        dto.setContent(comment.getContent());
        dto.setRating(comment.getRating());
        dto.setUserName(comment.getUserName());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        return dto;
    }

    public Comment toEntity(CommentDto dto) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();
        if (dto.getId() != null) {
            comment.setId(dto.getId());
        }
        comment.setUserId(dto.getUserId());
        comment.setPizzaId(dto.getPizzaId());
        comment.setContent(dto.getContent());
        comment.setRating(dto.getRating());
        comment.setUserName(dto.getUserName());

        return comment;
    }
}
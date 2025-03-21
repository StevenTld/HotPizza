package com.services.impl;

import com.dtos.CommentDto;
import com.entities.Comment;
import com.mappers.CommentMapper;
import com.repositories.CommentRepository;
import com.services.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentDto addComment(CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPizzaId(Long pizzaId) {
        List<Comment> comments = commentRepository.findByPizzaIdOrderByCreatedAtDesc(pizzaId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUserId(String userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateComment(String commentId, CommentDto commentDto) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        // Update only mutable fields
        existingComment.setContent(commentDto.getContent());
        existingComment.setRating(commentDto.getRating());

        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toDto(updatedComment);
    }

    @Override
    public void deleteComment(String commentId) {
        // Verify comment exists before deleting
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingForPizza(Long pizzaId) {
        List<Comment> comments = commentRepository.findByPizzaId(pizzaId);

        if (comments.isEmpty()) {
            return 0.0;
        }

        return comments.stream()
                .mapToInt(Comment::getRating)
                .average()
                .orElse(0.0);
    }
}
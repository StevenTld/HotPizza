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
    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le commentaire avec l'ID %d n'existe pas", id)));
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPizzaId(Long pizzaId) {
        return commentRepository.findByPizzaId(pizzaId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateComment(Long id, CommentDto commentDto, Long requestUserId, boolean isAdmin) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le commentaire avec l'ID %d n'existe pas", id)));

        // Vérifier que l'utilisateur est le propriétaire du commentaire ou un admin
        if (!existingComment.getUserId().equals(requestUserId) && !isAdmin) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce commentaire");
        }

        // Mettre à jour uniquement les champs autorisés
        existingComment.setContent(commentDto.getContent());
        existingComment.setRating(commentDto.getRating());

        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toDto(updatedComment);
    }

    @Override
    public boolean deleteComment(Long id, Long requestUserId, boolean isAdmin) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le commentaire avec l'ID %d n'existe pas", id)));

        // Vérifier que l'utilisateur est le propriétaire du commentaire ou un admin
        if (!comment.getUserId().equals(requestUserId) && !isAdmin) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce commentaire");
        }

        commentRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingForPizza(Long pizzaId) {
        List<Comment> comments = commentRepository.findByPizzaId(pizzaId);
        if (comments.isEmpty()) {
            return 0.0;
        }

        int sum = comments.stream()
                .mapToInt(Comment::getRating)
                .sum();

        return (double) sum / comments.size();
    }
}
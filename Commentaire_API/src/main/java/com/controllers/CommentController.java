package com.controllers;

import com.dtos.CommentDto;
import com.services.CommentService;
import com.utils.UserUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:8079", allowCredentials = "true")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Récupérer tous les commentaires d'une pizza
    @GetMapping("/pizza/{pizzaId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPizzaId(@PathVariable Long pizzaId) {
        List<CommentDto> comments = commentService.getCommentsByPizzaId(pizzaId);
        return ResponseEntity.ok(comments);
    }

    // Récupérer la note moyenne d'une pizza
    @GetMapping("/pizza/{pizzaId}/rating")
    public ResponseEntity<Map<String, Double>> getAverageRatingForPizza(@PathVariable Long pizzaId) {
        Double averageRating = commentService.getAverageRatingForPizza(pizzaId);
        return ResponseEntity.ok(Map.of("averageRating", averageRating != null ? averageRating : 0.0));
    }

    // Récupérer tous les commentaires d'un utilisateur
    @GetMapping("/user")
    public ResponseEntity<List<CommentDto>> getUserComments() {
        try {
            Long userId = UserUtil.getCurrentUserId();
            List<CommentDto> comments = commentService.getCommentsByUserId(userId);
            return ResponseEntity.ok(comments);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Récupérer un commentaire par ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        try {
            CommentDto comment = commentService.getCommentById(id);
            return ResponseEntity.ok(comment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer un nouveau commentaire
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            commentDto.setUserId(userId);
            CommentDto createdComment = commentService.createComment(commentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Mettre à jour un commentaire
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody CommentDto commentDto) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            boolean isAdmin = UserUtil.isCurrentUserAdmin();

            CommentDto updatedComment = commentService.updateComment(id, commentDto, userId, isAdmin);
            return ResponseEntity.ok(updatedComment);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Supprimer un commentaire
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteComment(@PathVariable Long id) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            boolean isAdmin = UserUtil.isCurrentUserAdmin();

            boolean result = commentService.deleteComment(id, userId, isAdmin);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
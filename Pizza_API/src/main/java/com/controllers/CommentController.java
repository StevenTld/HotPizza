package com.controllers;

import com.dtos.CommentDto;
import com.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:8079", allowCredentials = "true")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Add a new comment
    @PostMapping
    public ResponseEntity<CommentDto> addComment(@Valid @RequestBody CommentDto commentDto) {
        CommentDto savedComment = commentService.addComment(commentDto);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    // Get comments for a specific pizza
    @GetMapping("/pizza/{pizzaId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPizzaId(@PathVariable Long pizzaId) {
        List<CommentDto> comments = commentService.getCommentsByPizzaId(pizzaId);
        return ResponseEntity.ok(comments);
    }

    // Get average rating for a pizza
    @GetMapping("/pizza/{pizzaId}/rating")
    public ResponseEntity<Double> getAverageRatingForPizza(@PathVariable Long pizzaId) {
        Double averageRating = commentService.getAverageRatingForPizza(pizzaId);
        return ResponseEntity.ok(averageRating);
    }

    // Get comments for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentDto>> getCommentsByUserId(@PathVariable String userId) {
        List<CommentDto> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }

    // Update a comment
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody CommentDto commentDto) {
        CommentDto updatedComment = commentService.updateComment(commentId, commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}

package com.pedrocoelho.restwebservices.learningrestwebservices.resources;

import com.pedrocoelho.restwebservices.learningrestwebservices.models.Comment;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentJpaResource {
    private CommentRepository commentRepository;

    public CommentJpaResource(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
}

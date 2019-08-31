package com.pedrocoelho.restwebservices.learningrestwebservices.resources;

import com.pedrocoelho.restwebservices.learningrestwebservices.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByUserId(Long id);
}

package com.pedrocoelho.restwebservices.learningrestwebservices.resources;

import com.pedrocoelho.restwebservices.learningrestwebservices.models.Comment;
import com.pedrocoelho.restwebservices.learningrestwebservices.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}

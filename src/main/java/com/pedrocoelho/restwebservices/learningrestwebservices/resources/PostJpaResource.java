package com.pedrocoelho.restwebservices.learningrestwebservices.resources;

import com.pedrocoelho.restwebservices.learningrestwebservices.exceptions.PostNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.exceptions.UserNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.models.Comment;
import com.pedrocoelho.restwebservices.learningrestwebservices.models.Post;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class PostJpaResource {
    private PostRepository postRepository;
    private CommentRepository commentRepository;

    public PostJpaResource(UserRepository userRepository,
                           PostRepository postRepository,
                           CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/posts")
    public List<Post> retrievePosts() {
        return postRepository.findAll();
    }

    @GetMapping("/posts/{id}")
    public EntityModel<Post> retrievePost(@PathVariable Long id) {
        Optional<Post> optPost = postRepository.findById(id);
        if(optPost.isEmpty())
            throw new PostNotFoundException("Post with id " + id + " not found!");

        EntityModel<Post> model = new EntityModel<>(optPost.get());
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrievePosts());
        model.add(linkTo.withRel("all-posts"));

        return model;
    }

    @PatchMapping("/posts/{id}")
    public EntityModel<Post> updateUserPost(@PathVariable Long id, @RequestBody Post post) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty())
            throw new PostNotFoundException(id.toString());

        Post record = postOptional.get();

        if (post.getDescription() != null)
            record.setDescription(post.getDescription());
K
        postRepository.save(record);

        EntityModel<Post> model = new EntityModel<>(record);
        WebMvcLinkBuilder linkToAllPostComments = linkTo(methodOn(this.getClass()).retrievePostComments(id));
        model.add(linkToAllPostComments.withRel("post-comments"));

        return model;
    }

    @GetMapping("/posts/{id}/comments")
    public List<Comment> retrievePostComments(@PathVariable Long id) {
        Optional<Post> optPost = postRepository.findById(id);
        if (optPost.isEmpty())
            throw new UserNotFoundException(id.toString());

        return optPost.get().getComments();
    }

//    @GetMapping("/posts/{pid}/comments/{id}")
//    public EntityModel<Comment> retrievePostComment(@PathVariable Long pid, @PathVariable Long) {
//        Optional<Post> optPost = postRepository.findById(pid);
//
//        EntityModel<Post> post =
//    }
}

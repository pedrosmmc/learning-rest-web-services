package com.pedrocoelho.restwebservices.learningrestwebservices.user;

import com.pedrocoelho.restwebservices.learningrestwebservices.comment.Comment;
import com.pedrocoelho.restwebservices.learningrestwebservices.comment.CommentRepository;
import com.pedrocoelho.restwebservices.learningrestwebservices.exception.PostNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.exception.UserNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.post.Post;
import com.pedrocoelho.restwebservices.learningrestwebservices.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.PostUpdate;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserJpaResource {

//    @Autowired
//    private UserDaoService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/jpa/users")
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/jpa/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new UserNotFoundException("" + id);
        EntityModel<User> model = new EntityModel<>(user.get());
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkTo.withRel("all-users"));

        return model;
    }

    @PostMapping("/jpa/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
        User createdUser = userRepository.save(user);
        // constructs a new URI from the request and send it back with the {id} of the created user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/jpa/users/{id}")
    public void deleteUser(@PathVariable long id) {
        userRepository.deleteById(id);
    }

    @PatchMapping("/jpa/users/{id}")
    public EntityModel<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new UserNotFoundException("" + id);

        User updateUser = userOptional.get();
        if (user.getName() != null)
            updateUser.setName(user.getName());
        if (user.getEmail() != null)
            updateUser.setEmail(user.getEmail());
        if (user.getBirthDate() != null)
            updateUser.setBirthDate(user.getBirthDate());
        User updatedUser = userRepository.save(updateUser);

        EntityModel<User> model = new EntityModel<>(updateUser);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkTo.withRel("all-users"));

        return model;
    }

    @GetMapping("/jpa/users/{id}/posts")
    public List<Post> retrieveAllPosts(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new UserNotFoundException("" + id);

        return userOptional.get().getPosts();
    }

    @GetMapping("/jpa/users/{id_user}/posts/{id}")
    public EntityModel<Post> retrievePost(@PathVariable Long id_user, @PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty())
            throw new UserNotFoundException("" + id);

        EntityModel<Post> model = new EntityModel<>(post.get());
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveAllPosts(id_user));
        model.add(linkToAllUserPosts.withRel("all-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkToAllUsers.withRel("all-users"));

        return model;
    }

    @PostMapping("/jpa/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable Long id, @RequestBody Post post) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new UserNotFoundException("" + id);

        User user = userOptional.get();
        post.setUser(user);
        Post createdPost = postRepository.save(post);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPost.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/jpa/users/{id}/posts/{id}")
    public void deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
    }

    @PatchMapping("/jpa/users/{id}/posts/{id}")
    public ResponseEntity<Object> updatePost(@PathVariable Long id, @RequestBody Post post) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty())
            throw new PostNotFoundException("" + id);
        Post updatedPost = postOptional.get();
        updatedPost.setTitle(post.getTitle());
        updatedPost.setDescription(post.getDescription());
        postRepository.save(updatedPost);

        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @GetMapping("/jpa/users/{id}/posts/{id}/comments")
    //TODO: should retrieve an EntityModel with HATEOAS
    public List<Comment> retrieveAllPostComments(@PathVariable Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty())
            throw new PostNotFoundException("" + id);

        return postOptional.get().getComments();
    }
}

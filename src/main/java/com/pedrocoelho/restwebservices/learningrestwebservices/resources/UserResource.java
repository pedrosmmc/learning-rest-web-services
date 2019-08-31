package com.pedrocoelho.restwebservices.learningrestwebservices.resources;

import com.pedrocoelho.restwebservices.learningrestwebservices.models.Comment;
import com.pedrocoelho.restwebservices.learningrestwebservices.exceptions.CommentNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.exceptions.PostNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.exceptions.UserNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.models.User;
import com.pedrocoelho.restwebservices.learningrestwebservices.models.Post;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Transactional
@RestController
public class UserResource {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private CommentRepository commentRepository;

    public UserResource(UserRepository userRepository,
                        PostRepository postRepository,
                        CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/users")
    public List<User> retrieveUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new UserNotFoundException(id.toString());

        EntityModel<User> model = new EntityModel<>(user.get());
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveUsers());
        model.add(linkTo.withRel("all-users"));

        return model;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userRepository.save(user);
        // constructs a new URI from the request and send it back with the {id} of the created user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PatchMapping("/users/{id}")
    public EntityModel<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updateUser = findUser(id);

        if (user.getName() != null)
            updateUser.setName(user.getName());
        if (user.getEmail() != null)
            updateUser.setEmail(user.getEmail());
        if (user.getBirthDate() != null)
            updateUser.setBirthDate(user.getBirthDate());
        User updatedUser = userRepository.save(updateUser);

        EntityModel<User> model = new EntityModel<>(updateUser);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveUsers());
        model.add(linkTo.withRel("all-users"));

        return model;
    }

    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveUserPosts(@PathVariable Long id) {
        return findUser(id).getPosts();
    }

    @GetMapping("/users/{uid}/posts/{id}")
    public EntityModel<Post> retrieveUserPost(@PathVariable Long uid, @PathVariable Long id) {
        User user = findUser(uid);
        Post post = findPost(id);

        if (!post.getUser().getId().equals(user.getId()))
            throw new PostNotFoundException("Post " + id + " not belong to this user!");

        EntityModel<Post> model = new EntityModel<>(post);
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveUserPosts(uid));
        model.add(linkToAllUserPosts.withRel("all-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveUsers());
        model.add(linkToAllUsers.withRel("all-users"));

        return model;
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Post> createUserPost(@PathVariable Long id, @RequestBody Post post) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new UserNotFoundException(id.toString());

        User user = userOptional.get();
        post.setUser(user);
        post.setCreated(new Date());
        Post createdPost = postRepository.save(post);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPost.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


    //TODO: solve this situation
//{
//    "timestamp": "2019-08-17T12:57:53.926+0000",
//        "message": "Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction",
//        "details": "uri=/users/3331/posts/4441"
//}
    @PatchMapping("/users/{uid}/posts/{id}")
    public EntityModel<Post> updateUserPost(@PathVariable Long uid, @PathVariable Long id, @RequestBody Post post) {
        User user = findUser(uid);
        Post record = findPost(id);

        if(!user.getId().equals(record.getUser().getId()))
            throw new PostNotFoundException("User has no privileges for alter this post!");

        if (post.getTitle() != null)
            record.setTitle(post.getTitle());

        if (post.getDescription() != null)
            record.setDescription(post.getDescription());

        postRepository.save(record);

        EntityModel<Post> model = new EntityModel<>(record);
        WebMvcLinkBuilder linkToAllPostComments = linkTo(methodOn(this.getClass()).retrieveUserPostComments(id));
        model.add(linkToAllPostComments.withRel("post-comments"));
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveUserPosts(uid));
        model.add(linkToAllUserPosts.withRel("user-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveUsers());
        model.add(linkToAllUsers.withRel("users"));

        return model;
    }

    @DeleteMapping("/users/{uid}/posts/{id}")
    public void deleteUserPost(@PathVariable Long uid, @PathVariable Long id) {
        User user = findUser(uid);
        Post post = findPost(id);

        if(!user.getId().equals(post.getUser().getId()))
            throw new PostNotFoundException("User has no privileges to delete this post!");

        postRepository.deleteById(id);
    }

    @GetMapping("/users/{uid}/posts/{id}/comments")
    public List<Comment> retrieveUserPostComments(@PathVariable Long id) {
        return findPost(id).getComments();
    }

    @GetMapping("/users/{uid}/comments")
    public List<Comment> retrieveUserComments(@PathVariable Long uid) {
        return commentRepository.findAllByUserId(uid);
    }

    @GetMapping("/users/{uid}/posts/{pid}/comments/{id}")
    public EntityModel<Comment> retrieveUserPostComment(@PathVariable Long uid, @PathVariable Long pid, @PathVariable Long id) {
        User user = findUser(uid);
        Post post = findPost(pid);
        Comment comment = findComment(id);

        if(!user.getId().equals(post.getUser().getId()))
            throw new PostNotFoundException(pid.toString());

        if(!post.getId().equals(comment.getPost().getId()))
            throw new CommentNotFoundException(id.toString());

        EntityModel<Comment> model = new EntityModel<>(comment);
        WebMvcLinkBuilder linkToAllPostComments = linkTo(methodOn(this.getClass()).retrieveUserPostComments(pid));
        model.add(linkToAllPostComments.withRel("post-comments"));
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveUserPosts(uid));
        model.add(linkToAllUserPosts.withRel("user-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveUsers());
        model.add(linkToAllUsers.withRel("users"));

        return model;
    }

    @PostMapping("/users/{id}/posts/{id}/comments")
    public ResponseEntity<Comment> createUserPostComment(@PathVariable Long id, @RequestBody Comment comment) {
        Post post = findPost(id);

        comment.setPost(post);
        comment.setCreated(new Date());
        Comment createdComment = commentRepository.save(comment);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdComment.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}/posts/{id}/comments/{id}")
    public void deleteUserPostComment(@PathVariable Long id) {

        // TODO: check privileges
        commentRepository.deleteById(id);
    }

    @PatchMapping("/users/{uid}/posts/{pid}/comments/{id}")
    public EntityModel<Comment> updateUserPostComment(@PathVariable Long uid, @PathVariable Long pid, @PathVariable Long id, @RequestBody Comment comment) {
        User user = findUser(uid);
        Post post = findPost(pid);
        Comment record = findComment(id);

        if(!user.getId().equals(post.getUser().getId()))
            throw new PostNotFoundException("User has no privileges for this post!");

        if(!user.getId().equals(record.getUser().getId()))
            throw new CommentNotFoundException("User has no privileges for this comment!");

        if(!post.getId().equals(record.getPost().getId()))
            throw new CommentNotFoundException(id.toString());

        if (comment.getText() != null)
            record.setText(comment.getText());

        commentRepository.save(record);

        EntityModel<Comment> model = new EntityModel<>(record);

        WebMvcLinkBuilder linkToAllPostComments = linkTo(methodOn(this.getClass()).retrieveUserPostComments(pid));
        model.add(linkToAllPostComments.withRel("post-comments"));
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveUserPosts(uid));
        model.add(linkToAllUserPosts.withRel("user-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveUsers());
        model.add(linkToAllUsers.withRel("users"));

        return model;
    }

    private User findUser(Long uid) {
        Optional<User> optUser = userRepository.findById(uid);
        if (optUser.isEmpty())
            throw new UserNotFoundException(uid.toString());
        return optUser.get();
    }

    private Post findPost(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty())
            throw new PostNotFoundException(id.toString());
        return postOptional.get();
    }

    private Comment findComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty())
            throw new CommentNotFoundException(id.toString());
        return commentOptional.get();
    }
}

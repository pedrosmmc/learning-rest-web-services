package com.pedrocoelho.restwebservices.learningrestwebservices.user;

import com.pedrocoelho.restwebservices.learningrestwebservices.comment.Comment;
import com.pedrocoelho.restwebservices.learningrestwebservices.comment.CommentRepository;
import com.pedrocoelho.restwebservices.learningrestwebservices.exception.CommentNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.exception.PostNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.exception.UserNotFoundException;
import com.pedrocoelho.restwebservices.learningrestwebservices.post.Post;
import com.pedrocoelho.restwebservices.learningrestwebservices.post.PostRepository;
import org.hibernate.type.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Transactional
@RestController
public class UserJpaResource {

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
    public EntityModel<User> retrieveUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new UserNotFoundException(id.toString());

        EntityModel<User> model = new EntityModel<>(user.get());
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkTo.withRel("all-users"));

        return model;
    }

    @PostMapping("/jpa/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userRepository.save(user);
        // constructs a new URI from the request and send it back with the {id} of the created user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/jpa/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PatchMapping("/jpa/users/{id}")
    public EntityModel<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new UserNotFoundException(id.toString());

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
    public List<Post> retrieveAllUserPosts(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new UserNotFoundException(id.toString());

        return userOptional.get().getPosts();
    }

    @GetMapping("/jpa/users/{id_user}/posts/{id}")
    public EntityModel<Post> retrieveUserPost(@PathVariable Long id_user, @PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty())
            throw new UserNotFoundException(id.toString());

        EntityModel<Post> model = new EntityModel<>(post.get());
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveAllUserPosts(id_user));
        model.add(linkToAllUserPosts.withRel("all-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkToAllUsers.withRel("all-users"));

        return model;
    }

    @PostMapping("/jpa/users/{id}/posts")
    public ResponseEntity<Post> createPost(@PathVariable Long id, @RequestBody Post post) {
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


//TODO:
//{
//    "timestamp": "2019-08-17T12:57:53.926+0000",
//        "message": "Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction",
//        "details": "uri=/jpa/users/3331/posts/4441"
//}
    @PatchMapping("/jpa/users/{uid}/posts/{id}")
    public EntityModel<Post> updatePost(@PathVariable Long uid, @PathVariable Long id, @RequestBody Post post) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty())
            throw new PostNotFoundException(id.toString());

        Post record = postOptional.get();

        if (post.getDescription() != null)
            record.setDescription(post.getDescription());

        postRepository.save(record);

        EntityModel<Post> model = new EntityModel<>(record);
        WebMvcLinkBuilder linkToAllPostComments = linkTo(methodOn(this.getClass()).retrieveAllPostComments(id));
        model.add(linkToAllPostComments.withRel("post-comments"));
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveAllUserPosts(uid));
        model.add(linkToAllUserPosts.withRel("user-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkToAllUsers.withRel("users"));

        return model;
    }

    @DeleteMapping("/jpa/users/{id}/posts/{id}")
    public void deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
    }

    @GetMapping("/jpa/posts")
    public List<Post> retriedAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/jpa/users/{id}/posts/{id}/comments")
    public List<Comment> retrieveAllPostComments(@PathVariable Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty())
            throw new PostNotFoundException(id.toString());

        return postOptional.get().getComments();
    }

    @GetMapping("/jpa/users/{uid}/posts/{pid}/comments/{id}")
    public EntityModel<Comment> retrievePostComment(@PathVariable Long uid, @PathVariable Long pid, @PathVariable Long id) {

        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty())
            throw new CommentNotFoundException(id.toString());

        EntityModel<Comment> model = new EntityModel<>(commentOptional.get());
        WebMvcLinkBuilder linkToAllPostComments = linkTo(methodOn(this.getClass()).retrieveAllPostComments(pid));
        model.add(linkToAllPostComments.withRel("post-comments"));
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveAllUserPosts(uid));
        model.add(linkToAllUserPosts.withRel("user-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkToAllUsers.withRel("users"));

        return model;
    }

    @PostMapping("/jpa/users/{id}/posts/{id}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long id, @RequestBody Comment comment) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty())
            throw new UserNotFoundException(id.toString());

        Post post = postOptional.get();
        comment.setPost(post);
        comment.setCreated(new Date());
        Comment createdComment = commentRepository.save(comment);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdComment.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/jpa/users/{id}/posts/{id}/comments/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentRepository.deleteById(id);
    }

    @PatchMapping("/jpa/users/{uid}/posts/{pid}/comments/{id}")
    public EntityModel<Comment> updateComment(@PathVariable Long uid, @PathVariable Long pid, @PathVariable Long id, @RequestBody Comment comment) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty())
            throw new CommentNotFoundException(id.toString());

        Comment record = commentOptional.get();

        if (comment.getText() != null)
            record.setText(comment.getText());

        commentRepository.save(record);

        EntityModel<Comment> model = new EntityModel<>(record);
        WebMvcLinkBuilder linkToAllPostComments = linkTo(methodOn(this.getClass()).retrieveAllPostComments(pid));
        model.add(linkToAllPostComments.withRel("post-comments"));
        WebMvcLinkBuilder linkToAllUserPosts = linkTo(methodOn(this.getClass()).retrieveAllUserPosts(uid));
        model.add(linkToAllUserPosts.withRel("user-posts"));
        WebMvcLinkBuilder linkToAllUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkToAllUsers.withRel("users"));

        return model;
    }
}

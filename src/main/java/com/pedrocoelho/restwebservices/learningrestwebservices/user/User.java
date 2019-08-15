package com.pedrocoelho.restwebservices.learningrestwebservices.user;

import com.pedrocoelho.restwebservices.learningrestwebservices.comment.Comment;
import com.pedrocoelho.restwebservices.learningrestwebservices.post.Post;
import org.hibernate.annotations.Where;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 2, message = "Name should have at least 2 characters")
    private String name;

    @NotNull
    @Email
    private String email;

    @Past
    private Date birthDate;

    @OneToMany(mappedBy="user")
//    @Where(clause = "IS_PUBLISHED = 'TRUE'") // DB FILTERING
    private List<Post> posts;

    @OneToMany(mappedBy="user")
    private List<Comment> comments;

    protected User() {
    }

    User(Long id, String name, String email, Date birthDate) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}

package com.pedrocoelho.restwebservices.learningrestwebservices.models;

import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Who is the owner of this ad?")
    @Size(min = 2, message = "Name should have at least 2 characters.")
    private String name;

    @NotNull(message = "You need to provide a valid email.")
    @Email
    private String email;

    @Past
    private Date birthDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
//    @Where(clause = "IS_PUBLISHED = 'TRUE'") // DB FILTERING
    private List<Post> posts;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
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

    public Long getId() {
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

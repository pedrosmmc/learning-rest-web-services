package com.pedrocoelho.restwebservices.learningrestwebservices.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @GeneratedValue
    @JsonIgnore
    @CreatedDate
    @FutureOrPresent
    @NotNull
    private Date created;

    @GeneratedValue
    @DateTimeFormat
    @FutureOrPresent
    private Date published;

    private boolean isPublished;

    @ManyToOne
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public Post() {
        this.isPublished = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getPublishedDate() {
        return published;
    }

    public void setPublishedDate(Date published) {
        this.published = published;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished() {
        if (isPublished)
            isPublished = false;
        isPublished = true;
    }

    public void setPublished(Boolean published) {
        if (published)
            isPublished = false;
        isPublished = true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", created=" + created +
                ", published=" + published +
                ", isPublished=" + isPublished +
                '}';
    }
}

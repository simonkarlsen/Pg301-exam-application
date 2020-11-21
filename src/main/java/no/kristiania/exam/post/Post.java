package no.kristiania.exam.post;

import net.minidev.json.annotate.JsonIgnore;
import no.kristiania.exam.user.User;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Post {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull(message = "Name cannot be null")
    @Length(min = 1, max = 255, message = "Length of description must be between 1 and 255 character(s)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}

package ch.fhnw.webec.booklist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Min(1)
    @Max(5)
    private int rating;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Column(columnDefinition = "TEXT")
    @NotEmpty
    private String comment;

    @ManyToOne
    @JsonIgnore
    private Book book;

    public Review() {}

    public Review(int rating, User user, String comment) {
        this.rating = rating;
        this.user = user;
        this.comment = comment;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
        this.book.addReview(this);
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserName() {
        return this.user == null ? "" : this.user.getUsername();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

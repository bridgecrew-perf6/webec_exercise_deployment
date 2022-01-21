package ch.fhnw.webec.booklist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    private String title;

    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotEmpty
    @ManyToMany
    @OrderBy("name ASC")
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    @OrderBy("createdDateTime DESC")
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    public Book() {}

    public Book(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        if (!this.getReviews().contains(review)) {
            this.getReviews().add(review);
        }

        if (review.getBook() != this) {
            review.setBook(this);
        }
    }

    public double getAverageRating() {
        var averageRating = this.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0);

        return this.roundToHalf(averageRating);
    }

    private double roundToHalf(double number) {
        return Math.round(number * 2) / 2.0;
    }
}

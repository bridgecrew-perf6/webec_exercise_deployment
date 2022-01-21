package ch.fhnw.webec.booklist.repository;

import ch.fhnw.webec.booklist.model.Book;
import ch.fhnw.webec.booklist.model.Review;
import ch.fhnw.webec.booklist.model.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import javax.validation.ConstraintViolationException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryIntegrationTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void testFindAll() {
        var books = this.bookRepository.findAll();
        var firstBook = books.get(0);
        var firstBookTopicNames = firstBook.getTopics().stream().map(Topic::getName).toList();

        assertEquals(3, books.size());
        assertEquals("Test book 1", firstBook.getTitle());
        assertEquals("Test book 1 description", firstBook.getDescription());
        assertTrue(firstBookTopicNames.contains("Test topic 1"));
        assertTrue(firstBookTopicNames.contains("Test topic 2"));
    }

    @Test
    public void testSaveBook() {
        var book = new Book("Hello World", "Lorem ipsum");
        var topic = this.topicRepository.findById(1).get();
        var review = new Review(5, null, "Very good!");

        book.setTopics(Set.of(topic));
        book.addReview(review);

        assertEquals(3, this.bookRepository.findAll().size());

        var savedBook = this.bookRepository.save(book);
        var savedReview = this.reviewRepository.findById(savedBook.getReviews().get(0).getId()).get();

        assertEquals(4, this.bookRepository.findAll().size());
        assertEquals("Hello World", savedBook.getTitle());
        assertEquals("Lorem ipsum", savedBook.getDescription());
        assertSame(savedReview, book.getReviews().get(0));
        assertTrue(savedBook.getTopics().contains(topic));
    }

    @Test
    public void testSaveInvalidBook() {
        assertThrows(ConstraintViolationException.class, () -> {
            this.bookRepository.save(new Book("Hello World", "Lorem ipsum"));
        });
    }

    @Test
    public void testUpdateBook() {
        var book = this.bookRepository.findById(1).get();
        var topic = this.topicRepository.findById(1).get();

        assertEquals("Test book 1", book.getTitle());

        book.setTitle("Updated test book 1");
        var savedBook = this.bookRepository.save(book);

        assertEquals(3, this.bookRepository.findAll().size());
        assertEquals("Updated test book 1", savedBook.getTitle());
        assertEquals("Test book 1 description", savedBook.getDescription());
        assertTrue(savedBook.getTopics().contains(topic));
    }

    @Test
    public void testDeleteBook() {
        var book = this.bookRepository.findById(1).get();

        assertEquals(3, this.bookRepository.findAll().size());

        this.bookRepository.delete(book);

        assertEquals(2, this.bookRepository.findAll().size());
    }

    @Test
    public void testDeleteBookCascading() {
        var book = this.bookRepository.findById(1).get();
        var review = new Review(5, null, "Very good!");

        assertEquals(0, this.reviewRepository.findAll().size());
        assertEquals(3, this.topicRepository.findAll().size());

        book.addReview(review);
        var savedBook = this.bookRepository.save(book);

        assertEquals(1, book.getReviews().size());
        assertEquals(1, savedBook.getReviews().size());
        assertEquals(1, this.reviewRepository.findAll().size());

        this.bookRepository.delete(book);

        assertTrue(this.bookRepository.findById(1).isEmpty());
        assertEquals(0, this.reviewRepository.findAll().size());
        assertEquals(3, this.topicRepository.findAll().size());
    }
}

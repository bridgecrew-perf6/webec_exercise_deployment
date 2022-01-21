package ch.fhnw.webec.booklist.model;

import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

public class BookUnitTest {
    private Validator createValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();

        return localValidatorFactoryBean;
    }

    @Test
    public void testAddReview() {
        var book = new Book();
        var review = new Review();

        assertEquals(0, book.getReviews().size());

        book.addReview(review);

        assertEquals(1, book.getReviews().size());
        assertTrue(book.getReviews().contains(review));
        assertEquals(book, review.getBook());
    }

    @Test
    public void testGetAverageRating() {
        var book = new Book();

        var review1 = new Review();
        review1.setRating(5);
        book.addReview(review1);
        assertEquals(5.0, book.getAverageRating(), 0.0001);

        var review2 = new Review();
        review2.setRating(2);
        book.addReview(review2);
        assertEquals(3.5, book.getAverageRating(), 0.0001);

        var review3 = new Review();
        review3.setRating(4);
        book.addReview(review3);
        assertEquals(3.5, book.getAverageRating(), 0.0001);
    }

    @Test
    public void testValidation() {
        var validator = createValidator();
        var book = new Book();
        var constraintViolations = validator.validate(book);

        assertEquals(3, constraintViolations.size());

        for (var violation : constraintViolations) {
            assertEquals("must not be empty", violation.getMessage());
        }
    }
}

package ch.fhnw.webec.booklist.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewUnitTest {
    private Validator createValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();

        return localValidatorFactoryBean;
    }

    private <T> List<ConstraintViolation<T>> getConstraintViolationsByPropertyPath(Set<ConstraintViolation<T>> constraintViolations, String propertyPath) {
        return constraintViolations.stream().filter(violation -> violation.getPropertyPath().toString().equals(propertyPath)).toList();
    }

    @Test
    public void testSetBook() {
        var book = new Book();
        var review = new Review();

        assertNull(review.getBook());

        review.setBook(book);

        assertEquals(1, book.getReviews().size());
        assertTrue(book.getReviews().contains(review));
        assertEquals(book, review.getBook());
    }

    @ValueSource(ints = {1, 3, 5})
    @ParameterizedTest
    public void testValidRating(int rating) {
        var validator = createValidator();
        var constraintViolations = validator.validate(new Review(rating, null, ""));
        var ratingViolations = this.getConstraintViolationsByPropertyPath(constraintViolations, "rating");

        assertEquals(0, ratingViolations.size());
    }

    @ValueSource(ints = {-1, 0, 6, 7})
    @ParameterizedTest
    public void testInvalidRating(int rating) {
        var validator = createValidator();
        var constraintViolations = validator.validate(new Review(rating, null, ""));
        var ratingViolations = this.getConstraintViolationsByPropertyPath(constraintViolations, "rating");

        assertEquals(1, ratingViolations.size());
    }
}

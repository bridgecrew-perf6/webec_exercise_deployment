package ch.fhnw.webec.booklist.repository;

import ch.fhnw.webec.booklist.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByBookId(int id);
}

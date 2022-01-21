package ch.fhnw.webec.booklist.repository;

import ch.fhnw.webec.booklist.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("""
        SELECT DISTINCT book FROM Book book
        INNER JOIN book.topics topic
        WHERE lower(book.title) LIKE lower(concat('%', :search, '%'))
            OR lower(book.description) LIKE lower(concat('%', :search, '%'))
            OR lower(topic.name) LIKE lower(concat('%', :search, '%'))
    """)
    List<Book> findBySearch(@Param("search") String search);
}

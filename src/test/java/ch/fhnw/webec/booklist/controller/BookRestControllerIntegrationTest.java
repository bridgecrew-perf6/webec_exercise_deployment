package ch.fhnw.webec.booklist.controller;

import ch.fhnw.webec.booklist.model.Book;
import ch.fhnw.webec.booklist.model.Topic;
import ch.fhnw.webec.booklist.repository.BookRepository;
import ch.fhnw.webec.booklist.repository.ReviewRepository;
import ch.fhnw.webec.booklist.repository.TopicRepository;
import ch.fhnw.webec.booklist.service.ProfanityService;
import ch.fhnw.webec.booklist.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
public class BookRestControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private TopicRepository topicRepository;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private ProfanityService profanityService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private JacksonTester<Book> bookJacksonTester;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void testFindAllBooks() throws Exception {
        when(this.bookRepository.findAll()).thenReturn(Arrays.asList(
            new Book("Test book 1", "Test book 1 description")
        ));

        this.mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Test book 1"))
            .andExpect(jsonPath("$[0].description").value("Test book 1 description"));
    }

    @Test
    public void testFindBookById() throws Exception {
        when(this.bookRepository.findById(1)).thenReturn(
            Optional.of(new Book("Test book 1", "Test book 1 description"))
        );

        this.mockMvc.perform(get("/api/books/{id}", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test book 1"))
            .andExpect(jsonPath("$.description").value("Test book 1 description"));
    }

    @Test
    public void testAddBook() throws Exception {
        var book = new Book("Test book 1", "Test book 1 description");

        book.setTopics(Set.of(new Topic()));

        when(this.bookRepository.save(any())).thenReturn(book);

        this.mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.bookJacksonTester.write(book).getJson())
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(book.getTitle()))
            .andExpect(jsonPath("$.description").value(book.getDescription()));

        verify(this.bookRepository, times(1)).save(any());
    }

    @Test
    public void testAddInvalidBook() throws Exception {
        this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.bookJacksonTester.write(new Book()).getJson())
            )
            .andExpect(status().isBadRequest());

        verify(this.bookRepository, never()).save(any());
    }

    @Test
    public void testUpdateBook() throws Exception {
        var bookId = 1;
        var book = new Book("Test book 1", "Test book 1 description");
        var updatedBook = new Book("Test updated book", "Test updated book description");

        updatedBook.setTopics(Set.of(new Topic()));

        when(this.bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(this.bookRepository.save(any())).thenReturn(updatedBook);

        this.mockMvc.perform(put("/api/books/{id}", bookId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.bookJacksonTester.write(updatedBook).getJson())
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(updatedBook.getTitle()))
            .andExpect(jsonPath("$.description").value(updatedBook.getDescription()));
    }

    @Test
    public void testDeleteBook() throws Exception {
        var bookId = 1;
        var book = new Book("Test book 1", "Test book 1 description");

        when(this.bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        this.mockMvc.perform(delete("/api/books/{id}", bookId))
            .andExpect(status().isNoContent());

        verify(this.bookRepository, times(1)).deleteById(bookId);
    }
}

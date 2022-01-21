package ch.fhnw.webec.booklist.controller;

import ch.fhnw.webec.booklist.model.Book;
import ch.fhnw.webec.booklist.model.User;
import ch.fhnw.webec.booklist.repository.BookRepository;
import ch.fhnw.webec.booklist.repository.ReviewRepository;
import ch.fhnw.webec.booklist.repository.TopicRepository;
import ch.fhnw.webec.booklist.service.ProfanityService;
import ch.fhnw.webec.booklist.service.UserService;
import com.mitchellbosecke.pebble.boot.autoconfigure.PebbleAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@Import(PebbleAutoConfiguration.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {
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

    @Test
    public void testIndex() throws Exception {
        this.mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Booklist")));
    }

    @Test
    public void testIndexWithBooks() throws Exception {
        when(this.bookRepository.findBySearch("")).thenReturn(Arrays.asList(
            new Book("My Book Title", "Lorem ipsum"),
            new Book("My Second Book Title", "Lorem ipsum dolor sit amet")
        ));

        this.mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("My Book Title")))
            .andExpect(content().string(containsString("Lorem ipsum")))
            .andExpect(content().string(containsString("My Second Book Title")))
            .andExpect(content().string(containsString("Lorem ipsum dolor sit amet")));
    }

    @Test
    public void testIndexAbbreviateDescription() throws Exception {
        when(this.bookRepository.findBySearch("")).thenReturn(Arrays.asList(
            new Book("My Book Title", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. THE END.")
        ));

        this.mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("My Book Title")))
            .andExpect(content().string(containsString("Lorem ipsum")))
            .andExpect(content().string(not(containsString("THE END"))));
    }

    @Test
    public void testSearch() throws Exception {
        var search = "my test search";

        this.mockMvc.perform(get("/?search={search}", search))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(search)));

        verify(this.bookRepository, times(1)).findBySearch(search);
        verify(this.bookRepository, never()).findAll();
    }

    @Test
    public void testShowBook() throws Exception {
        var bookId = 1;
        var book = new Book("Test title", "Test description");

        when(this.bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        this.mockMvc.perform(get("/books/{id}/", bookId))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Test title")))
            .andExpect(content().string(containsString("Test description")));

        verify(this.bookRepository, times(1)).findById(bookId);
    }

    @Test
    public void testDeleteBook() throws Exception {
        var bookId = 1;
        var book = new Book("Test title", "Test description");

        when(this.bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        this.mockMvc.perform(
                post("/books/{id}/delete", bookId)
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection());

        verify(this.bookRepository, times(1)).findById(bookId);
        verify(this.bookRepository, times(1)).delete(book);
    }

    @Test
    public void testAddReview() throws Exception {
        var bookId = 1;
        var book = new Book("Test title", "Test description");

        when(this.userService.loadUserByUsername("user")).thenReturn(new User("user", "user", Set.of("ROLE_USER")));
        when(this.bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        this.mockMvc.perform(
                post("/books/{id}/reviews/add", bookId)
                    .with(csrf())
                    .with(user("user"))
                    .content("name=Jane+Doe&rating=4&comment=Pretty+solid.")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect(status().is3xxRedirection());

        verify(this.reviewRepository, times(1)).save(any());
    }

    @Test
    public void testAddReviewWithBadWords() throws Exception {
        var bookId = 1;
        var book = new Book("Test title", "Test description");

        when(this.bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(this.profanityService.containsBadWords("Darn good!")).thenReturn(true);

        this.mockMvc.perform(
                post("/books/{id}/reviews/add", bookId)
                    .with(csrf())
                    .with(user("user"))
                    .content("rating=5&comment=Darn+good!")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect(status().isOk());

        verify(this.reviewRepository, never()).save(any());
    }

    @Test
    public void testAddBookWithUser() throws Exception {
        this.mockMvc.perform(
                post("/books/add")
                    .with(csrf())
                    .with(user("user").roles("USER"))
            )
            .andExpect(status().isForbidden());
    }
}

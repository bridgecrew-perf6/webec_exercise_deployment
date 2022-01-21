package ch.fhnw.webec.booklist.controller;

import ch.fhnw.webec.booklist.model.Book;
import ch.fhnw.webec.booklist.model.Review;
import ch.fhnw.webec.booklist.model.User;
import ch.fhnw.webec.booklist.repository.BookRepository;
import ch.fhnw.webec.booklist.repository.ReviewRepository;
import ch.fhnw.webec.booklist.service.ProfanityService;
import ch.fhnw.webec.booklist.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.List;

@RequestMapping(path = "/api/books", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Tag(name = "Books API", description = "Provides CRUD support for book entities.")
public class BookRestController {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final ProfanityService profanityService;
    private final UserService userService;

    public BookRestController(BookRepository bookRepository, ReviewRepository reviewRepository, ProfanityService profanityService, UserService userService) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.profanityService = profanityService;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Book>> findAll() {
        return ResponseEntity.ok().body(this.bookRepository.findAll());
    }

    @ApiResponse(responseCode = "404", content = @Content)
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public ResponseEntity<Book> findById(@PathVariable int id) {
        var book = this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(book);
    }

    @ApiResponse(responseCode = "400", content = @Content, description = "The book in the request body contains validation errors.")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.bookRepository.save(book));
    }

    @ApiResponse(responseCode = "404", content = @Content)
    @ApiResponse(responseCode = "400", content = @Content, description = "The book in the request body contains validation errors.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @RequestMapping(path = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> updateBook(@PathVariable int id, @Valid @RequestBody Book book, BindingResult bindingResult) {
        if (!this.bookRepository.findById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        book.setId(id);

        return ResponseEntity.ok(this.bookRepository.save(book));
    }

    @ApiResponse(responseCode = "404", content = @Content)
    @ApiResponse(responseCode = "204", content = @Content)
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteBook(@PathVariable int id) {
        if (!this.bookRepository.findById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        this.bookRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "{id}/reviews", method = RequestMethod.GET)
    public ResponseEntity<List<Review>> findReviewsByBook(@PathVariable int id) {
        return ResponseEntity.ok().body(this.reviewRepository.findByBookId(id));
    }

    @ApiResponse(responseCode = "404", content = @Content)
    @ApiResponse(responseCode = "403", content = @Content)
    @ApiResponse(responseCode = "400", content = @Content, description = "The review in the request body contains validation errors.")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class)))
    @RequestMapping(path = "{id}/reviews", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Review> addBookReview(@PathVariable int id, @Valid @RequestBody Review review, BindingResult bindingResult, Authentication authentication) {
        var book = this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (this.profanityService.containsBadWords(review.getComment())) {
            bindingResult.addError(new FieldError("review", "comment", "Your comment contains bad words, please watch your language!"));
        }

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            review.setUser((User) this.userService.loadUserByUsername(authentication.getName()));
            review.setBook(book);

            return ResponseEntity.status(HttpStatus.CREATED).body(this.reviewRepository.save(review));
        }
    }
}

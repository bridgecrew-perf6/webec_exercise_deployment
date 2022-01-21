package ch.fhnw.webec.booklist.controller;

import ch.fhnw.webec.booklist.model.Book;
import ch.fhnw.webec.booklist.model.Review;
import ch.fhnw.webec.booklist.model.User;
import ch.fhnw.webec.booklist.repository.BookRepository;
import ch.fhnw.webec.booklist.repository.ReviewRepository;
import ch.fhnw.webec.booklist.repository.TopicRepository;
import ch.fhnw.webec.booklist.service.ProfanityService;
import ch.fhnw.webec.booklist.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@PreAuthorize("hasRole('ADMIN')")
@Controller
public class BookController {
    private final BookRepository bookRepository;
    private final TopicRepository topicRepository;
    private final ReviewRepository reviewRepository;
    private final ProfanityService profanityService;
    private final UserService userService;

    public BookController(
        BookRepository bookRepository,
        TopicRepository topicRepository,
        ReviewRepository reviewRepository,
        ProfanityService profanityService,
        UserService userService
    ) {
        this.bookRepository = bookRepository;
        this.topicRepository = topicRepository;
        this.reviewRepository = reviewRepository;
        this.profanityService = profanityService;
        this.userService = userService;
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String index(@RequestParam() Optional<String> search, Model model, HttpServletRequest r, HttpSession s) {
        model.addAttribute("search", search.orElse(""));
        model.addAttribute("books", this.bookRepository.findBySearch(search.orElse("")));

        return "book/index";
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "/books/{id}", method = RequestMethod.GET)
    public String showBook(@PathVariable int id, Model model) {
        model.addAttribute("book", this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return "book/show";
    }

    @RequestMapping(path = "/books/add", method = RequestMethod.GET)
    public String addBook(Model model) {
        model.addAttribute("topics", this.topicRepository.findAll());

        return "book/add-or-edit";
    }

    @RequestMapping(path = "/books/add", method = RequestMethod.POST)
    public String addBook(@Valid Book book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("topics", this.topicRepository.findAll());
            model.addAttribute("book", book);

            return "book/add-or-edit";
        } else {
            this.bookRepository.save(book);

            return "redirect:/books/" + book.getId();
        }
    }

    @RequestMapping(path = "/books/{id}/edit", method = RequestMethod.GET)
    public String editBook(@PathVariable int id, Model model) {
        model.addAttribute("topics", this.topicRepository.findAll());
        model.addAttribute("book", this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return "book/add-or-edit";
    }

    @RequestMapping(path = "/books/{id}/edit", method = RequestMethod.POST)
    public String editBook(@Valid Book book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("topics", this.topicRepository.findAll());
            model.addAttribute("book", book);

            return "book/add-or-edit";
        } else {
            this.bookRepository.save(book);

            return "redirect:/books/" + book.getId();
        }
    }

    @RequestMapping(path = "/books/{id}/delete", method = RequestMethod.POST)
    public String deleteBook(@PathVariable int id) {
        this.bookRepository.delete(this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return "redirect:/";
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @RequestMapping(path = "/books/{id}/reviews/add", method = RequestMethod.POST)
    public String addBookReview(@PathVariable int id, @Valid Review review, BindingResult bindingResult, Authentication authentication, Model model) {
        var book = this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (this.profanityService.containsBadWords(review.getComment())) {
            bindingResult.addError(new FieldError("review", "comment", "Your comment contains bad words, please watch your language!"));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            model.addAttribute("review", review);

            return "book/show";
        } else {
            review.setUser((User) this.userService.loadUserByUsername(authentication.getName()));
            review.setBook(book);
            this.reviewRepository.save(review);

            return "redirect:/books/" + book.getId();
        }
    }
}

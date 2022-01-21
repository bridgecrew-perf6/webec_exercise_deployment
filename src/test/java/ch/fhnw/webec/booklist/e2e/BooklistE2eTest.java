package ch.fhnw.webec.booklist.e2e;

import ch.fhnw.webec.booklist.e2e.page.IndexPage;
import ch.fhnw.webec.booklist.e2e.page.AddOrEditBookPage;
import ch.fhnw.webec.booklist.e2e.page.ShowBookPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(WebDriverConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooklistE2eTest {
    @LocalServerPort
    private int port;
    @Autowired
    private WebDriver webDriver;
    private IndexPage indexPage;

    @BeforeEach
    public void setUp() {
        this.indexPage = new IndexPage(this.webDriver, this.port);
    }

    @Test
    public void testSearchBookTitles() {
        this.indexPage.goToIndexPage();

        assertEquals(3, this.indexPage.getBookTitles().size());

        this.indexPage.doSearch("Test book 1");

        assertEquals(1, this.indexPage.getBookTitles().size());
        assertTrue(this.indexPage.getBookTitles().contains("Test book 1"));
    }

    @Test
    public void testSearchBookTopics() {
        this.indexPage.goToIndexPage();

        assertEquals(3, this.indexPage.getBookTitles().size());

        this.indexPage.doSearch("Test topic 1");

        assertEquals(2, this.indexPage.getBookTitles().size());
        assertTrue(this.indexPage.getBookTitles().contains("Test book 1"));
        assertTrue(this.indexPage.getBookTitles().contains("Test book 3"));
    }

    @Test
    public void testShowBook() {
        var showBookPage = this.indexPage.goToShowBookPage(1);

        assertEquals("Test book 1", showBookPage.getHeading());
        assertEquals("Test book 1 description", showBookPage.getDescription());
        assertTrue(showBookPage.getTopicNames().contains("test topic 1"));
        assertTrue(showBookPage.getTopicNames().contains("test topic 2"));
    }

    @Test
    @DirtiesContext
    public void testAddBook() {
        this.indexPage.doLogin("admin", "admin");
        var addBookPage = this.indexPage.goToAddBookPage();
        var abstractPage = addBookPage.addBook("Test title", "Test description", List.of("Test topic 1"));

        if (abstractPage instanceof ShowBookPage showBookPage) {
            assertEquals("Test title", showBookPage.getHeading());
            assertEquals("Test description", showBookPage.getDescription());
            assertTrue(showBookPage.getTopicNames().contains("test topic 1"));
        } else {
            fail();
        }
    }

    @Test
    public void testAddBookInvalid() {
        this.indexPage.doLogin("admin", "admin");
        var addBookPage = this.indexPage.goToAddBookPage();
        var abstractPage = addBookPage.addBook("Test title", "", List.of("Test topic 1"));

        if (abstractPage instanceof AddOrEditBookPage) {
            assertEquals( 1, addBookPage.getFieldErrors("book-description").size());
            assertTrue(addBookPage.getFieldErrors("book-description").contains("must not be empty"));
        } else {
            fail();
        }
    }

    @Test
    @DirtiesContext
    public void testEditBook() {
        this.indexPage.doLogin("admin", "admin");
        var showBookPage = this.indexPage.goToShowBookPage(1);

        assertEquals(showBookPage.getHeading(), "Test book 1");

        var editBookPage = showBookPage.goToEditBookPage(1);

        editBookPage.setTitle("Updated book 1 title");
        var abstractPage = editBookPage.submitForm();

        if (abstractPage instanceof ShowBookPage showBookPageEdited) {
            assertEquals("Updated book 1 title", showBookPageEdited.getHeading());
        } else {
            fail();
        }
    }

    @Test
    @DirtiesContext
    public void testDeleteBook() {
        if (this.webDriver instanceof HtmlUnitDriver) {
            return; // Confirm dialog not supported for HtmlUnitDriver
        }

        this.indexPage.doLogin("admin", "admin");
        var indexPage = this.indexPage.goToIndexPage();

        assertTrue(indexPage.getBookTitles().contains("Test book 1"));

        var showBookPage = indexPage.goToShowBookPage(1);

        assertEquals("Test book 1", showBookPage.getHeading());

        var abstractPage = showBookPage.deleteBook();

        if (abstractPage instanceof IndexPage indexPageEdited) {
            assertFalse(indexPageEdited.getBookTitles().contains("Test book 1"));
        } else {
            fail();
        }
    }

    @Test
    @DirtiesContext
    public void testAddReview() {
        this.indexPage.doLogin("admin", "admin");
        var showBookPage = this.indexPage.goToShowBookPage(1);

        assertEquals(0, showBookPage.getReviewComments().size());

        showBookPage.addReview(4, "Pretty solid");

        assertEquals(1, showBookPage.getReviewComments().size());
        assertEquals("Pretty solid", showBookPage.getReviewComments().get(0));
    }

    @AfterEach
    public void tearDown() {
        this.webDriver.quit();
    }
}

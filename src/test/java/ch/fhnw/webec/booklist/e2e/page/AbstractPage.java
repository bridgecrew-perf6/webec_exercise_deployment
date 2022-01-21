package ch.fhnw.webec.booklist.e2e.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

abstract public class AbstractPage {
    protected final WebDriver webDriver;
    protected final int port;

    public AbstractPage(WebDriver webDriver, int port) {
        this.webDriver = webDriver;
        this.port = port;

        PageFactory.initElements(webDriver, this);
    }

    public AbstractPage doLogin(String username, String password) {
        return this.goToLoginPage().doLogin(username, password);
    }

    public void doSearch(String search) {
        this.webDriver.navigate().to(this.getUriBuilder().queryParam("search", search).build().toString());
    }

    public AddOrEditBookPage goToAddBookPage() {
        this.webDriver.navigate().to(this.getUriBuilder().path("/books/add").build().toString());

        return new AddOrEditBookPage(this.webDriver, this.port);
    }

    public AddOrEditBookPage goToEditBookPage(int bookId) {
        this.webDriver.navigate().to(this.getUriBuilder().path("/books/" + bookId + "/edit").build().toString());

        return new AddOrEditBookPage(this.webDriver, this.port);
    }

    public IndexPage goToIndexPage() {
        this.webDriver.navigate().to(this.getUriBuilder().path("/").build().toString());

        return new IndexPage(this.webDriver, this.port);
    }

    public ShowBookPage goToShowBookPage(int bookId) {
        this.webDriver.navigate().to(this.getUriBuilder().path("/books/" + bookId).build().toString());

        return new ShowBookPage(this.webDriver, this.port);
    }

    public LoginPage goToLoginPage() {
        this.webDriver.navigate().to(this.getUriBuilder().path("/login").build().toString());

        return new LoginPage(this.webDriver, this.port);
    }

    public UriBuilder getUriBuilder() {
        return UriComponentsBuilder.fromUriString("http://localhost:%d/".formatted(this.port));
    }
}

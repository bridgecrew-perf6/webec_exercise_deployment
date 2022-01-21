package ch.fhnw.webec.booklist.e2e.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class IndexPage extends AbstractPage {
    @FindBy(css = ".book-list__heading")
    private List<WebElement> bookListHeadingElements;

    public IndexPage(WebDriver webDriver, int port) {
        super(webDriver, port);
    }

    public List<String> getBookTitles() {
        return this.bookListHeadingElements.stream().map(WebElement::getText).toList();
    }
}

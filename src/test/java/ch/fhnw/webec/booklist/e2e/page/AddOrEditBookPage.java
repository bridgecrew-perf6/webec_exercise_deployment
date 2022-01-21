package ch.fhnw.webec.booklist.e2e.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import java.util.List;

public class AddOrEditBookPage extends AbstractPage {
    @FindBy(id = "book-title")
    private WebElement titleInputElement;

    @FindBy(id = "book-description")
    private WebElement descriptionInputElement;

    @FindBy(id = "book-topics")
    private WebElement topicsInputElement;

    @FindBy(css = ".form [type=\"submit\"]")
    private WebElement submitButtonElement;

    public AddOrEditBookPage(WebDriver webDriver, int port) {
        super(webDriver, port);
    }

    public AbstractPage addBook(String title, String description, List<String> topics) {
        this.setTitle(title);
        this.setDescription(description);
        this.setTopics(topics);

        return this.submitForm();
    }

    public void setTitle(String title) {
        this.titleInputElement.clear();
        this.titleInputElement.sendKeys(title);
    }

    public void setDescription(String description) {
        this.descriptionInputElement.clear();
        this.descriptionInputElement.sendKeys(description);
    }

    public void setTopics(List<String> topics) {
        var topicsSelect = new Select(this.topicsInputElement);

        topicsSelect.deselectAll();

        for (var topic : topics) {
            topicsSelect.selectByVisibleText(topic);
        }
    }

    public AbstractPage submitForm() {
        this.submitButtonElement.click();

        if (this.webDriver.getCurrentUrl().contains("/books/add")) {
            return this;
        } else {
            return new ShowBookPage(this.webDriver, this.port);
        }
    }

    public String getTitle() {
        return this.titleInputElement.getText();
    }

    public String getDescription() {
        return this.descriptionInputElement.getText();
    }

    public List<String> getFieldErrors(String fieldName) {
        return this.webDriver.findElements(
            By.cssSelector(".form__field-" + fieldName + " .form__error")
        ).stream().map(WebElement::getText).toList();
    }
}



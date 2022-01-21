package ch.fhnw.webec.booklist.e2e.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ShowBookPage extends AbstractPage {
    @FindBy(css = "h1")
    private WebElement headingElement;

    @FindBy(css = ".book-detail__description")
    private WebElement descriptionElement;

    @FindBy(css = "[value=\"Delete\"]")
    private WebElement deleteButtonElement;

    @FindBy(css = ".topic-list__item")
    private List<WebElement> topicItemElements;

    @FindBy(id = "review-name")
    private WebElement nameInputElement;

    @FindBy(id = "review-rating")
    private WebElement ratingInputElement;

    @FindBy(id = "review-comment")
    private WebElement commentInputElement;

    @FindBy(css = ".reviews__add-form [type=\"submit\"]")
    private WebElement submitButtonElement;

    @FindBy(className = "review")
    private List<WebElement> reviewElements;

    public ShowBookPage(WebDriver webDriver, int port) {
        super(webDriver, port);
    }

    public String getHeading() {
        return this.headingElement.getText();
    }

    public String getDescription() {
        return this.descriptionElement.getText();
    }

    public List<String> getTopicNames() {
        return this.topicItemElements.stream().map(WebElement::getText).toList();
    }

    public AbstractPage deleteBook() {
        this.deleteButtonElement.click();
        this.webDriver.switchTo().alert().accept();

        if (this.webDriver.getCurrentUrl().contains("/books")) {
            return this;
        } else {
            return new IndexPage(this.webDriver, this.port);
        }
    }

    public ShowBookPage addReview(int rating, String comment) {
        this.setRating(rating);
        this.setComment(comment);

        return this.submitForm();
    }

    public void setRating(int rating) {
        var ratingSelect = new Select(this.ratingInputElement);

        ratingSelect.selectByValue("" + rating);
    }

    public void setComment(String comment) {
        this.commentInputElement.clear();
        this.commentInputElement.sendKeys(comment);
    }

    public ShowBookPage submitForm() {
        this.submitButtonElement.click();

        return this;
    }

    public List<String> getReviewComments() {
        return this.reviewElements.stream().map(
            reviewElement -> reviewElement.findElement(By.cssSelector(".review__comment")).getText()
        ).toList();
    }
}

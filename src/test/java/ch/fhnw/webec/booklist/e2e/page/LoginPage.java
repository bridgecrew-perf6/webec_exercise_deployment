package ch.fhnw.webec.booklist.e2e.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends AbstractPage {
    @FindBy(id = "user-username")
    private WebElement usernameInputElement;

    @FindBy(id = "user-password")
    private WebElement passwordInputElement;

    @FindBy(css = ".form [type=\"submit\"]")
    private WebElement submitButtonElement;

    public LoginPage(WebDriver webDriver, int port) {
        super(webDriver, port);
    }

    public AbstractPage doLogin(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);

        return this.submitForm();
    }

    public void setUsername(String username) {
        this.usernameInputElement.clear();
        this.usernameInputElement.sendKeys(username);
    }

    public void setPassword(String password) {
        this.passwordInputElement.clear();
        this.passwordInputElement.sendKeys(password);
    }

    public AbstractPage submitForm() {
        this.submitButtonElement.click();

        if (this.webDriver.getCurrentUrl().contains("/login")) {
            return this;
        } else {
            return new IndexPage(this.webDriver, this.port);
        }
    }
}

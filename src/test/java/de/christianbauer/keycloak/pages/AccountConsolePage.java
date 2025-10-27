package de.christianbauer.keycloak.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AccountConsolePage {
    private final WebDriver webDriver;
    private final String keycloakBaseUrl;
    private final WebDriverWait wait;

    @FindBy(css = "button[id='landingSignInButton']")
    private WebElement signInButton;

    @FindBy(linkText = "Register")
    private WebElement registerLink;

    public AccountConsolePage(WebDriver webDriver, String keycloakBaseUrl) {
        this.webDriver = webDriver;
        this.keycloakBaseUrl = keycloakBaseUrl;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        PageFactory.initElements(webDriver, this);
    }

    public AccountConsolePage open(String realm) {
        webDriver.navigate().to(keycloakBaseUrl + "/realms/" + realm + "/account/");
        return this;
    }

    public RegistrationPage goToRegistration() {
        WebElement registerLinkElement = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Register"))
        );
        registerLinkElement.click();
        return new RegistrationPage(webDriver);
    }
}

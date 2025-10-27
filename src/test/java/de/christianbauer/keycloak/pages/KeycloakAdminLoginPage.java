package de.christianbauer.keycloak.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class KeycloakAdminLoginPage  {
    private final WebDriver webDriver;
    private final String keycloakBaseUrl;

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "kc-login")
    private WebElement loginButton;

    public KeycloakAdminLoginPage(WebDriver webDriver, String keycloakBaseUrl) {
        this.webDriver = webDriver;
        this.keycloakBaseUrl = keycloakBaseUrl;
        PageFactory.initElements(webDriver, this);
    }

    public KeycloakAdminLoginPage open() {
        webDriver.navigate().to(keycloakBaseUrl + "/admin/");
        return this;
    }

    public KeycloakAdminConsolePage login(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.click();
        return new KeycloakAdminConsolePage(webDriver, keycloakBaseUrl);
    }
}

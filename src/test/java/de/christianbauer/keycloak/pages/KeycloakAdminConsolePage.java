package de.christianbauer.keycloak.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class KeycloakAdminConsolePage {
    private final WebDriver webDriver;
    private final String keycloakBaseUrl;
    private final WebDriverWait wait;

    @FindBy(css = "button[id='nav-toggle']")
    private WebElement navBarToggleButton;

    @FindBy(css = "a[href*='/users'], a[data-testid='users']")
    private WebElement usersMenuItem;

    public KeycloakAdminConsolePage(WebDriver webDriver, String keycloakBaseUrl) {
        this.webDriver = webDriver;
        this.keycloakBaseUrl = keycloakBaseUrl;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        PageFactory.initElements(webDriver, this);
    }

    public RealmUsersPage navigateToRealmUsers(String realmName) {
        webDriver.navigate().to(keycloakBaseUrl + "/admin/master/console/#/master/realms");

        WebElement realmOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), '" + realmName + "') or @data-testid='" + realmName + "']")
        ));
        realmOption.click();

        wait.until(ExpectedConditions.elementToBeClickable(navBarToggleButton));
        navBarToggleButton.click();

        wait.until(ExpectedConditions.elementToBeClickable(usersMenuItem));
        usersMenuItem.click();

        return new RealmUsersPage(webDriver);
    }
}

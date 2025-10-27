package de.christianbauer.keycloak.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RealmUsersPage {
    private final WebDriver webDriver;
    private final WebDriverWait wait;

    @FindBy(css = "input[type='search'], input[placeholder*='Search']")
    private WebElement searchInput;

    @FindBy(css = "button[type='submit'], button.pf-c-button")
    private WebElement searchButton;

    @FindBy(css = "button[id='nav-toggle']")
    private WebElement navBarToggleButton;

    public RealmUsersPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        PageFactory.initElements(webDriver, this);
    }

    public void searchAndOpenUser(String username) {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.clear();
        searchInput.sendKeys(username);

        // Click search button or press Enter
        searchInput.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.elementToBeClickable(navBarToggleButton));
        navBarToggleButton.click();

        // Wait for search results and click on the user
        WebElement userLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), '" + username + "')]")
        ));
        userLink.click();

    }
}

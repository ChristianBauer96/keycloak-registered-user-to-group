package de.christianbauer.keycloak.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UserDetailsPage {
    private final WebDriver webDriver;
    private final WebDriverWait wait;

    @FindBy(linkText = "Groups")
    private WebElement groupsTab;

    public UserDetailsPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        PageFactory.initElements(webDriver, this);
    }

    public UserGroupsPage navigateToGroups() {
        wait.until(ExpectedConditions.elementToBeClickable(groupsTab));
        groupsTab.click();
        return new UserGroupsPage(webDriver);
    }
}

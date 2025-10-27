package de.christianbauer.keycloak.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class UserGroupsPage {
    private final WebDriver webDriver;
    private final WebDriverWait wait;

    @FindBy(css = "table tbody tr")
    private List<WebElement> groupRows;

    public UserGroupsPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        PageFactory.initElements(webDriver, this);
    }

    public boolean isUserInGroup(String groupName) {
        List<String> groups = getAssignedGroups();
        return groups.stream().anyMatch(group -> group.contains(groupName));
    }

    private List<String> getAssignedGroups() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));

        return groupRows.stream()
                .map(this::mapToGroupName)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    private String mapToGroupName(WebElement row) {
        List<WebElement> cells = row.findElements(By.tagName("td"));
        if (containsGroupNameColumn(cells.size())) {
            return cells.get(1).getText();
        }
        return "";
    }

    private boolean containsGroupNameColumn(int cellSize) {
        return cellSize > 1;
    }
}

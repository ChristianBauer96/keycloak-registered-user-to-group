package de.christianbauer.keycloak.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class RegistrationPage {

    private final WebDriver webDriver;

    @FindBy(id = "firstName")
    private WebElement firstNameInput;

    @FindBy(id = "lastName")
    private WebElement lastNameInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "user.attributes.selectedGroup")
    private WebElement groupSelectionInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "password-confirm")
    private WebElement passwordConfirmInput;

    @FindBy(css = "input[type='submit']")
    private WebElement registerButton;

    public RegistrationPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    public RegistrationPage fillFirstName(String firstName) {
        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);
        return this;
    }

    public RegistrationPage fillLastName(String lastName) {
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);
        return this;
    }

    public RegistrationPage fillEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
        return this;
    }

    public RegistrationPage selectGroupByIndex(int index) {
        Select groupSelect = new Select(groupSelectionInput);
        groupSelect.selectByIndex(index);
        return this;
    }

    public RegistrationPage fillPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }

    public RegistrationPage fillPasswordConfirm(String passwordConfirm) {
        passwordConfirmInput.clear();
        passwordConfirmInput.sendKeys(passwordConfirm);
        return this;
    }

    public void submitRegistration() {
        registerButton.click();
    }

    public void register(String firstName, String lastName, String email, String password) {
        fillFirstName(firstName)
                .fillLastName(lastName)
                .fillEmail(email)
                .fillPassword(password)
                .fillPasswordConfirm(password)
                .submitRegistration();
    }

    public void register(String firstName, String lastName, String email, String password, int index) {
        fillFirstName(firstName)
                .fillLastName(lastName)
                .fillEmail(email)
                .selectGroupByIndex(index)
                .fillPassword(password)
                .fillPasswordConfirm(password)
                .submitRegistration();
    }
}

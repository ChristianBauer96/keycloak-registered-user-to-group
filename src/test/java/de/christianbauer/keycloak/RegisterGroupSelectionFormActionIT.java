package de.christianbauer.keycloak;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import de.christianbauer.keycloak.pages.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.VncRecordingContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class RegisterGroupSelectionFormActionIT {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterGroupSelectionFormActionIT.class);

    private static final Network DOCKER_NETWORK = Network.newNetwork();
    private static final String KEYCLOAK_BASE_URL = "http://keycloak:8080";
    private static final String CUSTOMER_USERNAME = "test@customer.com";
    private static final String PARTNER_USERNAME = "test@partner.com";

    @Container
    private static final KeycloakContainer KEYCLOAK_TEST_CONTAINER =
            KeycloakTestContainer.createKeycloakContainer(DOCKER_NETWORK, LOG);

    @Container
    private static final BrowserWebDriverContainer BROWSER = new BrowserWebDriverContainer<>()
            .withCapabilities(new ChromeOptions().addArguments("--no-sandbox", "--disable-dev-shm-usage"))
            .dependsOn(KEYCLOAK_TEST_CONTAINER)
            .withRecordingMode(
                    BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL,
                    new File("target"),
                    VncRecordingContainer.VncRecordingFormat.MP4)
            .withSharedMemorySize(512L * 1024L * 1024L)
            .withLogConsumer(new Slf4jLogConsumer(LOG).withSeparateOutputStreams())
            .withNetwork(DOCKER_NETWORK);

    private RemoteWebDriver webDriver;

    @BeforeAll
    public static void beforeAll() {
        LOG.info("Running tests with Image: {}", KeycloakTestContainer.getKeycloakImage());
    }

    @BeforeEach
    void before() {
        webDriver = setupDriver();
    }

    private static RemoteWebDriver setupDriver() {
        RemoteWebDriver driver = new RemoteWebDriver(
                BROWSER.getSeleniumAddress(),
                new ChromeOptions()
        );
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(90));
        return driver;
    }

    @Test
    void testCustomerRegistration() {
        AccountConsolePage accountConsolePage = accountConsolePage().open("test");
        RegistrationPage registrationPage = accountConsolePage.goToRegistration();
        registrationPage.register("Test", "User",
                CUSTOMER_USERNAME, "SecurePass123!");

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/account"));

        validateUserIsInGroup("customer", CUSTOMER_USERNAME);
    }

    private void validateUserIsInGroup(String groupName, String userName) {
        KeycloakAdminLoginPage adminLoginPage = getKeycloakAdminLoginPage();
        adminLoginPage.open();

        KeycloakAdminConsolePage adminConsole = adminLoginPage.login("admin", "admin");

        RealmUsersPage usersPage = adminConsole.navigateToRealmUsers("test");
        usersPage.searchAndOpenUser(userName);
        UserDetailsPage userDetailsPage = new UserDetailsPage(webDriver);

        UserGroupsPage userGroupsPage = userDetailsPage.navigateToGroups();

        assertTrue(userGroupsPage.isUserInGroup(groupName),
                "User should be assigned to group: " + groupName);
    }

    @Test
    void testPartnerRegistration() {
        AccountConsolePage accountConsolePage = accountConsolePage().open("test");
        RegistrationPage registrationPage = accountConsolePage.goToRegistration();
        registrationPage.register("Test", "User",
                PARTNER_USERNAME, "SecurePass123!", 1);

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/account"));

        validateUserIsInGroup("partner", PARTNER_USERNAME);
    }

    private AccountConsolePage accountConsolePage() {
        return new AccountConsolePage(webDriver, KEYCLOAK_BASE_URL);
    }

    private KeycloakAdminLoginPage getKeycloakAdminLoginPage() {
        return new KeycloakAdminLoginPage(webDriver, KEYCLOAK_BASE_URL);
    }

    @AfterEach
    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
            webDriver = null;
        }
    }

    @AfterAll
    static void tearDownNetwork() {
        DOCKER_NETWORK.close();
    }
}

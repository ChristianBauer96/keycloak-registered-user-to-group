package de.christianbauer.keycloak;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.slf4j.Logger;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

public class KeycloakTestContainer {


    private static final String KEYCLOAK_ADMIN_USERNAME = "admin";
    private static final String KEYCLOAK_ADMIN_PASSWORD = "admin";
    private static final int KEYCLOAK_HTTP_PORT = 8080;
    private static final int KEYCLOAK_METRICS_PORT = 9000;
    private static final String KEYCLOAK_CONTAINER_NAME = "keycloak";
    private static final String PROPERTY_KEYCLOAK_VERSION = "keycloak.version";
    private static final String KEYCLOAK_LATEST_VERSION = "latest";
    private static final String KEYCLOAK_VERSION = System.getProperty(PROPERTY_KEYCLOAK_VERSION, KEYCLOAK_LATEST_VERSION);
    private static final String REALM_NAME = "/test-realm.json";
    private static final String PROVIDER_CLASSES_LOCATION = "target/classes";


    static KeycloakContainer createKeycloakContainer(Network network, Logger logger) {
        String imageName = getKeycloakImage();
        KeycloakContainer keycloakContainer = new KeycloakContainer(imageName);
        return keycloakContainer
                .withNetwork(network)
                .withRealmImportFile(REALM_NAME)
                .withExposedPorts(KEYCLOAK_HTTP_PORT, KEYCLOAK_METRICS_PORT)
                .withStartupTimeout(Duration.ofSeconds(90))
                .withNetworkAliases(KEYCLOAK_CONTAINER_NAME)
                .withAdminUsername(KEYCLOAK_ADMIN_USERNAME)
                .withAdminPassword(KEYCLOAK_ADMIN_PASSWORD)
                .withProviderClassesFrom(PROVIDER_CLASSES_LOCATION)
                .withLogConsumer(new Slf4jLogConsumer(logger).withSeparateOutputStreams())
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("test-themes.jar"),
                        "/opt/keycloak/providers/test-themes.jar"
                );
    }

    public static String getKeycloakImage() {
        return "quay.io/keycloak/keycloak:" + KEYCLOAK_VERSION;
    }
}

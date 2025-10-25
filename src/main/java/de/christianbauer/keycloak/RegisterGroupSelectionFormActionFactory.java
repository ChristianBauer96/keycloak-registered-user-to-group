package de.christianbauer.keycloak;

import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

import static de.christianbauer.keycloak.RegistrationGroupSelectionConstants.CONFIG_GROUP_MAPPINGS;

public class RegisterGroupSelectionFormActionFactory implements FormActionFactory {

    private static List<ProviderConfigProperty> configProperties;

    static {
        configProperties = new ArrayList<>();

        ProviderConfigProperty groupsList = new ProviderConfigProperty();
        groupsList.setName(CONFIG_GROUP_MAPPINGS);
        groupsList.setLabel("Available Groups for registration.");
        groupsList.setType(ProviderConfigProperty.STRING_TYPE);
        groupsList.setHelpText("Format: translationKey1:groupName1,translationKey2:groupName2 " +
                "(e.g., 'group.customer:customers,group.partner:partners')");
        configProperties.add(groupsList);
    }

    @Override
    public String getDisplayType() {
        return "Group Selection";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Allows users to select a group during registration";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public FormAction create(KeycloakSession keycloakSession) {
        return new RegisterGroupSelectionFormAction();
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return RegistrationGroupSelectionConstants.PROVIDER_ID;
    }
}
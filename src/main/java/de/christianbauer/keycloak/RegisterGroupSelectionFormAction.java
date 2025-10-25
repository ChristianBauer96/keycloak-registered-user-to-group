package de.christianbauer.keycloak;

import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.christianbauer.keycloak.RegistrationGroupSelectionConstants.*;

public class RegisterGroupSelectionFormAction implements FormAction {

    private final Logger LOG = LoggerFactory.getLogger(RegisterGroupSelectionFormAction.class);

    @Override
    public void buildPage(FormContext formContext, LoginFormsProvider loginFormsProvider) {
        AuthenticatorConfigModel config = formContext.getAuthenticatorConfig();
        Map<String, String> availableGroups = new LinkedHashMap<>();
        if (formContextConfigExists(config)) {
            String configuredGroupMappings = config.getConfig().get(CONFIG_GROUP_MAPPINGS);

            if (configuredGroupMappingsExists(configuredGroupMappings)) {
                Arrays.stream(configuredGroupMappings.split(GROUPS_CHOICE_DELIMITER))
                        .forEach(groupMapping ->
                                collectTranslationKeyAndGroupName(groupMapping, availableGroups));
            }
        }

        loginFormsProvider.setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups);
    }

    private boolean formContextConfigExists(AuthenticatorConfigModel config) {
        return config != null && config.getConfig() != null;
    }

    private boolean configuredGroupMappingsExists(String configuredGroupMappings) {
        return configuredGroupMappings != null && !configuredGroupMappings.isEmpty();
    }

    private void collectTranslationKeyAndGroupName(String groupMapping, Map<String, String> availableGroups) {
        String[] parts = groupMapping.trim().split(GROUP_NAME_TRANSLATION_KEY_DELIMITER);
        if (isValidGroupMappingsFormat(parts)) {
            String translationKey = parts[0].trim();
            String groupName = parts[1].trim();
            availableGroups.put(translationKey, groupName);
        }
    }

    private boolean isValidGroupMappingsFormat(String[] groupMappingParts) {
        return groupMappingParts.length == 2;
    }

    @Override
    public void validate(ValidationContext validationContext) {
        MultivaluedMap<String, String> formData = validationContext.getHttpRequest().getDecodedFormParameters();

        String selectedGroup = formData.getFirst(FIELD_GROUP_SELECTION);
        List<FormMessage> errors = validateSelectedGroup(selectedGroup, validationContext.getAuthenticatorConfig());

        if (!errors.isEmpty()) {
            LOG.warn("Register group selection form validation failed with {} errors", errors.size());
            validationContext.error(Errors.INVALID_REGISTRATION);
            validationContext.validationError(formData, errors);
            return;
        }

        validationContext.success();
    }

    private List<FormMessage> validateSelectedGroup(String selectedGroup,
                                                    AuthenticatorConfigModel config) {
        List<FormMessage> errors = new ArrayList<>();

        if (Validation.isBlank(selectedGroup)) {
            LOG.warn("No group for registration selected: {}", selectedGroup);
            errors.add(new FormMessage(FIELD_GROUP_SELECTION, "Please select a group"));
        } else {
            if (formContextConfigExists(config)) {
                String configuredGroupMappings = config.getConfig().get(CONFIG_GROUP_MAPPINGS);
                boolean validGroup = existSelectedGroupInMappings(configuredGroupMappings, selectedGroup);

                if (!validGroup) {
                    LOG.warn("No valid group for registration selected: {}", selectedGroup);
                    errors.add(new FormMessage(FIELD_GROUP_SELECTION, "group.selection.invalid"));
                }
            }
        }
        return errors;
    }
    private boolean existSelectedGroupInMappings(String configuredGroupMappings, String selectedGroup) {
        boolean validGroup = false;

        if (configuredGroupMappingsExists(configuredGroupMappings)) {
            String[] mappings = configuredGroupMappings.split(GROUPS_CHOICE_DELIMITER);
            for (String mapping : mappings) {
                String[] parts = mapping.trim().split(GROUP_NAME_TRANSLATION_KEY_DELIMITER);
                if (isSelectedGroup(selectedGroup, parts)) {
                    validGroup = true;
                    LOG.trace("Valid group for registration selected: {}", selectedGroup);
                    break;
                }
            }
        }

        return validGroup;
    }

    private boolean isSelectedGroup(String selectedGroup, String[] parts) {
        return parts.length == 2 && parts[1].trim().equals(selectedGroup);
    }

    @Override
    public void success(FormContext formContext) {
        MultivaluedMap<String, String> formData = formContext.getHttpRequest().getDecodedFormParameters();
        String selectedGroup = formData.getFirst(FIELD_GROUP_SELECTION);
        UserModel user = formContext.getUser();
        if (groupWasSelected(selectedGroup)) {
            Optional<GroupModel> group = formContext.getRealm()
                    .getGroupsStream()
                    .filter(groupModel -> foundGroup(selectedGroup, groupModel))
                    .findFirst();
            group.ifPresent(user::joinGroup);
        }
    }

    private boolean groupWasSelected(String selectedGroup) {
        return selectedGroup != null && !selectedGroup.isEmpty();
    }

    private boolean foundGroup(String selectedGroup, GroupModel groupModel) {
        return groupModel.getName().equals(selectedGroup);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }
}

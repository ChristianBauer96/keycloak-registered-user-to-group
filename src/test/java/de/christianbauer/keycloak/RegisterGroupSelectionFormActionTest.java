package de.christianbauer.keycloak;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.utils.FormMessage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static de.christianbauer.keycloak.RegistrationGroupSelectionConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterGroupSelectionFormActionTest {

    @InjectMocks
    private RegisterGroupSelectionFormAction registerGroupSelectionFormAction;

    @Mock
    private FormContext formContext;

    @Mock
    private ValidationContext validationContext;

    @Mock
    private LoginFormsProvider loginFormsProvider;

    @Mock
    private AuthenticatorConfigModel authenticatorConfigModel;

    @Mock
    private HttpRequest httpRequest;

    private Map<String, String> formContextMap;
    private String groupChoices;
    private Map<String, String> availableGroups;
    private MultivaluedMap<String, String> decodedFormData;
    private String selectedGroup;
    private List<FormMessage> errors;

    @BeforeEach
    void before() {
        formContextMap = new HashMap<>();
        groupChoices = "group.customer:customers,group.partner:partners";

        availableGroups = new HashMap<>();

        decodedFormData = new MultivaluedHashMap<>();
        selectedGroup = "partners";

        errors = new ArrayList<>();
    }

    @Test
    void testBuildPage() {
        formContextMap.put(CONFIG_GROUP_MAPPINGS, groupChoices);

        availableGroups.put("group.customer", "customers");
        availableGroups.put("group.partner", "partners");

        when(formContext.getAuthenticatorConfig()).thenReturn(authenticatorConfigModel);
        when(authenticatorConfigModel.getConfig()).thenReturn(formContextMap);

        when(loginFormsProvider.setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups))
                .thenReturn(loginFormsProvider);
        registerGroupSelectionFormAction.buildPage(formContext, loginFormsProvider);

        verify(loginFormsProvider).setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups);
    }

    @ParameterizedTest
    @CsvSource({
            "group:customer:customers",
            "group.customer.customers"
    })
    void testBuildPage_invalidChoices(String choices) {
        formContextMap.put(CONFIG_GROUP_MAPPINGS, choices);
        when(formContext.getAuthenticatorConfig()).thenReturn(authenticatorConfigModel);
        when(authenticatorConfigModel.getConfig()).thenReturn(formContextMap);

        when(loginFormsProvider.setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups))
                .thenReturn(loginFormsProvider);
        registerGroupSelectionFormAction.buildPage(formContext, loginFormsProvider);

        verify(loginFormsProvider).setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups);
    }

    @Test
    void testBuildPage_noFormContext() {
        when(formContext.getAuthenticatorConfig()).thenReturn(null);

        when(loginFormsProvider.setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups))
                .thenReturn(loginFormsProvider);
        registerGroupSelectionFormAction.buildPage(formContext, loginFormsProvider);

        verify(loginFormsProvider).setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups);
    }

    @Test
    void testBuildPage_availableGroupsNotSet() {
        when(formContext.getAuthenticatorConfig()).thenReturn(authenticatorConfigModel);
        when(authenticatorConfigModel.getConfig()).thenReturn(null);

        when(loginFormsProvider.setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups))
                .thenReturn(loginFormsProvider);
        registerGroupSelectionFormAction.buildPage(formContext, loginFormsProvider);

        verify(loginFormsProvider).setAttribute(AVAILABLE_GROUPS_ATTRIBUTE_NAME, availableGroups);
    }

    @Test
    void testValidate() {
        formContextMap.put(CONFIG_GROUP_MAPPINGS, groupChoices);

        when(validationContext.getHttpRequest()).thenReturn(httpRequest);
        when(httpRequest.getDecodedFormParameters()).thenReturn(decodedFormData);
        decodedFormData.put(FIELD_GROUP_SELECTION, Collections.singletonList(selectedGroup));

        when(validationContext.getAuthenticatorConfig()).thenReturn(authenticatorConfigModel);
        when(authenticatorConfigModel.getConfig()).thenReturn(formContextMap);

        doNothing().when(validationContext).success();

        registerGroupSelectionFormAction.validate(validationContext);

        verify(validationContext).success();
    }

    @Test
    void testValidate_selectedGroupIsBlank() {
        when(validationContext.getHttpRequest()).thenReturn(httpRequest);
        when(httpRequest.getDecodedFormParameters()).thenReturn(decodedFormData);

        decodedFormData.put(FIELD_GROUP_SELECTION, Collections.singletonList(""));
        doNothing().when(validationContext).error(Errors.INVALID_REGISTRATION);
        doNothing().when(validationContext).validationError(any(), anyList());
        when(validationContext.getAuthenticatorConfig()).thenReturn(null);

        registerGroupSelectionFormAction.validate(validationContext);
        verify(validationContext).error(Errors.INVALID_REGISTRATION);
        verify(validationContext).validationError(
                eq(decodedFormData),
                argThat(errorList ->
                        errorList.size() == 1 &&
                                FIELD_GROUP_SELECTION.equals(errorList.get(0).getField())&&
                                "group.selection.required".equals(errorList.get(0).getMessage())
                )
        );
    }

    @Test
    void testValidate_noGroupMappings() {
        when(validationContext.getHttpRequest()).thenReturn(httpRequest);
        when(httpRequest.getDecodedFormParameters()).thenReturn(decodedFormData);
        decodedFormData.put(FIELD_GROUP_SELECTION, Collections.singletonList(selectedGroup));

        when(validationContext.getAuthenticatorConfig()).thenReturn(authenticatorConfigModel);
        when(authenticatorConfigModel.getConfig()).thenReturn(formContextMap);

        registerGroupSelectionFormAction.validate(validationContext);
        verify(validationContext).error(Errors.INVALID_REGISTRATION);
        verify(validationContext).validationError(
                eq(decodedFormData),
                argThat(errorList ->
                        errorList.size() == 1 &&
                                FIELD_GROUP_SELECTION.equals(errorList.get(0).getField())&&
                                "group.selection.invalid".equals(errorList.get(0).getMessage())
                )
        );
    }

    @Test
    void testValidate_invalidSelectedGroup() {
        selectedGroup = "invalidGroup";
        formContextMap.put(CONFIG_GROUP_MAPPINGS, groupChoices);

        when(validationContext.getHttpRequest()).thenReturn(httpRequest);
        when(httpRequest.getDecodedFormParameters()).thenReturn(decodedFormData);
        decodedFormData.put(FIELD_GROUP_SELECTION, Collections.singletonList(selectedGroup));

        when(validationContext.getAuthenticatorConfig()).thenReturn(authenticatorConfigModel);
        when(authenticatorConfigModel.getConfig()).thenReturn(formContextMap);

        registerGroupSelectionFormAction.validate(validationContext);

        verify(validationContext).error(Errors.INVALID_REGISTRATION);
        verify(validationContext).validationError(
                eq(decodedFormData),
                argThat(errorList ->
                        errorList.size() == 1 &&
                                FIELD_GROUP_SELECTION.equals(errorList.get(0).getField())&&
                                "group.selection.invalid".equals(errorList.get(0).getMessage())
                )
        );    }
}

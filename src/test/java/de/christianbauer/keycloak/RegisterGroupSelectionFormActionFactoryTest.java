package de.christianbauer.keycloak;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static de.christianbauer.keycloak.RegistrationGroupSelectionConstants.CONFIG_GROUP_MAPPINGS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class RegisterGroupSelectionFormActionFactoryTest {

    @InjectMocks
    private RegisterGroupSelectionFormActionFactory registerGroupSelectionFormActionFactory;

    @Test
    void testGetDisplayType() {
        String result = registerGroupSelectionFormActionFactory.getDisplayType();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("Group Selection");
    }

    @Test
    void testGetHelpText() {
        String result = registerGroupSelectionFormActionFactory.getHelpText();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("Allows users to select a group during registration");
    }

    @Test
    void testGetRequirementChoices() {
        AuthenticationExecutionModel.Requirement[] result = registerGroupSelectionFormActionFactory.getRequirementChoices();
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(new AuthenticationExecutionModel.Requirement[]{
                        AuthenticationExecutionModel.Requirement.REQUIRED,
                        AuthenticationExecutionModel.Requirement.DISABLED
                });
    }

    @Test
    void testGetConfigProperties() {
        List<ProviderConfigProperty> result = registerGroupSelectionFormActionFactory.getConfigProperties();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst()).isNotNull();
        assertThat(result.getFirst().getName()).isEqualTo(CONFIG_GROUP_MAPPINGS);
        assertThat(result.getFirst().getLabel()).isEqualTo("Available Groups for registration.");
        assertThat(result.getFirst().getType()).isEqualTo(ProviderConfigProperty.STRING_TYPE);
        assertThat(result.getFirst().getHelpText())
                .isEqualTo("Format: translationKey1:groupName1,translationKey2:groupName2" +
                        " (e.g., 'group.customer:customers,group.partner:partners')");
    }

    @Test
    void testGetId() {
        String result = registerGroupSelectionFormActionFactory.getId();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(RegistrationGroupSelectionConstants.PROVIDER_ID);
    }
}

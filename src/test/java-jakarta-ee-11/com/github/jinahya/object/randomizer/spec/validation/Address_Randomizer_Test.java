package com.github.jinahya.object.randomizer.spec.validation;

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that a randomized {@link Address} satisfies the constraints the Jakarta Validation specification declares on
 * it.
 * <p>
 * This is what the {@code jakarta-ee-NN} profiles exist for: the {@link Address} under test is the one the Jakarta
 * Validation 3.1 specification publishes, and the validator which judges it is that generation's reference
 * implementation. This class is declared under {@code src/test/java-jakarta-ee-11}, with the rest of that
 * specification's classes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Address_Randomizer_Test {

    /**
     * The number of instances each test draws; more than one, so that a lucky draw is not mistaken for an engine which
     * reads the constraints.
     */
    private static final int DRAWS = 32;

    private static ValidatorFactory factory;

    @BeforeAll
    static void openValidatorFactory() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }

    @DisplayName("get() -> an instance which satisfies the specification's constraints")
    @Test
    void get_Valid_() {
        final var validator = factory.getValidator();
        final var randomizer = new Address_Randomizer();
        for (int i = 0; i < DRAWS; i++) {
            final var instance = randomizer.get();
            assertThat(instance).isNotNull();
            assertThat(validator.validate(instance)).as("violations of draw #%d: %s", i, instance).isEmpty();
        }
    }

    @DisplayName("the randomizer is located for Address by the naming convention")
    @Test
    void locatedByTheConvention_() {
        assertThat(ObjectRandomizerUtils.newRandomizerInstanceOf(Address.class))
                .get()
                .isInstanceOf(Address_Randomizer.class);
    }

    /**
     * The one test which proves the {@code src/test/java-jakarta-ee-NN} source roots are wired up: the specification
     * version compiled into {@link Address} has to be the one the active profile selected, which surefire hands in as a
     * system property.
     */
    @DisplayName("the Address under test is the one of the active profile's specification")
    @Test
    void specificationVersion_OfTheActiveProfile_() {
        final var expected = System.getProperty("jakarta.validation.spec.version");
        assertThat(expected).as("the property the active jakarta-ee-NN profile sets").isNotNull();
        assertThat(Address.SPECIFICATION_VERSION)
                .as("the Address compiled from src/test/java-jakarta-ee-NN")
                .isEqualTo(expected);
    }
}

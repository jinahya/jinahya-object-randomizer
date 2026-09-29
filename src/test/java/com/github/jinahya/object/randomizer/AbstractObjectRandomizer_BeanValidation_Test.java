package com.github.jinahya.object.randomizer;

import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.instancio.settings.Keys;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests which flavors honor {@code jakarta.validation.constraints} on a target class, and how each is turned on.
 * <p>
 * The API alone is what an engine reads the constraints through; the reference implementation, which these tests build
 * a {@link Validator} from, is what decides whether the instance an engine produced actually satisfies them. Both, and
 * the platform generation they belong to, are chosen by the active {@code jakarta-ee-NN} profile, so this class is what
 * that profile exists to exercise.
 * <p>
 * {@link EasyRandomObjectRandomizer} is deliberately absent: Easy Random 6 removed its constraint support outright, so
 * there is no configuration under which it would pass, and asserting that it <em>fails</em> would be asserting on a
 * random draw.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class AbstractObjectRandomizer_BeanValidation_Test {

    /**
     * A target class carrying the constraints which are verified here.
     *
     * @implNote Accessors are declared for {@link PodamObjectRandomizer}, which writes through a setter and
     *         never assigns a field; the constraints themselves are declared on the <em>fields</em>, which is where
     *         every one of the three engines looks.
     */
    public static class Constrained {

        @Size(min = 4, max = 8)
        private String name;

        @Email
        private String email;

        @Min(10)
        @Max(20)
        private int rank;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(final String email) {
            this.email = email;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(final int rank) {
            this.rank = rank;
        }
    }

    /**
     * The flavor which honors the constraints with no configuration at all.
     */
    static class ConstrainedPodamRandomizer
            extends PodamObjectRandomizer<Constrained> {

        ConstrainedPodamRandomizer() {
            super(Constrained.class, List.of());
        }
    }

    /**
     * Instancio honors the constraints because {@link InstancioObjectRandomizer} sets
     * {@link Keys#BEAN_VALIDATION_ENABLED} in its default settings. Nothing here has to be overridden.
     */
    static class ConstrainedInstancioRandomizer
            extends InstancioObjectRandomizer<Constrained> {

        ConstrainedInstancioRandomizer() {
            super(Constrained.class, List.of());
        }
    }

    /**
     * Fixture Monkey honors the constraints because {@code fixture-monkey-jakarta-validation} is on the test classpath,
     * which {@link FixtureMonkeyObjectRandomizer} detects and registers {@link JakartaValidationPlugin} for. Nothing
     * here has to be overridden.
     */
    static class ConstrainedFixtureMonkeyRandomizer
            extends FixtureMonkeyObjectRandomizer<Constrained> {

        ConstrainedFixtureMonkeyRandomizer() {
            super(Constrained.class, List.of());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static ValidatorFactory factory;

    @BeforeAll
    static void openValidatorFactory() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }

    /**
     * The number of instances {@link #assertValid(ObjectRandomizer)} draws.
     *
     * @implNote More than one, deliberately: a single draw which happens to satisfy a constraint proves nothing
     *         about an engine which does not read it. A {@code @Min(10) @Max(20) int}, left unconstrained, lands in
     *         range about five times in a billion, so a handful of draws separates honoring a constraint from being
     *         lucky.
     */
    private static final int DRAWS = 32;

    /**
     * Asserts that every instance the specified randomizer draws violates none of the constraints of its class.
     *
     * @param randomizer the randomizer to draw from.
     */
    private static void assertValid(final ObjectRandomizer<Constrained> randomizer) {
        final var validator = factory.getValidator();
        for (int i = 0; i < DRAWS; i++) {
            final var instance = randomizer.get();
            assertThat(instance).isNotNull();
            assertThat(validator.validate(instance))
                    .as("violations of draw #%d: name=%s, email=%s, rank=%d",
                        i, instance.getName(), instance.getEmail(), instance.getRank())
                    .isEmpty();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("PodamObjectRandomizer.get() -> an instance which satisfies its constraints, unconfigured")
    @Test
    void get_Valid_OfPodam() {
        assertValid(new ConstrainedPodamRandomizer());
    }

    @DisplayName("InstancioObjectRandomizer.get() -> an instance which satisfies its constraints, once enabled")
    @Test
    void get_Valid_OfInstancioWithBeanValidationEnabled() {
        assertValid(new ConstrainedInstancioRandomizer());
    }

    @DisplayName("FixtureMonkeyObjectRandomizer.get() -> an instance which satisfies its constraints, with the plugin")
    @Test
    void get_Valid_OfFixtureMonkeyWithTheJakartaValidationPlugin() {
        assertValid(new ConstrainedFixtureMonkeyRandomizer());
    }
}

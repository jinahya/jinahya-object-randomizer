package com.github.jinahya.object.randomizer;

import jakarta.validation.spi.ValidationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the Jakarta Validation provider on the test classpath is the one the active profile selected.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class _Validation_Provider_Test {

    /**
     * The one test which proves the provider axis of the {@code jakarta-ee-NN-PROVIDER} profiles is wired up. The
     * providers are listed the way the specification's default resolver discovers them, as services of
     * {@link ValidationProvider}; exactly one has to be found, and it has to be the one the active profile names, which
     * surefire hands in as a system property. With two found, which one
     * {@code Validation.buildDefaultValidatorFactory()} picks is not decided by the specification, so a second one is a
     * failure, not a curiosity.
     */
    @DisplayName("the one validation provider found is the one of the active profile")
    @Test
    void provider_OfTheActiveProfile_() {
        final var expected = System.getProperty("jakarta.validation.provider");
        assertThat(expected).as("the property the active jakarta-ee-NN-PROVIDER profile sets").isNotNull();
        assertThat(ServiceLoader.load(ValidationProvider.class).stream().map(ServiceLoader.Provider::type))
                .as("the validation providers on the test classpath")
                .singleElement()
                .extracting(Class::getName)
                .isEqualTo(expected);
    }
}

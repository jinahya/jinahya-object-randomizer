package com.github.jinahya.object.randomizer.spec.validation.address;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the {@link Address} itself, rather than anything which randomizes it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Address_Test {

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

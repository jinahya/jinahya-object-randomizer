package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the {@link FrenchZipCode} itself, rather than anything which randomizes an {@link Address} carrying it;
 * that both of its composing constraints are applied, and that what {@link FrenchZipCodes} writes is what they accept.
 * <p>
 * This is what gives a randomizer's green result its meaning: an engine which satisfied nothing and a constraint the
 * validator never reached would look alike from the outside.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class FrenchZipCode_Test {

    private static ValidatorFactory factory;

    @BeforeAll
    static void openValidatorFactory() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }

    @DisplayName("the composing @Pattern and @Size of the composed constraint are both applied")
    @Test
    void validate_AsComposed_() {
        final var validator = factory.getValidator();
        final var address = new Address();
        address.setZipCode("75008");
        assertThat(validator.validate(address)).as("five digits").isEmpty();
        address.setZipCode("7500");
        assertThat(validator.validate(address)).as("four digits, so the composing @Size fails").isNotEmpty();
        address.setZipCode("ABCDE");
        assertThat(validator.validate(address)).as("five letters, so the composing @Pattern fails").isNotEmpty();
        address.setZipCode(null);
        assertThat(validator.validate(address)).as("null, which both composing constraints hold for").isEmpty();
    }

    /**
     * The number of zip codes drawn; more than one, since the value is random and a short one, or one with a
     * character out of range, would otherwise come out only now and then.
     */
    private static final int DRAWS = 64;

    @DisplayName("what FrenchZipCodes writes is a zip code the composed constraint accepts")
    @Test
    void newZipCode_Accepted_() {
        final var validator = factory.getValidator();
        final var address = new Address();
        for (int i = 0; i < DRAWS; i++) {
            address.setZipCode(FrenchZipCodes.newZipCode());
            assertThat(address.getZipCode()).matches("[0-9]{5}");
            assertThat(validator.validate(address)).as("violations of draw #%d: %s", i, address).isEmpty();
        }
    }
}

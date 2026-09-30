package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the constraints on {@link Address} themselves, rather than anything which randomizes it, and that what
 * {@link ZipCodes} writes is what they accept.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Address_Test {

    /**
     * The number of zip codes drawn; more than one, since the value is random.
     */
    private static final int DRAWS = 64;

    private static ValidatorFactory factory;

    @BeforeAll
    static void openValidatorFactory() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }

    @DisplayName("the @Pattern and the @Size on the field are both applied")
    @Test
    void validate_AsDeclared_() {
        final var validator = factory.getValidator();
        final var address = new Address();
        address.setZipCode("75008");
        assertThat(validator.validate(address)).as("five digits").isEmpty();
        address.setZipCode("7500");
        assertThat(validator.validate(address)).as("four digits, so the @Size fails").isNotEmpty();
        address.setZipCode("ABCDE");
        assertThat(validator.validate(address)).as("five letters, so the @Pattern fails").isNotEmpty();
        address.setZipCode(null);
        assertThat(validator.validate(address)).as("null, which both constraints hold for").isEmpty();
    }

    @DisplayName("what ZipCodes writes is a zip code the constraints accept")
    @Test
    void newZipCode_Accepted_() {
        final var validator = factory.getValidator();
        final var address = new Address();
        for (int i = 0; i < DRAWS; i++) {
            address.setZipCode(ZipCodes.newZipCode());
            assertThat(address.getZipCode()).matches("[0-9]{5}");
            assertThat(validator.validate(address)).as("violations of draw #%d: %s", i, address).isEmpty();
        }
    }
}

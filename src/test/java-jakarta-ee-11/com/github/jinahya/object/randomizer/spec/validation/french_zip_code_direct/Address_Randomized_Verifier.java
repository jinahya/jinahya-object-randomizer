package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

import com.github.jinahya.object.randomizer.ObjectRandomizer;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifications of a <em>randomized</em> {@link Address} -- of the value, that is, not of whatever produced it.
 * <p>
 * The contract splits in two. {@link #verify(Address)} is what every flavor owes: the field is filled.
 * {@link #verifyValid(ObjectRandomizer)} adds that the value satisfies the constraints the specification declares.
 * <p>
 * Neither half stands alone. Both constraints on the field hold for {@code null}, so an engine which writes nothing
 * draws no violation; and an engine which writes without reading the pattern draws one on every attempt. The two
 * assertions tell those two failures apart from a pass.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomizer_Constants
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class Address_Randomized_Verifier {

    /**
     * The number of instances {@link #verifyValid(ObjectRandomizer)} draws.
     */
    private static final int DRAWS = 32;

    /**
     * Holds the validator factory, so that it is built once, and only when a test actually validates.
     */
    private static final class ValidatorHolder {

        private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

        private ValidatorHolder() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    private static Validator validator() {
        return ValidatorHolder.FACTORY.getValidator();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Verifies what every flavor owes: nothing is excluded, so the field is filled.
     *
     * @param value the randomized instance to verify.
     * @return the {@code value}, so that a caller may go on asserting something of its own.
     */
    public static Address verify(final Address value) {
        assertThat(value).isNotNull();
        assertThat(value.getZipCode()).as("excluded from nothing, and so filled").isNotNull();
        return value;
    }

    /**
     * Verifies, in addition to {@link #verify(Address)}, that every instance the specified randomizer draws satisfies
     * the constraints the specification declares.
     *
     * @param randomizer the randomizer to draw from.
     */
    public static void verifyValid(final ObjectRandomizer<Address> randomizer) {
        final var validator = validator();
        for (int i = 0; i < DRAWS; i++) {
            final var value = verify(randomizer.get());
            assertThat(validator.validate(value)).as("violations of draw #%d: %s", i, value).isEmpty();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Address_Randomized_Verifier() {
        throw new AssertionError("instantiation is not allowed");
    }
}

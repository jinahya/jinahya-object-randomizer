package com.github.jinahya.object.randomizer.spec.validation;

import com.github.jinahya.object.randomizer.ObjectRandomizer;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifications of a <em>randomized</em> {@link Address} -- of the value, that is, not of whatever produced it.
 * <p>
 * The contract splits in two, and the split is the whole point of randomizing a class the specification constrains.
 * {@link #verify(Address)} holds for all four engines: the fields are filled. {@link #verifyValid(ObjectRandomizer)}
 * does not -- it holds only for an engine which <em>reads</em> the constraints, and Easy Random 6 can not.
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
     *
     * @implNote More than one, deliberately: a single draw which happens to satisfy a constraint proves nothing
     *         about an engine which never read it.
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
     * Verifies what every flavor owes: nothing is excluded, so every field is filled.
     *
     * @param value the randomized instance to verify.
     * @return the {@code value}, so that a caller may go on asserting something of its own.
     */
    public static Address verify(final Address value) {
        assertThat(value).isNotNull();
        assertThat(value.getStreet1()).as("excluded from nothing, and so filled").isNotNull();
        assertThat(value.getZipCode()).as("excluded from nothing, and so filled").isNotNull();
        assertThat(value.getCity()).as("excluded from nothing, and so filled").isNotNull();
        return value;
    }

    /**
     * Verifies, in addition to {@link #verify(Address)}, that every instance the specified randomizer draws satisfies
     * the constraints the specification declares.
     *
     * @param randomizer the randomizer to draw from.
     * @apiNote Only for an engine which reads the constraints. {@code Address_Randomizer_EasyRandom} does not,
     *         and is not wrong to: Easy Random 6 removed its constraint support outright.
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

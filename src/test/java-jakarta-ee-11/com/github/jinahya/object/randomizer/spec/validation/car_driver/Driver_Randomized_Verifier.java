package com.github.jinahya.object.randomizer.spec.validation.car_driver;

import com.github.jinahya.object.randomizer.ObjectRandomizer;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifications of a <em>randomized</em> {@link Driver} -- of the value, that is, not of whatever produced it.
 * <p>
 * The contract splits in two. {@link #verify(Driver)} is what every flavor owes: the fields are filled, the
 * cascaded {@link Car} included. {@link #verifyValid(ObjectRandomizer)} adds that the value satisfies the
 * constraints the specification declares, and every flavor here owes that too.
 * <p>
 * The two halves are not independent here, which is why neither stands alone. {@code @AssertTrue} holds for
 * {@code null}, as a constraint which leaves the null check to {@code @NotNull} must, and the specification declares
 * no {@code @NotNull} on {@code passedDrivingTest}, on {@code roadWorthy}, or on {@code car}. An engine which filled
 * none of the three would draw no violation at all; it is {@link #verify(Driver)} which keeps that empty pass from
 * counting as constraint support.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Driver_Randomizer_Constants
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class Driver_Randomized_Verifier {

    /**
     * The number of instances {@link #verifyValid(ObjectRandomizer)} draws.
     *
     * @implNote More than one, deliberately: a single draw which happens to satisfy a constraint proves nothing
     *         about an engine which never read it. A boolean makes that plain -- an engine which ignores
     *         {@code @AssertTrue} still draws {@code true} half the time.
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
     * Verifies what every flavor owes: nothing is excluded, so every field is filled, in the cascaded {@link Car} as
     * well as in the {@link Driver} itself.
     *
     * @param value the randomized instance to verify.
     * @return the {@code value}, so that a caller may go on asserting something of its own.
     */
    public static Driver verify(final Driver value) {
        assertThat(value).isNotNull();
        assertThat(value.getPassedDrivingTest()).as("excluded from nothing, and so filled").isNotNull();
        assertThat(value.getCar()).as("cascaded into, and so filled").isNotNull();
        assertThat(value.getCar().getType()).as("excluded from nothing, and so filled").isNotNull();
        assertThat(value.getCar().getRoadWorthy()).as("excluded from nothing, and so filled").isNotNull();
        return value;
    }

    /**
     * Verifies, in addition to {@link #verify(Driver)}, that every instance the specified randomizer draws satisfies
     * the constraints the specification declares.
     *
     * @param randomizer the randomizer to draw from.
     * @apiNote Validating the default group is enough to reach all of them: {@link Driver} and {@link Car} each
     *         redefine their default group as a sequence, which is what carries the validation into {@link Minimal}
     *         and {@link Later}.
     */
    public static void verifyValid(final ObjectRandomizer<Driver> randomizer) {
        final var validator = validator();
        for (int i = 0; i < DRAWS; i++) {
            final var value = verify(randomizer.get());
            assertThat(validator.validate(value)).as("violations of draw #%d: %s", i, value).isEmpty();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Driver_Randomized_Verifier() {
        throw new AssertionError("instantiation is not allowed");
    }
}

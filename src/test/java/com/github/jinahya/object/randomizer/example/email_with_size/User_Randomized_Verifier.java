package com.github.jinahya.object.randomizer.example.email_with_size;

import com.github.jinahya.object.randomizer.ObjectRandomizer;

import static com.github.jinahya.object.randomizer._Validation_Test_Utils.assertValid;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifications of a <em>randomized</em> {@link User} -- of the value, that is, not of whatever produced it.
 * <p>
 * The contract splits in two. {@link #verify(User)} is what every flavor owes: the field is filled.
 * {@link #verifyValid(ObjectRandomizer)} adds that the value satisfies both constraints the field carries, and every
 * flavor here owes that too.
 * <p>
 * Neither half stands alone in this package, and that is the whole difficulty of it. Both constraints hold for
 * {@code null}, so an engine which writes nothing at all draws no violation; and an engine which writes a string of the
 * constrained length without reading the {@code @Email} draws a violation on every attempt. The two assertions separate
 * those two failures from a pass, and a flavor has to clear both.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see User_Randomizer_Constants
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class User_Randomized_Verifier {

    /**
     * The number of instances {@link #verifyValid(ObjectRandomizer)} draws.
     *
     * @implNote More than one, deliberately: a single draw which happens to satisfy a constraint proves nothing
     *         about an engine which never read it.
     */
    private static final int DRAWS = 32;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Verifies what every flavor owes: nothing is excluded, so the field is filled.
     *
     * @param value the randomized instance to verify.
     * @return the {@code value}, so that a caller may go on asserting something of its own.
     */
    public static User verify(final User value) {
        assertThat(value).isNotNull();
        assertThat(value.getEmail()).as("excluded from nothing, and so filled").isNotNull();
        return value;
    }

    /**
     * Verifies, in addition to {@link #verify(User)}, that every instance the specified randomizer draws satisfies both
     * constraints the {@code email} field carries.
     *
     * @param randomizer the randomizer to draw from.
     */
    public static void verifyValid(final ObjectRandomizer<User> randomizer) {
        for (int i = 0; i < DRAWS; i++) {
            assertValid(verify(randomizer.get()));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private User_Randomized_Verifier() {
        throw new AssertionError("instantiation is not allowed");
    }
}

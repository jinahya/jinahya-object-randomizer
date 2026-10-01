package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

import com.github.jinahya.object.randomizer.ObjectRandomizer;

import static com.github.jinahya.object.randomizer._Validation_Test_Utils.assertValid;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifications of a <em>randomized</em> {@link Address} -- of the value, that is, not of whatever produced it.
 * <p>
 * The contract splits in two. {@link #verify(Address)} is what every flavor owes: the field is filled.
 * {@link #verifyValid(ObjectRandomizer)} adds that the value satisfies the constraint the specification declares, and
 * every flavor here owes that too.
 * <p>
 * Neither half stands alone in this package, and that is the whole difficulty of it. Both composing constraints of
 * {@link FrenchZipCode} hold for {@code null}, so an engine which writes nothing at all draws no violation; and an
 * engine which writes a value it did not read the pattern for draws a violation on every attempt. The two assertions
 * separate those two failures from a pass, and an engine has to clear both.
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
     * the constraint the specification declares.
     *
     * @param randomizer the randomizer to draw from.
     */
    public static void verifyValid(final ObjectRandomizer<Address> randomizer) {
        for (int i = 0; i < DRAWS; i++) {
            assertValid(verify(randomizer.get()));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Address_Randomized_Verifier() {
        throw new AssertionError("instantiation is not allowed");
    }
}

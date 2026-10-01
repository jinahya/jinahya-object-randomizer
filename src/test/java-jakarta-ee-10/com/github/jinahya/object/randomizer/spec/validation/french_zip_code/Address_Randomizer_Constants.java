package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

import java.util.List;

/**
 * Constants shared by every randomizer of {@link Address}, whichever engine it uses.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class Address_Randomizer_Constants {

    /**
     * The attributes every randomizer of {@link Address} leaves alone; none.
     * <p>
     * Excluding the one field there is would leave it at {@code null}, and {@code null} satisfies both of the composing
     * constraints -- {@link jakarta.validation.constraints.Pattern @Pattern} and
     * {@link jakarta.validation.constraints.Size @Size} each hold for it, as a constraint which leaves the null check
     * to {@code @NotNull} must. An exclusion would therefore turn this example into one that every engine passes
     * without writing anything, which is the opposite of what it is here to measure.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of();

    // -----------------------------------------------------------------------------------------------------------------
    private Address_Randomizer_Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}

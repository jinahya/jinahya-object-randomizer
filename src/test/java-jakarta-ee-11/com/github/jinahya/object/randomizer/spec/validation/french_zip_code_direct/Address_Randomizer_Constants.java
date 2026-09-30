package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

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
     * Excluding the one field there is would leave it at {@code null}, and {@code null} satisfies both constraints on
     * it, each of which leaves the null check to a {@code @NotNull} the specification does not declare here. The
     * example would then be one every engine passes without writing anything.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of();

    // -----------------------------------------------------------------------------------------------------------------
    private Address_Randomizer_Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}

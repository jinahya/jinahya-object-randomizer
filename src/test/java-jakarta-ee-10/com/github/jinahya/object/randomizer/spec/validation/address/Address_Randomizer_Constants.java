package com.github.jinahya.object.randomizer.spec.validation.address;

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
     * Every field the Jakarta Validation 3.0 specification declares on {@code Address} carries a constraint, and every
     * one of those constraints is {@code @NotNull}. An exclusion would leave that field at {@code null}, which is
     * precisely what the specification forbids, so there is nothing here to exclude.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of();

    // -----------------------------------------------------------------------------------------------------------------
    private Address_Randomizer_Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}

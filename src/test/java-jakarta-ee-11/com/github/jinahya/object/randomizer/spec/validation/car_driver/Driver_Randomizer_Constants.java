package com.github.jinahya.object.randomizer.spec.validation.car_driver;

import java.util.List;

/**
 * Constants shared by every randomizer of {@link Driver}, whichever engine it uses.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Driver_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class Driver_Randomizer_Constants {

    /**
     * The attributes every randomizer of {@link Driver} leaves alone; none.
     * <p>
     * It is empty by choice rather than by necessity: the Jakarta Validation 3.1 specification puts no
     * {@code @NotNull} on {@code car}, so excluding it would leave a valid instance. It would also take the cascade
     * out of the example, which is half of what {@link Driver} is here to measure, so nothing is excluded.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of();

    // -----------------------------------------------------------------------------------------------------------------
    private Driver_Randomizer_Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}

package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The zip code every randomizer of {@link Address} in this package writes for itself.
 * <p>
 * It is a class of static methods, rather than a base class or an interface, because neither of those can carry it. A
 * base class cannot: each flavor already extends the class of its engine, and Java has one superclass. An interface
 * cannot either, and it is worth being precise about why -- a {@code default} method loses to a class method of the
 * same signature, so a mixin declaring {@code get()} would be silently ignored by a randomizer which inherits
 * {@code get()} from its engine, with nothing failing to say so.
 * <p>
 * What is left is a helper each randomizer calls from the hook its own engine gives it, which is what the three here
 * do.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FrenchZipCode
 */
@Slf4j
public final class FrenchZipCodes {

    /**
     * The name of the field a zip code is written to; {@value}.
     */
    public static final String FIELD_ZIP_CODE = "zipCode";

    /**
     * The number of digits a zip code carries; {@value}, which is what the {@code @Size} composed into
     * {@link FrenchZipCode} asks for, as its {@code @Pattern} asks for those characters to be digits.
     */
    private static final int DIGITS = 5;

    /**
     * The bound of the value drawn, which is ten to the power of {@link #DIGITS}.
     */
    private static final int BOUND = 100_000;

    /**
     * Returns a new zip code which satisfies {@link FrenchZipCode}.
     *
     * @return a string of exactly {@value #DIGITS} digits.
     * @implNote The value is zero-padded rather than drawn from the upper decade, so that every zip code the
     *         constraint accepts can come out, {@code 00000} included; the {@code @Pattern} takes a leading zero as
     *         readily as any other digit.
     */
    public static String newZipCode() {
        return String.format("%0" + DIGITS + "d", ThreadLocalRandom.current().nextInt(BOUND));
    }

    /**
     * Writes a new zip code to the specified instance, and returns it.
     *
     * @param instance the instance to write to.
     * @return the {@code instance}.
     * @implNote The value the engine left is overwritten rather than repaired: no engine here reaches one the
     *         composed constraint accepts, so there is nothing to keep.
     */
    public static Address repaired(final Address instance) {
        log.debug("repairing {}", instance);
        instance.setZipCode(newZipCode());
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private FrenchZipCodes() {
        throw new AssertionError("instantiation is not allowed");
    }
}

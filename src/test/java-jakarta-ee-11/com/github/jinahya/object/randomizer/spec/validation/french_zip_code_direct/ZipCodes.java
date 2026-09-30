package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The zip code the randomizers of this package which need one write for themselves.
 * <p>
 * Not every randomizer here does, which is the difference between this package and the {@code french_zip_code} one
 * beside it: with the constraints declared on the field, one of the three engines reads them and needs no help.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class ZipCodes {

    /**
     * The number of digits a zip code carries; {@value}, which is what the {@code @Size} on {@code Address.zipCode}
     * asks for, as the {@code @Pattern} beside it asks for those characters to be digits.
     */
    private static final int DIGITS = 5;

    /**
     * The bound of the value drawn, which is ten to the power of {@link #DIGITS}.
     */
    private static final int BOUND = 100_000;

    /**
     * Returns a new zip code which satisfies the constraints on {@code Address.zipCode}.
     *
     * @return a string of exactly {@value #DIGITS} digits.
     */
    public static String newZipCode() {
        return String.format("%0" + DIGITS + "d", ThreadLocalRandom.current().nextInt(BOUND));
    }

    /**
     * Writes a new zip code to the specified instance, and returns it.
     *
     * @param instance the instance to write to.
     * @return the {@code instance}.
     */
    public static Address repaired(final Address instance) {
        instance.setZipCode(newZipCode());
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ZipCodes() {
        throw new AssertionError("instantiation is not allowed");
    }
}

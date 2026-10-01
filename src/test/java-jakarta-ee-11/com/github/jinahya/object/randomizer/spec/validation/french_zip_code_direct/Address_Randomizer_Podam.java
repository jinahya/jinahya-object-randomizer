package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

/**
 * A PODAM randomizer of {@link Address}.
 * <p>
 * PODAM reads the {@code @Size} on the field -- it draws exactly five characters -- and not the {@code @Pattern} beside
 * it, which {@code PodamObjectRandomizer} documents: a {@code @Pattern} yields {@code null}, and where another
 * constraint has already settled the value, it is simply left unread. So {@link #get()} is overridden to write a zip
 * code the pattern accepts.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Address_Randomizer_Podam
        extends PodamObjectRandomizer<Address> {

    /**
     * Creates a new instance.
     */
    public Address_Randomizer_Podam() {
        super(Address.class, Address_Randomizer_Constants.EXCLUDED_FIELDS);
    }

    /**
     * {@inheritDoc}
     *
     * @return a randomized instance whose {@code zipCode} satisfies the {@code @Pattern} on it, which the engine alone
     *         does not read.
     * @implSpec The engine populates the instance first, and {@link ZipCodes#repaired(Address) repaired} then
     *         writes the one field it filled without reading the pattern.
     */
    @Override
    public Address get() {
        return ZipCodes.repaired(super.get());
    }
}

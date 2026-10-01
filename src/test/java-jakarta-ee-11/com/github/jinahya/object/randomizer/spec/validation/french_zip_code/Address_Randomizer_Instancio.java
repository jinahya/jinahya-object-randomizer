package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

import com.github.jinahya.object.randomizer.InstancioObjectRandomizer;
import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;

/**
 * A Instancio randomizer of {@link Address}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link ObjectRandomizerUtils#newRandomizerInstanceOf(Class) newRandomizerInstanceOf} probes -- which is only
 * {@code AddressRandomizer} and {@code Address_Randomizer} -- and neither is either flavor beside it. Nothing in this
 * package is located by the convention, then: a randomizer here is chosen by naming the engine it is wanted for, and
 * the convention itself is covered by {@code ObjectRandomizerUtils_Convention_Test}, which owes nothing to a
 * specification example. Each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * {@code InstancioObjectRandomizer} sets {@code Keys.BEAN_VALIDATION_ENABLED} in the settings it returns by default,
 * and Instancio still writes a value it never read the pattern for, because the {@code @Pattern} is reached through a
 * composed constraint. So {@link #get()} is overridden to write a valid one, which is what this package is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Address_Randomizer_Instancio
        extends InstancioObjectRandomizer<Address> {

    /**
     * Creates a new instance.
     */
    public Address_Randomizer_Instancio() {
        super(Address.class, Address_Randomizer_Constants.EXCLUDED_FIELDS);
    }

    /**
     * {@inheritDoc}
     *
     * @return a randomized instance whose {@code zipCode} satisfies {@link FrenchZipCode}, which the engine alone does
     *         not reach.
     * @implSpec The engine populates the instance first, and {@link FrenchZipCodes#repaired(Address) repaired}
     *         then writes the one field it could not.
     */
    @Override
    public Address get() {
        return FrenchZipCodes.repaired(super.get());
    }
}

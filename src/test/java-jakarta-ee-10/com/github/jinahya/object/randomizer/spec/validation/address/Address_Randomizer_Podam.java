package com.github.jinahya.object.randomizer.spec.validation.address;

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

/**
 * A PODAM randomizer of {@link Address}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link ObjectRandomizerUtils#newRandomizerInstanceOf(Class) newRandomizerInstanceOf} probes -- which is only
 * {@code AddressRandomizer} and {@code Address_Randomizer} -- and neither is either flavor beside it. Nothing in this
 * package is located by the convention, then: a randomizer here is chosen by naming the engine it is wanted for, and
 * the convention itself is covered by {@code ObjectRandomizerUtils_Convention_Test}, which owes nothing to a
 * specification example. Each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * PODAM is the one engine which honors {@code jakarta.validation.constraints} with no configuration at all, so this
 * class adds nothing.
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
}

package com.github.jinahya.object.randomizer.spec.validation;

import com.github.jinahya.object.randomizer.EasyRandomObjectRandomizer;

/**
 * An Easy Random randomizer of {@link Address}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils#locateStandard(Class) locateStandard} probes --
 * which is only {@code AddressRandomizer} and {@code Address_Randomizer} -- so it is never located, and never competes
 * with {@link Address_Randomizer}, the one located by the convention. It stands beside the three other flavors so that
 * each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * Easy Random 6 removed its constraint support outright, so there is no configuration which would make this flavor
 * honor what the specification declares. It fills the fields, and no more, which is why its test calls
 * {@link Address_Randomized_Verifier#verify(Address)} and not
 * {@link Address_Randomized_Verifier#verifyValid(com.github.jinahya.object.randomizer.ObjectRandomizer)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Address_Randomizer_EasyRandom
        extends EasyRandomObjectRandomizer<Address> {

    /**
     * Creates a new instance.
     */
    public Address_Randomizer_EasyRandom() {
        super(Address.class, Address_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}

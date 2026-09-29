package com.github.jinahya.object.randomizer.spec.validation;

import com.github.jinahya.object.randomizer.InstancioObjectRandomizer;

/**
 * An Instancio randomizer of {@link Address}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils#locateStandard(Class) locateStandard} probes --
 * which is only {@code AddressRandomizer} and {@code Address_Randomizer} -- so it is never located, and never competes
 * with {@link Address_Randomizer}, the one located by the convention. It stands beside the three other flavors so that
 * each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * Nothing is overridden to make the constraints honored: {@code InstancioObjectRandomizer} sets
 * {@code Keys.BEAN_VALIDATION_ENABLED} in the settings it returns by default.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomizer
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
}

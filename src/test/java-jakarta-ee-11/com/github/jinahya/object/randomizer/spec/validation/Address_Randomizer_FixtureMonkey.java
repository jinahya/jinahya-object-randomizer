package com.github.jinahya.object.randomizer.spec.validation;

import com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer;

/**
 * A Fixture Monkey randomizer of {@link Address}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils#locateStandard(Class) locateStandard} probes --
 * which is only {@code AddressRandomizer} and {@code Address_Randomizer} -- so it is never located, and never competes
 * with {@link Address_Randomizer}, the one located by the convention. It stands beside the two other flavors so that
 * each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * Nothing is overridden to make the constraints honored: {@code FixtureMonkeyObjectRandomizer} registers the
 * {@code JakartaValidationPlugin} itself when {@code fixture-monkey-jakarta-validation} is on the classpath, which it
 * is here, at {@code test} scope.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Address_Randomizer_FixtureMonkey
        extends FixtureMonkeyObjectRandomizer<Address> {

    /**
     * Creates a new instance.
     */
    public Address_Randomizer_FixtureMonkey() {
        super(Address.class, Address_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}

package com.github.jinahya.object.randomizer.spec.validation;

import com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

/**
 * A Fixture Monkey randomizer of {@link Address}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils#locateStandard(Class) locateStandard} probes --
 * which is only {@code AddressRandomizer} and {@code Address_Randomizer} -- so it is never located, and never competes
 * with {@link Address_Randomizer}, the one located by the convention. It stands beside the three other flavors so that
 * each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * Fixture Monkey reads the constraints only through {@link JakartaValidationPlugin}, which is what
 * {@link #getFixtureMonkey()} is overridden to register.
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

    /**
     * {@inheritDoc}
     *
     * @return an engine with {@link JakartaValidationPlugin} registered.
     */
    @Override
    protected FixtureMonkey getFixtureMonkey() {
        return FixtureMonkey.builder()
                .plugin(new JakartaValidationPlugin())
                .build();
    }
}

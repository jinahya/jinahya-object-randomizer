package com.github.jinahya.object.randomizer.spec.validation;

import com.github.jinahya.object.randomizer.InstancioObjectRandomizer;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

/**
 * An Instancio randomizer of {@link Address}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils#locateStandard(Class) locateStandard} probes --
 * which is only {@code AddressRandomizer} and {@code Address_Randomizer} -- so it is never located, and never competes
 * with {@link Address_Randomizer}, the one located by the convention. It stands beside the three other flavors so that
 * each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * Instancio reads the constraints only once {@link Keys#BEAN_VALIDATION_ENABLED} is set, which is what
 * {@link #getInstancioSettings()} is overridden for here.
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

    /**
     * {@inheritDoc}
     *
     * @return settings with {@link Keys#BEAN_VALIDATION_ENABLED} turned on.
     */
    @Override
    protected Settings getInstancioSettings() {
        return super.getInstancioSettings().set(Keys.BEAN_VALIDATION_ENABLED, true);
    }
}

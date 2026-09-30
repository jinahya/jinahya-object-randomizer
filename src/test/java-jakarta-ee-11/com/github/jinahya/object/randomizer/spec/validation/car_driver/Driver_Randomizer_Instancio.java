package com.github.jinahya.object.randomizer.spec.validation.car_driver;

import com.github.jinahya.object.randomizer.InstancioObjectRandomizer;
import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;

/**
 * A Instancio randomizer of {@link Driver}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link ObjectRandomizerUtils#newRandomizerInstanceOf(Class) newRandomizerInstanceOf} probes -- which is only
 * {@code DriverRandomizer} and {@code Driver_Randomizer} -- and neither is either flavor beside it. Nothing in this
 * package is located by the convention, then: a randomizer here is chosen by naming the engine it is wanted for, and
 * the convention itself is covered by {@code ObjectRandomizerUtils_Convention_Test}, which owes nothing to a
 * specification example. Each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * Nothing is overridden to make the constraints honored: {@code InstancioObjectRandomizer} sets
 * {@code Keys.BEAN_VALIDATION_ENABLED} in the settings it returns by default.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Driver_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Driver_Randomizer_Instancio
        extends InstancioObjectRandomizer<Driver> {

    /**
     * Creates a new instance.
     */
    public Driver_Randomizer_Instancio() {
        super(Driver.class, Driver_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}

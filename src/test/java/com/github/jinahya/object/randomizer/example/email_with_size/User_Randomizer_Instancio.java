package com.github.jinahya.object.randomizer.example.email_with_size;

import com.github.jinahya.object.randomizer.InstancioObjectRandomizer;
import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;

/**
 * An Instancio randomizer of {@link User}, which needs nothing overridden.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link ObjectRandomizerUtils#newRandomizerInstanceOf(Class) newRandomizerInstanceOf} probes -- which is only
 * {@code UserRandomizer} and {@code User_Randomizer} -- and neither is either flavor beside it. Nothing in this package
 * is located by the convention, then: a randomizer here is chosen by naming the engine it is wanted for, and the
 * convention itself is covered by {@code ObjectRandomizerUtils_Convention_Test}. Each engine has exactly one class
 * here, and they can be read, and tested, as a set.
 * <p>
 * {@code InstancioObjectRandomizer} sets {@code Keys.BEAN_VALIDATION_ENABLED} in the settings it returns by default,
 * and that is the whole of what this flavor needs: the engine reads both constraints and writes a short address, of the
 * shape {@code f@of.net}, which satisfies the two together. The body of this class is therefore a constructor and
 * nothing else, which is the point of carrying it beside {@link User_Randomizer_Podam}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see User_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class User_Randomizer_Instancio
        extends InstancioObjectRandomizer<User> {

    /**
     * Creates a new instance.
     */
    public User_Randomizer_Instancio() {
        super(User.class, User_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}

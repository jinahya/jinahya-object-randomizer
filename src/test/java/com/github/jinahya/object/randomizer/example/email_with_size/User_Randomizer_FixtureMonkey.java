package com.github.jinahya.object.randomizer.example.email_with_size;

import com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer;
import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;

/**
 * A Fixture Monkey randomizer of {@link User}, which needs nothing overridden.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link ObjectRandomizerUtils#newRandomizerInstanceOf(Class) newRandomizerInstanceOf} probes -- which is only
 * {@code UserRandomizer} and {@code User_Randomizer} -- and neither is either flavor beside it. Nothing in this package
 * is located by the convention, then: a randomizer here is chosen by naming the engine it is wanted for, and the
 * convention itself is covered by {@code ObjectRandomizerUtils_Convention_Test}. Each engine has exactly one class
 * here, and they can be read, and tested, as a set.
 * <p>
 * {@code FixtureMonkeyObjectRandomizer} registers the {@code JakartaValidationPlugin} itself when
 * {@code fixture-monkey-jakarta-validation} is on the classpath, which it is here, at {@code test} scope, and that is
 * the whole of what this flavor needs: the engine reads both constraints and writes an address which satisfies the two
 * together. It reaches further into the grammar than the other two do -- a literal-domain address such as
 * {@code th5@[4.4.80.225]}, which is well-formed and which the reference implementation accepts -- so a caller who
 * wants something a person would recognize should write the value itself rather than take what this draws.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see User_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class User_Randomizer_FixtureMonkey
        extends FixtureMonkeyObjectRandomizer<User> {

    /**
     * Creates a new instance.
     */
    public User_Randomizer_FixtureMonkey() {
        super(User.class, User_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}

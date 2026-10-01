package com.github.jinahya.object.randomizer.example.email_with_size;

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

/**
 * A PODAM randomizer of {@link User}, and the one flavor here which has to write the value itself.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link ObjectRandomizerUtils#newRandomizerInstanceOf(Class) newRandomizerInstanceOf} probes -- which is only
 * {@code UserRandomizer} and {@code User_Randomizer} -- and neither is either flavor beside it. Nothing in this package
 * is located by the convention, then: a randomizer here is chosen by naming the engine it is wanted for, and the
 * convention itself is covered by {@code ObjectRandomizerUtils_Convention_Test}. Each engine has exactly one class
 * here, and they can be read, and tested, as a set.
 * <p>
 * PODAM honors {@code jakarta.validation.constraints} with no configuration at all, and honors each of these two on its
 * own: a lone {@code @Email} yields an address, and a lone {@code @Size} yields a string of the asked-for length.
 * Together, the {@code @Size} wins and the {@code @Email} is dropped -- in either declaration order -- so the value is
 * a random string of between {@value User#EMAIL_SIZE_MIN} and {@value User#EMAIL_SIZE_MAX} characters with no {@code @}
 * anywhere in it. That is what {@link #get()} repairs, and it is the gap this package exists to show.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see User_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class User_Randomizer_Podam
        extends PodamObjectRandomizer<User> {

    /**
     * Creates a new instance.
     */
    public User_Randomizer_Podam() {
        super(User.class, User_Randomizer_Constants.EXCLUDED_FIELDS);
    }

    /**
     * {@inheritDoc}
     *
     * @return a randomized instance whose {@code email} satisfies both constraints, the {@code @Email} of which the
     *         engine alone does not reach.
     * @implSpec The engine populates the instance first, and {@link Emails#repaired(User) repaired} then writes
     *         the one field it could not.
     */
    @Override
    public User get() {
        return Emails.repaired(super.get());
    }
}

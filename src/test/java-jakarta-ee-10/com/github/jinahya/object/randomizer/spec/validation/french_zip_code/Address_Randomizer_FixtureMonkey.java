package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer;
import com.navercorp.fixturemonkey.ArbitraryBuilder;

/**
 * A Fixture Monkey randomizer of {@link Address}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link ObjectRandomizerUtils#newRandomizerInstanceOf(Class) newRandomizerInstanceOf} probes -- which is only
 * {@code AddressRandomizer} and {@code Address_Randomizer} -- and neither is either flavor beside it. Nothing in this
 * package is located by the convention, then: a randomizer here is chosen by naming the engine it is wanted for, and
 * the convention itself is covered by {@code ObjectRandomizerUtils_Convention_Test}, which owes nothing to a
 * specification example. Each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * {@code FixtureMonkeyObjectRandomizer} registers the {@code JakartaValidationPlugin} itself when
 * {@code fixture-monkey-jakarta-validation} is on the classpath, which it is here, at {@code test} scope, and the
 * engine still raises an {@link IllegalArgumentException} rather than sample a class whose field carries the composed
 * constraint. So the value is written onto the builder, before sampling, which is what this package is about; see
 * {@link #getArbitraryBuilder()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomized_Verifier
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
     * @return a builder which already carries a zip code satisfying {@link FrenchZipCode}.
     * @implSpec Unlike the other flavors, this one cannot write the field after the fact: the engine raises an
     *         {@link IllegalArgumentException} while sampling a class whose field carries the composed constraint, so
     *         {@code get()} never returns an instance to repair. Setting the value on the builder leaves the engine
     *         nothing to generate for that field, which is the hook
     *         {@link FixtureMonkeyObjectRandomizer#getArbitraryBuilder()} documents.
     */
    @Override
    protected ArbitraryBuilder<Address> getArbitraryBuilder() {
        return super.getArbitraryBuilder().set(FrenchZipCodes.FIELD_ZIP_CODE, FrenchZipCodes.newZipCode());
    }
}

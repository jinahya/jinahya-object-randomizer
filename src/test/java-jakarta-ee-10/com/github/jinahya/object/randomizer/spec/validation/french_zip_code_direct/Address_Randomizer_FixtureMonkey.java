package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

import com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer;

/**
 * A Fixture Monkey randomizer of {@link Address}.
 * <p>
 * Nothing is overridden here, and that is what this package shows. Fixture Monkey reads both constraints off the field
 * and generates a value which satisfies them, so this flavor needs no help at all -- while the same engine, in the
 * {@code french_zip_code} package beside this one, refuses outright to sample the very same requirement wrapped in a
 * composed constraint.
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
}

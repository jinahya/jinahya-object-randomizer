package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the Fixture Monkey flavor is wired up against {@link Address}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Address_Randomizer_FixtureMonkey_Test {

    @DisplayName("get() -> an instance which satisfies the constraints of the Jakarta Validation 3.1 specification")
    @Test
    void get_AsSpecified_() {
        Address_Randomized_Verifier.verifyValid(new Address_Randomizer_FixtureMonkey());
    }
}

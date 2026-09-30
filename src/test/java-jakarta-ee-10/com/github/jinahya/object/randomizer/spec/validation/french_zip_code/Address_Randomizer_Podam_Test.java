package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the PODAM flavor is wired up against {@link Address}, the class the Jakarta Validation 3.0
 * specification publishes, and against the {@link FrenchZipCode} the same specification composes out of a
 * {@code @Pattern} and a {@code @Size}.
 * <p>
 * What the contract is, and how it is checked, belong to {@link Address_Randomized_Verifier}, so that this class says
 * only which engine it is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Address_Randomizer_Podam_Test {

    @DisplayName("get() -> an instance which satisfies the constraints of the Jakarta Validation 3.0 specification")
    @Test
    void get_AsSpecified_() {
        Address_Randomized_Verifier.verifyValid(new Address_Randomizer_Podam());
    }
}

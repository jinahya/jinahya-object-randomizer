package com.github.jinahya.object.randomizer.spec.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the Easy Random flavor is wired up against {@link Address}, the class the Jakarta Validation 3.1
 * specification publishes.
 * <p>
 * What the contract is, and how it is checked, belong to {@link Address_Randomized_Verifier}, so that this class says
 * only which engine it is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Address_Randomizer_EasyRandom_Test {

    @DisplayName("get() -> an instance which is filled, though it can not satisfy the constraints")
    @Test
    void get_AsSpecified_() {
        Address_Randomized_Verifier.verify(new Address_Randomizer_EasyRandom().get());
    }
}

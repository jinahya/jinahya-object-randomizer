package com.github.jinahya.object.randomizer.spec.validation.car_driver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the PODAM flavor is wired up against {@link Driver}, the class the Jakarta Validation 3.0
 * specification publishes.
 * <p>
 * What the contract is, and how it is checked, belong to {@link Driver_Randomized_Verifier}, so that this class says
 * only which engine it is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Driver_Randomizer_Podam_Test {

    @DisplayName("get() -> an instance which satisfies the constraints of the Jakarta Validation 3.0 specification")
    @Test
    void get_AsSpecified_() {
        Driver_Randomized_Verifier.verifyValid(new Driver_Randomizer_Podam());
    }
}

package com.github.jinahya.object.randomizer.example.email_with_size;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the PODAM flavor satisfies both constraints the {@code email} field of {@link User} carries, the
 * {@link jakarta.validation.constraints.Email @Email} and the {@link jakarta.validation.constraints.Size @Size}
 * together.
 * <p>
 * What the contract is, and how it is checked, belong to {@link User_Randomized_Verifier}, so that this class says only
 * which engine it is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class User_Randomizer_Podam_Test {

    @DisplayName("get() -> an instance whose email satisfies both the @Email and the @Size")
    @Test
    void get_Valid_() {
        User_Randomized_Verifier.verifyValid(new User_Randomizer_Podam());
    }
}

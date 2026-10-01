package com.github.jinahya.object.randomizer.example.email_with_size;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.object.randomizer._Validation_Test_Utils.validate;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the constrained field itself, rather than anything which randomizes a {@link User} carrying it; that both of
 * its constraints are applied, and that what {@link Emails} writes is what the two of them accept.
 * <p>
 * This is what gives a randomizer's green result its meaning. A flavor which satisfied only the {@code @Size} and a
 * validator which never reached the {@code @Email} would look alike from the outside, and the first assertion below is
 * what tells them apart: it hands the validator the very thing PODAM produces on its own -- a string of the right
 * length and no address -- and requires a violation.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Emails_Test {

    @DisplayName("the @Email and the @Size on the one field are both applied")
    @Test
    void validate_BothApplied_() {
        final var user = new User();
        user.setEmail("a@bc.com");
        assertThat(validate(user)).as("an address of eight characters").isEmpty();
        user.setEmail("abcdefghij");
        assertThat(validate(user))
                .as("in range, and no address, so the @Email fails")
                .singleElement()
                .satisfies(v -> assertThat(v.getPropertyPath()).hasToString(Emails.FIELD_EMAIL));
        user.setEmail("a@b.c");
        assertThat(validate(user))
                .as("an address of five characters, so the @Size fails")
                .singleElement()
                .satisfies(v -> assertThat(v.getPropertyPath()).hasToString(Emails.FIELD_EMAIL));
        user.setEmail(null);
        assertThat(validate(user)).as("null, which both constraints hold for").isEmpty();
    }

    /**
     * The number of addresses drawn; more than one, since both parts are random and a length out of range, or a
     * malformed part, would otherwise come out only now and then.
     */
    private static final int DRAWS = 64;

    @DisplayName("what Emails writes is an address both constraints accept")
    @Test
    void newEmail_Accepted_() {
        final var user = new User();
        for (int i = 0; i < DRAWS; i++) {
            user.setEmail(Emails.newEmail());
            assertThat(user.getEmail())
                    .matches("[a-z0-9]{1,8}@[a-z0-9]{1,8}\\.com")
                    .hasSizeBetween(User.EMAIL_SIZE_MIN, User.EMAIL_SIZE_MAX);
            assertThat(validate(user)).as("violations of draw #%d: %s", i, user).isEmpty();
        }
    }

    @DisplayName("repaired(user) -> the same instance, carrying a new address")
    @Test
    void repaired_SameInstance_() {
        final var user = new User();
        user.setEmail("whatever the engine left");
        assertThat(Emails.repaired(user))
                .isSameAs(user)
                .extracting(User::getEmail, InstanceOfAssertFactories.STRING)
                .matches("[a-z0-9]{1,8}@[a-z0-9]{1,8}\\.com");
    }
}

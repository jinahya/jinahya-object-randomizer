package com.github.jinahya.object.randomizer.example.email_with_size;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A target with one field carrying two constraints, which an engine has to satisfy together.
 * <p>
 * Neither constraint is interesting on its own. {@link Email @Email} alone is honored by all three flavors, and so is
 * {@link Size @Size} alone; it is the pair that separates them, because a value which satisfies one of them says
 * nothing about the other. A string of the right length is not an address, and an address of the wrong length is not
 * accepted either.
 * <p>
 * The bounds are deliberately wide enough for an ordinary address, so that an engine which <em>does</em> read the
 * {@code @Email} is not failed by the {@code @Size} it also read: what is measured here is whether both were read, not
 * whether the two can be satisfied at the same time.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote Accessors come from Lombok, which is on the processor path of the test compilation: PODAM writes a
 *         property through its setter and never assigns a field, while the constraints stay on the <em>field</em>,
 *         which is where all three engines look for them.
 * @see Emails
 * @see User_Randomized_Verifier
 */
@ToString
@Setter
@Getter
public class User {

    /**
     * The lower bound of the {@link Size @Size} on the {@code email} field; {@value}.
     */
    public static final int EMAIL_SIZE_MIN = 6;

    /**
     * The upper bound of the {@link Size @Size} on the {@code email} field; {@value}.
     */
    public static final int EMAIL_SIZE_MAX = 31;

    @Email
    @Size(min = EMAIL_SIZE_MIN, max = EMAIL_SIZE_MAX)
    private String email;
}

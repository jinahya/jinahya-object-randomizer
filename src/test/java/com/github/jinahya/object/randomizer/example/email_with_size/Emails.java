package com.github.jinahya.object.randomizer.example.email_with_size;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The address a randomizer of {@link User} writes when its engine does not reach one.
 * <p>
 * It is a class of static methods, rather than a base class or an interface, because neither of those can carry it. A
 * base class cannot: each flavor already extends the class of its engine, and Java has one superclass. An interface
 * cannot either, and it is worth being precise about why -- a {@code default} method loses to a class method of the
 * same signature, so a mixin declaring {@code get()} would be silently ignored by a randomizer which inherits
 * {@code get()} from its engine, with nothing failing to say so.
 * <p>
 * Only {@link User_Randomizer_Podam} calls it; the other two flavors need nothing from here. It is declared all the
 * same, rather than inlined into that one randomizer, so that what the engine could not do is stated in one place and
 * {@link Emails_Test} can hold the value to the constraints by itself.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see User
 */
@Slf4j
public final class Emails {

    /**
     * The name of the field an address is written to; {@value}.
     */
    public static final String FIELD_EMAIL = "email";

    /**
     * The characters a drawn local part, and a drawn label, are made of.
     *
     * @implNote Lowercase letters and digits only. The grammar an address may use is far wider, and none of the
     *         rest of it is the point here: what is wanted is a value no reader has to check against a specification to
     *         see is an address.
     */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * The longest local part, and the longest label, drawn; {@value}.
     *
     * @implNote Chosen so that the whole address lands inside the {@code @Size} with room to spare. The
     *         shortest it yields is {@code a@b.com}, of seven characters, and the longest is twenty-one -- against
     *         bounds of {@value User#EMAIL_SIZE_MIN} and {@value User#EMAIL_SIZE_MAX}. A value which only just cleared
     *         a bound would turn a failure of this helper into a failure that reads like a failure of the engine.
     */
    private static final int PART_LENGTH_MAX = 8;

    /**
     * The top-level domain every drawn address ends with; {@value}.
     */
    private static final String TOP_LEVEL_DOMAIN = ".com";

    private static String newPart() {
        final var random = ThreadLocalRandom.current();
        final var length = random.nextInt(PART_LENGTH_MAX) + 1;
        final var builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }

    /**
     * Returns a new address which satisfies both constraints the {@code email} field of {@link User} carries.
     *
     * @return a well-formed address of between seven and twenty-one characters.
     */
    public static String newEmail() {
        return newPart() + '@' + newPart() + TOP_LEVEL_DOMAIN;
    }

    /**
     * Writes a new address to the specified instance, and returns it.
     *
     * @param instance the instance to write to.
     * @return the {@code instance}.
     * @implNote The value the engine left is overwritten rather than repaired: PODAM writes a string of the
     *         constrained length and nothing else, so there is no address there to keep.
     */
    public static User repaired(final User instance) {
        log.debug("repairing {}", instance);
        instance.setEmail(newEmail());
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Emails() {
        throw new AssertionError("instantiation is not allowed");
    }
}

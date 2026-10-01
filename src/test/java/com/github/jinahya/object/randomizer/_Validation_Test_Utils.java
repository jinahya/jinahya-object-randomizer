package com.github.jinahya.object.randomizer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utilities, shared by every test which validates a value, for reaching a {@link Validator} and asserting on what it
 * finds.
 * <p>
 * What is validated here is a <em>value</em>, never whatever produced it. A randomizer's verifier draws an instance and
 * hands it to {@link #assertValid(Object, Class[])}; a test of a constraint itself hands over a value made by hand and
 * requires a violation through {@link #validate(Object, Class[])}. Both halves are wanted, and in equal measure: a
 * constraint the validator never reaches cannot be told apart from one an engine satisfies, so a green result means
 * something only where the red one has been shown to be reachable.
 * <p>
 * This class is declared in the root test package, rather than beside any one of its callers, because the
 * {@code jakarta-ee-NN} source roots are added to the same test compilation as {@code src/test/java} and so can reach
 * it. One copy therefore serves the examples and both generations of the specification transcriptions, which is what
 * keeps a validator from being built by hand in each of them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class _Validation_Test_Utils {

    /**
     * Applies a new validator factory, closed when this method returns, to the specified function, and returns the
     * result.
     *
     * @param function the function to apply.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @apiNote The factory does not outlive the call, so a function which returns it, or anything which reads
     *         from it later, returns something already closed. What a {@link jakarta.validation.ConstraintViolation}
     *         carries is safe to return: it is built before the factory closes, and closing clears caches rather than
     *         reaching into what they handed out.
     * @implNote A factory per call, rather than one held for the run. {@link ValidatorFactory} is
     *         {@link AutoCloseable}, and that is the whole of what is known about it from its contract; whether a given
     *         release of an implementation happens to hold anything which needs releasing is a detail of that release,
     *         and not something to build on. Building one costs roughly three milliseconds, which is paid here
     *         deliberately.
     */
    public static <R> R applyValidationFactory(final Function<? super ValidatorFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            return function.apply(factory);
        }
    }

    /**
     * Applies a validator, from a new validator factory, to the specified function, and returns the result.
     *
     * @param function the function to apply.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @see #applyValidationFactory(Function)
     */
    public static <R> R applyValidator(final Function<? super Validator, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyValidationFactory(f -> function.apply(f.getValidator()));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Validates the specified object, against the specified groups, and returns the violations found.
     *
     * @param object the object to validate.
     * @param groups the groups to validate against; none for the default group, which a
     *               {@link jakarta.validation.GroupSequence @GroupSequence} on the object's class redefines.
     * @param <T>    the type of the {@code object}.
     * @return the violations found; empty when the {@code object} is valid.
     */
    public static <T> Set<ConstraintViolation<T>> validate(final T object, final Class<?>... groups) {
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(groups, "groups is null");
        return applyValidator(v -> v.validate(object, groups));
    }

    /**
     * Asserts that the specified object violates no constraint, and returns it.
     *
     * @param object the object to validate.
     * @param groups the groups to validate against; none for the default group.
     * @param <T>    the type of the {@code object}.
     * @return the {@code object}, so that a caller may go on asserting something of its own.
     */
    public static <T> T assertValid(final T object, final Class<?>... groups) {
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(groups, "groups is null");
        assertThat(validate(object, groups))
                .as("constraint violations on %s with %s", object, Arrays.toString(groups))
                .isEmpty();
        return object;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private _Validation_Test_Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}

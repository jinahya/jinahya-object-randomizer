package com.github.jinahya.object.randomizer;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities for {@link ObjectRandomizer}.
 *
 * <h2>The naming convention</h2>
 * A randomizer is found for a target class by name. The randomizer class is a <em>sibling</em> of the target &mdash;
 * declared in the same package, beside it &mdash; which implements {@link ObjectRandomizer} and carries a postfix of
 * either {@code "Randomizer"} or {@code "_Randomizer"}. For a target class {@code Foo}, that is {@code FooRandomizer},
 * probed first, and then {@code Foo_Randomizer}.
 * <p>
 * The convention spans source sets: a {@code Foo} declared in {@code main} and a {@code FooRandomizer} declared in
 * {@code test} are the same package, and both are on the test classpath.
 * <p>
 * A class which is not a sibling of the target is never located: neither a local nor an anonymous class, which can not
 * carry the required name, nor a class nested inside the target, which would have to be declared in the source of the
 * target itself. A class nested in a target class of {@code main} therefore has to be declared as a top-level class to
 * be randomizable here. Note that a subclass of a class which has a randomizer is located by the convention, and not by
 * the randomizer of its superclass, which could not produce instances of the subclass anyway.
 * <p>
 * A located class is instantiated reflectively, and so has to declare an accessible no-argument constructor which
 * supplies the target class to its superclass.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ObjectRandomizer
 * @see #newRandomizedInstanceOf(Class)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ObjectRandomizerUtils {

    /**
     * Locates, by the {@linkplain ObjectRandomizerUtils naming convention}, the randomizer class of the specified
     * target class.
     *
     * @param target the target class whose randomizer class is located.
     * @return the randomizer class of the {@code target}; {@code null} when not found.
     * @implNote Only the name is judged here. The postfixes are probed in order, and the first class which
     *         exists is returned, whatever it is; whether it implements {@link ObjectRandomizer}, and whether it can be
     *         instantiated, is checked where the class is put to use, and failed on there.
     * @see _Utils#siblingClassForPostfixes(Class, String...)
     */
    @Nullable
    static Class<?> locateStandard(final Class<?> target) {
        assert target != null;
        return _Utils.siblingClassForPostfixes(target, "Randomizer", "_Randomizer");
    }

    /**
     * Merges specified iterables of excluded fields.
     *
     * @param excludedFields     the first iterable of excluded fields.
     * @param moreExcludedFields the second iterable of excluded fields.
     * @return an {@link Iterable} of merged excluded fields.
     * @throws NullPointerException when either argument is {@code null}.
     * @apiNote This method is for a subclass which adds to the exclusions of the randomizer it extends.
     *         Elements are concatenated as they are, in order, with neither deduplication nor validation; the
     *         {@link AbstractObjectRandomizer#AbstractObjectRandomizer(Class, Iterable) randomizer constructor} strips
     *         them, drops the blank and the {@code null} ones, and deduplicates the rest.
     */
    public static Iterable<String> moreExcludedFields(final Iterable<String> excludedFields,
                                                      final Iterable<String> moreExcludedFields) {
        Objects.requireNonNull(excludedFields, "excludedFields is null");
        Objects.requireNonNull(moreExcludedFields, "moreExcludedFields is null");
        return Stream.concat(
                StreamSupport.stream(excludedFields.spliterator(), false),
                StreamSupport.stream(moreExcludedFields.spliterator(), false)
        ).toList();
    }

    // ---------------------------------------------------------------------------------------------------------------------

    /**
     * Returns, an optional of, a new instance of the randomizer class located for the specified target class by the
     * {@linkplain ObjectRandomizerUtils naming convention}.
     * <p>
     * An empty optional means one thing: no randomizer is located for the {@code target}. When one <em>is</em> located,
     * it is used, and anything wrong with it &mdash; it does not implement {@link ObjectRandomizer}, or it can not be
     * instantiated &mdash; fails, rather than being reported as an absence: a class named by the convention is one the
     * developer meant to be used.
     *
     * @param target the target class.
     * @param <T>    the target type parameter.
     * @return an optional of a new instance of the randomizer located for the {@code target}; {@code empty} when no
     *         randomizer applies.
     * @throws NullPointerException when the {@code target} is {@code null}.
     * @throws RuntimeException     when the located randomizer is unusable.
     * @apiNote The randomizer is returned as an {@code ObjectRandomizer<T>} without the class it is declared
     *         for being checked against the {@code target}, for that declaration proves less than it appears to; a
     *         randomizer is verified on what it <em>produces</em>, which is what
     *         {@link #newRandomizedInstanceOf(Class)} does. A caller which calls {@link ObjectRandomizer#get() get()}
     *         on the returned randomizer itself makes that check its own.
     * @see #newRandomizedInstanceOf(Class)
     */
    @SuppressWarnings({
            "unchecked"
    })
    public static <T> Optional<ObjectRandomizer<T>> newRandomizerInstanceOf(final Class<T> target) {
        Objects.requireNonNull(target, "target is null");
        final ObjectRandomizer<?> randomizer =
                _Utils.newLocatedInstance(target, ObjectRandomizer.class, locateStandard(target));
        if (randomizer == null) {
            return Optional.empty();
        }
        // one is provided, so it is meant to be used; from here, anything wrong with it is a fault
        return Optional.of((ObjectRandomizer<T>) randomizer);
    }

    /**
     * Returns, an optional of, a randomized instance of the specified target class, using the randomizer located for it
     * by the {@linkplain ObjectRandomizerUtils naming convention}.
     * <p>
     * An empty optional means one thing: no randomizer is located for the {@code target}. When one <em>is</em> located,
     * it is used, and anything wrong with it &mdash; it does not implement {@link ObjectRandomizer}, it can not be
     * instantiated, or it produces something which is not an instance of the {@code target} &mdash; fails, rather than
     * being reported as an absence: a class named by the convention is one the developer meant to be used.
     *
     * @param target the target class.
     * @param <T>    the target type parameter.
     * @return an optional of randomized instance of the {@code target}; {@code empty} when no randomizer applies.
     * @throws NullPointerException when the {@code target} is {@code null}.
     * @throws RuntimeException     when the located randomizer is unusable, or produces nothing, or produces something
     *                              which is not a {@code target}; and when it throws.
     * @apiNote The randomizer is verified on what it produced, rather than on the class it is declared for: an
     *         {@code ObjectRandomizer<Foo>} whose {@link ObjectRandomizer#get() get()} returns a {@code Bar} is
     *         well-formed at compile time, erasure leaving nothing to enforce it, so the produced instance is the
     *         evidence which matters.
     * @see #newRandomizerInstanceOf(Class)
     */
    public static <T> Optional<T> newRandomizedInstanceOf(final Class<T> target) {
        // the randomizer was located, so it is meant to be used; from here, anything wrong with it is a fault
        return newRandomizerInstanceOf(target)
                .map(r -> _Utils.produced(target, r, r.get()));
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private ObjectRandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}

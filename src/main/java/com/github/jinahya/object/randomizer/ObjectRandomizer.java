package com.github.jinahya.object.randomizer;

import java.util.function.Supplier;

/**
 * An interface for randomizing instances of a specific class.
 * <p>
 * This is the role which the {@linkplain ObjectRandomizerUtils naming convention} locates for a target class, and which
 * {@link ObjectRandomizerUtils#newRandomizedInstanceOf(Class)} calls. Implement it directly for a randomizer which owes
 * nothing to the machinery of {@link AbstractObjectRandomizer}; extend that class, or one of the flavors beside it, for
 * the exclusions, and the engine, it brings.
 *
 * @param <T> the type of the instances to randomize.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AbstractObjectRandomizer
 * @see ObjectRandomizerUtils#newRandomizedInstanceOf(Class)
 */
@FunctionalInterface
public interface ObjectRandomizer<T>
        extends Supplier<T> {

    /**
     * Returns a randomized instance of the class this randomizer is for.
     *
     * @return a randomized instance.
     */
    @Override
    T get();
}

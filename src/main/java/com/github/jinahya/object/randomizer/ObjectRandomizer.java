package com.github.jinahya.object.randomizer;

/*-
 * #%L
 * jinahya-object-randomizer
 * %%
 * Copyright (C) 2026 Jin Kwon
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.validation.constraints.NotNull;

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
     * @return a randomized instance; never {@code null}.
     * @apiNote The {@link NotNull} on the return value states the contract; nothing enforces it, for a randomizer is
     *         instantiated, and called, directly. {@link ObjectRandomizerUtils#newRandomizedInstanceOf(Class)} drops a
     *         {@code null} as the product of a broken randomizer.
     */
    @NotNull
    @Override
    T get();
}

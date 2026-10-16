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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A skeletal implementation of {@link ObjectRandomizer}, for randomizing a specific class.
 * <p>
 * This class itself is engine-agnostic; extend one of the flavors declared beside it to pick an engine, or extend this
 * class directly and implement {@link #get() get()} to populate instances by hand. All engines are declared as
 * {@code provided} dependencies, so a consumer brings only the one it uses.
 * <p>
 * Slots named in {@link #excludedPaths} are left at whatever value the instance already carries, which is how a
 * generated identifier, a version, or an auditing column is kept out of the way of the persistence provider.
 *
 * <h2>Excluded paths</h2>
 * An excluded path is a <em>hint</em>: the names of slots which the engine is asked not to randomize, matched against
 * whatever the engine discovers as slots, and honored as far as the engine allows. Each flavor documents what it
 * matches a path against, and what it can not exclude.
 * <p>
 * The word <em>path</em> is chosen ahead of what every engine can do. A single name, such as {@code "address"}, is what
 * every flavor honors, by handing it to its engine. A dotted path, such as {@code "address.address1"}, is honored only
 * by a flavor whose engine can exclude by path, within the limits its documentation states; every other flavor ignores
 * it. A subclass which needs more overrides {@link #get()}, and adjusts what the engine returns.
 * <ul>
 *   <li>A <em>simple</em> path, of a single segment such as {@code "id"}, is handed to the engine's own exclusion
 *       mechanism, and so is scoped as that engine scopes an exclusion; read each flavor's documentation before
 *       treating two flavors as interchangeable.</li>
 *   <li>A <em>nested</em> path, of {@code .}-separated segments such as {@code "address.address1"}, is anchored at
 *       the target instance: its first segment names a slot of the target, and each of the others a slot of the object
 *       the segments before it reach. A segment which reaches a collection, an array, an {@link java.util.Optional},
 *       or a {@link java.util.Map}, reaches every element in it, so {@code "addresses.address1"} names the
 *       {@code address1} of every element of {@code addresses}; whether a key of a map is reached, too, is the
 *       engine's to say. {@link PodamObjectRandomizer}, {@link InstancioObjectRandomizer}, and
 *       {@link FixtureMonkeyObjectRandomizer} ignore a nested path, for their engines can not exclude by path.</li>
 * </ul>
 * A path which names nothing is not an error, for a subclass commonly passes a superset of paths.
 *
 * @param <T> the type of the instances to randomize.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PodamObjectRandomizer
 * @see InstancioObjectRandomizer
 * @see FixtureMonkeyObjectRandomizer
 * @see ObjectRandomizerUtils#newRandomizedInstanceOf(Class)
 * @see #newTargetInstance()
 */
public abstract class AbstractObjectRandomizer<T>
        implements ObjectRandomizer<T> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param targetClass   the class to be randomized.
     * @param excludedPaths paths of the slots to be excluded from randomization; each is split on runs of {@code .} and
     *                      white space, and rejoined with {@code .}, so that {@code " a . b "}, {@code "a..b"}, and
     *                      {@code "a b"} are all {@code "a.b"}; duplicates are dropped, and so is a path under another
     *                      one, such as {@code "address.city"} beside {@code "address"}.
     * @throws NullPointerException     when either argument is {@code null}, or when an element is {@code null}.
     * @throws IllegalArgumentException when an element is left with no segment, as {@code ""}, {@code "  "}, and
     *                                  {@code "."} are.
     * @apiNote A subclass is expected to declare a no-argument constructor which supplies both arguments, for
     *         that is how a located randomizer class is instantiated.
     * @see ObjectRandomizerUtils#moreExcludedPaths(Iterable, Iterable)
     */
    protected AbstractObjectRandomizer(final @NotNull Class<T> targetClass,
                                       final @NotNull Iterable<@NotBlank String> excludedPaths) {
        super();
        Objects.requireNonNull(targetClass, "targetClass is null");
        Objects.requireNonNull(excludedPaths, "excludedPaths is null");
        this.targetClass = targetClass;
        this.excludedPaths = _Paths.prune(
                StreamSupport.stream(excludedPaths.spliterator(), false)
                        .map(v -> _Paths.normalize(Objects.requireNonNull(v, "excludedPaths contains null")))
                        .collect(Collectors.toSet())
        );
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Returns a randomized instance of the {@link #targetClass}.
     *
     * @return a randomized instance of the {@link #targetClass}.
     */
    @Override
    public abstract T get();

    /**
     * Returns a new, yet to be randomized, instance of the {@link #targetClass}.
     *
     * @return a new instance of the {@link #targetClass}.
     * @throws RuntimeException when the {@link #targetClass} declares no no-argument constructor, or when that
     *                          constructor is inaccessible, abstract, or throws.
     * @implSpec The default implementation invokes the no-argument constructor of the {@link #targetClass},
     *         making it accessible where required, so that a {@code private} one is enough. Override this method for a
     *         class which declares no no-argument constructor, or which needs state assigned before it is randomized;
     *         an override may return a subclass of the {@link #targetClass}. Only {@link PodamObjectRandomizer} and
     *         {@link InstancioObjectRandomizer} call it &mdash; {@link FixtureMonkeyObjectRandomizer} lets its engine
     *         construct the instance.
     */
    protected T newTargetInstance() {
        return _Utils.newInstance(targetClass);
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * The target type to randomize.
     */
    protected final Class<T> targetClass;

    /**
     * An unmodifiable set of paths of the slots to exclude from the randomization, each normalized to its segments,
     * none of which carries white space, joined by {@code .}.
     * <p>
     * A path under another one, such as {@code "address.postalCode"} under {@code "address"}, is not in this set, for
     * the slot it names is never randomized anyway; no engine is handed a rule for a slot it was already told to leave
     * alone.
     *
     * @apiNote No name of a slot carries a {@code .}, so a name looked up in this set can only ever match a
     *         simple path. A flavor matches a name against this set in a predicate of its own; one which hands names to
     *         its engine as data hands the simple paths only.
     */
    protected final Set<String> excludedPaths;
}

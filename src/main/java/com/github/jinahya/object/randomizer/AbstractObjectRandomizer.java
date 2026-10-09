package com.github.jinahya.object.randomizer;

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
 * <ul>
 *   <li>A <em>simple</em> path, of a single segment such as {@code "id"}, is handed to the engine's own exclusion
 *       mechanism, and so is scoped as that engine scopes an exclusion; read each flavor's documentation before
 *       treating two flavors as interchangeable.</li>
 *   <li>A <em>nested</em> path, of {@code .}-separated segments such as {@code "address.address1"}, is anchored at
 *       the target instance: its first segment names a slot of the target, and each of the others a slot of the object
 *       the segments before it reach. A segment which reaches a collection, an array, an {@link java.util.Optional},
 *       or the values of a {@link java.util.Map}, reaches every element in it, so {@code "addresses.address1"} names
 *       the {@code address1} of every element of {@code addresses}. A slot so named is left at the value which a
 *       freshly constructed owner carries in it &mdash; for an owner the engine created itself, there is no other value
 *       to keep. A flavor whose engine can not exclude by path resets those slots once the engine is done, through
 *       {@link #resetNestedExcludedPaths(Object)}.</li>
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
     *                      {@code "a b"} are all {@code "a.b"}; duplicates are dropped.
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
     *         {@link InstancioObjectRandomizer} call it &mdash; the other two flavors let their engine construct the
     *         instance.
     */
    protected T newTargetInstance() {
        return _Utils.newInstance(targetClass);
    }

    /**
     * Resets, on the specified randomized instance, every slot which a nested path of {@link #excludedPaths} names, to
     * the value which a freshly constructed owner of that slot carries in it.
     *
     * @param instance the randomized instance.
     * @return the {@code instance}, as reset.
     * @throws NullPointerException when the {@code instance} is {@code null}.
     * @apiNote This is the fallback of a flavor whose engine can not exclude by path; an override of
     *         {@link #get()}, randomizing by hand or with an engine of its own, may call it likewise. Simple paths are
     *         not touched, for they are the engine's to honor.
     * @implNote Slots are found, and reset, by reflection, on the declared fields of the runtime class of each
     *         owner and of its superclasses. A fresh owner is constructed through its no-argument constructor, one per
     *         reset slot, so that a mutable value that constructor assigns is never shared. Nothing is written which is
     *         not taken from such a fresh owner: an owner whose class has no usable one keeps the slot as it is, as
     *         does a slot which can be neither read nor written, such as a component of a record, each logged. An enum
     *         constant, being shared by the whole of the program, is never walked into, and a key of a map is never
     *         reset.
     */
    protected T resetNestedExcludedPaths(final T instance) {
        Objects.requireNonNull(instance, "instance is null");
        excludedPaths.stream()
                .filter(_Paths::isNested)
                .forEach(v -> _Paths.reset(instance, _Paths.segments(v)));
        return instance;
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

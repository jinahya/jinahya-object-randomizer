package com.github.jinahya.object.randomizer;

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
 * Fields named in {@link #excludedFields} are left at whatever value the instance already carries, which is how a
 * generated identifier, a version, or an auditing column is kept out of the way of the persistence provider.
 *
 * @param <T> the type of the instances to randomize.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PodamObjectRandomizer
 * @see EasyRandomObjectRandomizer
 * @see InstancioObjectRandomizer
 * @see FixtureMonkeyObjectRandomizer
 * @see ObjectRandomizerUtils#newRandomizedInstanceOf(Class)
 * @see #newTargetInstance()
 */
public abstract class AbstractObjectRandomizer<T>
        implements ObjectRandomizer<T> {

//SEP:CONSTRUCTORS

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param targetClass    the class to be randomized.
     * @param excludedFields fields to be excluded from randomization; {@code null}, and blank, elements are dropped,
     *                       and the rest are stripped and deduplicated.
     * @throws NullPointerException when either argument is {@code null}.
     * @apiNote A subclass is expected to declare a no-argument constructor which supplies both arguments, for
     *         that is how a located randomizer class is instantiated.
     * @see ObjectRandomizerUtils#moreExcludedFields(Iterable, Iterable)
     */
    protected AbstractObjectRandomizer(final Class<T> targetClass, final Iterable<String> excludedFields) {
        super();
        Objects.requireNonNull(targetClass, "targetClass is null");
        Objects.requireNonNull(excludedFields, "excludedFields is null");
        this.targetClass = targetClass;
        this.excludedFields = StreamSupport.stream(excludedFields.spliterator(), false)
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(v -> !v.isBlank())
                .collect(Collectors.toUnmodifiableSet());
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
     *         {@link InstancioObjectRandomizer} call it -- the other two flavors let their engine construct the
     *         instance.
     * @see _Utils#newInstance(Class)
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
     * An unmodifiable set of field names to exclude from the randomization.
     */
    protected final Set<String> excludedFields;
}

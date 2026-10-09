package com.github.jinahya.object.randomizer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.co.jemos.podam.api.AbstractClassInfoStrategy;
import uk.co.jemos.podam.api.ClassInfo;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

import java.util.HashSet;
import java.util.List;

/**
 * An abstract randomizer which randomizes instances using <a href="https://mtedone.github.io/podam/">PODAM</a>.
 * <p>
 * This flavor populates an instance obtained from {@link #newTargetInstance()}, which is overridable; pick it when the
 * target class must be constructed a particular way.
 * <p>
 * <strong>The target class has to expose accessors.</strong> PODAM writes a property through its setter, and
 * recurses into one through its getter; it never assigns a field reflectively. A class which declares only fields,
 * which is the ordinary shape of a JPA entity mapped with field access, is therefore left <em>entirely
 * unpopulated</em>, silently and without an error. Use {@link InstancioObjectRandomizer} or
 * {@link FixtureMonkeyObjectRandomizer} for such a class — bearing in mind that, of those two, only
 * {@link InstancioObjectRandomizer} uses {@link #newTargetInstance()} — or extend {@link AbstractObjectRandomizer}
 * directly and populate the instance by hand.
 * <p>
 * This flavor honors {@code jakarta.validation.constraints} with nothing added to the classpath and nothing overridden;
 * PODAM reads them through its {@link uk.co.jemos.podam.common.BeanValidationStrategy}. Of the others,
 * {@link FixtureMonkeyObjectRandomizer} honors them once {@code fixture-monkey-jakarta-validation} is on the classpath,
 * and {@link InstancioObjectRandomizer} once {@link InstancioObjectRandomizer#getInstancioSettings() its settings} say
 * so. Read the {@code @implNote} below before treating this flavor as the safe default: its support is real, but
 * partial.
 *
 * @param <T> the type of the instances to randomize.
 * @implNote Constraint support is partial, and is keyed to the <em>field</em>: an annotation declared on a
 *         getter, or on a setter, is not seen, even though the value is written through the setter. Of those verified
 *         against PODAM 8.0.2, {@code @Size}, {@code @Email}, {@code @Past}, and a {@code @Min}/ {@code @Max} pair are
 *         honored, while a lone {@code @Max} is ignored and {@code @Pattern} yields {@code null}. Those which are
 *         honored are honored <em>one at a time</em>: a field carrying both a {@code @Size} and an {@code @Email}
 *         yields a string of the constrained length and no address, in either declaration order, the {@code @Email}
 *         having been dropped. Do not take a randomized instance to be a valid one without validating it.
 * @see <a href="https://mtedone.github.io/podam/">PODAM</a>
 * @see InstancioObjectRandomizer
 * @see FixtureMonkeyObjectRandomizer
 * @see uk.co.jemos.podam.common.BeanValidationStrategy
 */
public abstract class PodamObjectRandomizer<T>
        extends AbstractObjectRandomizer<T> {

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
     * @see AbstractObjectRandomizer#AbstractObjectRandomizer(Class, Iterable)
     */
    public PodamObjectRandomizer(final @NotNull Class<T> targetClass,
                                 final @NotNull Iterable<@NotBlank String> excludedPaths) {
        super(targetClass, excludedPaths);
    }

//SEP8//

    /**
     * Creates a new data provider strategy.
     *
     * @return a new data provider strategy.
     * @see RandomDataProviderStrategyImpl#RandomDataProviderStrategyImpl()
     */
    protected DataProviderStrategy getDataProviderStrategy() {
        return new RandomDataProviderStrategyImpl();
    }

    /**
     * Creates a new class info strategy which excludes the simple paths of {@link #excludedPaths} from the
     * {@link #targetClass}, and from any subclass of it.
     *
     * @return a new class info strategy which excludes the simple paths of {@link #excludedPaths}
     * @implNote A new instance, rather than
     *         {@link uk.co.jemos.podam.api.DefaultClassInfoStrategy#getInstance()}, whose excluded fields, being held
     *         by a singleton and never removed, would leak into every other randomizer of a same target class.
     *         <p>
     *         The exclusions are applied by overriding {@link AbstractClassInfoStrategy#getClassInfo(Class)}, rather
     *         than by {@link AbstractClassInfoStrategy#addExcludedField(Class, String) registering} them: PODAM
     *         introspects the <em>runtime</em> class of the instance being populated, and looks its exclusions up by
     *         exact class, so a registration made for the {@link #targetClass} is silently ignored whenever
     *         {@link #newTargetInstance()} yields a subclass — which an override of that method is explicitly allowed
     *         to do. Registrations made by a subclass, through {@code addExcludedField}, are merged in, so that
     *         customization keeps working.
     *         <p>
     *         A {@link ClassInfo} describes a class, not a place in the object graph, so nested paths can not be
     *         expressed here at all; {@link #get()} resets them once PODAM is done.
     */
    protected ClassInfoStrategy getClassInfoStrategy() {
        return new AbstractClassInfoStrategy() {
            @Override
            public ClassInfo getClassInfo(final Class<?> pojoClass) {
                final var excluded = new HashSet<String>();
                // exclusions registered through addExcludedField(Class, String), which are keyed by exact class
                final var registered = getExcludedFields(pojoClass);
                if (registered != null) {
                    excluded.addAll(registered);
                }
                if (targetClass.isAssignableFrom(pojoClass)) {
                    // PODAM takes these as field names; a nested path is no name of a field, so it is not handed over
                    excludedPaths.stream().filter(v -> !_Paths.isNested(v)).forEach(excluded::add);
                }
                // getExtraMethods(Class) is a raw map lookup, and may be null; the ClassInfo constructor adds the
                // argument to a collection without a null check
                final var extraMethods = getExtraMethods(pojoClass);
                return getClassInfo(
                        pojoClass,
                        getExcludedAnnotations(),
                        excluded,
                        this,
                        extraMethods == null ? List.of() : extraMethods
                );
            }
        };
    }

    /**
     * Creates a new factory created with a data provider strategy from the
     * {@link #getDataProviderStrategy() dataProviderStrategy} method, and set with a class info strategy from the
     * {@link #getClassInfoStrategy() classInfoStrategy} method.
     *
     * @return a new factory.
     * @see PodamFactoryImpl#PodamFactoryImpl(DataProviderStrategy)
     * @see #getClassInfoStrategy()
     * @see PodamFactory#setClassStrategy(ClassInfoStrategy)
     */
    protected PodamFactory getPodamFactory() {
        final var factory = new PodamFactoryImpl(getDataProviderStrategy());
        factory.setClassStrategy(getClassInfoStrategy());
        return factory;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec Populates a {@link #newTargetInstance() new instance} of the {@link #targetClass}, using a
     *         factory from the {@link #getPodamFactory() podamFactory} method, and then
     *         {@link #resetNestedExcludedPaths(Object) resets} the slots which nested excluded paths name.
     * @see PodamFactory#populatePojo(Object, java.lang.reflect.Type...)
     */
    @Override
    public T get() {
        return resetNestedExcludedPaths(getPodamFactory().populatePojo(newTargetInstance()));
    }
}

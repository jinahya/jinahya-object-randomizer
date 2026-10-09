package com.github.jinahya.object.randomizer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jeasy.random.DefaultExclusionPolicy;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.jeasy.random.api.ExclusionPolicy;
import org.jeasy.random.api.RandomizerContext;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An abstract randomizer which randomizes instances using
 * <a href="https://github.com/j-easy/easy-random">Easy Random</a>.
 * <p>
 * This flavor assigns fields reflectively, and so populates a class which declares no accessors at all — which is the
 * ordinary shape of a JPA entity mapped with field access, and precisely the shape {@link PodamObjectRandomizer} leaves
 * untouched. Pick it for such a class, bearing in mind that it does not use {@link #newTargetInstance()}.
 * <p>
 * <strong>The constructor does run, when there is one to run.</strong> Easy Random asks the target class for a
 * declared no-argument constructor and invokes it, making it accessible where required, and falls back to Objenesis —
 * which runs no constructor and no field initializer — only when that constructor is missing or throws. A JPA entity
 * always declares one, so a collection initialized at its declaration, and anything else the constructor assigns,
 * survives the randomization. What does <em>not</em> survive is whatever only an override of
 * {@link #newTargetInstance()} would have assigned: this flavor never calls that method, so the constructor Easy Random
 * invokes is the target class's own. Use {@link PodamObjectRandomizer}, or {@link InstancioObjectRandomizer}, when
 * {@link #newTargetInstance()} has to be honored.
 * <p>
 * <strong>Bean validation constraints are not honored, and can not be.</strong> Easy Random 6 removed its
 * constraint support outright: the {@code easy-random-bean-validation} artifact, and the
 * {@code org.jeasy.random.validation} package it carried, are gone. A class annotated with
 * {@code jakarta.validation.constraints} is randomized as if it declared no constraints at all — silently, and without
 * an error. Use {@link PodamObjectRandomizer}, which honors them by default, or {@link InstancioObjectRandomizer} or
 * {@link FixtureMonkeyObjectRandomizer}, each of which honors them once configured to.
 *
 * @param <T> the type of the instances to randomize.
 * @implNote Easy Random 6 is a single artifact — {@code org.jeasy:easy-random} — replacing the 5.x
 *         {@code easy-random-core}, {@code easy-random-bean-validation}, and {@code easy-random-randomizers} trio; its
 *         constraint support, which bound {@code javax.validation.constraints} and hence honored nothing on this
 *         platform anyway, was dropped along with it. That also takes {@code javax.validation:validation-api} off the
 *         classpath for good.
 * @see <a href="https://github.com/j-easy/easy-random">Easy Random</a>
 * @see PodamObjectRandomizer
 * @see InstancioObjectRandomizer
 * @see FixtureMonkeyObjectRandomizer
 */
abstract class EasyRandomObjectRandomizer<T>
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
    EasyRandomObjectRandomizer(final @NotNull Class<T> targetClass,
                               final @NotNull Iterable<@NotBlank String> excludedPaths) {
        super(targetClass, excludedPaths);
    }

//SEP8//

    /**
     * Returns the seed for the engine of the {@link #getEasyRandom() easyRandom} method.
     *
     * @return the seed.
     * @implSpec The default implementation returns a fresh value, from {@link ThreadLocalRandom#nextLong()}, on
     *         every invocation; override it, returning a constant, for a reproducible sequence.
     * @implNote An explicit seed is required here. {@link EasyRandomParameters#EasyRandomParameters()} starts
     *         at {@link EasyRandomParameters#DEFAULT_SEED}, which is a constant, and {@link #get()} builds a new engine
     *         on every invocation; left alone, every instance this randomizer ever produces would carry identical
     *         values, and a second one could not be persisted alongside the first under a unique constraint.
     * @see EasyRandomParameters#seed(long)
     */
    protected long getSeed() {
        return ThreadLocalRandom.current().nextLong();
    }

    /**
     * Creates new parameters which exclude the simple paths of {@link #excludedPaths} declared on the
     * {@link #targetClass}, or on any of its supertypes, and the nested ones where they lead.
     *
     * @return new parameters which exclude {@link #excludedPaths}.
     * @implNote Each exclusion is narrowed to the fields which the {@link #targetClass} actually declares or
     *         inherits, so that a field of a same name, declared on an unrelated type reached through an association,
     *         is still randomized. Note that the predicate identifies a field <em>declaration</em>, and that Easy
     *         Random evaluates it everywhere in the graph: when the {@link #targetClass} and an associated type inherit
     *         the excluded field from a common supertype, it is excluded on both. The {@link PodamObjectRandomizer}
     *         flavor, which scopes exclusions by the runtime class being populated, excludes it only on the
     *         {@link #targetClass} and its subclasses. The declaring class is matched with
     *         {@link Class#isAssignableFrom(Class)}, rather than with
     *         {@link FieldPredicates#inClass(Class) FieldPredicates.inClass}, whose exact
     *         {@link Class#equals(Object) equality} would leave a field inherited from a
     *         {@code jakarta.persistence.MappedSuperclass}, or from an abstract entity class, randomized; a generated
     *         identifier, a version, and an auditing column are usually declared exactly there. Names are matched by
     *         {@link String#equals(Object) equality}, rather than by
     *         {@link FieldPredicates#named(String) FieldPredicates.named}, which treats its argument as a regular
     *         expression, so that both flavors read an exclusion the same way.
     *         <p>
     *         Nested paths are excluded natively, by an {@link ExclusionPolicy} which matches the path Easy Random has
     *         descended, {@link RandomizerContext#getCurrentField()}, followed by the field at hand. That path descends
     *         into the elements of a collection, an array, and a map, so {@code "addresses.address1"} is excluded on
     *         every element, as the other flavors reset it; a bean <em>key</em> of a map is excluded too, which the
     *         others leave alone. The policy alone is not enough, though, for Easy Random
     *         <em>shares</em> beans: once it has populated
     *         {@link EasyRandomParameters#getObjectPoolSize() objectPoolSize} beans of a type, it hands out one of
     *         those again, populated wherever it was first reached, so {@link #get()} resets the nested paths as well.
     * @see EasyRandomParameters#excludeField(java.util.function.Predicate)
     * @see EasyRandomParameters#exclusionPolicy(ExclusionPolicy)
     * @see #getSeed()
     */
    protected EasyRandomParameters getEasyRandomParameters() {
        final var parameters = new EasyRandomParameters().seed(getSeed());
        parameters.excludeField(
                f -> excludedPaths.contains(f.getName()) && f.getDeclaringClass().isAssignableFrom(targetClass)
        );
        parameters.exclusionPolicy(new DefaultExclusionPolicy() {
            @Override
            public boolean shouldBeExcluded(final Field field, final RandomizerContext context) {
                if (super.shouldBeExcluded(field, context)) {
                    return true;
                }
                // the path of the parent, which is empty at the root, where a simple path has been handled above
                final var parent = context.getCurrentField();
                return !parent.isEmpty()
                       && excludedPaths.contains(parent + _Constants.EXCLUDED_PATH_SEPARATOR + field.getName());
            }
        });
        return parameters;
    }

    /**
     * Creates a new randomizer created with parameters from the {@link #getEasyRandomParameters() easyRandomParameters}
     * method.
     *
     * @return a new randomizer.
     * @see EasyRandom#EasyRandom(EasyRandomParameters)
     * @see #getEasyRandomParameters()
     */
    protected EasyRandom getEasyRandom() {
        return new EasyRandom(getEasyRandomParameters());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec Returns a new object of the {@link #targetClass}, from a randomizer of the
     *         {@link #getEasyRandom() easyRandom} method, with the slots which nested excluded paths name
     *         {@link #resetNestedExcludedPaths(Object) reset}. The parameters exclude those slots already, but a bean
     *         which Easy Random shares between two slots is populated where it is first reached; the reset makes the
     *         exclusion hold wherever else it is reached. A bean so shared with a slot which no path excludes has its
     *         excluded slot reset there too, for it is one and the same object.
     * @implNote Unlike {@link PodamObjectRandomizer} and {@link InstancioObjectRandomizer}, this class does not
     *         use the {@link #newTargetInstance()} method; Easy Random instantiates the {@link #targetClass} itself,
     *         through the {@link org.jeasy.random.ObjenesisObjectFactory} which
     *         {@link EasyRandomParameters#EasyRandomParameters()} installs by default. That factory, despite its name,
     *         tries {@link Class#getDeclaredConstructor(Class[])} first, and reaches for Objenesis only when that
     *         constructor is absent or throws — verified against Easy Random 6.0.1. A value which the target class's
     *         own no-argument constructor assigns therefore survives; a value which only an override of
     *         {@link #newTargetInstance()} would assign does not. Use {@link PodamObjectRandomizer}, or
     *         {@link InstancioObjectRandomizer}, when that matters.
     * @see EasyRandom#nextObject(Class)
     */
    @Override
    public T get() {
        return resetNestedExcludedPaths(getEasyRandom().nextObject(targetClass));
    }
}

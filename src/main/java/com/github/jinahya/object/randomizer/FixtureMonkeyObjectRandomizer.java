package com.github.jinahya.object.randomizer;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.property.DefaultPropertyGenerator;

import java.util.Objects;

/**
 * An abstract randomizer which randomizes instances using
 * <a href="https://naver.github.io/fixture-monkey">Fixture Monkey</a>.
 * <p>
 * This flavor assigns fields reflectively, through the
 * {@link FieldReflectionArbitraryIntrospector field-reflection introspector}, and so populates a class which declares
 * no accessors at all. Like {@link EasyRandomObjectRandomizer}, and unlike {@link InstancioObjectRandomizer}, it does
 * not use {@link #newTargetInstance()}: the engine constructs the instance itself, so an override of that method has no
 * say. It does, however, go through a <em>no-argument constructor</em> — where {@link EasyRandomObjectRandomizer}
 * bypasses constructors entirely — so a value which the no-argument constructor, or a field initializer, assigns is in
 * place before the fields are written, and an excluded field keeps it.
 * <p>
 * It is also the flavor whose builder a subclass can drive declaratively: override {@link #getArbitraryBuilder()} and
 * set, fix, or post-condition a property by name before it is sampled.
 * <p>
 * <strong>Bean validation constraints are honored only when asked for.</strong> Constraint support lives in a
 * separate plugin: add {@code com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation} and register its
 * {@code JakartaValidationPlugin} by overriding {@link #getFixtureMonkey()}. The {@code javax} counterpart,
 * {@code fixture-monkey-javax-validation}, is the wrong one for this platform.
 *
 * @param <T> the type of the instances to randomize.
 * @implNote A no-argument constructor is <em>required</em>: the introspector resolves one reflectively and
 *         rethrows, unchecked, when the target class declares none. A field it can not write is logged as a warning and
 *         left alone, rather than raising an error, so — as with every flavor here — do not take a randomized instance
 *         to be a complete one without asserting on it.
 * @see <a href="https://naver.github.io/fixture-monkey">Fixture Monkey</a>
 * @see PodamObjectRandomizer
 * @see EasyRandomObjectRandomizer
 * @see InstancioObjectRandomizer
 * @see FieldReflectionArbitraryIntrospector
 */
public abstract class FixtureMonkeyObjectRandomizer<T>
        extends AbstractObjectRandomizer<T> {

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param targetClass    the class to be randomized.
     * @param excludedFields fields to be excluded from randomization.
     */
    public FixtureMonkeyObjectRandomizer(final Class<T> targetClass, final Iterable<String> excludedFields) {
        super(targetClass, excludedFields);
    }

//SEP8//

    /**
     * Creates a new engine which writes fields reflectively and which generates no property named in
     * {@link #excludedFields}.
     *
     * @return a new engine.
     * @implNote The exclusions are applied by replacing the property generator for the {@link #targetClass},
     *         and for any subclass of it
     *         ({@link com.navercorp.fixturemonkey.FixtureMonkeyBuilder#pushAssignableTypePropertyGenerator(Class,
     *         com.navercorp.fixturemonkey.api.property.PropertyGenerator) pushAssignableTypePropertyGenerator}), which
     *         drops the excluded properties before anything is generated — leaving each at whatever the no-argument
     *         constructor assigned, a primitive default included. Unlike the predicate the other flavors use, this is
     *         scoped by <em>type</em> rather than by field declaration, so a field of a same name on an unrelated
     *         associated type is unaffected, while an inherited field, which the generator enumerates among the
     *         target's properties, is excluded. Properties are enumerated with
     *         {@link DefaultPropertyGenerator#FIELD_METHOD_PROPERTY_GENERATOR}, which is what the engine would use
     *         anyway, and
     *         {@link com.navercorp.fixturemonkey.FixtureMonkeyBuilder#defaultNotNull(boolean) defaultNotNull} keeps an
     *         association from being sampled as {@code null}.
     * @see FixtureMonkey#builder()
     */
    protected FixtureMonkey getFixtureMonkey() {
        return FixtureMonkey.builder()
                .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
                .defaultNotNull(true)
                .pushAssignableTypePropertyGenerator(targetClass,
                                                     property -> DefaultPropertyGenerator.FIELD_METHOD_PROPERTY_GENERATOR
                                                             .generateChildProperties(property).stream()
                                                             .filter(child -> !excludedFields.contains(
                                                                     child.getName()))
                                                             .toList())
                .build();
    }

    /**
     * Creates a new builder, for the {@link #targetClass}, from the engine of the
     * {@link #getFixtureMonkey() fixtureMonkey} method.
     *
     * @return a new builder for the {@link #targetClass}.
     * @implSpec Override this method to customize an instance before it is sampled — say,
     *         {@code super.getArbitraryBuilder().set("name", "...")}.
     * @see FixtureMonkey#giveMeBuilder(Class)
     * @see #getFixtureMonkey()
     */
    protected ArbitraryBuilder<T> getArbitraryBuilder() {
        return getFixtureMonkey().giveMeBuilder(targetClass);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NullPointerException when the engine samples {@code null}, which it does for a type it can not
     *                              introspect.
     * @implSpec Samples a new instance from the {@link #getArbitraryBuilder() arbitraryBuilder}.
     * @see ArbitraryBuilder#sample()
     */
    @Override
    public T get() {
        return Objects.requireNonNull(getArbitraryBuilder().sample(), "Fixture Monkey returned null");
    }
}

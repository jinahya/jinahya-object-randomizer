package com.github.jinahya.object.randomizer;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.plugin.Plugin;
import com.navercorp.fixturemonkey.api.property.DefaultPropertyGenerator;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * An abstract randomizer which randomizes instances using
 * <a href="https://naver.github.io/fixture-monkey">Fixture Monkey</a>.
 * <p>
 * This flavor assigns fields reflectively, through the
 * {@link FieldReflectionArbitraryIntrospector field-reflection introspector}, and so populates a class which declares
 * no accessors at all. Unlike {@link InstancioObjectRandomizer}, it does not use {@link #newTargetInstance()}: the
 * engine constructs the instance itself, so an override of that method has no say. It does, however, go through a
 * <em>no-argument constructor</em>, so a value which that constructor, or a field initializer, assigns is in place
 * before the fields are written, and an excluded field keeps it.
 * <p>
 * It is also the flavor whose builder a subclass can drive declaratively: override {@link #getArbitraryBuilder()} and
 * set, fix, or post-condition a property by name before it is sampled.
 * <p>
 * <strong>Bean validation constraints are honored once the plugin is on the classpath.</strong> Unlike every other
 * engine here, Fixture Monkey keeps its constraint support in a <em>second</em> artifact: add
 * {@code com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation}, and {@link #getFixtureMonkey()} finds its
 * {@code JakartaValidationPlugin} and registers it. Nothing is overridden, and this module does not depend on that
 * artifact — the lookup is reflective, so leaving it off the classpath is not an error, only a
 * {@link System.Logger.Level#DEBUG DEBUG} line and constraints which go unread. The {@code javax} counterpart,
 * {@code fixture-monkey-javax-validation}, is the wrong one for this platform.
 *
 * @param <T> the type of the instances to randomize.
 * @implNote A no-argument constructor is <em>required</em>: the introspector resolves one reflectively and
 *         rethrows, unchecked, when the target class declares none. A field it can not write is logged as a warning and
 *         left alone, rather than raising an error, so — as with every flavor here — do not take a randomized instance
 *         to be a complete one without asserting on it.
 * @see <a href="https://naver.github.io/fixture-monkey">Fixture Monkey</a>
 * @see PodamObjectRandomizer
 * @see InstancioObjectRandomizer
 * @see FieldReflectionArbitraryIntrospector
 */
public abstract class FixtureMonkeyObjectRandomizer<T>
        extends AbstractObjectRandomizer<T> {

    private static final System.Logger logger = System.getLogger(FixtureMonkeyObjectRandomizer.class.getName());

    /**
     * Holds the {@code JakartaValidationPlugin}, when there is one to hold.
     *
     * @implNote Resolved once, and reflectively: constraint support is a separate artifact,
     *         {@code com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation}, which this module does not depend
     *         on. The plugin interface it implements does, however, come with the engine itself, so the instance can be
     *         handed to the builder without this class ever referencing the plugin type. A {@code null} means the
     *         artifact is absent, which is not an error -- it is the classpath of a consumer which did not ask for
     *         constraints.
     */
    private static final class PluginHolder {

        private static final String PLUGIN_CLASS_NAME =
                "com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin";

        private static final @Nullable Plugin INSTANCE = locate();

        private static @Nullable Plugin locate() {
            try {
                return (Plugin) Class.forName(PLUGIN_CLASS_NAME).getConstructor().newInstance();
            } catch (final ClassNotFoundException cnfe) {
                logger.log(System.Logger.Level.DEBUG,
                           () -> PLUGIN_CLASS_NAME + " is not on the classpath;"
                                 + " jakarta.validation.constraints will not be honored");
                return null;
            } catch (final ReflectiveOperationException roe) {
                logger.log(System.Logger.Level.WARNING, () -> "failed to instantiate " + PLUGIN_CLASS_NAME, roe);
                return null;
            }
        }

        private PluginHolder() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param targetClass    the class to be randomized.
     * @param excludedFields fields to be excluded from randomization; {@code null}, and blank, elements are dropped,
     *                       and the rest are stripped and deduplicated.
     * @throws NullPointerException when either argument is {@code null}.
     * @apiNote A subclass is expected to declare a no-argument constructor which supplies both arguments, for
     *         that is how a located randomizer class is instantiated.
     * @see AbstractObjectRandomizer#AbstractObjectRandomizer(Class, Iterable)
     */
    public FixtureMonkeyObjectRandomizer(final Class<T> targetClass, final Iterable<String> excludedFields) {
        super(targetClass, excludedFields);
    }

    // -----------------------------------------------------------------------------------------------------------------

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
     *         association from being sampled as {@code null}. The {@code JakartaValidationPlugin} is registered here
     *         too, when the artifact which carries it is on the classpath.
     * @implSpec An override which does not build on what this method returns gives up the exclusions, and the
     *         plugin, along with them.
     * @see FixtureMonkey#builder()
     */
    protected FixtureMonkey getFixtureMonkey() {
        final var builder = FixtureMonkey.builder()
                .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
                .defaultNotNull(true)
                .pushAssignableTypePropertyGenerator(
                        targetClass,
                        property -> DefaultPropertyGenerator.FIELD_METHOD_PROPERTY_GENERATOR
                                .generateChildProperties(property).stream()
                                .filter(child -> !excludedFields.contains(child.getName()))
                                .toList()
                );
        if (PluginHolder.INSTANCE != null) {
            builder.plugin(PluginHolder.INSTANCE);
        }
        return builder.build();
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

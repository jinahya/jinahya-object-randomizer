package com.github.jinahya.object.randomizer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.instancio.Instancio;
import org.instancio.InstancioObjectApi;
import org.instancio.Select;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

/**
 * An abstract randomizer which randomizes instances using <a href="https://www.instancio.org">Instancio</a>.
 * <p>
 * This flavor assigns fields reflectively, and so populates a class which declares no accessors at all, yet, unlike
 * {@link FixtureMonkeyObjectRandomizer}, it <em>fills an instance it is handed</em>, from {@link #newTargetInstance()},
 * which is overridable. It is, of the flavors which do not require accessors, the one to pick when the target class
 * must be constructed a particular way.
 * <p>
 * <strong>A value already assigned is kept.</strong> Only a {@code null} field, and a primitive still at its
 * default, is filled; whatever a constructor, or an override of {@link #newTargetInstance()}, has assigned survives. A
 * generated identifier deliberately initialized to a sentinel value is therefore preserved even when it is not named in
 * {@link #excludedPaths}.
 * <p>
 * <strong>Bean validation constraints are honored.</strong> Instancio reads
 * {@code jakarta.validation.constraints} — the Jakarta annotations, not the {@code javax} ones — once
 * {@link org.instancio.settings.Keys#BEAN_VALIDATION_ENABLED} is set, which Instancio leaves off but
 * {@link #getInstancioSettings()} turns on. Nothing has to be overridden. Override it to turn the setting back off, or
 * to add {@link org.instancio.settings.Keys#JPA_ENABLED}, which has {@code jakarta.persistence.Column#length()}
 * respected as well:
 * <pre>{@code
 * @Override
 * protected Settings getInstancioSettings() {
 *     return super.getInstancioSettings()
 *             .set(Keys.JPA_ENABLED, true);
 * }
 * }</pre>
 *
 * @param <T> the type of the instances to randomize.
 * @implNote {@link Instancio#ofObject(Object)}, and the {@link InstancioObjectApi#fill() fill()} which
 *         terminates it, are marked {@link org.instancio.documentation.ExperimentalApi} as of Instancio 6.0.0; an
 *         immutable target class, whose fields a constructor assigns once, is not a fit for this flavor at all.
 *         Constraint support is keyed to the <em>field</em> by default
 *         ({@link org.instancio.settings.Keys#BEAN_VALIDATION_TARGET}), which is the right default for an entity mapped
 *         with field access.
 * @see <a href="https://www.instancio.org">Instancio</a>
 * @see PodamObjectRandomizer
 * @see FixtureMonkeyObjectRandomizer
 * @see org.instancio.settings.Keys#BEAN_VALIDATION_ENABLED
 * @see org.instancio.settings.Keys#JPA_ENABLED
 */
public abstract class InstancioObjectRandomizer<T>
        extends AbstractObjectRandomizer<T> {

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param targetClass    the class to be randomized.
     * @param excludedPaths paths of the slots to be excluded from randomization; each is split on runs of
     *                      {@code .} and white space, and rejoined with {@code .}, so that {@code " a . b "},
     *                      {@code "a..b"}, and {@code "a b"} are all {@code "a.b"}; duplicates are dropped.
     * @throws NullPointerException     when either argument is {@code null}, or when an element is {@code null}.
     * @throws IllegalArgumentException when an element is left with no segment, as {@code ""}, {@code "  "}, and
     *                                  {@code "."} are.
     * @apiNote A subclass is expected to declare a no-argument constructor which supplies both arguments, for
     *         that is how a located randomizer class is instantiated.
     * @see AbstractObjectRandomizer#AbstractObjectRandomizer(Class, Iterable)
     */
    public InstancioObjectRandomizer(final @NotNull Class<T> targetClass,
                                     final @NotNull Iterable<@NotBlank String> excludedPaths) {
        super(targetClass, excludedPaths);
    }

//SEP8//

    /**
     * Returns the settings for the population of each instance.
     *
     * @return the settings.
     * @implSpec The default implementation returns {@link Settings#create()} with
     *         {@link org.instancio.settings.Keys#BEAN_VALIDATION_ENABLED} set, which Instancio itself leaves off.
     *         {@link org.instancio.settings.Keys#JPA_ENABLED JPA} support stays off, since it reads
     *         {@code jakarta.persistence} annotations which a plain target class does not carry. An override which does
     *         not build on what this method returns gives up the constraint support along with it.
     * @see Settings#create()
     * @see org.instancio.settings.Keys#BEAN_VALIDATION_ENABLED
     */
    protected Settings getInstancioSettings() {
        return Settings.create()
                .set(Keys.BEAN_VALIDATION_ENABLED, true);
    }

    /**
     * Creates a population API for the specified instance, with the simple paths of {@link #excludedPaths} already
     * ignored.
     *
     * @param instance the instance to be populated.
     * @return a population API for the {@code instance}.
     * @implNote Each exclusion is narrowed to the fields which the <em>runtime</em> class of the
     *         {@code instance} declares or inherits — an override of {@link #newTargetInstance()} is explicitly allowed
     *         to return a subclass of the {@link #targetClass} — so that a field of a same name, declared on an
     *         unrelated type reached through an association, is still populated. The predicate identifies a field
     *         <em>declaration</em>, so a field which the target class and an associated type both inherit from a
     *         common supertype is ignored on both. The selector is made
     *         {@link org.instancio.LenientSelector#lenient() lenient}, since a name which matches nothing is a
     *         legitimate exclusion here — a subclass commonly passes a superset of names — while Instancio, in strict
     *         mode, would fail on an unused selector.
     *         <p>
     *         Nested paths are not expressed here. Instancio scopes a selector
     *         {@link org.instancio.ScopeableSelector#within(org.instancio.Scope...) within} enclosing slots, but not
     *         <em>directly</em> within them, so {@code "a.b"} would also match an {@code a.x.b}; {@link #get()} resets
     *         them, exactly, once Instancio is done.
     * @see Instancio#ofObject(Object)
     * @see #getInstancioSettings()
     */
    protected InstancioObjectApi<T> getInstancio(final T instance) {
        return Instancio.ofObject(instance)
                .withSettings(getInstancioSettings())
                .ignore(Select.fields(f -> excludedPaths.contains(f.getName())
                                           && f.getDeclaringClass().isAssignableFrom(instance.getClass()))
                                .lenient());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec Fills the instance from {@link #newTargetInstance()}, through the
     *         {@link #getInstancio(Object) instancio} API, {@link #resetNestedExcludedPaths(Object) resets} the slots
     *         which nested excluded paths name, and returns that very instance.
     * @see InstancioObjectApi#fill()
     */
    @Override
    public T get() {
        final T instance = newTargetInstance();
        getInstancio(instance).fill();
        return resetNestedExcludedPaths(instance);
    }
}

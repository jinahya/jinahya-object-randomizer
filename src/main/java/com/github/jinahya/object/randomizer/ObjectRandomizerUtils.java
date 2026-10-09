package com.github.jinahya.object.randomizer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link ObjectRandomizer}.
 *
 * <h2>The naming convention</h2>
 * A randomizer is found for a target class by name. The randomizer class is a <em>sibling</em> of the target, which
 * implements {@link ObjectRandomizer} and whose binary name is the binary name of the target followed by a postfix of
 * either {@code "Randomizer"} or {@code "_Randomizer"}. For a top-level target class {@code Foo}, that is a top-level
 * {@code FooRandomizer} of the same package name, probed first, and then {@code Foo_Randomizer}; for a target class
 * {@code Outer.Foo}, nested in {@code Outer}, it is {@code Outer.FooRandomizer}, nested beside it in {@code Outer}.
 * <p>
 * The name is resolved through the class loader of the target, so, for a top-level target, the convention spans source
 * sets, jars and modules alike: a {@code Foo} declared in {@code main} and a {@code FooRandomizer} declared in
 * {@code test} share the package name, and both are on the test classpath. On the module path, though, a package can
 * not be split across named modules, so the randomizer belongs to the module of the target, or is patched into it, as
 * test runners do.
 * <p>
 * A class which is not a sibling of the target is never located: neither a local nor an anonymous class, which can not
 * carry the required name, nor a class nested inside the target, nor a top-level class named after a nested target. A
 * nested sibling has to be declared in the source of the enclosing class; a class nested in a class of {@code main}
 * therefore has to be declared as a top-level class to be randomizable from {@code test}. Note that a subclass of a
 * class which has a randomizer is located by the convention, and not by the randomizer of its superclass, which could
 * not produce instances of the subclass anyway.
 * <p>
 * A located class is instantiated reflectively, and so has to declare an accessible no-argument constructor which
 * supplies the target class to its superclass.
 *
 * <h2>Absence and failure</h2>
 * A lookup here returns an {@link Optional}, and an empty one carries no reason. The convention may name nothing at
 * all; it may name a class which is not an {@link ObjectRandomizer}; the randomizer may produce nothing, or something
 * which is not an instance of the target. None of those is raised &mdash; a caller which needs to tell them apart does
 * its own checking &mdash; though each is logged where it is passed over, the ones which look like a misconfiguration
 * at {@link System.Logger.Level#WARNING WARNING}.
 * <p>
 * One thing does fail: a class which the convention locates, and which is therefore meant to be used, but which can not
 * be instantiated.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ObjectRandomizer
 * @see #newRandomizedInstanceOf(Class)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ObjectRandomizerUtils {

    private static final System.Logger logger = System.getLogger(ObjectRandomizerUtils.class.getName());

    /**
     * The postfixes of the {@linkplain ObjectRandomizerUtils naming convention}, in the order they are probed.
     */
    private static final String[] POSTFIXES = {"Randomizer", "_Randomizer"};

    /**
     * Merges specified iterables of excluded paths.
     *
     * @param excludedPaths     the first iterable of excluded paths.
     * @param moreExcludedPaths the second iterable of excluded paths.
     * @return an unmodifiable {@link Iterable} of merged excluded paths.
     * @throws NullPointerException when either argument is {@code null}, or when an element of either is
     *                              {@code null}.
     * @apiNote This method is for a subclass which adds to the exclusions of the randomizer it extends.
     *         Elements are concatenated as they are, in order, with no deduplication; the
     *         {@link AbstractObjectRandomizer#AbstractObjectRandomizer(Class, Iterable) randomizer constructor} strips
     *         them, rejects a blank one, and deduplicates the rest.
     */
    public static Iterable<String> moreExcludedPaths(
            final @NotNull Iterable<@NotBlank String> excludedPaths,
            final @NotNull Iterable<@NotBlank String> moreExcludedPaths) {
        Objects.requireNonNull(excludedPaths, "excludedPaths is null");
        Objects.requireNonNull(moreExcludedPaths, "moreExcludedPaths is null");
        final var merged = new ArrayList<String>();
        excludedPaths.forEach(merged::add);
        moreExcludedPaths.forEach(merged::add);
        // List.copyOf rejects a null element, as the randomizer constructor would
        return List.copyOf(merged);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Locates, by the {@linkplain ObjectRandomizerUtils naming convention}, the randomizer class of the specified
     * targetClass class.
     *
     * @param targetClass the targetClass class whose randomizer class is located.
     * @return an optional of the randomizer class of the {@code targetClass}; {@code empty} when the convention names
     *         no usable one.
     * @throws NullPointerException when the {@code targetClass} is {@code null}.
     * @implNote The postfixes are probed in order, and the first candidate which exists <em>and</em> implements
     *         {@link ObjectRandomizer} is taken; whether it can be instantiated is left to
     *         {@link _Utils#newInstance(Class)}.
     *         <p>
     *         A candidate is passed over, rather than failed on, when it does not exist, when it can not be loaded, and
     *         when it does not implement {@link ObjectRandomizer} -- the probe simply continues with the next postfix,
     *         and an empty optional carries no reason. The last two are a misconfiguration all the same, so each is
     *         logged at {@link System.Logger.Level#WARNING WARNING}; a name which merely does not exist is logged at
     *         {@link System.Logger.Level#TRACE TRACE}.
     */
    static Optional<Class<?>> randomizerClassOf(final Class<?> targetClass) {
        Objects.requireNonNull(targetClass, "targetClass is null");
        // the class loader of the targetClass, rather than the one of this class, for the targetClass may have been
        // loaded by an other class loader
        final var classLoader = Optional.ofNullable(targetClass.getClassLoader())
                .orElseGet(ObjectRandomizerUtils.class::getClassLoader);
        return Arrays.stream(POSTFIXES)
                .map(p -> targetClass.getName() + p)
                .<Class<?>>map(n -> {
                    final Class<?> clazz;
                    try {
                        // no initialization; the class is merely being probed
                        clazz = Class.forName(n, false, classLoader);
                    } catch (final ClassNotFoundException cnfe) {
                        logger.log(System.Logger.Level.TRACE, "no class named {0}; target: {1}", n, targetClass);
                        return null;
                    } catch (final LinkageError le) {
                        // Class.forName links the class, which resolves its superclass; a candidate whose supertype
                        // is missing, or is otherwise unloadable, therefore fails with an Error rather than an
                        // exception. A probe is speculative, so this does not end it -- but, unlike a name which
                        // simply does not exist, it is a misconfiguration worth seeing.
                        logger.log(System.Logger.Level.WARNING,
                                   "failed to load a candidate; className: " + n + "; probing further", le);
                        return null;
                    }
                    if (!ObjectRandomizer.class.isAssignableFrom(clazz)) {
                        // the name claims a role which the class does not fill; the probe goes on, as it does for a
                        // name which does not exist, but -- unlike that one -- this is worth seeing
                        logger.log(System.Logger.Level.WARNING, "not an {0}; class: {1}, target: {2}",
                                   ObjectRandomizer.class, clazz, targetClass);
                        return null;
                    }
                    logger.log(System.Logger.Level.TRACE, "located {0}; target: {1}", clazz, targetClass);
                    return clazz;
                })
                .filter(Objects::nonNull)
                .findFirst();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns, an optional of, a new instance of the randomizer class located for the specified targetClass class by
     * the {@linkplain ObjectRandomizerUtils naming convention}.
     * <p>
     * An empty optional carries no reason: the convention names no class for the {@code targetClass}, or the class it
     * names is not an {@link ObjectRandomizer}. A class which <em>is</em> located, though, is one the developer meant
     * to be used, so failing to instantiate it is a fault, and is raised rather than reported as an absence.
     *
     * @param targetClass the targetClass class.
     * @param <T>         the targetClass type parameter.
     * @return an optional of a new instance of the randomizer located for the {@code targetClass}; {@code empty} when
     *         the convention names no usable one.
     * @throws NullPointerException when the {@code targetClass} is {@code null}.
     * @throws RuntimeException     when the located randomizer can not be instantiated.
     * @apiNote The randomizer is returned as an {@code ObjectRandomizer<T>} without the class it is declared
     *         for being checked against the {@code targetClass}, for that declaration proves less than it appears to; a
     *         randomizer is verified on what it <em>produces</em>, which is what
     *         {@link #newRandomizedInstanceOf(Class)} does. A caller which calls {@link ObjectRandomizer#get() get()}
     *         on the returned randomizer itself makes that check its own.
     * @see #newRandomizedInstanceOf(Class)
     */
    @SuppressWarnings({
            "unchecked"
    })
    public static <T> Optional<ObjectRandomizer<T>> newRandomizerInstanceOf(final Class<T> targetClass) {
        return randomizerClassOf(targetClass)
                .map(c -> (ObjectRandomizer<T>) _Utils.newInstance(c));
    }

    /**
     * Returns, an optional of, a randomized instance of the specified targetClass class, using the randomizer located
     * for it by the {@linkplain ObjectRandomizerUtils naming convention}.
     * <p>
     * An empty optional means simply that no instance is handed back, with no reason attached: either no randomizer is
     * located for the {@code targetClass}, or the one located produces nothing, or it produces something which is not
     * an instance of the {@code targetClass}. A caller which needs to tell those apart calls
     * {@link #newRandomizerInstanceOf(Class)} and does its own checking.
     *
     * @param targetClass the targetClass class.
     * @param <T>         the targetClass type parameter.
     * @return an optional of randomized instance of the {@code targetClass}; {@code empty} when none is obtained.
     * @throws NullPointerException when the {@code targetClass} is {@code null}.
     * @throws RuntimeException     when the located randomizer can not be instantiated, and when its
     *                              {@link ObjectRandomizer#get() get()} throws.
     * @apiNote The randomizer is judged on what it produced, rather than on the class it is declared for: an
     *         {@code ObjectRandomizer<Foo>} whose {@link ObjectRandomizer#get() get()} returns a {@code Bar} is
     *         well-formed at compile time, erasure leaving nothing to enforce it, so the produced instance is the
     *         evidence which matters. One which fails that test is dropped, rather than raised on.
     * @see #newRandomizerInstanceOf(Class)
     */
    public static <T> Optional<T> newRandomizedInstanceOf(final Class<T> targetClass) {
        return newRandomizerInstanceOf(targetClass)
                .map(r -> {
                    final var instance = r.get();
                    if (instance == null) {
                        // the contract of get() promises an instance; one which hands back nothing is broken
                        logger.log(System.Logger.Level.WARNING, "produced null; randomizer: {0}, target: {1}",
                                   r, targetClass);
                        return null;
                    }
                    if (!targetClass.isInstance(instance)) {
                        // erasure let this through at compile time; dropped, as documented, but worth seeing
                        logger.log(System.Logger.Level.WARNING,
                                   "produced an incompatible instance; class: {0}, randomizer: {1}, target: {2}",
                                   instance.getClass(), r, targetClass);
                        return null;
                    }
                    return targetClass.cast(instance);
                });
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private ObjectRandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}

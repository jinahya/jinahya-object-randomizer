package com.github.jinahya.object.randomizer;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities, internal to this package, for locating and instantiating classes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __InstantiatorUtils#locateStandard(Class)
 * @see __RandomizerUtils#locateStandard(Class)
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
final class ___Utils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Finds a sibling class of the specified type, which has any of the specified postfixes.
     *
     * @param type      the type.
     * @param postfixes the postfix candidates, in the order they are probed.
     * @return the first sibling class which exists; {@code null} when none does.
     * @apiNote This method judges nothing but the name. A class which is found is returned, whatever it is, for
     *         a class named by the convention is a class the developer meant to provide: whether it extends the
     *         expected role, and whether it can be instantiated, is the caller's to check, and to fail on.
     * @implNote Every probed class name is logged at {@link System.Logger.Level#TRACE TRACE}, so that a class
     *         which is not located, due to a name not following the convention, can be diagnosed. A candidate which is
     *         found but can not be loaded is logged at {@link System.Logger.Level#WARNING WARNING}, and the probe
     *         continues with the next postfix.
     */
    @Nullable
    static Class<?> siblingClassForPostfixes(final Class<?> type, final String... postfixes) {
        Objects.requireNonNull(type, "type is null");
        if (Objects.requireNonNull(postfixes, "postfixes is null").length == 0) {
            throw new IllegalArgumentException("postfixes is empty");
        }
        // the class loader of the type, rather than the one of this class, for the type may have been loaded by an
        // other class loader
        final var classLoader = Optional.ofNullable(type.getClassLoader())
                .orElseGet(___Utils.class::getClassLoader);
        final String typeName = type.getName();
        for (final String postfix : postfixes) {
            // isBlank() already ignores surrounding whitespace, so stripping first was redundant here;
            // the strip() below is not -- it normalizes the postfix actually used to build the name
            if (postfix == null || postfix.isBlank()) {
                continue;
            }
            final String className = typeName + postfix.strip();
            try {
                // no initialization; the class is merely being probed
                final Class<?> clazz = Class.forName(className, false, classLoader);
                logger.log(System.Logger.Level.TRACE, "located {0} for {1}", clazz, type);
                return clazz;
            } catch (final ClassNotFoundException cnfe) {
                logger.log(System.Logger.Level.TRACE, "no class named {0}", className);
            } catch (final LinkageError le) {
                // Class.forName links the class, which resolves its superclass; a candidate whose supertype is
                // missing, or is otherwise unloadable, therefore fails with an Error rather than an exception.
                // A probe is speculative, so this does not end it -- but, unlike a name which simply does not
                // exist, it is a misconfiguration worth seeing.
                logger.log(System.Logger.Level.WARNING, "failed to load " + className + "; probing further", le);
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Returns the instance a located producer produced, as the specified target class.
     *
     * @param target   the class the caller requires an instance of.
     * @param producer the located producer; an {@link __Instantiator} or a {@link __Randomizer}.
     * @param produced what the {@code producer} produced.
     * @param <T>      the target class type parameter.
     * @return the {@code produced} instance, as the {@code target}.
     * @throws RuntimeException when the {@code produced} instance is {@code null}, or is not an instance of the
     *                          {@code target}.
     * @apiNote A producer is checked on what it <em>produced</em>, rather than on the class it is declared for,
     *         because the declaration proves less than it appears to: a {@code __Randomizer<Foo>} whose {@code get()}
     *         returns a {@code Bar} is well-formed at compile time, erasure leaving nothing to enforce it. The produced
     *         instance is the evidence which matters -- it is what the caller receives.
     * @implNote A producer which was located, and then produces the wrong thing, is a fault rather than an
     *         absence: it is thrown, not logged and skipped. A developer who declares a counterpart by the naming
     *         convention meant it to be used, and would rather be told than quietly fall back.
     */
    @SuppressWarnings({
            "java:S112" // Generic exceptions should never be thrown
    })
    static <T> T produced(final Class<T> target, final Object producer, final @Nullable Object produced) {
        assert target != null;
        assert producer != null;
        if (produced == null) {
            throw new RuntimeException(producer + ", located for " + target + ", produced nothing");
        }
        if (!target.isInstance(produced)) {
            throw new RuntimeException(
                    producer + ", located for " + target + ", produced a " + produced.getClass()
            );
        }
        return target.cast(produced);
    }


    /**
     * Requires that a located class extends the supertype of the role it was located for.
     *
     * @param located   the located class.
     * @param supertype the supertype of the role.
     * @param target    the class the {@code located} class was located for; for diagnostics only.
     * @return the {@code located} class.
     * @throws RuntimeException when the {@code located} class does not extend the {@code supertype}.
     * @apiNote A class named by the convention, which is not of the role that name claims, is a fault; the
     *         probe does not judge it, so that the failure is reported here, where the role is known.
     */
    @SuppressWarnings({
            "java:S112" // Generic exceptions should never be thrown
    })
    static Class<?> requireSubtype(final Class<?> located, final Class<?> supertype, final Class<?> target) {
        assert located != null;
        assert supertype != null;
        if (!supertype.isAssignableFrom(located)) {
            throw new RuntimeException(
                    located + ", located for " + target + ", does not extend " + supertype
            );
        }
        return located;
    }

    /**
     * Returns a new instance of the specified located class, as the role it was located for.
     *
     * @param target  the class the {@code located} class was located for; for diagnostics only.
     * @param role    the supertype of the role the {@code located} class was located for; an
     *                {@link __Instantiator} or a {@link __Randomizer}.
     * @param located the located class; {@code null}, when none was located.
     * @param <R>     the role type parameter.
     * @return a new instance of the {@code located} class, as the {@code role}; {@code null}, when the
     *         {@code located} class is {@code null}.
     * @throws RuntimeException when the {@code located} class does not extend the {@code role}, or can not be
     *                          instantiated.
     * @apiNote Absence and fault are kept apart here: a {@code null} is returned for a class which was never
     *         located, and left for the caller to interpret -- a missing instantiator is an absence to fall back on,
     *         while a missing randomizer may not be -- whereas a class which <em>was</em> located, and is then of the
     *         wrong role, or is not instantiable, throws.
     *         <p>
     *         No check of the class the located instance is declared for is made here: a producer is verified on what
     *         it {@link #produced(Class, Object, Object) produced}, which is the evidence that actually matters, and
     *         which the caller is left to apply.
     * @see #requireSubtype(Class, Class, Class)
     * @see #newInstance(Class)
     */
    @Nullable
    static <R> R newLocatedInstance(final Class<?> target, final Class<R> role, final @Nullable Class<?> located) {
        assert target != null;
        assert role != null;
        if (located == null) {
            return null;
        }
        requireSubtype(located, role, target);
        return role.cast(newInstance(located));
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Returns the reason why the specified class can not possibly declare a usable no-argument constructor.
     *
     * @param clazz the class to check.
     * @return the reason, as a predicate phrase; {@code null} when the {@code clazz} may declare one.
     * @apiNote This method recognizes only the shapes which are non-instantiable by construction, so that
     *         {@link #newInstance(Class)} can name the actual problem; the remaining failures, such as a class which
     *         simply declares no no-argument constructor, are left to reflection to report.
     * @see #newInstance(Class)
     */
    @Nullable
    static String reasonNotInstantiable(final Class<?> clazz) {
        assert clazz != null;
        if (clazz.isPrimitive()) {
            return "a primitive type";
        }
        if (clazz.isArray()) {
            return "an array type";
        }
        if (clazz.isInterface()) {
            return "an interface";
        }
        if (clazz.isEnum()) {
            return "an enum";
        }
        // a record is deliberately absent: its canonical constructor takes no argument when it declares no component,
        // and it may declare a no-argument constructor explicitly in any case, so it is left to the lookup below
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return "abstract";
        }
        // a member class which is not static; its constructors all take the enclosing instance
        if (clazz.getDeclaringClass() != null && !Modifier.isStatic(clazz.getModifiers())) {
            return "an inner class; declare it 'static'";
        }
        return null;
    }

    /**
     * Creates a new instance of the specified class, using its no-argument constructor.
     *
     * @param clazz the class to be instantiated.
     * @param <T>   the type of the instance to create.
     * @return a new instance of the {@code clazz}.
     * @throws IllegalArgumentException when the {@code clazz} is of a shape which can not declare a no-argument
     *                                  constructor, such as an interface, an enum, an abstract class, or an inner
     *                                  class.
     * @throws RuntimeException         when the {@code clazz} declares no no-argument constructor, or when that
     *                                  constructor is inaccessible or throws.
     * @implNote The no-argument constructor is made
     *         {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) accessible} when required, so that a
     *         class may keep it {@code private} and still be instantiated here.
     * @see #reasonNotInstantiable(Class)
     * @see Class#getDeclaredConstructor(Class[])
     * @see Constructor#newInstance(Object...)
     */
    @SuppressWarnings({
            "java:S112" // Generic exceptions should never be thrown
    })
    static <T> T newInstance(final Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        {
            final var reason = reasonNotInstantiable(clazz);
            if (reason != null) {
                throw new IllegalArgumentException("unable to instantiate " + clazz + "; it is " + reason);
            }
        }
        final Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (final NoSuchMethodException nsme) {
            throw new RuntimeException("no no-arg constructor of " + clazz, nsme);
        }
        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance();
        } catch (final InvocationTargetException ite) {
            // the constructor itself threw; wrap what it threw, rather than the reflective wrapper around it, so
            // that the cause of the failure is one getCause() away, as it is for every other failure here
            throw new RuntimeException("failed to instantiate " + clazz, ite.getCause());
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate " + clazz, roe);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private ___Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}

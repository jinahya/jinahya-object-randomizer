package com.github.jinahya.object.randomizer;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utilities, internal to this package, for parsing excluded paths, and for resetting the slots which nested ones name.
 *
 * <h2>A path</h2>
 * A path is a {@code .}-separated sequence of segments, each naming a slot of the object reached by the segments before
 * it, the first naming a slot of the target instance itself: {@code "id"} names the {@code id} of the target, and
 * {@code "address.address1"} names the {@code address1} of the target's {@code address}. A segment which reaches a
 * container reaches every element in it: each element of an {@link Iterable}, and of an array of references, and each
 * value of a {@link Map}, so {@code "addresses.address1"} names the {@code address1} of every element of the target's
 * {@code addresses}. Containers nested in containers are flattened likewise, and an {@link Optional} is a container of
 * at most one element.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AbstractObjectRandomizer#excludedPaths
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
final class _Paths {

    private static final System.Logger logger = System.getLogger(_Paths.class.getName());

    /**
     * Normalizes the specified path: splits it on {@link _Constants#EXCLUDED_PATH_SEPARATOR_PATTERN}, and joins its
     * segments with {@value _Constants#EXCLUDED_PATH_SEPARATOR}; {@code " a . b "}, {@code "a..b"}, and {@code "a b"}
     * all normalize to {@code "a.b"}, and {@code "a."} to {@code "a"}.
     *
     * @param path the path to normalize.
     * @return the normalized path, which carries no white space, and no separator but between two segments.
     * @throws IllegalArgumentException when the {@code path} has no segment, as {@code ""}, {@code "  "}, and
     *                                  {@code "."} do.
     * @apiNote Segments are not verified otherwise: what names a slot is the engine's to say, and a segment
     *         which names nothing is harmless.
     */
    static String normalize(final String path) {
        assert path != null;
        // only a leading separator yields an empty segment; Pattern#split drops trailing ones
        final var segments = Arrays.stream(_Constants.EXCLUDED_PATH_SEPARATOR_PATTERN.split(path))
                .filter(v -> !v.isEmpty())
                .toList();
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("a blank excluded path: '" + path + "'");
        }
        return String.join(_Constants.EXCLUDED_PATH_SEPARATOR, segments);
    }

    /**
     * Returns an unmodifiable set of the specified normalized paths, less each one which another of them is a proper
     * prefix of, segment by segment; {@code "a"} prunes {@code "a.b"} and {@code "a.b.c"}, but not {@code "ab.c"}.
     *
     * @param paths the normalized paths.
     * @return an unmodifiable set of the {@code paths}, pruned.
     */
    static Set<String> prune(final Set<String> paths) {
        return paths.stream()
                .filter(v -> {
                    for (int i = v.indexOf(_Constants.EXCLUDED_PATH_SEPARATOR); i != -1;
                         i = v.indexOf(_Constants.EXCLUDED_PATH_SEPARATOR, i + 1)) {
                        if (paths.contains(v.substring(0, i))) {
                            logger.log(System.Logger.Level.DEBUG, "pruned {0}, under {1}", v, v.substring(0, i));
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Tells whether the specified normalized path is nested, of more than one segment.
     *
     * @param path the normalized path.
     * @return {@code true} when the {@code path} is nested; {@code false} when it is a simple one.
     */
    static boolean isNested(final String path) {
        return path.contains(_Constants.EXCLUDED_PATH_SEPARATOR);
    }

    /**
     * Returns the segments of the specified normalized path.
     *
     * @param path the normalized path.
     * @return an unmodifiable list of the segments of the {@code path}.
     */
    static List<String> segments(final String path) {
        return List.of(path.split(Pattern.quote(_Constants.EXCLUDED_PATH_SEPARATOR)));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Resets, on every object the specified segments reach from the specified root, the slot the last segment names, to
     * the value which a freshly constructed object of the same class carries in it.
     *
     * @param root     the object the {@code segments} start from.
     * @param segments the segments of the path; at least one.
     * @implNote A fresh owner is constructed, through its no-argument constructor, for each slot reset, so that
     *         a mutable value which that constructor assigns, an empty collection say, is never shared between two
     *         owners. Nothing is ever written which is not taken from such a fresh owner: an owner whose class declares
     *         no usable no-argument constructor keeps the slot as it is, for a value made up in its place could be
     *         written into an object which other parts of the program share. For the same reason, an enum constant is
     *         never walked into, nor written on; it is one object for the whole of the program. Nothing here fails: a
     *         segment which names no field, a field which can be neither read nor written, and an owner which can not
     *         be constructed afresh, are each logged and passed over, for an exclusion is a hint.
     */
    static void reset(final Object root, final List<String> segments) {
        assert root != null;
        assert !segments.isEmpty();
        List<Object> owners = new ArrayList<>();
        flatten(root, owners);
        for (final var segment : segments.subList(0, segments.size() - 1)) {
            final var next = new ArrayList<>();
            for (final var owner : owners) {
                final var field = field(owner.getClass(), segment);
                if (field == null) {
                    continue;
                }
                flatten(read(field, owner), next);
            }
            owners = next;
        }
        final var last = segments.get(segments.size() - 1);
        for (final var owner : owners) {
            final var field = field(owner.getClass(), last);
            if (field == null) {
                continue;
            }
            final var fresh = fresh(field, owner.getClass());
            if (fresh != NONE) {
                write(field, owner, fresh);
            }
        }
    }

    /**
     * A sentinel for a value which can not be had; distinct from {@code null}, which is a value.
     */
    private static final Object NONE = new Object();

    /**
     * Finds the instance field of the specified name, on the specified class or on the nearest of its superclasses.
     *
     * @return the field; {@code null} when there is none, or when it can not be made accessible.
     */
    private static @Nullable Field field(final Class<?> clazz, final String name) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            final Field field;
            try {
                field = c.getDeclaredField(name);
            } catch (final NoSuchFieldException nsfe) {
                continue;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                break;
            }
            try {
                field.setAccessible(true);
            } catch (final RuntimeException re) {
                // InaccessibleObjectException, for a class of a module which does not open its package to this one
                logger.log(System.Logger.Level.WARNING, "can not access " + field + "; passing over", re);
                return null;
            }
            return field;
        }
        logger.log(System.Logger.Level.DEBUG, "no instance field named {0} in {1}; passing over", name, clazz);
        return null;
    }

    private static @Nullable Object read(final Field field, final Object owner) {
        try {
            return field.get(owner);
        } catch (final IllegalAccessException iae) {
            logger.log(System.Logger.Level.WARNING, "failed to read " + field + "; passing over", iae);
            return null;
        }
    }

    private static void write(final Field field, final Object owner, final @Nullable Object value) {
        try {
            field.set(owner, value);
        } catch (final IllegalAccessException | RuntimeException e) {
            // a final field of a record, or of a hidden class, is never writable, accessible or not
            logger.log(System.Logger.Level.WARNING, "failed to reset " + field + "; passing over", e);
        }
    }

    /**
     * Returns the value which a freshly constructed instance of the specified class carries in the specified field.
     *
     * @return the value, which may be {@code null}; {@link #NONE} when no such instance can be constructed, or when the
     *         value can not be read off one.
     */
    private static @Nullable Object fresh(final Field field, final Class<?> ownerClass) {
        final Object owner;
        try {
            owner = _Utils.newInstance(ownerClass);
        } catch (final RuntimeException re) {
            logger.log(System.Logger.Level.WARNING,
                       () -> "can not construct " + ownerClass + " afresh; leaving " + field + " as it is", re);
            return NONE;
        }
        try {
            return field.get(owner);
        } catch (final IllegalAccessException iae) {
            logger.log(System.Logger.Level.WARNING, "failed to read " + field + "; leaving it as it is", iae);
            return NONE;
        }
    }

    /**
     * Adds the specified value to the specified list, or, for a container, each element in it, recursively.
     */
    private static void flatten(final @Nullable Object value, final List<Object> sink) {
        if (value == null) {
            return;
        }
        if (value instanceof Enum<?>) {
            // a constant is shared by the whole of the program; nothing on it is ours to reset
            logger.log(System.Logger.Level.DEBUG, "not walking into an enum constant, {0}; passing over", value);
        } else if (value instanceof Iterable<?> iterable) {
            iterable.forEach(e -> flatten(e, sink));
        } else if (value instanceof Map<?, ?> map) {
            // values only; a key is never reset, for that would corrupt the map it is a key of
            map.values().forEach(v -> flatten(v, sink));
        } else if (value instanceof Optional<?> optional) {
            optional.ifPresent(v -> flatten(v, sink));
        } else if (value instanceof Object[] array) {
            Arrays.stream(array).forEach(e -> flatten(e, sink));
        } else if (!value.getClass().isArray()) {
            sink.add(value);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private _Paths() {
        throw new AssertionError("instantiation is not allowed");
    }
}

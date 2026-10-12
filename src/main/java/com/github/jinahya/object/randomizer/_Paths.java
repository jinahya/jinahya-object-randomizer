package com.github.jinahya.object.randomizer;

/*-
 * #%L
 * jinahya-object-randomizer
 * %%
 * Copyright (C) 2026 Jin Kwon
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities, internal to this package, for refining excluded paths: each is {@link #normalize(String) normalized}, and
 * the set of them {@link #prune(Set) pruned}, once, when a randomizer is constructed; what a path names is the engine's
 * to say.
 * <p>
 * A path is a {@value _Constants#EXCLUDED_PATH_SEPARATOR}-separated sequence of segments; a <em>simple</em> one has a
 * single segment, such as {@code "id"}, and a <em>nested</em> one more, such as {@code "address.address1"}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AbstractObjectRandomizer#excludedPaths
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
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
                        final var prefix = v.substring(0, i);
                        if (paths.contains(prefix)) {
                            logger.log(System.Logger.Level.DEBUG, "pruned {0}, under {1}", v, prefix);
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

    // ---------------------------------------------------------------------------------------------------------------------
    private _Paths() {
        throw new AssertionError("instantiation is not allowed");
    }
}

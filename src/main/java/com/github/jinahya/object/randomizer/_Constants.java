package com.github.jinahya.object.randomizer;

import java.util.regex.Pattern;

/**
 * Constants, internal to this package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class _Constants {

    /**
     * The separator with which the segments of an excluded path are joined.
     */
    static final String EXCLUDED_PATH_SEPARATOR = ".";

    /**
     * A regular expression for what separates the segments of an excluded path, as it is given: a run of
     * {@value #EXCLUDED_PATH_SEPARATOR}, white space ({@link Character#isWhitespace(int)}), and space separators
     * ({@code \p{Zs}}, which takes in a no-break space), in any mix.
     *
     * @apiNote White space is a separator, rather than a part of a segment, so that no engine is ever handed a
     *         name with white space in it; {@code "a b"} is read as {@code "a.b"}.
     */
    static final String EXCLUDED_PATH_SEPARATOR_REGEX = "[.\\p{javaWhitespace}\\p{Zs}]+";

    /**
     * The compiled pattern of {@link #EXCLUDED_PATH_SEPARATOR_REGEX}.
     */
    static final Pattern EXCLUDED_PATH_SEPARATOR_PATTERN = Pattern.compile(EXCLUDED_PATH_SEPARATOR_REGEX);

    // ---------------------------------------------------------------------------------------------------------------------
    private _Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}

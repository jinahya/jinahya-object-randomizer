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
     * A pattern of what separates the segments of an excluded path, as it is given: a run of
     * {@value #EXCLUDED_PATH_SEPARATOR}, white space ({@link Character#isWhitespace(int)}), and space separators
     * ({@code \p{Zs}}, which takes in a no-break space), in any mix.
     *
     * @apiNote White space is a separator, rather than a part of a segment, so that no engine is ever handed a
     *         name with white space in it; {@code "a b"} is read as {@code "a.b"}.
     */
    static final Pattern EXCLUDED_PATH_SEPARATOR_PATTERN = Pattern.compile("[.\\p{javaWhitespace}\\p{Zs}]+");

    // ---------------------------------------------------------------------------------------------------------------------
    private _Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}

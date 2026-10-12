package com.github.jinahya.object.randomizer.example.email_with_size;

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

import java.util.List;

/**
 * Constants shared by every randomizer of {@link User}, whichever engine it uses.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see User_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class User_Randomizer_Constants {

    /**
     * The fields every randomizer of {@link User} leaves alone; none.
     * <p>
     * Excluding the one field there is would leave it at {@code null}, and {@code null} satisfies both of its
     * constraints -- {@link jakarta.validation.constraints.Email @Email} and
     * {@link jakarta.validation.constraints.Size @Size} each hold for it, as a constraint which leaves the null check
     * to {@code @NotNull} must. An exclusion would therefore turn this example into one that every engine passes
     * without writing anything, which is the opposite of what it is here to measure.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of();

    // -----------------------------------------------------------------------------------------------------------------
    private User_Randomizer_Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}

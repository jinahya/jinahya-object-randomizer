package com.github.jinahya.object.randomizer.spec.validation.car_driver;

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
 * Constants shared by every randomizer of {@link Driver}, whichever engine it uses.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Driver_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class Driver_Randomizer_Constants {

    /**
     * The attributes every randomizer of {@link Driver} leaves alone; none.
     * <p>
     * It is empty by choice rather than by necessity: the Jakarta Validation 3.1 specification puts no {@code @NotNull}
     * on {@code car}, so excluding it would leave a valid instance. It would also take the cascade out of the example,
     * which is half of what {@link Driver} is here to measure, so nothing is excluded.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of();

    // -----------------------------------------------------------------------------------------------------------------
    private Driver_Randomizer_Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}

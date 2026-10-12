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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the Instancio flavor is wired up against {@link Driver}, the class the Jakarta Validation 3.1
 * specification publishes.
 * <p>
 * What the contract is, and how it is checked, belong to {@link Driver_Randomized_Verifier}, so that this class says
 * only which engine it is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Driver_Randomizer_Instancio_Test {

    @DisplayName("get() -> an instance which satisfies the constraints of the Jakarta Validation 3.1 specification")
    @Test
    void get_AsSpecified_() {
        Driver_Randomized_Verifier.verifyValid(new Driver_Randomizer_Instancio());
    }
}

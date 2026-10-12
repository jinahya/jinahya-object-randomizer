package com.github.jinahya.object.randomizer.example.naming_convention;

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

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confirms the one requirement this package is about: a caller which names only the target class gets the randomizer
 * declared for it, and an instance from it, with nothing registered anywhere.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class WidgetRandomizer_Test {

    @DisplayName("newRandomizerInstanceOf(Widget.class) -> the randomizer named for the target")
    @Test
    void newRandomizerInstanceOf_Located_() {
        assertThat(ObjectRandomizerUtils.newRandomizerInstanceOf(Widget.class))
                .get()
                .isInstanceOf(WidgetRandomizer.class);
    }

    @DisplayName("newRandomizedInstanceOf(Widget.class) -> an instance, the randomizer never being named")
    @Test
    void newRandomizedInstanceOf_Randomized_() {
        assertThat(ObjectRandomizerUtils.newRandomizedInstanceOf(Widget.class))
                .get()
                .satisfies(w -> assertThat(w.getLabel()).as("filled by the located randomizer").isNotNull());
    }
}

package com.github.jinahya.object.randomizer.example.more_excluded_paths;

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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confirms the one requirement this package is about: an extending randomizer excludes what it names <em>and</em> what
 * the randomizer it extends already excluded.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Record_Randomizer_Test {

    /**
     * The number of instances drawn; more than one, since a randomizer which wrote an excluded field could draw the
     * value it was meant to leave.
     */
    private static final int DRAWS = 32;

    @DisplayName("the base randomizer leaves the id alone, and fills the version")
    @Test
    void get_IdExcluded_() {
        final var randomizer = new Record_Randomizer();
        for (int i = 0; i < DRAWS; i++) {
            final var record = randomizer.get();
            assertThat(record.getId()).as("excluded by the base: draw #%d", i).isEqualTo(Record.UNASSIGNED);
            assertThat(record.getVersion()).as("excluded by nothing: draw #%d", i).isNotEqualTo(Record.UNASSIGNED);
            assertThat(record.getName()).as("excluded by nothing: draw #%d", i).isNotNull();
        }
    }

    @DisplayName("the extending randomizer leaves both the id it inherits and the version it adds")
    @Test
    void get_IdAndVersionExcluded_() {
        final var randomizer = new Record_Randomizer_Versionless();
        for (int i = 0; i < DRAWS; i++) {
            final var record = randomizer.get();
            assertThat(record.getId()).as("inherited from the base: draw #%d", i).isEqualTo(Record.UNASSIGNED);
            assertThat(record.getVersion()).as("added by the subclass: draw #%d", i).isEqualTo(Record.UNASSIGNED);
            assertThat(record.getName()).as("excluded by nothing: draw #%d", i).isNotNull();
        }
    }
}

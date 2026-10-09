package com.github.jinahya.object.randomizer.example.more_excluded_paths;

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

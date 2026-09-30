package com.github.jinahya.object.randomizer.example.excluded_fields;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confirms the one requirement this package is about: an excluded field is left at the value the instance already
 * carries, while the randomizer goes on filling everything else.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Entity_Randomizer_Test {

    /**
     * The number of instances drawn; more than one, since a randomizer which wrote the excluded field could draw the
     * value it was meant to leave.
     */
    private static final int DRAWS = 32;

    @DisplayName("get() -> an instance whose excluded id is untouched, and whose name is filled")
    @Test
    void get_IdExcluded_() {
        final var randomizer = new Entity_Randomizer();
        for (int i = 0; i < DRAWS; i++) {
            final var entity = randomizer.get();
            assertThat(entity.getId())
                    .as("excluded, and so left at what the instance already carried: draw #%d", i)
                    .isEqualTo(Entity.UNASSIGNED);
            assertThat(entity.getName())
                    .as("excluded from nothing, and so filled: draw #%d", i)
                    .isNotNull();
        }
    }
}

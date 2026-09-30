package com.github.jinahya.object.randomizer.example.naming_convention;

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

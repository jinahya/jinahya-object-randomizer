package com.github.jinahya.object.randomizer.example.new_target_instance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confirms the one requirement this package is about: a randomizer which overrides {@code newTargetInstance()}
 * randomizes a class the default construction could not reach, and the engine fills the instance it was handed.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Message_Randomizer_Test {

    @DisplayName("get() -> the instance the randomizer constructed, filled by the engine")
    @Test
    void get_Constructed_() {
        final var message = new Message_Randomizer().get();
        assertThat(message).isNotNull();
        assertThat(message.getChannel())
                .as("settled by newTargetInstance(), and unwritable afterwards")
                .isEqualTo(Message_Randomizer.CHANNEL);
        assertThat(message.getBody()).as("filled by the engine").isNotNull();
    }
}

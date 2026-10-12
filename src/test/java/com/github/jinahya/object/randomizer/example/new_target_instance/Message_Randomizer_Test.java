package com.github.jinahya.object.randomizer.example.new_target_instance;

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

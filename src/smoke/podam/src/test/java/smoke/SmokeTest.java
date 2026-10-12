package smoke;

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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SmokeTest {

    private static void assertAbsent(final String className) {
        assertThrows(ClassNotFoundException.class, () -> Class.forName(className), className);
    }

    @Test
    void runsOnTheTargetedRelease() {
        assertEquals(17, Runtime.version().feature(), "not run on Java 17");
    }

    @Test
    void bringsOnlyTheChosenEngine() {
        assertAbsent("org.instancio.Instancio");
        assertAbsent("com.navercorp.fixturemonkey.FixtureMonkey");
    }

    @Test
    void randomizes() {
        final var foo = ObjectRandomizerUtils.newRandomizedInstanceOf(Foo.class).orElseThrow();
        assertNotNull(foo.getName(), "name");
        assertNull(foo.getId(), "id, which is excluded");
    }
}

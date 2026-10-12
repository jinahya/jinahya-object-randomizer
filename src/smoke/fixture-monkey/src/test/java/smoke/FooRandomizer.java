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

import com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer;

import java.util.List;

/**
 * The randomizer of {@link Foo}, declared under {@code test}, as a consumer would.
 */
class FooRandomizer
        extends FixtureMonkeyObjectRandomizer<Foo> {

    FooRandomizer() {
        super(Foo.class, List.of("id"));
    }
}

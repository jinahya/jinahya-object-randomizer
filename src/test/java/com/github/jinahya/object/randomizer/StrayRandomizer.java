package com.github.jinahya.object.randomizer;

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

import java.util.List;

/**
 * A top-level randomizer named after the nested {@link ObjectRandomizerUtils_Convention_Test.Host.Stray}, which the
 * convention must not locate for it, for it is no sibling of that class.
 *
 * @see ObjectRandomizerUtils_Convention_Test
 */
class StrayRandomizer
        extends AbstractObjectRandomizer<ObjectRandomizerUtils_Convention_Test.Host.Stray> {

    StrayRandomizer() {
        super(ObjectRandomizerUtils_Convention_Test.Host.Stray.class, List.of());
    }

    @Override
    public ObjectRandomizerUtils_Convention_Test.Host.Stray get() {
        return new ObjectRandomizerUtils_Convention_Test.Host.Stray();
    }
}

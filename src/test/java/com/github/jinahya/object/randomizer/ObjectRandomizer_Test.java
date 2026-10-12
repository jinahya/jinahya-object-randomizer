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

import java.util.Objects;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
abstract class ObjectRandomizer_Test<T extends ObjectRandomizer<U>, U> {

    ObjectRandomizer_Test(final Class<T> randomizerClass, final Class<U> targetClass) {
        super();
        this.randomizerClass = Objects.requireNonNull(randomizerClass, "randomizerClass is null");
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    final Class<T> randomizerClass;

    final Class<U> targetClass;
}

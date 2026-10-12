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

import java.util.List;

/**
 * A randomizer which extends {@link Record_Randomizer} and leaves the {@code version} alone as well.
 * <p>
 * The {@code id} the base excludes is nowhere in this class: it names only the field which is its own, and the base
 * merges the two.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Record_Randomizer_Versionless
        extends Record_Randomizer {

    /**
     * Creates a new instance.
     */
    public Record_Randomizer_Versionless() {
        super(List.of(Record.FIELD_VERSION));
    }
}

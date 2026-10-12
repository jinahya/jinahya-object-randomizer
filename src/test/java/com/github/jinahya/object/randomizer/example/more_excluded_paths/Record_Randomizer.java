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

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

import java.util.List;

/**
 * The base randomizer of {@link Record}, which excludes its {@code id}.
 * <p>
 * It is written to be extended: the constructor an extending randomizer calls takes the names that randomizer adds, and
 * merges them with {@link #EXCLUDED_FIELDS} through
 * {@link ObjectRandomizerUtils#moreExcludedPaths(Iterable, Iterable) moreExcludedPaths}. A subclass therefore names
 * only what is its own, and stays correct when this class comes to exclude something more.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Record_Randomizer_Versionless
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Record_Randomizer
        extends PodamObjectRandomizer<Record> {

    /**
     * The exclusions this randomizer applies of its own, which an extending randomizer adds to rather than restates.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of(Record.FIELD_ID);

    /**
     * Creates a new instance which excludes nothing beyond {@link #EXCLUDED_FIELDS}.
     */
    public Record_Randomizer() {
        this(List.of());
    }

    /**
     * Creates a new instance which excludes the specified names as well as {@link #EXCLUDED_FIELDS}.
     *
     * @param moreExcludedPaths the names to exclude in addition.
     */
    protected Record_Randomizer(final Iterable<String> moreExcludedPaths) {
        super(Record.class, ObjectRandomizerUtils.moreExcludedPaths(EXCLUDED_FIELDS, moreExcludedPaths));
    }
}

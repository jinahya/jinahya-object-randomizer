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

/**
 * A target with two fields a randomizer might leave alone and one it should always fill.
 * <p>
 * Both excludable fields carry a value from the moment the instance exists, which is what makes an exclusion
 * observable.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class Record {

    /**
     * The value the excludable fields carry before anything assigns one; {@value}.
     */
    public static final long UNASSIGNED = -1L;

    /**
     * The name of the field the base randomizer excludes; {@value}.
     */
    public static final String FIELD_ID = "id";

    /**
     * The name of the field only the extending randomizer excludes; {@value}.
     */
    public static final String FIELD_VERSION = "version";

    @Override
    public String toString() {
        return super.toString() + '{'
               + "id=" + id
               + ",version=" + version
               + ",name=" + name
               + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    private Long id = UNASSIGNED;

    private Long version = UNASSIGNED;

    /**
     * A field neither randomizer excludes, as the control.
     */
    private String name;
}

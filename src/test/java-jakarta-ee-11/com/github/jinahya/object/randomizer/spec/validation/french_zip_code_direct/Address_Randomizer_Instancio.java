package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

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

import com.github.jinahya.object.randomizer.InstancioObjectRandomizer;

/**
 * A Instancio randomizer of {@link Address}.
 * <p>
 * Instancio reads the {@code @Size} on the field -- it draws exactly five characters -- and not the {@code @Pattern}
 * beside it, although {@code InstancioObjectRandomizer} sets {@code Keys.BEAN_VALIDATION_ENABLED} by default. So
 * {@link #get()} is overridden to write a zip code the pattern accepts.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Address_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Address_Randomizer_Instancio
        extends InstancioObjectRandomizer<Address> {

    /**
     * Creates a new instance.
     */
    public Address_Randomizer_Instancio() {
        super(Address.class, Address_Randomizer_Constants.EXCLUDED_FIELDS);
    }

    /**
     * {@inheritDoc}
     *
     * @return a randomized instance whose {@code zipCode} satisfies the {@code @Pattern} on it, which the engine alone
     *         does not read.
     * @implSpec The engine populates the instance first, and {@link ZipCodes#repaired(Address) repaired} then
     *         writes the one field it filled without reading the pattern.
     */
    @Override
    public Address get() {
        return ZipCodes.repaired(super.get());
    }
}

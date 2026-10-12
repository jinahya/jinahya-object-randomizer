package com.github.jinahya.object.randomizer.example.naming_convention;

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

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

import java.util.List;

/**
 * The randomizer of {@link Widget}, named so that the convention finds it.
 * <p>
 * The name carries the whole of the wiring: the target's own name, and the {@code Randomizer} postfix, in the target's
 * package. It is the first of the two postfixes probed, the other being {@code _Randomizer}, which the specification
 * examples use.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class WidgetRandomizer
        extends PodamObjectRandomizer<Widget> {

    /**
     * Creates a new instance.
     *
     * @apiNote The no-argument constructor is what a located randomizer is instantiated by, and so is not
     *         optional here.
     */
    public WidgetRandomizer() {
        super(Widget.class, List.of());
    }
}

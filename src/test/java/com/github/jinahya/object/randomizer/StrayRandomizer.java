package com.github.jinahya.object.randomizer;

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

package com.github.jinahya.object.randomizer;

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

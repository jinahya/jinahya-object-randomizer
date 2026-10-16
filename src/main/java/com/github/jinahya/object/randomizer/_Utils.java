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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utilities, internal to this package, for instantiating classes reflectively.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ObjectRandomizerUtils
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S112", // Generic exceptions should never be thrown
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
final class _Utils {

    /**
     * Creates a new instance of the specified class, using its no-argument constructor.
     *
     * @param clazz the class to be instantiated.
     * @param <T>   the type of the instance to create.
     * @return a new instance of the {@code clazz}.
     * @throws RuntimeException when the {@code clazz} declares no no-argument constructor, or when that constructor is
     *                          inaccessible or throws; when the {@code clazz} is of a shape which can not be
     *                          instantiated at all, such as an interface, an enum, an abstract class, or an inner
     *                          class; and when the {@code clazz} can not be linked, or initialized, which is what a
     *                          randomizer whose engine is missing from the classpath fails with. The
     *                          {@link LinkageError} is the cause, and the message names the {@code clazz}, which the
     *                          error itself does not.
     * @apiNote The shape of the {@code clazz} is not examined beforehand. Every shape which can not be
     *         instantiated fails here anyway -- on the constructor lookup, or on the instantiation itself -- and
     *         reflection names the failure; a check ahead of it would only duplicate that.
     * @implNote The no-argument constructor is made
     *         {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) accessible} when required, so that a
     *         class may keep it {@code private} and still be instantiated here.
     * @see Class#getDeclaredConstructor(Class[])
     * @see Constructor#newInstance(Object...)
     */
    static <T> T newInstance(final Class<T> clazz) {
        assert clazz != null;
        final Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (final NoSuchMethodException nsme) {
            // an interface, an enum, and an inner class all land here: none of them declares a constructor which
            // takes no argument at all
            throw new RuntimeException("no no-arg constructor; class: " + clazz, nsme);
        } catch (final LinkageError le) {
            // the constructor lookup is the first thing to link the class, and so to verify it, along with its
            // supertypes; a randomizer whose engine is missing from the classpath fails right here, with a
            // NoClassDefFoundError which names a class of the engine, and nothing else -- so name the class, too
            throw new RuntimeException("failed to link; class: " + clazz, le);
        }
        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance();
        } catch (final InvocationTargetException ite) {
            // the constructor itself threw; wrap what it threw, rather than the reflective wrapper around it, so
            // that the cause of the failure is one getCause() away, as it is for every other failure here
            throw new RuntimeException("failed to instantiate; class: " + clazz, ite.getCause());
        } catch (final ReflectiveOperationException roe) {
            // an abstract class lands here: it may well declare a no-argument constructor, which simply can not be
            // invoked on its own
            throw new RuntimeException("failed to instantiate; class: " + clazz, roe);
        } catch (final LinkageError le) {
            // a static initializer which fails lands here, as an ExceptionInInitializerError, and so does one which
            // reaches for a class the classpath lacks
            throw new RuntimeException("failed to initialize; class: " + clazz, le);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private _Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}

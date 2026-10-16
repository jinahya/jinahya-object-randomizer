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

import java.io.IOException;

/**
 * A class loader which sees no class of a chosen engine, and which defines every class of this package itself, so that
 * a flavor, and a randomizer which extends it, link against a classpath from which the engine is missing.
 * <p>
 * Every other name is delegated to the parent, and so is {@link ObjectRandomizer}, the role, so that a randomizer
 * defined here is still an {@code ObjectRandomizer} to the parent, and to the probe. Every other class of this package
 * has to be defined here, rather than delegated, for a class resolves what it references through its own defining
 * loader: a flavor the parent defined would find the engine, however the engine is hidden from a child.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class EngineHidingClassLoader
        extends ClassLoader {

    private static final String PACKAGE_PREFIX = EngineHidingClassLoader.class.getPackageName() + '.';

    private static final String ROLE_NAME = ObjectRandomizer.class.getName();

    /**
     * Creates a new instance which hides every class whose name starts with the specified prefix.
     *
     * @param hiddenPrefix the prefix of the names to hide, such as {@code "uk.co.jemos."}.
     */
    EngineHidingClassLoader(final String hiddenPrefix) {
        super(EngineHidingClassLoader.class.getClassLoader());
        this.hiddenPrefix = hiddenPrefix;
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        if (name.startsWith(hiddenPrefix)) {
            throw new ClassNotFoundException(name);
        }
        if (!name.startsWith(PACKAGE_PREFIX) || name.equals(ROLE_NAME)) {
            return super.loadClass(name, resolve);
        }
        synchronized (getClassLoadingLock(name)) {
            final var loaded = findLoadedClass(name);
            if (loaded != null) {
                return loaded;
            }
            final byte[] bytes;
            try (var stream = getParent().getResourceAsStream(name.replace('.', '/') + ".class")) {
                if (stream == null) {
                    throw new ClassNotFoundException(name);
                }
                bytes = stream.readAllBytes();
            } catch (final IOException ioe) {
                throw new ClassNotFoundException(name, ioe);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    private final String hiddenPrefix;
}

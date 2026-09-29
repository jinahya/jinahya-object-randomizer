package com.github.jinahya.object.randomizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class ObjectRandomizerUtils_Convention_Test {

    // ---------------------------------------------------------------------------------- a sibling randomizer, and a
    // ------------------------------------------------------------------- subclass which declares one of its own
    static class Sup {

    }

    static class SupRandomizer
            extends AbstractObjectRandomizer<Sup> {

        SupRandomizer() {
            super(Sup.class, List.of());
        }

        @Override
        public Sup get() {
            return new Sup();
        }
    }

    static class Sub
            extends Sup {

    }

    static class SubRandomizer
            extends AbstractObjectRandomizer<Sub> {

        SubRandomizer() {
            super(Sub.class, List.of());
        }

        @Override
        public Sub get() {
            return new Sub();
        }
    }

    // ------------------ a nested target class, whose counterpart is nested in the counterpart of its
    // ------------------ enclosing class: an arrangement the convention deliberately does not consult
    static class Outer {

        static class Inner {

        }
    }

    static class OuterRandomizer
            extends AbstractObjectRandomizer<Outer> {

        OuterRandomizer() {
            super(Outer.class, List.of());
        }

        @Override
        public Outer get() {
            return new Outer();
        }

        static class InnerRandomizer
                extends AbstractObjectRandomizer<Outer.Inner> {

            InnerRandomizer() {
                super(Outer.Inner.class, List.of());
            }

            @Override
            public Outer.Inner get() {
                return new Outer.Inner();
            }
        }
    }

    // ------------------------------------------------------------------------------ one which follows no convention
    static class Bare {

    }

    // ------------------------------------------------- a target carrying both postfixes, and one carrying only the
    // ------------------------------------------------- underscored one, which no other test in this set exercises
    static class Both {

    }

    static class BothRandomizer
            extends AbstractObjectRandomizer<Both> {

        BothRandomizer() {
            super(Both.class, List.of());
        }

        @Override
        public Both get() {
            return new Both();
        }
    }

    static class Both_Randomizer
            extends AbstractObjectRandomizer<Both> {

        Both_Randomizer() {
            super(Both.class, List.of());
        }

        @Override
        public Both get() {
            return new Both();
        }
    }

    static class Underscored {

    }

    static class Underscored_Randomizer
            extends AbstractObjectRandomizer<Underscored> {

        Underscored_Randomizer() {
            super(Underscored.class, List.of());
        }

        @Override
        public Underscored get() {
            return new Underscored();
        }
    }

    /**
     * A class loader which defines the nested classes of this test itself, and fails one chosen name with a
     * {@link NoClassDefFoundError} -- which is what {@link Class#forName(String, boolean, ClassLoader)} raises for a
     * class whose supertype can not be loaded, and which is an {@link Error}, not a {@link ClassNotFoundException}.
     */
    private static final class UnloadableClassLoader
            extends ClassLoader {

        private UnloadableClassLoader(final String unloadableName) {
            super(ObjectRandomizerUtils_Convention_Test.class.getClassLoader());
            this.unloadableName = unloadableName;
        }

        @Override
        protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            if (name.equals(unloadableName)) {
                throw new NoClassDefFoundError(name);
            }
            // only this test's own nested classes are defined here; everything else, the supertypes included,
            // stays with the parent, so a defined class still links against the ordinary types
            if (!name.startsWith(ObjectRandomizerUtils_Convention_Test.class.getName() + "$")) {
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

        private final String unloadableName;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("locateStandard(Sup.class) -> SupRandomizer")
    @Test
    void standard_SupRandomizer_Sup() {
        assertThat(ObjectRandomizerUtils.randomizerClassOf(Sup.class)).contains(SupRandomizer.class);
    }

    @DisplayName("locateStandard(Sub.class) -> SubRandomizer; not the randomizer of its superclass")
    @Test
    void standard_SubRandomizer_Sub() {
        assertThat(ObjectRandomizerUtils.randomizerClassOf(Sub.class)).contains(SubRandomizer.class);
    }

    @DisplayName("newRandomizedInstanceOf(Sub.class) -> a Sub; not a Sup")
    @Test
    void newRandomizedInstanceOf_Sub_Sub() {
        assertThat(ObjectRandomizerUtils.newRandomizedInstanceOf(Sub.class))
                .isPresent()
                .containsInstanceOf(Sub.class);
    }

    @DisplayName("locateStandard(Outer.Inner.class) -> empty;"
                 + " the enclosing chain is not consulted, so a nested target class has no randomizer")
    @Test
    void standard_Empty_EnclosingChainNotConsulted() {
        assertThat(ObjectRandomizerUtils.randomizerClassOf(Outer.Inner.class)).isEmpty();
    }

    @DisplayName("locateStandard(Bare.class) -> empty")
    @Test
    void standard_Empty_Bare() {
        assertThat(ObjectRandomizerUtils.randomizerClassOf(Bare.class)).isEmpty();
    }

    @DisplayName("locateStandard(Underscored.class) -> Underscored_Randomizer; the second postfix is probed too")
    @Test
    void standard_UnderscoredRandomizer_Underscored() {
        assertThat(ObjectRandomizerUtils.randomizerClassOf(Underscored.class))
                .contains(Underscored_Randomizer.class);
    }

    @DisplayName("locateStandard(Both.class) -> BothRandomizer; \"Randomizer\" is probed before \"_Randomizer\"")
    @Test
    void standard_TheUnderscorelessOneWins_Both() {
        assertThat(ObjectRandomizerUtils.randomizerClassOf(Both.class)).contains(BothRandomizer.class);
    }

    @DisplayName("a probe whose every candidate fails to load finds nothing, rather than raising the error")
    @Test
    void standard_Empty_EveryCandidateFailsToLoad() throws ClassNotFoundException {
        final var loader = new UnloadableClassLoader(SupRandomizer.class.getName());
        final var target = loader.loadClass(Sup.class.getName());
        assertThat(ObjectRandomizerUtils.randomizerClassOf(target)).isEmpty();
    }
}

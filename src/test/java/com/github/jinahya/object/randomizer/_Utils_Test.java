package com.github.jinahya.object.randomizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link _Utils}, covering the shapes which can, and can not, be instantiated reflectively.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3577" // Test classes should comply with a naming convention
})
class _Utils_Test {

    /**
     * A record which declares no component, and whose canonical constructor therefore takes no argument.
     */
    record Empty() {

    }

    /**
     * A record which declares components, and a no-argument constructor explicitly.
     */
    record Defaulted(int x, int y) {

        Defaulted() {
            this(0, 0);
        }
    }

    /**
     * A record whose only constructor is a canonical one which takes arguments.
     */
    record Componentized(int x) {

    }

    /**
     * A class whose no-argument constructor is {@code private}.
     */
    static final class Private {

        private Private() {
            super();
        }
    }

    /**
     * A class whose no-argument constructor throws.
     */
    static final class Throwing {

        static final IllegalStateException THROWN = new IllegalStateException("thrown");

        Throwing() {
            throw THROWN;
        }
    }

    /**
     * An abstract class, which declares a no-argument constructor which can not be invoked on its own.
     */
    abstract static class Abstract {

    }

    /**
     * An interface, which declares no constructor at all.
     */
    interface Interface {

    }

    /**
     * An enum, whose only constructor takes the name and the ordinal of a constant.
     */
    enum Enum {

    }

    /**
     * An inner class, whose only constructor takes the enclosing instance.
     */
    @SuppressWarnings({
            "InnerClassMayBeStatic"
    })
    final class Inner {

    }

    // -------------------------------------------------------------------------------------------------- instantiated

    @DisplayName("a class, or a record, which has a no-argument constructor is instantiated")
    @ParameterizedTest
    @ValueSource(classes = {Empty.class, Defaulted.class, Object.class})
    void newInstance(final Class<?> clazz) {
        assertThat(_Utils.newInstance(clazz)).isExactlyInstanceOf(clazz);
    }

    @DisplayName("a private no-argument constructor is made accessible, and invoked")
    @Test
    void newInstance_PrivateConstructor() {
        assertThat(_Utils.newInstance(Private.class)).isExactlyInstanceOf(Private.class);
    }

    // ----------------------------------------------------------------------------------------------------- rejected

    @DisplayName("a shape which declares no no-argument constructor is reported as such")
    @ParameterizedTest
    @ValueSource(classes = {Componentized.class, Interface.class, Enum.class, Inner.class})
    void newInstance_RuntimeException_NoNoArgConstructor(final Class<?> clazz) {
        assertThatThrownBy(() -> _Utils.newInstance(clazz))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("no no-arg constructor")
                .hasCauseInstanceOf(NoSuchMethodException.class);
    }

    @DisplayName("an abstract class, whose no-argument constructor can not be invoked on its own, is rejected")
    @Test
    void newInstance_RuntimeException_Abstract() {
        assertThatThrownBy(() -> _Utils.newInstance(Abstract.class))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("failed to instantiate")
                .hasCauseInstanceOf(InstantiationException.class);
    }

    @DisplayName("what a constructor throws is the cause, one getCause() away, not the reflective wrapper around it")
    @Test
    void newInstance_RuntimeException_ConstructorThrows() {
        assertThatThrownBy(() -> _Utils.newInstance(Throwing.class))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("failed to instantiate")
                .cause().isSameAs(Throwing.THROWN);
    }
}

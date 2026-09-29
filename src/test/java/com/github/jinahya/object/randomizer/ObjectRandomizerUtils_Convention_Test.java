package com.github.jinahya.object.randomizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("locateStandard(Sup.class) -> SupRandomizer")
    @Test
    void standard_SupRandomizer_Sup() {
        assertThat(ObjectRandomizerUtils.locateStandard(Sup.class)).isSameAs(SupRandomizer.class);
    }

    @DisplayName("locateStandard(Sub.class) -> SubRandomizer; not the randomizer of its superclass")
    @Test
    void standard_SubRandomizer_Sub() {
        assertThat(ObjectRandomizerUtils.locateStandard(Sub.class)).isSameAs(SubRandomizer.class);
    }

    @DisplayName("newRandomizedInstanceOf(Sub.class) -> a Sub; not a Sup")
    @Test
    void newRandomizedInstanceOf_Sub_Sub() {
        assertThat(ObjectRandomizerUtils.newRandomizedInstanceOf(Sub.class))
                .isPresent()
                .containsInstanceOf(Sub.class);
    }

    @DisplayName("locateStandard(Outer.Inner.class) -> null;"
                 + " the enclosing chain is not consulted, so a nested target class has no randomizer")
    @Test
    void standard_Null_EnclosingChainNotConsulted() {
        assertThat(ObjectRandomizerUtils.locateStandard(Outer.Inner.class)).isNull();
    }

    @DisplayName("locateStandard(Bare.class) -> null")
    @Test
    void standard_Null_Bare() {
        assertThat(ObjectRandomizerUtils.locateStandard(Bare.class)).isNull();
    }
}

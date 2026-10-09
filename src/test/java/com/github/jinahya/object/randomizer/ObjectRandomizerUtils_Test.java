package com.github.jinahya.object.randomizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class ObjectRandomizerUtils_Test {

    static class Bean {

    }

    /**
     * A randomizer whose exclusions are merged, and, hence, carry duplicate elements.
     */
    static class BeanRandomizer
            extends AbstractObjectRandomizer<Bean> {

        BeanRandomizer() {
            super(
                    Bean.class,
                    ObjectRandomizerUtils.moreExcludedPaths(
                            List.of(" a "),
                            List.of("a", "b")
                    )
            );
        }

        @Override
        public Bean get() {
            return new Bean();
        }

        Set<String> excludedPaths() {
            return excludedPaths;
        }
    }

//SEP:producer covariance -- a randomizer is located by name, then checked against the class it is declared for

    /**
     * A superclass whose conventionally named randomizer is declared for a subclass of it, which a randomizer of the
     * superclass may be: every instance it produces is a {@code Sup}.
     */
    static class Sup {

    }

    static class Sub
            extends Sup {

    }

    static class SupRandomizer
            extends AbstractObjectRandomizer<Sub> {

        SupRandomizer() {
            super(Sub.class, List.of());
        }

        @Override
        public Sub get() {
            return new Sub();
        }
    }

    /**
     * A class whose conventionally named randomizer is declared for its superclass, which can not produce instances of
     * it.
     */
    static class Narrowed
            extends Sup {

    }

    static class NarrowedRandomizer
            extends AbstractObjectRandomizer<Sup> {

        NarrowedRandomizer() {
            super(Sup.class, List.of());
        }

        @Override
        public Sup get() {
            return new Sup();
        }
    }

    /**
     * A class whose conventionally named randomizer is declared for an unrelated class.
     */
    static class Unrelated {

    }

    static class Foreign {

    }

    static class UnrelatedRandomizer
            extends AbstractObjectRandomizer<Foreign> {

        UnrelatedRandomizer() {
            super(Foreign.class, List.of());
        }

        @Override
        public Foreign get() {
            return new Foreign();
        }
    }

    /**
     * A class whose conventionally named randomizer breaks the contract of {@link ObjectRandomizer#get() get()}, and
     * produces nothing at all.
     */
    static class Hollow {

    }

    static class HollowRandomizer
            extends AbstractObjectRandomizer<Hollow> {

        HollowRandomizer() {
            super(Hollow.class, List.of());
        }

        @Override
        @SuppressWarnings({
                "java:S2637" // the contract is broken on purpose
        })
        public Hollow get() {
            return null;
        }
    }

    /**
     * A class whose conventionally named sibling exists, but is not an {@link ObjectRandomizer} at all: a fault the
     * probe deliberately does not judge, so that it is reported where the role is known.
     */
    static class Misnamed {

    }

    static class MisnamedRandomizer {

    }

    // ---------------------------------------------------------------------------------------------------------------------
    @DisplayName("moreExcludedPaths(a, b) -> a, then b, as they are")
    @Test
    void moreExcludedPaths_Concatenated_() {
        assertThat(ObjectRandomizerUtils.moreExcludedPaths(List.of("a", "b"), List.of("b", "c")))
                .containsExactly("a", "b", "b", "c");
    }

    @DisplayName("moreExcludedPaths(empty, empty) -> empty")
    @Test
    void moreExcludedPaths_Empty_Empty() {
        assertThat(ObjectRandomizerUtils.moreExcludedPaths(List.of(), List.of())).isEmpty();
    }

    @DisplayName("moreExcludedPaths(null, _) / moreExcludedPaths(_, null) -> NullPointerException")
    @Test
    void moreExcludedPaths_NullPointerException_Null() {
        assertThatThrownBy(() -> ObjectRandomizerUtils.moreExcludedPaths(null, List.of()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ObjectRandomizerUtils.moreExcludedPaths(List.of(), null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("moreExcludedPaths(_, [null]) / moreExcludedPaths([null], _) -> NullPointerException")
    @Test
    void moreExcludedPaths_NullPointerException_NullElement() {
        assertThatThrownBy(() -> ObjectRandomizerUtils.moreExcludedPaths(List.of(), Arrays.asList("a", null)))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ObjectRandomizerUtils.moreExcludedPaths(Arrays.asList(null, "a"), List.of()))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("the randomizer constructor strips, and deduplicates, the merged exclusions")
    @Test
    void excludedPaths_StrippedDeduped_Merged() {
        assertThat(new BeanRandomizer().excludedPaths()).containsExactlyInAnyOrder("a", "b");
    }

    @DisplayName("newRandomizerInstanceOf(Bean.class) -> present, the sibling BeanRandomizer")
    @Test
    void newRandomizerInstanceOf_Present_Bean() {
        assertThat(ObjectRandomizerUtils.newRandomizerInstanceOf(Bean.class))
                .isPresent()
                .containsInstanceOf(BeanRandomizer.class);
    }

    @DisplayName("newRandomizerInstanceOf(Foreign.class) -> empty; no randomizer is named for it")
    @Test
    void newRandomizerInstanceOf_Empty_NoSibling() {
        assertThat(ObjectRandomizerUtils.newRandomizerInstanceOf(Foreign.class)).isEmpty();
    }

    @DisplayName("newRandomizerInstanceOf(Narrowed.class) -> present; what it produces is not checked here")
    @Test
    void newRandomizerInstanceOf_Present_RandomizerOfSuperclass() {
        assertThat(ObjectRandomizerUtils.newRandomizerInstanceOf(Narrowed.class))
                .isPresent()
                .containsInstanceOf(NarrowedRandomizer.class);
    }

    @DisplayName("newRandomizerInstanceOf(null) -> NullPointerException")
    @Test
    void newRandomizerInstanceOf_NullPointerException_Null() {
        assertThatThrownBy(() -> ObjectRandomizerUtils.newRandomizerInstanceOf(null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("newRandomizerInstanceOf(Misnamed.class) -> empty;"
                 + " a sibling named by the convention which is not an ObjectRandomizer is passed over")
    @Test
    void newRandomizerInstanceOf_Empty_SiblingIsNotARandomizer() {
        assertThat(ObjectRandomizerUtils.newRandomizerInstanceOf(Misnamed.class)).isEmpty();
    }

    @DisplayName("newRandomizedInstanceOf(Bean.class) -> present, from the sibling BeanRandomizer")
    @Test
    void newRandomizedInstanceOf_Present_Bean() {
        assertThat(ObjectRandomizerUtils.newRandomizedInstanceOf(Bean.class))
                .isPresent()
                .containsInstanceOf(Bean.class);
    }

    @DisplayName("newRandomizedInstanceOf(Sup.class) -> present; a randomizer declared for a subclass produces a Sup")
    @Test
    void newRandomizedInstanceOf_Present_RandomizerOfSubclass() {
        assertThat(ObjectRandomizerUtils.newRandomizedInstanceOf(Sup.class))
                .isPresent()
                .containsInstanceOf(Sub.class);
    }

    @DisplayName("newRandomizedInstanceOf(Narrowed.class) -> empty;"
                 + " NarrowedRandomizer produces a Sup, which is not a Narrowed, so nothing is handed back")
    @Test
    void newRandomizedInstanceOf_Empty_RandomizerProducesASuperclass() {
        assertThat(ObjectRandomizerUtils.newRandomizedInstanceOf(Narrowed.class)).isEmpty();
    }

    @DisplayName("newRandomizedInstanceOf(Unrelated.class) -> empty;"
                 + " UnrelatedRandomizer produces a Foreign, which is not an Unrelated, so nothing is handed back")
    @Test
    void newRandomizedInstanceOf_Empty_RandomizerProducesAnUnrelatedClass() {
        assertThat(ObjectRandomizerUtils.newRandomizedInstanceOf(Unrelated.class)).isEmpty();
    }

    @DisplayName("newRandomizedInstanceOf(Hollow.class) -> empty;"
                 + " HollowRandomizer produces null, so nothing is handed back")
    @Test
    void newRandomizedInstanceOf_Empty_RandomizerProducesNull() {
        assertThat(ObjectRandomizerUtils.newRandomizedInstanceOf(Hollow.class)).isEmpty();
    }
}

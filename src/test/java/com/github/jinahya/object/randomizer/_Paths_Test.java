package com.github.jinahya.object.randomizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link _Paths#reset(Object, List)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class _Paths_Test {

    static class Leaf {

        String value;

        int count = 7;

        List<String> tags = new ArrayList<>();
    }

    static class NoDefaultConstructor {

        NoDefaultConstructor(final String value, final int count) {
            this.value = value;
            this.count = count;
        }

        String value;

        int count;
    }

    record Pair(String value) {

    }

    static class Root {

        Leaf leaf;

        Leaf[] array;

        Map<String, Leaf> map;

        Optional<Leaf> optional;

        List<List<Leaf>> nested;

        NoDefaultConstructor odd;

        Pair pair;
    }

    private static Leaf leaf() {
        final var leaf = new Leaf();
        leaf.value = "randomized";
        leaf.count = 1;
        leaf.tags = new ArrayList<>(List.of("randomized"));
        return leaf;
    }

    @DisplayName("a slot is reset to what a fresh owner carries, and a fresh value is never shared")
    @Test
    void reset_FreshValue() {
        final var root = new Root();
        root.array = new Leaf[]{leaf(), leaf()};
        _Paths.reset(root, List.of("array", "count"));
        _Paths.reset(root, List.of("array", "tags"));
        assertThat(root.array).allSatisfy(l -> {
            assertThat(l.count).isEqualTo(7);
            assertThat(l.tags).isEmpty();
            assertThat(l.value).isEqualTo("randomized");
        });
        assertThat(root.array[0].tags).isNotSameAs(root.array[1].tags);
    }

    @DisplayName("containers are reached through: arrays, map values, optionals, and nested collections")
    @Test
    void reset_Containers() {
        final var root = new Root();
        root.leaf = leaf();
        root.map = Map.of("k", leaf());
        root.optional = Optional.of(leaf());
        root.nested = List.of(List.of(leaf(), leaf()), List.of(leaf()));
        for (final var head : List.of("leaf", "map", "optional", "nested")) {
            _Paths.reset(root, List.of(head, "value"));
        }
        assertThat(root.leaf.value).isNull();
        assertThat(root.map.get("k").value).isNull();
        assertThat(root.optional.orElseThrow().value).isNull();
        assertThat(root.nested).allSatisfy(l -> assertThat(l).allSatisfy(e -> assertThat(e.value).isNull()));
    }

    @DisplayName("an owner with no no-argument constructor is reset to the default of the type")
    @Test
    void reset_NoDefaultConstructor() {
        final var root = new Root();
        root.odd = new NoDefaultConstructor("randomized", 3);
        _Paths.reset(root, List.of("odd", "value"));
        _Paths.reset(root, List.of("odd", "count"));
        assertThat(root.odd.value).isNull();
        assertThat(root.odd.count).isZero();
    }

    @DisplayName("nothing fails: a null on the way, a name which matches nothing, and an unwritable slot")
    @Test
    void reset_PassesOver() {
        final var root = new Root();
        root.pair = new Pair("randomized");
        _Paths.reset(root, List.of("leaf", "value"));       // null on the way
        _Paths.reset(root, List.of("nothing", "value"));    // no such field
        _Paths.reset(root, List.of("pair", "value"));       // a record component, which is never writable
        assertThat(root.pair.value()).isEqualTo("randomized");
    }
}

package com.github.jinahya.object.randomizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link _Paths}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3577" // Test classes should comply with a naming convention
})
class _Paths_Test {

    // ------------------------------------------------------------------------------------------------------- normalize

    @DisplayName("runs of dots, white space, and space separators separate segments, and the path is rejoined")
    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "id          | id",
            "' id '      | id",
            "a.b         | a.b",
            "a..b        | a.b",
            "' a . b '   | a.b",
            "'a b'       | a.b",
            "'a\tb'      | a.b",
            "a.          | a",
            ".a          | a",
            "e-f.1g      | e-f.1g"
    })
    void normalize(final String path, final String expected) {
        assertThat(_Paths.normalize(path)).isEqualTo(expected);
    }

    @DisplayName("a no-break space, and an ideographic space, separate segments too")
    @ParameterizedTest
    @ValueSource(strings = {"a b", "a　b", "a\n.b"})
    void normalize_SpaceSeparators(final String path) {
        assertThat(_Paths.normalize(path)).isEqualTo("a.b");
    }

    @DisplayName("a path with no segment is rejected")
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", " ", ".", " . ", "..", ". \t ."})
    void normalize_IllegalArgumentException_NoSegment(final String path) {
        assertThatThrownBy(() -> _Paths.normalize(path)).isInstanceOf(IllegalArgumentException.class);
    }

    // ----------------------------------------------------------------------------------------------------------- prune

    @DisplayName("a path under another one is pruned, segment by segment, and nothing else")
    @Test
    void prune() {
        final var pruned = _Paths.prune(Set.of("a", "a.b", "a.b.c", "ab.c", "x.y", "x.y.z", "x.yz", "p.q.r"));
        assertThat(pruned).containsExactlyInAnyOrder("a", "ab.c", "x.y", "x.yz", "p.q.r");
    }

    @DisplayName("the pruned set is unmodifiable")
    @Test
    void prune_Unmodifiable() {
        final var pruned = _Paths.prune(Set.of("a"));
        assertThatThrownBy(() -> pruned.add("b")).isInstanceOf(UnsupportedOperationException.class);
    }

    @DisplayName("an empty set prunes to an empty set")
    @Test
    void prune_Empty() {
        assertThat(_Paths.prune(Set.of())).isEmpty();
    }

    // -------------------------------------------------------------------------------------------------------- isNested

    @DisplayName("a path of more than one segment is nested; one of a single segment is simple")
    @ParameterizedTest
    @CsvSource({"id, false", "a.b, true", "a.b.c, true"})
    void isNested(final String path, final boolean expected) {
        assertThat(_Paths.isNested(path)).isEqualTo(expected);
    }
}

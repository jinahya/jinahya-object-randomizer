package smoke;

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SmokeTest {

    private static void assertAbsent(final String className) {
        assertThrows(ClassNotFoundException.class, () -> Class.forName(className), className);
    }

    @Test
    void runsOnTheTargetedRelease() {
        assertEquals(17, Runtime.version().feature(), "not run on Java 17");
    }

    @Test
    void bringsOnlyTheChosenEngine() {
        assertAbsent("uk.co.jemos.podam.api.PodamFactory");
        assertAbsent("com.navercorp.fixturemonkey.FixtureMonkey");
    }

    @Test
    void randomizes() {
        final var foo = ObjectRandomizerUtils.newRandomizedInstanceOf(Foo.class).orElseThrow();
        assertNotNull(foo.getName(), "name");
        assertNull(foo.getId(), "id, which is excluded");
    }
}

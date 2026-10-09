package smoke;

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

import java.util.List;

/**
 * The randomizer of {@link Foo}, declared under {@code test}, as a consumer would.
 */
class FooRandomizer
        extends PodamObjectRandomizer<Foo> {

    FooRandomizer() {
        super(Foo.class, List.of("id"));
    }
}

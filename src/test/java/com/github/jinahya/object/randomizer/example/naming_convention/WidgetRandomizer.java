package com.github.jinahya.object.randomizer.example.naming_convention;

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

import java.util.List;

/**
 * The randomizer of {@link Widget}, named so that the convention finds it.
 * <p>
 * The name carries the whole of the wiring: the target's own name, and the {@code Randomizer} postfix, in the target's
 * package. It is the first of the two postfixes probed, the other being {@code _Randomizer}, which the specification
 * examples use.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class WidgetRandomizer
        extends PodamObjectRandomizer<Widget> {

    /**
     * Creates a new instance.
     *
     * @apiNote The no-argument constructor is what a located randomizer is instantiated by, and so is not
     *         optional here.
     */
    public WidgetRandomizer() {
        super(Widget.class, List.of());
    }
}

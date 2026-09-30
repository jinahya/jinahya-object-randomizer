package com.github.jinahya.object.randomizer.example.excluded_fields;

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

import java.util.List;

/**
 * A randomizer of {@link Entity} which excludes its {@code id}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Entity_Randomizer
        extends PodamObjectRandomizer<Entity> {

    /**
     * Creates a new instance.
     */
    public Entity_Randomizer() {
        super(Entity.class, List.of(Entity.FIELD_ID));
    }
}

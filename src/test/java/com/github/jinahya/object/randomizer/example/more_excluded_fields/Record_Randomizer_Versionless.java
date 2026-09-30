package com.github.jinahya.object.randomizer.example.more_excluded_fields;

import java.util.List;

/**
 * A randomizer which extends {@link Record_Randomizer} and leaves the {@code version} alone as well.
 * <p>
 * The {@code id} the base excludes is nowhere in this class: it names only the field which is its own, and the base
 * merges the two.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Record_Randomizer_Versionless
        extends Record_Randomizer {

    /**
     * Creates a new instance.
     */
    public Record_Randomizer_Versionless() {
        super(List.of(Record.FIELD_VERSION));
    }
}

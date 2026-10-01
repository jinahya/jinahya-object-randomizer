package com.github.jinahya.object.randomizer.example.more_excluded_fields;

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

import java.util.List;

/**
 * The base randomizer of {@link Record}, which excludes its {@code id}.
 * <p>
 * It is written to be extended: the constructor an extending randomizer calls takes the names that randomizer adds, and
 * merges them with {@link #EXCLUDED_FIELDS} through
 * {@link ObjectRandomizerUtils#moreExcludedFields(Iterable, Iterable) moreExcludedFields}. A subclass therefore names
 * only what is its own, and stays correct when this class comes to exclude something more.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Record_Randomizer_Versionless
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Record_Randomizer
        extends PodamObjectRandomizer<Record> {

    /**
     * The exclusions this randomizer applies of its own, which an extending randomizer adds to rather than restates.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of(Record.FIELD_ID);

    /**
     * Creates a new instance which excludes nothing beyond {@link #EXCLUDED_FIELDS}.
     */
    public Record_Randomizer() {
        this(List.of());
    }

    /**
     * Creates a new instance which excludes the specified names as well as {@link #EXCLUDED_FIELDS}.
     *
     * @param moreExcludedFields the names to exclude in addition.
     */
    protected Record_Randomizer(final Iterable<String> moreExcludedFields) {
        super(Record.class, ObjectRandomizerUtils.moreExcludedFields(EXCLUDED_FIELDS, moreExcludedFields));
    }
}

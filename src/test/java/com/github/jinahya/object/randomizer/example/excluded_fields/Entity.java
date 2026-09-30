package com.github.jinahya.object.randomizer.example.excluded_fields;

/**
 * A target whose {@code id} stands for a value the persistence provider assigns, and which a randomizer must
 * therefore leave alone.
 * <p>
 * The {@code id} carries {@link #UNASSIGNED} from the moment the instance exists, which is what makes the exclusion
 * observable: a randomizer which honored it leaves that value in place, and one which did not writes over it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class Entity {

    /**
     * The value an {@code id} carries before anything assigns one; {@value}.
     */
    public static final long UNASSIGNED = -1L;

    /**
     * The name of the excluded field; {@value}.
     */
    public static final String FIELD_ID = "id";

    @Override
    public String toString() {
        return super.toString() + '{'
               + "id=" + id
               + ",name=" + name
               + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * The excluded field.
     */
    private Long id = UNASSIGNED;

    /**
     * A field excluded from nothing, as the control: whatever is said of {@link #id} means little unless the very
     * same randomizer is seen to fill this one.
     */
    private String name;
}

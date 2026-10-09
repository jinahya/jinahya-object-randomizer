package com.github.jinahya.object.randomizer.example.more_excluded_paths;

/**
 * A target with two fields a randomizer might leave alone and one it should always fill.
 * <p>
 * Both excludable fields carry a value from the moment the instance exists, which is what makes an exclusion
 * observable.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class Record {

    /**
     * The value the excludable fields carry before anything assigns one; {@value}.
     */
    public static final long UNASSIGNED = -1L;

    /**
     * The name of the field the base randomizer excludes; {@value}.
     */
    public static final String FIELD_ID = "id";

    /**
     * The name of the field only the extending randomizer excludes; {@value}.
     */
    public static final String FIELD_VERSION = "version";

    @Override
    public String toString() {
        return super.toString() + '{'
               + "id=" + id
               + ",version=" + version
               + ",name=" + name
               + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    private Long id = UNASSIGNED;

    private Long version = UNASSIGNED;

    /**
     * A field neither randomizer excludes, as the control.
     */
    private String name;
}

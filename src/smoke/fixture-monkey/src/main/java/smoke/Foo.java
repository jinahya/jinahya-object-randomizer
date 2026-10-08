package smoke;

/**
 * A target class of {@code main}, with accessors, which every engine populates.
 */
public class Foo {

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

    private Long id;

    private String name;
}

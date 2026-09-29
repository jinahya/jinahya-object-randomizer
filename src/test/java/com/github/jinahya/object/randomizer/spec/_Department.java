package com.github.jinahya.object.randomizer.spec;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The specification's {@code Department} example; the owning side of the association an {@link _Employee} requires.
 * <p>
 * The {@code name} is unique, deliberately: a randomizer which produced a constant -- which is what
 * {@link com.github.jinahya.object.randomizer.__Randomizer.___OfEasyRandom ___OfEasyRandom} would do with a
 * fixed seed -- would hand out the same name twice, and a caller which requires it to be unique would fail on
 * the second, which is a failure worth having.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Department_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Department {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{'
               + "id=" + id
               + ",name=" + name
               + '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
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

    public Set<_Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(final Set<_Employee> employees) {
        this.employees = employees;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Long id;

    private String name;

    /**
     * The inverse side of {@link _Employee#getDepartment()}.
     *
     * @implNote Excluded from randomization. It is the inverse side, so nothing here is written back, and a
     *         randomizer which filled it would have to invent {@link _Employee} instances -- each of which
     *         requires a {@code _Department}, which is where this started.
     */
    private Set<_Employee> employees = new LinkedHashSet<>();
}

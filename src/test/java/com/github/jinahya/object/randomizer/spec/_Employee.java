package com.github.jinahya.object.randomizer.spec;

import java.time.LocalDate;

/**
 * The specification's {@code Employee} example, carrying every shape the three roles have to cope with.
 * <p>
 * A generated {@code id} and a {@code version}, which belong to the provider; a {@code hireDate}, which the
 * {@link _Employee_Instantiator} assigns; an {@link _Address}, which is embedded rather than declared here; a
 * {@code department} which may not be {@code null}, and which a caller has to supply; and
 * a {@code manager} of this very type, which is where a randomizer left alone would recurse.
 * <p>
 * Accessors are declared throughout, so that the
 * {@link com.github.jinahya.object.randomizer.__Randomizer.___OfPodam ___OfPodam} flavor, which writes through
 * setters and never assigns a field, can populate it at all.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Employee_Instantiator
 * @see _Employee_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Employee {

    /**
     * The name of the {@code department} attribute; the {@code mappedBy} of {@link _Department#getEmployees()} names
     * it.
     */
    public static final String ATTRIBUTE_NAME_DEPARTMENT = "department";

    /**
     * The name of the {@code manager} attribute.
     */
    public static final String ATTRIBUTE_NAME_MANAGER = "manager";

    /**
     * The name of the {@code hireDate} attribute.
     */
    public static final String ATTRIBUTE_NAME_HIRE_DATE = "hireDate";

    /**
     * The name of the {@code id} attribute.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    /**
     * The name of the {@code version} attribute.
     */
    public static final String ATTRIBUTE_NAME_VERSION = "version";

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{'
               + "id=" + id
               + ",version=" + version
               + ",name=" + name
               + ",hireDate=" + hireDate
               + ",salary=" + salary
               + ",address=" + address
               + '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
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

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(final LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(final Long salary) {
        this.salary = salary;
    }

    public _Address getAddress() {
        return address;
    }

    public void setAddress(final _Address address) {
        this.address = address;
    }

    public _Department getDepartment() {
        return department;
    }

    public void setDepartment(final _Department department) {
        this.department = department;
    }

    public _Employee getManager() {
        return manager;
    }

    public void setManager(final _Employee manager) {
        this.manager = manager;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Long id;

    private Long version;

    private String name;

    /**
     * The date this employee was hired.
     *
     * @implNote Assigned by the {@link _Employee_Instantiator}, and excluded from randomization, so that a
     *         value which arrives with the instance survives all the way to the database. Keeping it out of the
     *         randomizer also keeps {@code java.time} out of the engines, which support it unevenly.
     */
    private LocalDate hireDate;

    private Long salary;

    private _Address address;

    /**
     * The department this employee belongs to; never {@code null} in the database.
     *
     * @implNote Excluded from randomization, and supplied by the caller: an engine which filled it would
     *         invent a {@code _Department} of its own.
     */
    private _Department department;

    /**
     * This employee's manager, if any.
     *
     * @implNote Excluded from randomization. Being of this very type, it is what an engine would follow into a
     *         graph of employees, each requiring a department of its own.
     */
    private _Employee manager;
}

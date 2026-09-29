package com.github.jinahya.object.randomizer.spec;

/**
 * An address, embedded in an {@link _Employee}.
 * <p>
 * This is the specification's {@code Address} example, reduced to four columns. It is here so that the randomizers have
 * a nested value to populate: an embeddable is the one shape whose attributes are reached through an attribute of the
 * entity, rather than declared on it, and so the one shape a flat exclusion name can not single out.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Address {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{'
               + "street=" + street
               + ",city=" + city
               + ",state=" + state
               + ",zipcode=" + zipcode
               + '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(final String zipcode) {
        this.zipcode = zipcode;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private String street;

    private String city;

    private String state;

    private String zipcode;
}

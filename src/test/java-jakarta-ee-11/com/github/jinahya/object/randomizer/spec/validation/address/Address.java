package com.github.jinahya.object.randomizer.spec.validation.address;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The {@code Address} of the Jakarta Validation 3.1 specification, as the target class of a randomizer.
 * <p>
 * Taken from the {@code Make use of group sequence} example of the
 * {@code constraint declaration and validation process} chapter, linked below; the {@code 3.1.0} tag of the
 * specification sources carries the listing that section renders. The three fields, and the constraints on them, are
 * the specification's; the class is here so that an engine's constraint support is measured against a shape the
 * specification itself publishes, rather than one invented in a unit test.
 * <p>
 * Everything belonging to a specification is declared under that specification's own
 * {@code src/test/java-jakarta-ee-NN} source root -- this class, its randomizers, and their tests alike -- and only
 * the root of the active {@code jakarta-ee-NN} profile is compiled.
 * Nothing is shared between the generations, even where two copies happen to have the same signature: a specification
 * is free to change one of them without the other, and the split is what keeps that from becoming a conflict.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote Two things the specification declares are deliberately absent. Its {@code @ZipCode} field
 *         constraint, and its {@code @ZipCodeCoherenceChecker} class-level constraint, are examples of <em>custom</em>
 *         constraints, whose validators the specification only sketches; no engine could satisfy either, so a
 *         randomized instance could never be valid. Dropping the class-level one leaves nothing in the
 *         {@code HighLevelCoherence} group, which in turn leaves the {@code Complete} group sequence empty, so both
 *         interfaces are dropped with it. What remains is the built-in constraints, which is what an engine reads.
 *         <p>
 *         Accessors are added, which the specification's class does not declare: {@code PodamObjectRandomizer} writes a
 *         property through its setter and never assigns a field.
 * @see <a href="https://jakarta.ee/specifications/bean-validation/3.1/jakarta-validation-spec-3.1
 *         .html#example-groupsequence">Jakarta Validation 3.1, Make use of group sequence</a>
 */
public class Address {

    /**
     * The Jakarta EE generation this copy was taken from; {@code 3.1}, of {@code jakarta-ee-11}.
     */
    public static final String SPECIFICATION_VERSION = "3.1";

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{'
               + "street1=" + street1
               + ",zipCode=" + zipCode
               + ",city=" + city
               + '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
    public String getStreet1() {
        return street1;
    }

    public void setStreet1(final String street1) {
        this.street1 = street1;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = 50)
    private String street1;

    /**
     * The zip code.
     *
     * @implNote The specification declares {@code @NotNull @ZipCode} here; the custom constraint is dropped.
     */
    @NotNull
    private String zipCode;

    @NotNull
    @Size(max = 30)
    private String city;
}

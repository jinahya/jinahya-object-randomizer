package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

/**
 * The {@code Address} of the Jakarta Validation 3.1 specification, as the target class of a randomizer, carrying the
 * specification's own zip code constraint.
 * <p>
 * Taken from the {@code Multi-valued constraint declaration} example of the
 * {@code constraint declaration and validation process} chapter, linked below; the {@code 3.1.0} tag of the
 * specification sources carries the listing that section renders. The class, and its single {@code zipCode} field, are
 * the specification's. It is a different {@code Address} from the one of the {@code address} package beside this one,
 * which the same specification publishes four chapters later.
 * <p>
 * The point of the class is the constraint on that field. It is the only bean the specification puts a zip code
 * constraint on, and {@link FrenchZipCode} is the only zip code constraint the specification publishes in full, so the
 * two are put together here: the {@code @Pattern} inside the composed constraint is a thing no other example in either
 * generation asks an engine for.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote The constraint on the field is changed, and it is the one invention in this package. The
 *         specification declares two {@code @ZipCode(countryCode = "fr")} constraints here, one per group, and its
 *         {@code ZipCodeValidator} is a custom validator the specification only names -- so, as with the
 *         {@code @ZipCode} which the {@code address} package drops, no engine could satisfy it and a randomized
 *         instance could never be valid. What replaces it is the specification's own French zip code constraint, from
 *         the composition example of the same chapter, which says the same thing about the same field and is published
 *         in full.
 *         <p>
 *         Accessors are added, which the specification's class does not declare: {@code PodamObjectRandomizer} writes a
 *         property through its setter and never assigns a field.
 * @see FrenchZipCode
 * @see <a href="https://jakarta.ee/specifications/bean-validation/3.1/jakarta-validation-spec-3.1
 *         .html#constraintsdefinitionimplementation-multipleconstraints">Jakarta Validation 3.1, Applying multiple
 *         constraints of the same type</a>
 */
public class Address {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{'
               + "zipCode=" + zipCode
               + '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @FrenchZipCode
    private String zipCode;
}

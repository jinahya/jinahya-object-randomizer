package com.github.jinahya.object.randomizer.spec.validation.french_zip_code_direct;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The {@code Address} of the Jakarta Validation 3.1 specification, carrying the constraints which compose the
 * specification's French zip code, declared directly on the field instead of through the composed annotation.
 * <p>
 * The class, and its single {@code zipCode} field, are the specification's, from the
 * {@code Multi-valued constraint declaration} example of the {@code constraint declaration and validation process}
 * chapter, linked below. The constraints on the field are the specification's too, from the
 * {@code Constraint composition} section of the same chapter -- which is where they are declared, on the
 * {@code @FrenchZipCode} of the {@code french_zip_code} package.
 * <p>
 * Putting them here directly is not a liberty taken with the listing; it is what the specification says the listing
 * means. Of the composed annotation it writes: annotating an element with it <q>is equivalent to annotating it with
 * {@code @Pattern(regexp="[0-9]*")}, {@code @Size(min=5, max=5)} (the composing annotations) and
 * {@code @FrenchZipCode}</q>. This class is that equivalence, minus the composed annotation which, having no validator
 * of its own, checks nothing.
 * <p>
 * The package exists because no engine honors that equivalence. A constraint an engine reads when it is declared on the
 * field is one it misses, or refuses outright, one level down; the two packages carry the same requirement, and they do
 * not come out the same.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote Accessors are added, which the specification's class does not declare:
 *         {@code PodamObjectRandomizer} writes a property through its setter and never assigns a field.
 * @see <a href="https://jakarta.ee/specifications/bean-validation/3.1/jakarta-validation-spec-3.1
 *         .html#constraintsdefinitionimplementation-constraintcomposition">Jakarta Validation 3.1, Constraint
 *         composition</a>
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
    @Pattern(regexp = "[0-9]*")
    @Size(min = 5, max = 5)
    private String zipCode;
}

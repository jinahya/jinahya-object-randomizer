package com.github.jinahya.object.randomizer.spec.validation.french_zip_code;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The {@code FrenchZipCode} of the Jakarta Validation 3.0 specification, as the constraint a randomizer has to
 * satisfy.
 * <p>
 * Taken from the {@code Composition is done by annotating the composed constraint} example of the
 * {@code constraint declaration and validation process} chapter, linked below; the {@code 3.0.0} tag of the
 * specification sources carries the listing that section renders. It is the only place either generation of the
 * specification puts a {@link Pattern} on anything.
 * <p>
 * Annotating an element with it is, as the specification puts it, equivalent to annotating that element with
 * {@code @Pattern(regexp="[0-9]*")} and {@code @Size(min=5, max=5)} -- the composing constraints -- as well as with
 * {@code @FrenchZipCode} itself. The composing constraints are what an engine has to read here, and the regular
 * expression is what it has to write a value against.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote One token of the listing is changed. The specification declares
 *         {@code @Constraint(validatedBy = FrenchZipCodeValidator.class)}, and then never publishes that validator --
 *         it is referenced five times across the chapter and defined nowhere, in either generation. An empty
 *         {@code validatedBy} is the specification's own form for a constraint which is nothing but its composition;
 *         the {@code @EmmanuelsEmail} example two listings later declares exactly
 *         {@code @Constraint(validatedBy = {})}. The composing constraints, which are the whole of what this constraint
 *         does to a value, are untouched.
 * @see Address
 * @see <a href="https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0
 *         .html#constraintsdefinitionimplementation-constraintcomposition">Jakarta Bean Validation 3.0, Constraint
 *         composition</a>
 */
@Pattern(regexp = "[0-9]*")
@Size(min = 5, max = 5)
@Constraint(validatedBy = {})
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface FrenchZipCode {

    String message() default "Wrong zip code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        FrenchZipCode[] value();
    }
}

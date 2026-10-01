package com.github.jinahya.object.randomizer.spec.validation.car_driver;

/**
 * The {@code Later} validation group of the Jakarta Validation 3.1 specification.
 * <p>
 * Unlike {@link Minimal}, this group is one the specification <em>uses</em> and never declares: {@link Car} names it in
 * the group sequence which redefines its default group, and the {@code Defining a group sequence} example names it
 * again, but no listing in either generation declares the interface itself. It is declared here, empty, because a group
 * is an interface and nothing more; declaring it invents no constraint, and without it the {@link Car} the
 * specification publishes does not compile.
 * <p>
 * This interface belongs to the Jakarta Validation 3.1 specification, and is declared under that generation's own
 * {@code src/test/java-jakarta-ee-11} source root, beside the {@link Car} which names it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Minimal
 * @see <a href="https://jakarta.ee/specifications/bean-validation/3.1/jakarta-validation-spec-3.1
 *         .html#constraintdeclarationvalidationprocess-validationroutine-graphvalidation">Jakarta Validation 3.1,
 *         Object graph validation</a>
 */
public interface Later {

}

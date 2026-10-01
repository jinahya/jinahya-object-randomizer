package com.github.jinahya.object.randomizer.spec.validation.car_driver;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * The {@code Car} of the Jakarta Validation 3.1 specification, as the class a randomized {@link Driver} cascades into.
 * <p>
 * Taken from the {@code Class Car with redefined default group} example of the {@code validation routine} chapter,
 * linked below; the {@code 3.1.0} tag of the specification sources carries the listing that section renders. The two
 * fields, the constraints on them, and the {@link GroupSequence} which redefines the default group are the
 * specification's.
 * <p>
 * The group sequence is what makes this class worth randomizing: {@code roadWorthy} is constrained in the {@link Later}
 * group, which no caller here requests, and yet validating the default group reaches it, because the sequence puts
 * {@code Later} after {@code Car}. An engine which reads only the constraints of the default group produces an instance
 * the specification rejects.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote Accessors are added, which the specification's class does not declare -- its listing says only
 *         {@code // setter/getters}: {@code PodamObjectRandomizer} writes a property through its setter and never
 *         assigns a field. The fields are made {@code private} with them, where the specification leaves them
 *         package-private.
 * @see Driver
 * @see <a href="https://jakarta.ee/specifications/bean-validation/3.1/jakarta-validation-spec-3.1
 *         .html#constraintdeclarationvalidationprocess-validationroutine-graphvalidation">Jakarta Validation 3.1,
 *         Object graph validation</a>
 */
@GroupSequence({Car.class, Later.class})
public class Car {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{'
               + "type=" + type
               + ",roadWorthy=" + roadWorthy
               + '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Boolean getRoadWorthy() {
        return roadWorthy;
    }

    public void setRoadWorthy(final Boolean roadWorthy) {
        this.roadWorthy = roadWorthy;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    private String type;

    /**
     * Whether the car is road worthy.
     *
     * @implNote The constraint belongs to {@link Later}, not to the default group, and is reached only through
     *         the group sequence this class declares.
     */
    @AssertTrue(groups = Later.class)
    private Boolean roadWorthy;
}

package com.github.jinahya.object.randomizer.spec.validation.car_driver;

import jakarta.validation.GroupSequence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;

/**
 * The {@code Driver} of the Jakarta Validation 3.1 specification, as the target class of a randomizer.
 * <p>
 * Taken from the {@code Class Driver with redefined default group} example of the {@code validation routine} chapter,
 * linked below; the {@code 3.1.0} tag of the specification sources carries the listing that section renders. The three
 * fields, the constraints on them, and the {@link GroupSequence} which redefines the default group are the
 * specification's; the class is here so that an engine's constraint support is measured against a shape the
 * specification itself publishes, rather than one invented in a unit test.
 * <p>
 * What it puts to an engine is a bound on a number rather than on a string, a boolean which has to come out one
 * particular way, a constraint declared in a group instead of the default one, and a cascade into the {@link Car}
 * beside it, whose own constraints an engine has to reach through the {@code @Valid}.
 * <p>
 * Everything belonging to a specification is declared under that specification's own
 * {@code src/test/java-jakarta-ee-NN} source root -- this class, {@link Car}, {@link Minimal}, {@link Later}, the
 * randomizers, and their tests alike -- and only the root of the active {@code jakarta-ee-NN} profile is compiled.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote Nothing of the listing is dropped: every constraint here is a built-in one, and the two groups it
 *         names are declared beside it, {@link Minimal} as the specification declares it elsewhere and {@link Later} as
 *         an empty interface the specification never declares at all.
 *         <p>
 *         Accessors are added, which the specification's class does not declare -- its listing says only
 *         {@code // setter/getters}: {@code PodamObjectRandomizer} writes a property through its setter and never
 *         assigns a field. The fields are made {@code private} with them, where the specification leaves them
 *         package-private.
 * @see Car
 * @see <a href="https://jakarta.ee/specifications/bean-validation/3.1/jakarta-validation-spec-3.1
 *         .html#constraintdeclarationvalidationprocess-validationroutine-graphvalidation">Jakarta Validation 3.1,
 *         Object graph validation</a>
 */
@GroupSequence({Minimal.class, Driver.class})
public class Driver {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{'
               + "age=" + age
               + ",passedDrivingTest=" + passedDrivingTest
               + ",car=" + car
               + '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public Boolean getPassedDrivingTest() {
        return passedDrivingTest;
    }

    public void setPassedDrivingTest(final Boolean passedDrivingTest) {
        this.passedDrivingTest = passedDrivingTest;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(final Car car) {
        this.car = car;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The age of the driver.
     *
     * @implNote The constraint belongs to {@link Minimal}, not to the default group, and is reached only
     *         through the group sequence this class declares, which puts {@code Minimal} first.
     */
    @Min(value = 18, groups = Minimal.class)
    private int age;

    @AssertTrue
    private Boolean passedDrivingTest;

    /**
     * The car of the driver.
     *
     * @implNote The specification declares no {@code @NotNull} here, so an instance whose {@code car} is
     *         {@code null} is valid; it is the cascade, not the presence, which the constraint asks for.
     */
    @Valid
    private Car car;
}

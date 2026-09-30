package com.github.jinahya.object.randomizer.spec.validation.car_driver;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the {@link Driver} itself, rather than anything which randomizes it; that the group sequences it and
 * {@link Car} declare are live, and that the cascade between them carries.
 * <p>
 * This is what gives a randomizer's green result its meaning: a constraint the validator never reaches cannot be told
 * apart from one an engine satisfies, so each constraint which only a sequence reaches is failed here on purpose,
 * with a hand-made value.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Driver_Test {

    private static ValidatorFactory factory;

    @BeforeAll
    static void openValidatorFactory() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }

    /**
     * Verifies that the group sequence {@link Driver} declares is live.
     *
     * @implNote {@code @Min} on {@code age} belongs to {@link Minimal} alone, so validating the default group
     *         reaches it only through the sequence.
     */
    @DisplayName("an age below the minimum violates, through the Minimal group of the sequence")
    @Test
    void validate_Violates_AgeBelowMinimum() {
        final var driver = newValidDriver();
        driver.setAge(16);
        assertThat(factory.getValidator().validate(driver))
                .singleElement()
                .satisfies(v -> assertThat(v.getPropertyPath()).hasToString("age"));
    }

    /**
     * Verifies that the group sequence {@link Car} declares is live, and that the cascade reaches it.
     *
     * @implNote {@code @AssertTrue} on {@code roadWorthy} belongs to {@link Later} alone, and {@code Car} is
     *         reached only through the {@code @Valid} on {@code Driver.car}, so this fails on both counts at once.
     */
    @DisplayName("a car which is not road worthy violates, through the cascade and the Later group of the sequence")
    @Test
    void validate_Violates_CarNotRoadWorthy() {
        final var driver = newValidDriver();
        driver.getCar().setRoadWorthy(false);
        assertThat(factory.getValidator().validate(driver))
                .singleElement()
                .satisfies(v -> assertThat(v.getPropertyPath()).hasToString("car.roadWorthy"));
    }

    private static Driver newValidDriver() {
        final var car = new Car();
        car.setType("Porsche");
        car.setRoadWorthy(true);
        final var driver = new Driver();
        driver.setAge(18);
        driver.setPassedDrivingTest(true);
        driver.setCar(car);
        assertThat(factory.getValidator().validate(driver)).as("the fixture itself is valid").isEmpty();
        return driver;
    }
}

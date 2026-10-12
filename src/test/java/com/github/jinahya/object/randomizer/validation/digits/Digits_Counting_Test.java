package com.github.jinahya.object.randomizer.validation.digits;

/*-
 * #%L
 * jinahya-object-randomizer
 * %%
 * Copyright (C) 2026 Jin Kwon
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.validation.constraints.Digits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static com.github.jinahya.object.randomizer._Validation_Test_Utils.applyValidator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Records how the active validation provider counts the digits of {@link Digits @Digits}, value by value.
 * <p>
 * Each row names a constraint, the type the value arrives as, the value, and the verdict of each provider: {@code HV}
 * for Hibernate Validator, {@code BVAL} for Apache BVal. Both providers count {@code precision() - scale()} integral
 * and {@code max(scale(), 0)} fractional digits; they differ over trailing zeros, which Hibernate Validator keeps on a
 * {@code BigDecimal} and on a {@code CharSequence}, and which BVal strips from a {@code BigDecimal} and keeps on a
 * {@code CharSequence}. And {@link BigDecimal#precision()} is {@code 1} for a zero of any scale, so the integral digits
 * of zero follow its scale: {@code 1} for {@code 0}, {@code -1} for {@code 0.00}.
 * <p>
 * The verdicts are the same on Jakarta EE 10 (Hibernate Validator 8.0, BVal 3.0) and Jakarta EE 11 (Hibernate Validator
 * 9.1, BVal 3.1).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://issues.apache.org/jira/browse/BVAL-90">BVAL-90</a>
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Digits_Counting_Test {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A carrier of {@code @Digits(integer = 0, fraction = 2)}, the constraint of a {@code NUMBER(2,2)} column.
     */
    static class Digits_0_2 {

        @Digits(integer = 0, fraction = 2)
        BigDecimal bigDecimal;

        @Digits(integer = 0, fraction = 2)
        String string;
    }

    /**
     * A carrier of {@code @Digits(integer = 1, fraction = 1)}, for trailing zeros of the fractional part.
     */
    static class Digits_1_1 {

        @Digits(integer = 1, fraction = 1)
        BigDecimal bigDecimal;

        @Digits(integer = 1, fraction = 1)
        String string;
    }

    /**
     * A carrier of {@code @Digits(integer = 0, fraction = 0)}, for a zero which no fraction can absorb.
     */
    static class Digits_0_0 {

        @Digits(integer = 0, fraction = 0)
        Long longValue;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private static final String HIBERNATE_VALIDATOR = "org.hibernate.validator.HibernateValidator";

    private static final String APACHE_BVAL = "org.apache.bval.jsr.ApacheValidationProvider";

    /**
     * Returns the verdict expected of the active provider, from the two columns of a row.
     *
     * @param hv   the verdict of Hibernate Validator; {@code true} for valid.
     * @param bval the verdict of Apache BVal; {@code true} for valid.
     * @return the verdict expected of the active provider.
     */
    private static boolean expected(final boolean hv, final boolean bval) {
        final var provider = System.getProperty("jakarta.validation.provider");
        assumeTrue(HIBERNATE_VALIDATOR.equals(provider) || APACHE_BVAL.equals(provider),
                   () -> "no verdicts recorded for provider: " + provider);
        return HIBERNATE_VALIDATOR.equals(provider) ? hv : bval;
    }

    /**
     * Validates the specified value against the constraints of the specified property, as if it were set on it.
     *
     * @param carrier  the class declaring the property.
     * @param property the name of the property.
     * @param value    the value.
     * @return {@code true} when no constraint is violated; {@code false} otherwise.
     */
    private static boolean valid(final Class<?> carrier, final String property, final Object value) {
        return applyValidator(v -> v.validateValue(carrier, property, value)).isEmpty();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Verifies the verdicts of {@code @Digits(integer = 0, fraction = 2)}. Every value is one a {@code NUMBER(2,2)}
     * column holds, so a numeric reading of the constraint accepts all of them; neither provider does.
     *
     * @param value          the value, as text.
     * @param hvBigDecimal   the verdict of Hibernate Validator on the value as a {@code BigDecimal}.
     * @param hvString       the verdict of Hibernate Validator on the value as a {@code String}.
     * @param bvalBigDecimal the verdict of Apache BVal on the value as a {@code BigDecimal}.
     * @param bvalString     the verdict of Apache BVal on the value as a {@code String}.
     */
    @DisplayName("@Digits(integer = 0, fraction = 2)")
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            VALUE, HV_BIG_DECIMAL, HV_STRING, BVAL_BIG_DECIMAL, BVAL_STRING
            0,     false,          false,     false,            false
            0.0,   true,           true,      false,            true
            0.00,  true,           true,      false,            true
            0.5,   true,           true,      true,             true
            0.50,  true,           true,      true,             true
            """)
    void digits_0_2_(final String value, final boolean hvBigDecimal, final boolean hvString,
                     final boolean bvalBigDecimal, final boolean bvalString) {
        assertThat(valid(Digits_0_2.class, "bigDecimal", new BigDecimal(value)))
                .as("valid: new BigDecimal(\"%s\")", value)
                .isEqualTo(expected(hvBigDecimal, bvalBigDecimal));
        assertThat(valid(Digits_0_2.class, "string", value))
                .as("valid: \"%s\"", value)
                .isEqualTo(expected(hvString, bvalString));
    }

    /**
     * Verifies the verdicts of {@code @Digits(integer = 1, fraction = 1)} over trailing zeros, which Hibernate
     * Validator counts and BVal counts on a {@code CharSequence} only.
     *
     * @param value          the value, as text.
     * @param hvBigDecimal   the verdict of Hibernate Validator on the value as a {@code BigDecimal}.
     * @param hvString       the verdict of Hibernate Validator on the value as a {@code String}.
     * @param bvalBigDecimal the verdict of Apache BVal on the value as a {@code BigDecimal}.
     * @param bvalString     the verdict of Apache BVal on the value as a {@code String}.
     */
    @DisplayName("@Digits(integer = 1, fraction = 1)")
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            VALUE, HV_BIG_DECIMAL, HV_STRING, BVAL_BIG_DECIMAL, BVAL_STRING
            0.5,   true,           true,      true,             true
            0.50,  false,          false,     true,             false
            5,     true,           true,      true,             true
            5.0,   true,           true,      true,             true
            5.00,  false,          false,     true,             false
            """)
    void digits_1_1_(final String value, final boolean hvBigDecimal, final boolean hvString,
                     final boolean bvalBigDecimal, final boolean bvalString) {
        assertThat(valid(Digits_1_1.class, "bigDecimal", new BigDecimal(value)))
                .as("valid: new BigDecimal(\"%s\")", value)
                .isEqualTo(expected(hvBigDecimal, bvalBigDecimal));
        assertThat(valid(Digits_1_1.class, "string", value))
                .as("valid: \"%s\"", value)
                .isEqualTo(expected(hvString, bvalString));
    }

    /**
     * Verifies that both providers reject a zero under {@code @Digits(integer = 0, fraction = 0)}: zero is counted with
     * one integral digit.
     *
     * @param value the value.
     * @param hv    the verdict of Hibernate Validator.
     * @param bval  the verdict of Apache BVal.
     */
    @DisplayName("@Digits(integer = 0, fraction = 0)")
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            VALUE, HV,    BVAL
            0,     false, false
            """)
    void digits_0_0_(final long value, final boolean hv, final boolean bval) {
        assertThat(valid(Digits_0_0.class, "longValue", value))
                .as("valid: %dL", value)
                .isEqualTo(expected(hv, bval));
    }
}

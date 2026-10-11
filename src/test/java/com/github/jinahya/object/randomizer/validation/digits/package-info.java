/**
 * The single context of this package: <strong>how the validation provider counts the digits of
 * {@link jakarta.validation.constraints.Digits @Digits}</strong>.
 * <p>
 * The specification defines {@code integer()} and {@code fraction()} only as the "maximum number of integral digits"
 * and the "maximum number of fractional digits accepted for this number", and says nothing about how they are counted:
 * whether trailing zeros of the fractional part count, how many integral digits zero has, and whether a
 * {@code BigDecimal} and a {@code CharSequence} holding the same text are judged alike. The two providers this project
 * runs against fill that gap differently, and neither of them consistently across its own supported types.
 * <p>
 * What is recorded here is the behaviour of each provider, not a requirement of the specification. A test fails when a
 * provider release changes its counting, which is the point: it is where to look again before a constraint here, or a
 * randomizer's output, is relied on. The case which surfaced it is a {@code NUMBER(2,2)} column mapped as
 * {@code @Digits(integer = 0, fraction = 2)}, under which a {@code BigDecimal} zero of scale {@code 0} is rejected.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
package com.github.jinahya.object.randomizer.validation.digits;

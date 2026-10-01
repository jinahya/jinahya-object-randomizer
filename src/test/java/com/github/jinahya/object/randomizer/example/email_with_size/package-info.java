/**
 * The single context of this package: <strong>two constraints on one field</strong>, and whether an engine satisfies
 * both of them at once.
 * <p>
 * A field carrying a single constraint says little about an engine's constraint support, because an engine which reads
 * one annotation and writes against it looks exactly like an engine which reads them all. Putting
 * {@link jakarta.validation.constraints.Email @Email} and {@link jakarta.validation.constraints.Size @Size} on the one
 * field separates the two: a value has to be a well-formed address <em>and</em> fall in the length range, and an engine
 * which honors only the second produces something which is plainly neither.
 * <p>
 * The three flavors do not agree here, which is the point of carrying all three. Instancio and Fixture Monkey satisfy
 * both constraints unaided; PODAM satisfies the {@code @Size} alone, so its randomizer writes the value itself. What
 * every flavor owes, either way, is one contract, and
 * {@link com.github.jinahya.object.randomizer.example.email_with_size.User_Randomized_Verifier} is where that contract
 * is stated.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
package com.github.jinahya.object.randomizer.example.email_with_size;

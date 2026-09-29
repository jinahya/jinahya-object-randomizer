/**
 * An example model, wired to the two roles of {@code jinahya-object-randomizer}.
 * <p>
 * {@link com.github.jinahya.object.randomizer.spec._Employee} and
 * {@link com.github.jinahya.object.randomizer.spec._Department} are plain classes -- no persistence annotations, no
 * framework of any kind -- shaped after the Jakarta Persistence specification's own example, because that shape
 * carries every case the roles have to cope with: a generated identifier and a version which the randomizer must
 * leave alone, a value assigned by an
 * {@link com.github.jinahya.object.randomizer.__Instantiator instantiator}, an embedded
 * {@link com.github.jinahya.object.randomizer.spec._Address}, a mandatory association, a self-association which an
 * engine would recurse into, and an inverse collection.
 * <p>
 * Each class is exercised through the counterparts declared beside it -- {@code _Employee_Instantiator} and
 * {@code _Employee_Randomizer} -- and the same target class is randomized by all four engines, so that a flavor is
 * measured against a realistic shape rather than against one invented in a unit test. What each flavor is required to
 * produce lives in a single {@code _Randomized_Verifier} per target, so that an engine-specific test says only which
 * engine it is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.object.randomizer.__RandomizerUtils#newRandomizedInstanceOf(java.lang.Class)
 */
package com.github.jinahya.object.randomizer.spec;

/**
 * Interfaces and classes for randomizing instances of arbitrary classes.
 * <p>
 * A target class is instantiated, and then randomized, by an
 * {@link com.github.jinahya.object.randomizer.__Instantiator instantiator} and a
 * {@link com.github.jinahya.object.randomizer.__Randomizer randomizer}, each of which is located, for the target
 * class, by a naming convention; see
 * {@link com.github.jinahya.object.randomizer.__InstantiatorUtils#locateStandard(java.lang.Class)} and
 * {@link com.github.jinahya.object.randomizer.__RandomizerUtils#locateStandard(java.lang.Class)} for the conventions
 * applied.
 *
 * <h2>The two roles</h2>
 * <dl>
 *   <dt>{@link com.github.jinahya.object.randomizer.__Instantiator}</dt>
 *   <dd>Creates a bare instance. Optional; when none is located, the target class is instantiated through its
 *       no-argument constructor, which may be {@code private}. Declare one for a class which has no no-argument
 *       constructor, or which needs constructor arguments to be usable.</dd>
 *   <dt>{@link com.github.jinahya.object.randomizer.__Randomizer}</dt>
 *   <dd>Fills an instance with random values, excluding the fields it is told to leave alone. Pick
 *       {@link com.github.jinahya.object.randomizer.__Randomizer.___OfPodam} to keep the instantiator in play, and
 *       for the {@code jakarta.validation.constraints} it honors out of the box, or
 *       {@link com.github.jinahya.object.randomizer.__Randomizer.___OfInstancio} to keep the instantiator in play
 *       for a class which declares no accessors, which PODAM would leave entirely unpopulated. The other two flavors,
 *       {@link com.github.jinahya.object.randomizer.__Randomizer.___OfEasyRandom} and
 *       {@link com.github.jinahya.object.randomizer.__Randomizer.___OfFixtureMonkey}, construct the instance
 *       themselves, and so ignore the instantiator. Of the four, Easy Random 6 is the one which can honor no Jakarta
 *       constraint at all.</dd>
 * </dl>
 *
 * <h2>Engines</h2>
 * Each of the four flavors is backed by a different engine, and every engine is a {@code provided} dependency: a
 * consumer puts exactly one of them on its classpath, and the flavors it does not use are never loaded. The base
 * {@link com.github.jinahya.object.randomizer.__Randomizer} class itself references no engine at all.
 *
 * <h2>Conventions</h2>
 * For a target class {@code Foo}, the convention probes {@code FooRandomizer} and then {@code Foo_Randomizer}, both
 * declared beside {@code Foo}; likewise for {@code Instantiator}. That is the whole rule, and it is the same for both
 * roles: a counterpart is a sibling of its target class. A class nested inside another therefore has to be declared as
 * a top-level class to have a counterpart of its own.
 *
 * <h2>Example</h2>
 * Given a class {@code Foo}, declare, beside it:
 * <pre>{@code
 * class FooRandomizer extends __Randomizer.___OfEasyRandom<Foo> {
 *     FooRandomizer() {
 *         super(Foo.class, List.of("id"));  // leave the generated identifier alone
 *     }
 * }
 * }</pre>
 * and a caller then obtains a randomized instance with a single call:
 * <pre>{@code
 * final var foo = __RandomizerUtils.newRandomizedInstanceOf(Foo.class).orElseThrow();
 * }</pre>
 * Note that each located class is instantiated reflectively, and so has to declare an accessible no-argument
 * constructor which supplies the target class to its superclass, exactly as above.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.object.randomizer;

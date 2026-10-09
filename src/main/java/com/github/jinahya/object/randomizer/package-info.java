/**
 * Interfaces and classes for randomizing instances of arbitrary classes.
 * <p>
 * A target class is randomized by an {@link com.github.jinahya.object.randomizer.ObjectRandomizer randomizer}, which is
 * located, for the target class, by a naming convention; see
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils} for the convention applied.
 *
 * <h2>The types</h2>
 * <dl>
 *   <dt>{@link com.github.jinahya.object.randomizer.ObjectRandomizer}</dt>
 *   <dd>The role itself; a {@link java.util.function.Supplier} of randomized instances, and what the convention
 *       locates.</dd>
 *   <dt>{@link com.github.jinahya.object.randomizer.AbstractObjectRandomizer}</dt>
 *   <dd>A skeletal implementation which holds the target class and the excluded paths, and which every flavor
 *       below extends. It references no engine at all.</dd>
 *   <dt>{@link com.github.jinahya.object.randomizer.PodamObjectRandomizer},
 *       {@link com.github.jinahya.object.randomizer.InstancioObjectRandomizer},
 *       {@link com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer}</dt>
 *   <dd>One flavor per engine.</dd>
 * </dl>
 *
 * <h2>Construction</h2>
 * A randomizer fills an instance with random values, excluding the slots it is told to leave alone &mdash; by a
 * path, such as {@code "id"} or {@code "address.address1"}; see
 * {@link com.github.jinahya.object.randomizer.AbstractObjectRandomizer} for how a path is read. How the instance
 * it fills comes to be depends on the flavor:
 * {@link com.github.jinahya.object.randomizer.PodamObjectRandomizer} and
 * {@link com.github.jinahya.object.randomizer.InstancioObjectRandomizer} take it from
 * {@link com.github.jinahya.object.randomizer.AbstractObjectRandomizer#newTargetInstance() newTargetInstance()},
 * which invokes the no-argument constructor of the target class &mdash; which may be {@code private} &mdash; and which
 * a randomizer overrides for a class that declares no such constructor, or that needs state assigned before it is
 * randomized. {@link com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer} constructs the instance
 * itself, and so never calls that method. Pick {@code PodamObjectRandomizer} for the
 * {@code jakarta.validation.constraints} it honors with nothing added, or
 * {@code InstancioObjectRandomizer} for a class which declares no accessors, which PODAM would leave entirely
 * unpopulated.
 *
 * <h2>Engines</h2>
 * Each of the three flavors is backed by a different engine, and every engine is a {@code provided} dependency: a
 * consumer puts exactly one of them on its classpath, and the flavors it does not use are never loaded. Neither
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizer} nor
 * {@link com.github.jinahya.object.randomizer.AbstractObjectRandomizer} references an engine at all.
 *
 * <h2>Conventions</h2>
 * For a target class {@code Foo}, the convention probes {@code FooRandomizer} and then {@code Foo_Randomizer}: the
 * binary name of {@code Foo} with a postfix appended, wherever the class loader of {@code Foo} finds it. That is the
 * whole rule: a randomizer is a sibling of its target class. For a top-level {@code Foo}, the sibling is a top-level
 * class of the same package name, in any source set; for a {@code Foo} nested in {@code Outer}, it is
 * {@code Outer.FooRandomizer}, which only the source of {@code Outer} can declare. A nested class of {@code main}
 * therefore has to be declared as a top-level class to have a randomizer under {@code test}.
 *
 * <h2>Example</h2>
 * Given a class {@code Foo}, declare, in the same package:
 * <pre>{@code
 * class FooRandomizer extends PodamObjectRandomizer<Foo> {
 *     FooRandomizer() {
 *         super(Foo.class, List.of("id"));  // leave the generated identifier alone
 *     }
 * }
 * }</pre>
 * and a caller then obtains a randomized instance with a single call:
 * <pre>{@code
 * final var foo = ObjectRandomizerUtils.newRandomizedInstanceOf(Foo.class).orElseThrow();
 * }</pre>
 * Note that each located class is instantiated reflectively, and so has to declare an accessible no-argument
 * constructor which supplies the target class to its superclass, exactly as above.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.object.randomizer;

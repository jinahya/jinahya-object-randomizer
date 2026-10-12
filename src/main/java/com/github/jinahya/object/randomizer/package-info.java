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
 * <h2>Caveats</h2>
 * <strong>This library delegates; it does not randomize.</strong> A flavor hands the target class, and the paths to
 * exclude, to its engine, and returns what the engine produces. It neither implements, extends, nor patches an
 * engine, and so it promises nothing about a value beyond what that engine itself promises.
 * <p>
 * <strong>No engine is promised to honor every {@code jakarta.validation.constraints} annotation</strong>, and fewer
 * still are promised to honor several of them on the one slot at once. {@code @Pattern} is the usual casualty, since
 * producing a string which matches an arbitrary regular expression, <em>and</em> satisfies the other constraints
 * beside it, is a hard problem in its own right. Given
 * <pre>
 * &#64;Pattern(regexp = "\\d+")
 * &#64;Size(min = 1, max = 2)
 * private String number;
 * </pre>
 * an engine may well honor the {@code @Size} and ignore the {@code @Pattern}, yielding something like
 * {@code "x$"}. A randomized instance is therefore not assumed to be a valid one; validate it, where validity
 * matters.
 * <p>
 * <strong>Nor is what an engine produces always what a caller wants</strong>, even when every constraint is honored.
 * Given
 * <pre>
 * &#64;Email
 * &#64;Size(min = 6, max = 255)
 * private String email;
 * </pre>
 * an engine which honors both still gives no promise that two instances never share an address, while a table may
 * declare the column {@code UNIQUE}.
 * <p>
 * Both gaps are closed the same way: <strong>every randomizer may override {@code get()}</strong>, and adjust the
 * instance the engine returns before handing it over. That is the one sanctioned hook, and it is always there,
 * whichever flavor is extended:
 * <pre>{@code
 * class FooRandomizer extends PodamObjectRandomizer<Foo> {
 *
 *     FooRandomizer() {
 *         super(Foo.class, List.of("id"));
 *     }
 *
 *     @Override
 *     public Foo get() {
 *         final var value = super.get();
 *         final var random = ThreadLocalRandom.current();
 *         // the engine may not have honored the @Pattern
 *         value.setNumber(Integer.toString(random.nextInt(100)));
 *         // honored or not, make the @Email unique enough for a UNIQUE column
 *         value.setEmail(UUID.randomUUID().toString().replace("-", "") + "@example.com");
 *         return value;
 *     }
 * }
 * }</pre>
 * Note that a value written in {@code get()} has to satisfy every constraint on its slot by itself; above, the
 * {@code number} is one or two digits, and the {@code email} is 44 characters.
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
 * Given a class {@code Foo} in a package {@code com.example},
 * <pre>{@code
 * package com.example;
 *
 * public class Foo {
 *     private Long id;
 *     private String name;
 *     // getters and setters
 * }
 * }</pre>
 * declare its randomizer in the <em>same package</em>, {@code com.example}, in this or any other source set:
 * <pre>{@code
 * package com.example;
 *
 * import com.github.jinahya.object.randomizer.PodamObjectRandomizer;
 * import java.util.List;
 *
 * class FooRandomizer extends PodamObjectRandomizer<Foo> {
 *     FooRandomizer() {
 *         super(Foo.class, List.of("id"));  // leave the generated identifier alone
 *     }
 * }
 * }</pre>
 * and a caller then obtains a randomized instance with a single call, the convention locating
 * {@code com.example.FooRandomizer}:
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

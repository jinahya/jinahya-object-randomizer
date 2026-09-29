# jinahya-object-randomizer

Randomizes instances of arbitrary classes with [PODAM][podam], [Instancio][instancio] or
[Fixture Monkey][fixture-monkey], behind one interface, with the randomizer of a target class
located by a naming convention.

Nothing here is specific to persistence, to testing, or to any framework: a target class is a
plain class.

## The convention

For a target class `Foo`, a randomizer is a **sibling** — declared in the same package, beside it —
named `FooRandomizer`, or `Foo_Randomizer`:

```java
class FooRandomizer extends PodamObjectRandomizer<Foo> {
    FooRandomizer() {
        super(Foo.class, List.of("id"));  // leave the generated identifier alone
    }
}
```

```java
final var foo = ObjectRandomizerUtils.newRandomizedInstanceOf(Foo.class).orElseThrow();
```

A located class is instantiated reflectively, so it has to declare an accessible no-argument
constructor which supplies the target class to its superclass, exactly as above.

## The types

`ObjectRandomizer<T>` is the role — a `Supplier<T>` of randomized instances, and what the
convention locates. `AbstractObjectRandomizer<T>` implements it, holding the target class and the
excluded fields, and the three flavors extend that, one per engine.

## The three flavors

| Flavor | Writes through | Uses `newTargetInstance()` | Exclusions scoped by | Bean validation |
| --- | --- | --- | --- | --- |
| `PodamObjectRandomizer` | accessors only | yes | runtime class | on by default |
| `InstancioObjectRandomizer` | reflection, fills a given instance | yes | field declaration | on by default |
| `FixtureMonkeyObjectRandomizer` | reflection | no | target type | needs a second artifact |

`newTargetInstance()` invokes the no-argument constructor of the target class, which may be
`private`. Override it for a class which declares no such constructor, or which needs state
assigned before it is randomized; an override may even return a subclass. Only the two flavors
marked above call it — Fixture Monkey lets its engine construct the instance.

`PodamObjectRandomizer` writes a property through its setter and recurses through its getter; it
never assigns a field, so a class which declares only fields is left **entirely unpopulated**,
silently. Use one of the other two for such a class.

The three exclusion scopes genuinely differ. A field inherited from a common supertype and shared
with an associated type is excluded on *both* types under Instancio, on the target subtree only
under PODAM, and on the target type only under Fixture Monkey. Read the class Javadoc before
treating the three as interchangeable.

## Bean validation

All three flavors honor `jakarta.validation.constraints` **without being configured to**. Nothing
is overridden, in any of them:

| Flavor | What turns it on | Reads constraints from |
| --- | --- | --- |
| `PodamObjectRandomizer` | the engine itself, via `BeanValidationStrategy` | fields |
| `InstancioObjectRandomizer` | `getInstancioSettings()` sets `Keys.BEAN_VALIDATION_ENABLED` | fields |
| `FixtureMonkeyObjectRandomizer` | `getFixtureMonkey()` registers `JakartaValidationPlugin` | fields |

The engines differ only in what that costs. Podam and Instancio read the constraints out of their
own artifact, so a consumer adds nothing. Fixture Monkey keeps its support in a **second artifact**,
`fixture-monkey-jakarta-validation`, and `getFixtureMonkey()` looks the plugin up *reflectively* —
registering it when it is on the classpath, logging at `DEBUG` when it is not. So this module never
depends on it, and a consumer decides by what they declare rather than by what they override.

Each of the three exposes the decision as an override, should you want the opposite:
`getInstancioSettings()` can clear the key, and `getFixtureMonkey()` can be replaced outright. Note
that an override which does not build on `super`'s return value gives up the field exclusions along
with the constraint support.

Nothing in `src/main` references the API, so it is **test scope only**: an engine reads a target
class's constraints reflectively, and a consumer that annotates its own classes already has the API
on its classpath. PODAM's support is partial — a lone `@Max` is ignored and `@Pattern` yields
`null` — so read `PodamObjectRandomizer`'s Javadoc before trusting a randomized instance to be a
valid one.

### `jakarta-ee-NN` profiles

The platform generation is the one axis in this build, and it moves three versions together: the
`jakarta.jakartaee-bom` that pins `jakarta.validation-api`, and the Hibernate Validator and
Expressly releases aligned with it. Jakarta Validation has a single implementation, so a profile
picks a *generation*, never an implementation.

| Profile | `jakarta.validation-api` | Hibernate Validator | Expressly |
| --- | --- | --- | --- |
| `jakarta-ee-11` (default) | 3.1.1 | 9.1.3.Final | 6.0.0 |
| `jakarta-ee-10` | 3.0.2 | 8.0.3.Final | 5.0.0 |

```shell
./mvnw test                      # jakarta-ee-11
./mvnw -P jakarta-ee-10 test
```

## Engines are `provided`

Every engine is declared `<scope>provided</scope>`: a consumer puts exactly the one it uses on its
classpath. Neither `ObjectRandomizer` nor `AbstractObjectRandomizer` references an engine in its
bytecode, and each flavor references only its own, so the flavors that are not used are never
loaded.

A randomizer whose engine is missing at run time fails to link, and the convention probe reports
that at `WARNING` rather than treating it as "no randomizer declared".

So declare this module and the one engine you picked, side by side. Two things are omitted
throughout: versions, which you pin in your own `<dependencyManagement>`, and `<scope>`, which is
yours to choose — a randomizer is a plain class, so whether these belong on your test classpath or
your compile classpath depends on what you randomize and when.

This module is the same in all three cases:

```xml
<dependency>
  <groupId>io.github.jinahya</groupId>
  <artifactId>jinahya-object-randomizer</artifactId>
</dependency>
```

### PODAM

One artifact. Constraints are honored with nothing else declared.

```xml
<dependency>
  <groupId>uk.co.jemos.podam</groupId>
  <artifactId>podam</artifactId>
</dependency>
```

### Instancio

One artifact. `instancio-core` carries the constraint support, which
`InstancioObjectRandomizer` enables by default.

```xml
<dependency>
  <groupId>org.instancio</groupId>
  <artifactId>instancio-core</artifactId>
</dependency>
```

### Fixture Monkey

**Two** artifacts, and the second is what makes constraints work. Leave it out and the engine fills
your instances while ignoring every constraint on them — with one `DEBUG` line to say so.

```xml
<dependency>
  <groupId>com.navercorp.fixturemonkey</groupId>
  <artifactId>fixture-monkey</artifactId>
</dependency>
<dependency>
  <groupId>com.navercorp.fixturemonkey</groupId>
  <artifactId>fixture-monkey-jakarta-validation</artifactId>
</dependency>
```

Two things to know about that second artifact. It declares a complete **Jakarta EE 9** stack at
`compile` scope — `hibernate-validator` 7.0.5.Final, `jakarta.validation-api` 3.0.2 and
`org.glassfish:jakarta.el` 4.0.2 — which will quietly downgrade an EE 10 or EE 11 classpath. And
`com.navercorp.fixturemonkey:fixture-monkey` pulls in **jqwik**, including `jqwik-engine`, a JUnit
Platform `TestEngine` that then registers itself alongside Jupiter. Neither is a defect; both are
surprises worth meeting on purpose.

For the first, exclude the three and let your own platform generation stand:

```xml
<dependency>
  <groupId>com.navercorp.fixturemonkey</groupId>
  <artifactId>fixture-monkey-jakarta-validation</artifactId>
  <exclusions>
    <exclusion>
      <groupId>org.hibernate.validator</groupId>
      <artifactId>hibernate-validator</artifactId>
    </exclusion>
    <exclusion>
      <groupId>jakarta.validation</groupId>
      <artifactId>jakarta.validation-api</artifactId>
    </exclusion>
    <exclusion>
      <groupId>org.glassfish</groupId>
      <artifactId>jakarta.el</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

Excluding is safe only because you replace them. The plugin's own bytecode references
`jakarta.validation` and nothing else — not one class in it mentions Hibernate — so the API has to
be there, at your generation. An implementation has to be there too: the plugin's
`JakartaArbitraryValidator` calls `Validation.buildDefaultValidatorFactory()`, which resolves one by
service lookup and throws when none is on the classpath. A consumer that annotates its own classes
already declares the API, and one that asserts a randomized instance is valid already declares an
implementation — which is why excluding costs nothing in practice. If you declare neither, pin the
versions instead of excluding.

Note also that `org.glassfish:jakarta.el` is the superseded expression-language implementation;
`org.glassfish.expressly:expressly` is its successor and what Hibernate Validator 8 and 9 expect.

Finally, `fixture-monkey-javax-validation` is the wrong artifact for this platform — the `javax`
annotations are not the ones any of these engines read.

[podam]: https://mtedone.github.io/podam/
[instancio]: https://www.instancio.org
[fixture-monkey]: https://naver.github.io/fixture-monkey

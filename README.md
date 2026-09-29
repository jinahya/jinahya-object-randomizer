# jinahya-object-randomizer

Randomizes instances of arbitrary classes with [PODAM][podam], [Easy Random][easy-random],
[Instancio][instancio] or [Fixture Monkey][fixture-monkey], behind one interface, with the
randomizer of a target class located by a naming convention.

Nothing here is specific to persistence, to testing, or to any framework: a target class is a
plain class.

## The convention

For a target class `Foo`, a randomizer is a **sibling** — declared in the same package, beside it —
named `FooRandomizer`, or `Foo_Randomizer`:

```java
class FooRandomizer extends EasyRandomObjectRandomizer<Foo> {
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
excluded fields, and the four flavors extend that, one per engine.

## The four flavors

| Flavor | Writes through | Uses `newTargetInstance()` | Exclusions scoped by | Bean validation |
| --- | --- | --- | --- | --- |
| `PodamObjectRandomizer` | accessors only | yes | runtime class | built in |
| `EasyRandomObjectRandomizer` | reflection | no | field declaration | never |
| `InstancioObjectRandomizer` | reflection, fills a given instance | yes | field declaration | opt in |
| `FixtureMonkeyObjectRandomizer` | reflection | no | target type | plugin |

`newTargetInstance()` invokes the no-argument constructor of the target class, which may be
`private`. Override it for a class which declares no such constructor, or which needs state
assigned before it is randomized; an override may even return a subclass. Only the two flavors
marked above call it — the other two let their engine construct the instance.

`PodamObjectRandomizer` writes a property through its setter and recurses through its getter; it
never assigns a field, so a class which declares only fields is left **entirely unpopulated**,
silently. Use one of the other three for such a class.

The three exclusion scopes genuinely differ. A field inherited from a common supertype and shared
with an associated type is excluded on *both* types under Easy Random and Instancio, on the target
subtree only under PODAM, and on the target type only under Fixture Monkey. Read the class Javadoc
before treating the four as interchangeable.

## Bean validation

Three of the four engines can read `jakarta.validation.constraints` off a target class, each turned
on differently:

| Flavor | How | Reads constraints from |
| --- | --- | --- |
| `PodamObjectRandomizer` | nothing to do | fields |
| `InstancioObjectRandomizer` | override `getInstancioSettings()`, set `Keys.BEAN_VALIDATION_ENABLED` | fields |
| `FixtureMonkeyObjectRandomizer` | override `getFixtureMonkey()`, register `JakartaValidationPlugin` | fields |
| `EasyRandomObjectRandomizer` | not possible | — |

Nothing in `src/main` references the API, so it is **test scope only**: an engine reads a target
class's constraints reflectively, and a consumer that annotates its own classes already has the API
on its classpath. Easy Random 6 removed its constraint support outright, and PODAM's is partial —
read `PodamObjectRandomizer`'s Javadoc before trusting a randomized instance to be a valid one.

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

## Origin

Extracted from [`jinahya-persistence`][jinahya-persistence], where these classes served JPA entity
tests. The JPA framing was motivational, not structural — the code never depended on
`jakarta.persistence`.

[podam]: https://mtedone.github.io/podam/
[easy-random]: https://github.com/j-easy/easy-random
[instancio]: https://www.instancio.org
[fixture-monkey]: https://naver.github.io/fixture-monkey
[jinahya-persistence]: https://github.com/jinahya/jinahya-persistence

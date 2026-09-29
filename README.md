# jinahya-object-randomizer

Randomizes instances of arbitrary classes with [PODAM][podam], [Easy Random][easy-random],
[Instancio][instancio] or [Fixture Monkey][fixture-monkey], behind one abstract class, with the
randomizer of a target class located by a naming convention.

Nothing here is specific to persistence, to testing, or to any framework: a target class is a
plain class.

## The convention

For a target class `Foo`, a randomizer is a **sibling** — declared in the same package, beside it —
named `FooRandomizer`, or `Foo_Randomizer`:

```java
class FooRandomizer extends __Randomizer.___OfEasyRandom<Foo> {
    FooRandomizer() {
        super(Foo.class, List.of("id"));  // leave the generated identifier alone
    }
}
```

```java
final var foo = __RandomizerUtils.newRandomizedInstanceOf(Foo.class).orElseThrow();
```

A located class is instantiated reflectively, so it has to declare an accessible no-argument
constructor which supplies the target class to its superclass, exactly as above.

An `__Instantiator`, located by the same convention (`FooInstantiator` / `Foo_Instantiator`), is
optional; without one the target class is constructed through its no-argument constructor, which
may be `private`.

## The four flavors

| Flavor | Writes through | Uses the instantiator | Exclusions scoped by | Bean validation |
| --- | --- | --- | --- | --- |
| `___OfPodam` | accessors only | yes | runtime class | built in |
| `___OfEasyRandom` | reflection | no | field declaration | never |
| `___OfInstancio` | reflection, fills a given instance | yes | field declaration | opt in |
| `___OfFixtureMonkey` | reflection | no | target type | plugin |

`___OfPodam` writes a property through its setter and recurses through its getter; it never assigns
a field, so a class which declares only fields is left **entirely unpopulated**, silently. Use one
of the other three for such a class.

The three exclusion scopes genuinely differ. A field inherited from a common supertype and shared
with an associated type is excluded on *both* types under Easy Random and Instancio, on the target
subtree only under PODAM, and on the target type only under Fixture Monkey. Read the class Javadoc
before treating the four as interchangeable.

## Engines are `provided`

Every engine is declared `<scope>provided</scope>`: a consumer puts exactly the one it uses on its
classpath. `__Randomizer` itself references no engine in its bytecode, and each `___Of*` nested
class references only its own, so the flavors that are not used are never loaded.

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

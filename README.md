# jinahya-object-randomizer

[![Java CI with Maven](https://github.com/jinahya/jinahya-object-randomizer/actions/workflows/maven.yml/badge.svg)](https://github.com/jinahya/jinahya-object-randomizer/actions/workflows/maven.yml)
[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=jinahya_jinahya-object-randomizer&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jinahya_jinahya-object-randomizer)

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-object-randomizer)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-object-randomizer)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-object-randomizer/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-object-randomizer)

Randomizes instances of arbitrary classes with [PODAM][podam], [Instancio][instancio] or
[Fixture Monkey][fixture-monkey], behind one interface, with the randomizer of a target class
located by a naming convention.

Nothing here is specific to persistence, to testing, or to any framework: a target class is a
plain class.

## The convention

For a target class `Foo`, a randomizer is a class of the same package *name* — declared in any
source set, jar or module which the class loader of the target can see — named `FooRandomizer`, or
`Foo_Randomizer`:

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

The randomizer is located by its fully qualified name, through the class loader of the target
class, so it need not be compiled with the target: a `FooRandomizer` under `src/test` serves a `Foo`
under `src/main`. On the module path, though, a package can not be split across named modules, so
the randomizer belongs to the module of the target, or is patched into it, as test runners do.

A nested target is no exception: the name is the target's binary name plus the postfix, so the
randomizer of `Outer.Foo` is `Outer.FooRandomizer`, nested beside it in `Outer`. Only the source of
`Outer` can declare that, so a nested class of `src/main` has to be made top-level to get a
randomizer under `src/test`.

## The types

`ObjectRandomizer<T>` is the role — a `Supplier<T>` of randomized instances, and what the
convention locates. `AbstractObjectRandomizer<T>` implements it, holding the target class and the
excluded paths, and the three flavors extend that, one per engine.

## The three flavors

| Flavor | Writes through | Uses `newTargetInstance()` | Single names scoped by | Bean validation |
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

## Excluded paths

The second constructor argument is a list of *paths*: names of the slots the engine is asked to
leave alone. An exclusion is a hint — each engine discovers slots its own way, and honors a path as
far as it can.

The word *path* is chosen ahead of what every engine can do. A **single name**, such as `"id"` or
`"address"`, is what every flavor honors, by handing it to its engine. A **dotted path**, such as
`"address.address1"`, is a best-effort extension: each flavor here honors it, within the limits
below, but a flavor of another engine, or one written by hand, need not.

```java
super(User.class, List.of("id", "address.address1"));
```

### Rules

- **A single name** goes to the engine's own exclusion mechanism, and is scoped as that engine
  scopes an exclusion — see *Single names scoped by* in the table above, and below.
- **A dotted path** is anchored at the target: the first segment names a slot of the target, each
  next one a slot of what the previous one reached. A segment which reaches a collection, an array,
  an `Optional` or a map's values reaches every element, so `"addresses.address1"` names the
  `address1` of every element of `addresses`.
- **A dotted slot is left at what a freshly constructed owner carries** — the engine created that
  owner, so there is no earlier value to keep. Nothing else is ever written: an owner which can not
  be constructed afresh, through a no-argument constructor, keeps the slot as the engine wrote it.
- **Paths are normalized.** A path is split on runs of `.` and white space and rejoined with `.`,
  so `" a . b "`, `"a..b"` and `"a b"` are all `"a.b"`, and `"a."` is `"a"`; no engine is ever
  handed a name with white space in it. Segments are not verified otherwise, for what names a slot
  is the engine's to say.
- **A path under another one is dropped**: `"address.postalCode"` beside `"address"` names a slot
  which is never randomized anyway, and no engine should be handed a rule for it.
- **A path which names nothing is not an error**, for a subclass commonly passes a superset of
  paths. Only a `null` path, rejected with a `NullPointerException`, and one left with no segment —
  `""`, `"  "`, `"."` — rejected with an `IllegalArgumentException`, fail.

### Per flavor

| Flavor | A single name is excluded on | A dotted path is honored by | Caveat |
| --- | --- | --- | --- |
| `PodamObjectRandomizer` | the target, and any instance of the target type in the graph, such as a `friend` of the same type | a reset, once PODAM is done | the setter of the slot has run, side effects and all |
| `InstancioObjectRandomizer` | every field of that name the target declares or inherits — on an associated type too, when inherited from a shared supertype | a reset, once Instancio is done | — |
| `FixtureMonkeyObjectRandomizer` | the target type and its subclasses only | a reset, once sampled | it wins over a `getArbitraryBuilder()` override which sets that very slot |

A flavor which resets does so through `resetNestedExcludedPaths(T)`, which an override of `get()`
can call too.

### Never excluded by a dotted path

- a component of a record, and a field reflection can not reach — of a `java.*` class, or of a
  module which does not open its package — left as the engine wrote it, and logged;
- a key of a map, which would corrupt the map it is a key of;
- anything on an enum constant, which is shared by the whole program and is never walked into.

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

The API is a **`provided`** dependency. An engine reads a target class's constraints reflectively,
and a consumer that annotates its own classes already has the API on its classpath; `src/main` uses
it only to annotate its own parameters — `@NotNull`, and `@NotBlank` on each excluded path — which
document a contract the code checks by hand. A consumer of Java SE alone needs nothing: javac and
reflection both skip an annotation whose class is absent. PODAM's support is partial — a lone `@Max` is ignored, `@Pattern` yields `null`,
and two constraints on one field are honored one at a time, so a field carrying both `@Size` and
`@Email` gets a string of the right length and no address — so read `PodamObjectRandomizer`'s
Javadoc before trusting a randomized instance to be a valid one.

### `jakarta-ee-NN` and `jakarta-ee-NN-PROVIDER` profiles

Two axes, both test-only, each a kind of profile, and a build activates one of each, of the same
generation:

| Profile | Sets |
| --- | --- |
| `jakarta-ee-11` (default), `jakarta-ee-10` | The *generation*: the `jakarta.jakartaee-bom`, which pins `jakarta.validation-api` and `jakarta.el-api`; Expressly; and the `src/test/java-jakarta-ee-NN` test source root. Not usable alone. |
| `jakarta-ee-NN-hibernate-validator` (default for 11), `jakarta-ee-NN-apache-bval` | The *provider*: the one Jakarta Validation implementation on the test classpath, at the release aligned with generation `NN`. |

```shell
./mvnw test                                                    # jakarta-ee-11 + jakarta-ee-11-hibernate-validator
./mvnw -P jakarta-ee-11,jakarta-ee-11-apache-bval test
./mvnw -P jakarta-ee-10,jakarta-ee-10-hibernate-validator test
./mvnw -P jakarta-ee-10,jakarta-ee-10-apache-bval test
```

Naming any profile turns both defaults off, so name both halves. A build with a provider of a
different generation, or with no provider, fails on the enforcer. A build with no `jakarta-ee-NN`
fails while Maven builds the model, because the BOM has no version without one.

## Jakarta EE alignment

Each generation is a set of versions that belong together, not a set of latest releases. The
platform BOM decides the specification, and every implementation is the release line written
against that specification. Each version below is the newest release of its line as of 2026-10-07:

| | Jakarta EE 10 | Jakarta EE 11 |
| --- | --- | --- |
| `jakarta.jakartaee-bom` | 10.0.0 | 11.0.0 |
| Jakarta Validation | 3.0 | 3.1 |
| `jakarta.validation-api` (pinned by the BOM) | 3.0.2 | 3.1.1 |
| Hibernate Validator | 8.0.x (8.0.5.Final) | 9.x (9.1.4.Final) |
| Apache BVal `bval-jsr` | 3.0.x (3.0.2) | 3.1.x (3.1.0) |
| Jakarta Expression Language | 5.0 | 6.0 |
| Expressly | 5.0.x (5.0.0) | 6.0.x (6.0.0) |
| Test source root | `src/test/java-jakarta-ee-10` | `src/test/java-jakarta-ee-11` |
| Profiles | `jakarta-ee-10` + `jakarta-ee-10-PROVIDER` | `jakarta-ee-11` + `jakarta-ee-11-PROVIDER` |

Where the alignment comes from:

- **Hibernate Validator.** Each release's POM declares the `jakarta.validation-api` it depends on,
  and its jar manifest declares the specification it implements: 8.0.5.Final depends on 3.0.2 and
  declares Jakarta Bean Validation 3.0; 9.1.4.Final depends on 3.1.1 and declares Jakarta
  Validation 3.1.
- **Apache BVal.** Each release's parent POM pins the `jakarta.validation-api` it builds against,
  and its jar manifest declares the specification it implements: 3.0.1 and later 3.0 releases
  build on 3.0.2 and declare Jakarta Bean Validation 3.0; 3.1.0 builds on 3.1.1 and declares
  Jakarta Validation 3.1. Avoid 3.0.0, which builds on the same API but still declares `2.0` in
  its manifest.
- **Expressly.** Expressly 5 implements Jakarta Expression Language 5.0 (EE 10), and Expressly 6
  implements 6.0 (EE 11). The expression language is the same under both providers. Each provider
  reaches it only through the `jakarta.el` API, so choosing the EL implementation is not part of
  choosing a provider.

Nothing outside the two profiles is allowed to move these versions. `fixture-monkey-jakarta-validation`
declares a complete Jakarta EE 9 stack at `compile` scope (see [Fixture Monkey](#fixture-monkey)).
This build excludes all three of its artifacts, so the active profiles supply the API, the
expression language and the provider. Without the exclusion, Hibernate Validator 7 would be a
second provider in every `apache-bval` run. `_Validation_Provider_Test` guards against this: it
asserts that exactly one provider is discovered, and that it is the one the active profile names.
`Address_Test` does the same for the specification version compiled from the generation's test
source root.

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

For the second, import `junit-bom`. Fixture Monkey also brings an older `junit-platform-engine`, at
the same depth as the one Jupiter brings, and Maven takes whichever is declared first; with
`fixture-monkey` declared ahead of `junit-jupiter`, a JUnit 6 run then fails to start, with a
`NoClassDefFoundError` for `org/junit/platform/engine/support/store/NamespacedHierarchicalStore`. The
BOM pins every JUnit artifact, the transitive ones included, to one version. Excluding is no fix,
for jqwik is how Fixture Monkey generates its values:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.junit</groupId>
      <artifactId>junit-bom</artifactId>
      <version><!-- your JUnit version --></version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

Finally, `fixture-monkey-javax-validation` is the wrong artifact for this platform — the `javax`
annotations are not the ones any of these engines read.

## Links

### This module

- [Source][repo] · [Issues][issues]
- [Maven Central][central] — `io.github.jinahya:jinahya-object-randomizer`
- [Javadoc][javadoc]

### PODAM

- [Site][podam] · [Source][podam-src]
- [Maven Central][podam-central] — `uk.co.jemos.podam:podam`

### Instancio

- [Site][instancio] · [User guide][instancio-guide] · [Source][instancio-src]
- [Maven Central][instancio-central] — `org.instancio:instancio-core`

### Fixture Monkey

- [Site][fixture-monkey] · [Source][fixture-monkey-src]
- [Maven Central][fixture-monkey-central] — `com.navercorp.fixturemonkey:fixture-monkey`
- [Maven Central][fixture-monkey-validation-central] — `com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation`,
  the second artifact, without which constraints are ignored
- [naver/fixture-monkey#1350][fixture-monkey-1350] — the upstream issue for the `compile`-scope EE 9
  stack that second artifact brings with it, asking for Hibernate Validator and Jakarta EL to be
  declared test-only there
- [jqwik] — arrives transitively with `fixture-monkey`, as a second JUnit Platform `TestEngine`

### Bean validation

- Jakarta Validation — [3.1][jakarta-validation-31] (EE 11, the default profile) · [3.0][jakarta-validation-30] (EE 10)
- Jakarta EE platform — [11][jakarta-ee-11] · [10][jakarta-ee-10]
- [Hibernate Validator][hibernate-validator] — the reference implementation; [reference guide][hibernate-validator-docs]
- [Apache BVal][apache-bval] — the second implementation; 3.1 for Jakarta Validation 3.1, 3.0 for 3.0
- [Expressly][expressly] — the expression language under either provider, superseding `org.glassfish:jakarta.el`

[repo]: https://github.com/jinahya/jinahya-object-randomizer
[issues]: https://github.com/jinahya/jinahya-object-randomizer/issues
[central]: https://central.sonatype.com/artifact/io.github.jinahya/jinahya-object-randomizer
[javadoc]: https://javadoc.io/doc/io.github.jinahya/jinahya-object-randomizer
[podam]: https://mtedone.github.io/podam/
[podam-src]: https://github.com/mtedone/podam
[podam-central]: https://central.sonatype.com/artifact/uk.co.jemos.podam/podam
[instancio]: https://www.instancio.org
[instancio-guide]: https://www.instancio.org/user-guide/
[instancio-src]: https://github.com/instancio/instancio
[instancio-central]: https://central.sonatype.com/artifact/org.instancio/instancio-core
[fixture-monkey]: https://naver.github.io/fixture-monkey/
[fixture-monkey-src]: https://github.com/naver/fixture-monkey
[fixture-monkey-central]: https://central.sonatype.com/artifact/com.navercorp.fixturemonkey/fixture-monkey
[fixture-monkey-validation-central]: https://central.sonatype.com/artifact/com.navercorp.fixturemonkey/fixture-monkey-jakarta-validation
[fixture-monkey-1350]: https://github.com/naver/fixture-monkey/issues/1350
[jqwik]: https://jqwik.net/
[jakarta-validation-31]: https://jakarta.ee/specifications/bean-validation/3.1/
[jakarta-validation-30]: https://jakarta.ee/specifications/bean-validation/3.0/
[jakarta-ee-11]: https://jakarta.ee/specifications/platform/11/
[jakarta-ee-10]: https://jakarta.ee/specifications/platform/10/
[hibernate-validator]: https://hibernate.org/validator/
[hibernate-validator-docs]: https://docs.jboss.org/hibernate/validator/9.0/reference/en-US/html_single/
[apache-bval]: https://bval.apache.org/
[expressly]: https://github.com/eclipse-ee4j/expressly

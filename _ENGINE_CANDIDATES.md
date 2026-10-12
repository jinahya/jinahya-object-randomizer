# Engine candidates

Libraries looked at as a possible fourth flavor, beside PODAM, Instancio and Fixture Monkey.

To fit, an engine has to fill an arbitrary `Class<T>` by itself — discovering its slots, recursing
into associated types — and leave alone the slots named by single names, as the existing flavors
do. A library which needs each slot listed, or the target annotated, does not fit.

Each was probed on 2026-10-12, against its latest release on Maven Central, with three targets: a
setter bean `Bean(id, name, age, bar)`, a class `Fields(id, name, bar)` declaring only private
fields and a package-private constructor, and a record `Rec(id, name, bar)`; `bar` is a setter bean
`Bar(city)`.

## Summary

| Library | Latest | Released | Fills `Bean` | Fills `Fields` | Fills `Rec` | Exclusion by name | Bean validation | Verdict |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Datafaker | 2.7.0 | 2026-06-24 | only with a `Schema` | only with a `Schema` | only with a `Schema` | n/a — inclusive | none | rejected |
| AutoParams | 11.3.2 | 2026-03-31 | with a writer | fails | yes | `InstanceFieldWriter#excluding` | `@Min`, `@Max` | possible |
| JFixture | 2.7.2 | 2017-09-04 | yes | no | yes | none | none | rejected |
| Fakegen | 0.2 | 2018-10-31 | yes | `null` | `null` | none | none | rejected |
| MeanBean | 3.0.0-M9 | 2020-10-04 | fails | fails | fails | none | none | rejected |

## Current activity

As of 2026-10-12, from GitHub.

| Library | Repository | Last commit | Commits, last 90 days | Latest release | Stars | Open issues + PRs | State |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Datafaker | [datafaker-net/datafaker](https://github.com/datafaker-net/datafaker) | 2026-10-12 | 73 | 2.7.0, 2026-06-24 | 1801 | 11 | active |
| AutoParams | [AutoParams/AutoParams](https://github.com/AutoParams/AutoParams) | 2026-04-07 | 0 | 11.4.0-beta.1, 2026-04-05 (11.3.2 stable, 2026-03-31) | 368 | 26 | maintained, quiet for six months |
| JFixture | [FlexTradeUKLtd/jfixture](https://github.com/FlexTradeUKLtd/jfixture) | 2020-10-15, a Dependabot bump | 0 | 2.7.2, 2017-09-04 | 112 | 18 | dormant |
| Fakegen | [DennisRippinger/fakegen](https://github.com/DennisRippinger/fakegen) | 2022-10-05, "Fix typos" | 0 | 0.2, 2018-10-31 | 11 | 4 | dormant |
| MeanBean | [meanbeanlib/meanbean](https://github.com/meanbeanlib/meanbean) | 2026-04-16, dependency upgrades | 0 | 3.0.0-M9, 2020-10-04 | 15 | 5 | dormant; no release since 2020 |

None of the repositories is archived. The 2026 MeanBean commits only upgraded dependencies for
vulnerabilities and updated badges; nothing has been released from them.

## Datafaker

`net.datafaker:datafaker`

A generator of realistic *values* — names, addresses, phone numbers — not of objects.
`BaseFaker.populate(Class<T>)` throws `The class Foo is not annotated with Fake` for a plain class;
`populate(Class<T>, Schema)` assigns only the fields the schema lists, and leaves `bar` `null`.
Every randomizer would be a hand-written schema, and the default path needs `@FakeForSchema` on the
target, so a target would no longer be a plain class.

## AutoParams

`io.github.autoparams:autoparams`

The only one actively maintained and able to fill a graph by itself. The entry point is
`new ResolutionContext().resolve(Class<T>)`; there is no `AutoParams.create(...)` — `AutoParams` is
the JUnit annotation.

- It constructs through a constructor, so a record fills fully. A setter bean comes back with every
  property unset unless `InstancePropertyWriter` or `InstanceFieldWriter` is applied.
- `new InstanceFieldWriter(Bean.class).excluding("id")` leaves `id` alone and fills the rest — but
  the nested `bar` came back as `Bar(city=null)`, the writer being scoped to the one class.
- `Fields`, whose constructor is package-private, can not be resolved at all. Whether a writer can
  fill an instance supplied by `newTargetInstance()` is not verified.
- Its constraint support is `@Min` and `@Max` only, on the numeric and collection generators.
- It brings `junit-jupiter-api`, `junit-jupiter-params`, `jakarta.validation-api` (3.0.2) and
  `jsr305` as compile-scope dependencies.

## JFixture

`com.flextrade.jfixture:jfixture`

A port of .NET's AutoFixture; `new JFixture().create(Class<T>)` fills `Bean` and `Rec`, nested
`bar` included, with values such as `Name8877c50b-…`. It writes through setters only, so `Fields`
comes back empty. `customise().propertyOf(Bean.class, "id", null)` throws a `NullPointerException`,
so there is no way to leave a slot alone. No release since 2017. It is unrelated to QuickPerf,
which is a JVM performance-testing library.

## Fakegen

`de.drippinger.fakegen:fakegen-core`

The method is `TestDataFiller#fillInstance(Class<T>)`; there is no `createRandomFilledInstance`.
It fills `Bean` recursively, but returns `null` for `Fields` and `Rec`. A single 0.2 release, from
2018, built on the abandoned `javafaker`.

## MeanBean

`com.github.meanbeanlib:meanbean`, formerly `org.meanbean:meanbean`

A tester of `equals`, `hashCode` and accessors. Its factories make values for the *properties* of
a bean under test, not beans: `new BeanTester().getFactoryCollection().getFactory(Bean.class)`
throws `NoSuchFactoryException`, and `FactoryCollection.getInstance()` throws a
`NullPointerException` (`context key not set`) outside a test. Milestones only since 2020;
`org.meanbean:meanbean` stopped in 2012.

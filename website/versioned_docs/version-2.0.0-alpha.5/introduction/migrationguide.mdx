---
id: migration
title: "Migration guide to 2.0.0"
keywords: [migration, upgrade, 2.0]
sidebar_position: 80
---

This page is a migration guide from **detekt 1.x to detekt 2.x**. It is split in two sections:

- **[User guide](#user-guide)** — for people consuming detekt through the Gradle plugin, the CLI or
  the Kotlin compiler plugin.
- **[Rule author guide](#rule-author-guide)** — for people who maintain a custom rule set, a custom
  report, or any other detekt extension.

If you find a missing or unclear step while migrating, please open an issue or — even better — a
pull request to improve this page.

:::tip Why so many changes?

detekt 1.0 shipped in 2019 and we kept backwards compatibility for the entire 1.x line. The 2.x
line is our opportunity to clean up the API, drop the K1 (legacy) Kotlin compiler in favour of the
official [Kotlin Analysis API](https://kotlin.github.io/analysis-api/), and align with modern
Gradle and Android Gradle Plugin versions. Most of the changes below are mechanical
search-and-replace; the only conceptually new piece is the Analysis API for rule authors.

:::

---

## User guide

### Minimum supported versions

| Tool                | detekt 1.23.x | detekt 2.0.x |
|---------------------|---------------|--------------|
| JDK (to run detekt) | 8             | 17           |
| Kotlin              | 1.9 / 2.0     | 2.4          |
| Gradle              | 6.7.1         | 7.6.3        |
| Android Gradle Plugin | 4.x         | 8.2.2        |

detekt itself is built and tested against the latest JDK 25, Kotlin 2.4, Gradle 9.x and AGP 9.x.

### Gradle plugin ID change

The plugin ID changed from `io.gitlab.arturbosch.detekt` to `dev.detekt`:

```diff
 plugins {
-    id("io.gitlab.arturbosch.detekt") version "1.23.8"
+    id("dev.detekt") version "2.0.0"
 }
```

If you used the legacy `apply plugin:` form, replace `"io.gitlab.arturbosch.detekt"` with `"dev.detekt"`
the same way.

### Maven coordinates change

All published artifacts moved from the `io.gitlab.arturbosch.detekt` group to `dev.detekt`. This
affects every direct dependency on a detekt module — custom rule sets, the CLI, the compiler plugin,
and any test utilities.

```diff
-implementation("io.gitlab.arturbosch.detekt:detekt-api:1.23.8")
+implementation("dev.detekt:detekt-api:2.0.0")
```

### Module renames

A few modules were renamed to make their purpose clearer:

| 1.x module                        | 2.x module                          |
|-----------------------------------|-------------------------------------|
| `detekt-formatting`               | `detekt-rules-ktlint-wrapper`       |
| `detekt-rules-documentation`      | `detekt-rules-comments`             |
| `detekt-rules-empty`              | `detekt-rules-empty-blocks`         |
| `detekt-rules-bugs`               | `detekt-rules-potential-bugs`       |
| `detekt-report-xml`               | `detekt-report-checkstyle`          |
| `detekt-report-md`                | `detekt-report-markdown`            |

If you referenced any of these artifacts directly (for example through `detektPlugins(...)` for
the formatting wrapper), update the coordinates accordingly:

```diff
 dependencies {
-    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
+    detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:2.0.0")
 }
```

### Rule set ID renames in YAML config

If you upgraded module names above, the matching keys in your `detekt.yml` also change:

```diff
-documentation:
+comments:
   UndocumentedPublicClass:
     active: true

-empty-blocks:
+empty-blocks: # was: empty
   EmptyFunctionBlock:
     active: true

-potential-bugs:
+potential-bugs: # was: potential-bugs in 1.x already, the *module* was bugs, the ruleset id changed
   ...
```

The `formatting` ruleset key is unchanged (only the artifact name moved).

### Rule renames

Several rules were renamed for consistency:

| 1.x rule                          | 2.x rule                              |
|-----------------------------------|---------------------------------------|
| `UnusedImports`                   | `UnusedImport`                        |
| `UnusedPrivateMember`             | `UnusedPrivateFunction` + `UnusedPrivateProperty` (split) |
| `CommentOverPrivateFunction`      | `DocumentationOverPrivateFunction`    |
| `CommentOverPrivateProperty`      | `DocumentationOverPrivateProperty`    |
| `MayBeConst`                      | `MayBeConstant`                       |
| `FunctionMinLength`               | `FunctionNameMinLength`               |
| `FunctionMaxLength`               | `FunctionNameMaxLength`               |
| `SpacingBetweenPackageAndImports` | `SpacingAfterPackageDeclaration`      |
| `UntilInsteadOfRangeTo`           | `RangeUntilInsteadOfRangeTo`          |
| `RedundantVisibilityModifierRule` | `RedundantVisibilityModifier`         |

Update your `detekt.yml`, baseline files, and any `@Suppress("...")` annotations referencing the
old names.

### Rules removed

The following rules were removed in 2.x because they no longer matched the official Kotlin style or
were superseded by ktlint rules or compiler warnings:

- `OptionalWhenBraces`
- `PreferToOverPairSyntax`
- `UnnecessaryAnnotationUseSiteTarget`

Remove them from your `detekt.yml`. The `MultiRule` mechanism was also removed; if you maintained
a custom multi-rule, see the **rule author guide** below.

### Report format ID renames

The IDs used on the CLI and Gradle plugin for the XML and Markdown reports changed:

| 1.x report id    | 2.x report id |
|------------------|---------------|
| `xml`            | `checkstyle`  |
| `md`             | `markdown`    |

```diff
-detekt --report xml:report.xml --report md:report.md
+detekt --report checkstyle:report.xml --report markdown:report.md
```

In Gradle:

```diff
 tasks.withType<Detekt>().configureEach {
     reports {
-        xml.required.set(true)
+        checkstyle.required.set(true)
-        md.required.set(true)
+        markdown.required.set(true)
     }
 }
```

The plain-text (`txt`) report was removed entirely.

### Configuration changes

Several configuration concepts were removed or replaced:

- **`maxIssues`** is gone. The build now fails on **any issue with severity `error`** instead of
  comparing a global count.
- **Issue weights / `excludeCorrectable` / per-ruleset `excludes`** were removed alongside the
  weight-based exit calculation.
- The **`output-report`** config block is removed — configure reports through the Gradle plugin or
  CLI flags instead.
- The deprecated `Severity` enum (`CodeSmell`, `Style`, `Defect`, …) is removed. Severity is now a
  three-value enum (`Info`, `Warning`, `Error`) configurable per rule via the standard
  `excludes` / `severity` keys.
- Threshold-style configs were renamed everywhere to `allowed...`:

| Rule                          | Old key                                    | New key              |
|-------------------------------|--------------------------------------------|----------------------|
| `LongMethod`                  | `threshold`                                | `allowedLines`       |
| `LargeClass`                  | `threshold`                                | `allowedLines`       |
| `LongParameterList`           | `functionThreshold` / `constructorThreshold` | `allowedFunctionParameters` / `allowedConstructorParameters` |
| `CyclomaticComplexMethod`     | `threshold`                                | `allowedComplexity`  |
| `CognitiveComplexMethod`      | `threshold`                                | `allowedComplexity`  |
| `ComplexCondition`            | `threshold`                                | `allowedComplexity`  |
| `ComplexInterface`            | `threshold`                                | `allowedDefinitions` |
| `NestedBlockDepth`            | `threshold`                                | `allowedDepth`       |
| `NestedScopeFunctions`        | `threshold`                                | `allowedDepth`       |
| `MethodOverloading`           | `threshold`                                | `allowedOverloads`   |
| `NamedArguments`              | `threshold`                                | `allowedArguments`   |
| `StringLiteralDuplication`    | `threshold`                                | `allowedDuplications`|
| `CouldBeSequence`             | `threshold`                                | `allowedOperations`  |

- `ForbiddenImport` config keys changed — see the [updated rule docs](../rules/style#forbiddenimport).
- `FunctionOnlyReturningConstant` no longer accepts `excludeAnnotatedFunction`; use
  `ignoreAnnotated` instead.
- `LateinitUsage` no longer accepts `excludeAnnotatedProperties`; use `ignoreAnnotated` instead.
- Comma-separated string values are no longer accepted where a list is expected — use proper YAML
  lists.
- Some XML-style baseline tags that were already deprecated in 1.x are now removed.

Run `./gradlew detektGenerateConfig` and diff against your existing `detekt.yml` for a quick
overview of changes that apply to your project.

### Type resolution is the new default

In 1.x, rules requiring type information were opt-in and gated by the `@RequiresTypeResolution`
annotation. In 2.x:

- The legacy K1 compiler (`BindingContext`) is gone. All type-aware rules use the official Kotlin
  [Analysis API](https://kotlin.github.io/analysis-api/).
- The Gradle plugin registers analysis tasks **per source set** and **per compilation**, so type
  resolution is wired up automatically when classpath information is available.
- You no longer need to register custom tasks just to enable type resolution.

If you had custom `Detekt` task registrations purely to wire up `classpath` and `jvmTarget`,
delete them — the autogenerated `detekt<SourceSet>` and `detekt<Compilation>` tasks already do it
for you.

### Gradle plugin behaviour

- The Worker API is now used by default — this lets detekt run in a separate classloader and
  isolates it from the rest of the Gradle build.
- The plugin compiles against the AGP API only (not against KGP itself), so it works with both
  the standalone Kotlin Gradle Plugin and AGP 9's built-in Kotlin support.
- The plugin's package moved from `io.gitlab.arturbosch.detekt.*` to `dev.detekt.gradle.*`. If you
  imported task classes in `buildSrc` or convention plugins, update the imports:

```diff
-import io.gitlab.arturbosch.detekt.Detekt
-import io.gitlab.arturbosch.detekt.extensions.DetektExtension
+import dev.detekt.gradle.Detekt
+import dev.detekt.gradle.extensions.DetektExtension
```

- The `detekt-gradle-plugin` artifact is now built lazily using Gradle's `Property` API. If you
  set values from `buildSrc`, prefer `.set(...)` / `.convention(...)` over `=` assignment.

### CLI changes

- The CLI uses **system-dependent path separators** consistently — on Windows, separators in
  reports and logs will be `\` rather than the mixed behaviour of 1.x.
- `--report` validation is stricter and emits clearer error messages for unknown formats.
- `--classpath` is validated when the CLI starts so misconfigured classpaths fail fast.
- A new `--filter` flag lets you exclude paths from analysis without modifying the config file.

### Compiler plugin coordinates

The compiler plugin moved too. If you applied it via Gradle's compiler plugin DSL:

```diff
 plugins {
-    id("io.gitlab.arturbosch.detekt") version "1.23.8"
+    id("dev.detekt.compiler-plugin") version "2.0.0"
 }
```

The compiler-plugin Maven coordinates also changed to `dev.detekt:detekt-compiler-plugin`.

### Baseline file format

Baseline files written by 1.x are still readable in 2.x, but new baselines created with
`detektBaseline` will be slightly cleaner (redundant empty tags and the deprecated XML tags are no
longer emitted). Re-generate baselines after upgrading for the best diff.

---

## Rule author guide

If you maintain a custom rule set, custom reports, custom processors, or any other detekt
extension, you'll need to update both your dependency coordinates and your code.

### Coordinates and imports

The mechanical part: replace `io.gitlab.arturbosch.detekt` with `dev.detekt` throughout your
project — both in your `build.gradle.kts` and in `import` statements.

The `detekt-api` artifact now lives at `dev.detekt:detekt-api`; its types moved from the
`io.gitlab.arturbosch.detekt.api` package to `dev.detekt.api`.

| 1.x module / package                                   | 2.x module / package                          |
|--------------------------------------------------------|-----------------------------------------------|
| `io.gitlab.arturbosch.detekt:detekt-api`               | `dev.detekt:detekt-api`                       |
| `io.gitlab.arturbosch.detekt.api`                      | `dev.detekt.api`                              |
| `io.gitlab.arturbosch.detekt:detekt-psi-utils`         | `dev.detekt:detekt-psi-utils`                 |
| `io.gitlab.arturbosch.detekt.rules.*` packages         | `dev.detekt.rules.*`                          |
| `io.gitlab.arturbosch.detekt:detekt-test`              | `dev.detekt:detekt-test`                      |
| `io.gitlab.arturbosch.detekt:detekt-test-utils`        | `dev.detekt:detekt-test-utils`                |
| Tooling, parser, generator, etc.                       | All moved to the `dev.detekt.*` namespace     |

New artifacts were introduced for testing:

- `dev.detekt:detekt-test-assertj` — the AssertJ-based assertion helpers (split out of `detekt-test`)
- `dev.detekt:detekt-test-junit` — JUnit 5 environment fixtures (`@KotlinCoreEnvironmentTest`, etc.)

If you depended on `detekt-test`, you most likely need to add one or both of these too.

### `Rule` API

The `Rule` class signature changed:

```diff
-class MyRule(config: Config) : Rule(config) {
-    override val issue = Issue(
-        javaClass.simpleName,
-        Severity.Style,
-        "Detects something I dislike.",
-        Debt.FIVE_MINS,
-    )
-
-    override fun visitNamedFunction(function: KtNamedFunction) {
-        report(CodeSmell(issue, Entity.from(function), "Don't do this."))
-    }
-}
+class MyRule(config: Config) : Rule(
+    config,
+    description = "Detects something I dislike.",
+) {
+    override fun visitNamedFunction(function: KtNamedFunction) {
+        report(Finding(Entity.from(function), "Don't do this."))
+    }
+}
```

Key points:

- `Rule` now takes the `description` as a constructor parameter — move the text out of `Issue`.
- The `Issue` class and the `issue` override are gone.
- `Debt` is gone — detekt no longer tracks per-issue debt minutes.
- `CodeSmell` was renamed to `Finding`. Its constructor no longer takes an `Issue`.
- `Severity` is now configured at the **rule level via YAML config**, not via an enum on `Issue`.
  Default severity is set when the rule's `RuleInstance` is constructed by the engine; you don't
  declare it in code anymore.
- The `Rule.severity` property and the per-call severity overrides are removed.
- `Rule` no longer carries an `aliases` set; aliases are declared through the `@Alias` annotation.

The `ThresholdedCodeSmell` type was also removed — emit a plain `Finding` instead and put the
threshold information into the message.

### `RuleSetProvider`

`RuleSetProvider` returns rule **factories**, not rule instances, and uses the
`RuleSet.Id` value class:

```diff
 class MyRuleSetProvider : RuleSetProvider {
-    override val ruleSetId = "MyRuleSet"
+    override val ruleSetId = RuleSet.Id("MyRuleSet")

-    override fun instance(config: Config) = RuleSet(
-        ruleSetId,
-        listOf(
-            MyRule(config),
-        ),
-    )
+    override fun instance(): RuleSet = RuleSet(
+        ruleSetId,
+        listOf(
+            ::MyRule,
+        ),
+    )
 }
```

This lets detekt construct each rule lazily with its own scoped config, and is the foundation for
running the same rule multiple times with different configurations.

### Type resolution: Analysis API instead of `BindingContext`

This is the biggest change for rule authors. In 1.x, type-aware rules looked like:

```kotlin
@RequiresTypeResolution
class MyRule(config: Config) : Rule(config) {
    override fun visitCallExpression(expression: KtCallExpression) {
        val descriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return
        // ...
    }
}
```

In 2.x, you implement the marker interface `RequiresAnalysisApi` and use the
[`analyze {}`](https://kotlin.github.io/analysis-api/) block:

```kotlin
class MyRule(config: Config) : Rule(
    config,
    description = "..."
), RequiresAnalysisApi {
    override fun visitCallExpression(expression: KtCallExpression) {
        analyze(expression) {
            val symbol = expression.resolveToCall()?.successfulFunctionCallOrNull()?.symbol
            // ...
        }
    }
}
```

`@RequiresTypeResolution` (the annotation) is gone. `BindingContext`, `ResolvedCall.isCalling`,
`KotlinType.fqNameOrNull`, `DataFlowValueFactory` and all other K1 helpers are removed from the
public API.

JetBrains maintains an official
[K1 → Analysis API migration guide](https://kotlin.github.io/analysis-api/migrating-from-k1.html)
that covers the API mapping in depth. For concrete detekt-flavoured examples, look at the rule
migration PRs we landed during the 2.0 alpha cycle — there are over 100 of them and most are 30–50
lines, so finding one that's structurally close to your rule is usually quick. A few good starting
points by category:

- **Style / complexity** — [#8220](https://github.com/detekt/detekt/pull/8220),
  [#8408](https://github.com/detekt/detekt/pull/8408),
  [#8482](https://github.com/detekt/detekt/pull/8482)
- **Potential bugs** — [#8157](https://github.com/detekt/detekt/pull/8157),
  [#8237](https://github.com/detekt/detekt/pull/8237),
  [#8246](https://github.com/detekt/detekt/pull/8246)
- **Exceptions** — [#8215](https://github.com/detekt/detekt/pull/8215),
  [#8216](https://github.com/detekt/detekt/pull/8216),
  [#8217](https://github.com/detekt/detekt/pull/8217)
- **Coroutines** — [#8259](https://github.com/detekt/detekt/pull/8259),
  [#8289](https://github.com/detekt/detekt/pull/8289),
  [#8290](https://github.com/detekt/detekt/pull/8290)
- **Naming** — [#8201](https://github.com/detekt/detekt/pull/8201),
  [#8202](https://github.com/detekt/detekt/pull/8202),
  [#8330](https://github.com/detekt/detekt/pull/8330)

### Testing rules

The testing API was streamlined:

- `compileAndLint(...)` → `lint(...)` (when no type info is needed)
- `compileAndLintWithContext(env, ...)` → `lintWithContext(env, ...)`
- AssertJ-style assertions live in a new module: add
  `testImplementation("dev.detekt:detekt-test-assertj:2.0.0")`
- The JUnit 5 environment extension lives in `dev.detekt:detekt-test-junit`. Annotate type-resolving
  test classes with `@KotlinCoreEnvironmentTest` as before.

If your test extended a `Spec` class that used `RequiresFullAnalysis`, that helper is no longer
exported — implement `RequiresAnalysisApi` on the rule itself and use `lintWithContext`.

### Configuration annotations

`@Configuration`, `@ActiveByDefault`, and `@RequiresTypeResolution` used to live in an internal
package. They moved to the public API and you should import them from
`dev.detekt.api.internal` (or the new public package, depending on the annotation):

```diff
-import io.gitlab.arturbosch.detekt.api.internal.Configuration
+import dev.detekt.api.Configuration
```

Most existing usages keep working with a simple package update.

### Custom reports

If you implemented a custom `OutputReport` or `ConsoleReport`:

- `OutputReport`'s `name` property is removed; provide the file path via the engine instead.
- The default file extension is no longer hard-coded — your report can pick any extension that
  makes sense.
- `ConsoleReport` is now an interface rather than an abstract class.
- `Detektion` is now immutable. If you needed to attach data to it, use the `Notification`
  mechanism or store state on your `Extension` instance.

### Other removed / renamed extension points

- `MultiRule` is gone — split it into individual `Rule`s.
- `BaseRule`, `ThresholdRule`, `LazyRegex`, `SingleAssign`, `SplitPattern`,
  `CommaSeparatedPattern`, `Compactable`, `HasEntity`, `Context` interface,
  `DefaultContext`, `FilePath`, `safeAs` extension — all removed.
- `FileProcessListener` no longer exposes a `BindingContext`. If your listener needed type info,
  port it to the Analysis API.
- `txt` report support is removed.
- `LicenseHeaderLoaderExtension` is no longer auto-registered as a processor.
- `DetektProgressListener` and `InvalidConfigurationError` are no longer part of the public API.

### Gradle plugin extension points

If you extended the Gradle plugin (custom task type, custom convention plugin):

- The plugin's task classes live in `dev.detekt.gradle.*` (and concrete tasks in
  `dev.detekt.gradle.plugin.*`).
- All inputs are exposed as Gradle `Property` / `ListProperty` / `ConfigurableFileCollection` —
  the old eager `var` properties are gone.
- The `Detekt` plain task is being retired in favour of source-set and compilation-aware tasks.
  Prefer wiring custom logic to the `detekt<SourceSet>` and `detekt<Compilation>` tasks.

---

## Need help?

- Track the 2.0.0 milestone here: [github.com/detekt/detekt/milestone/42](https://github.com/detekt/detekt/milestone/42)
- Browse the alpha changelogs in [Changelog 2.0.0](/changelog-2.0.0) for an exhaustive
  list of changes per release.
- If you hit a migration issue that isn't covered here, open an issue and tag it `migration` —
  we'll fold the answer back into this page.

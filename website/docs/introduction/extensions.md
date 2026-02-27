---
id: extensions
title: "Extending detekt"
keywords: [extensions, rulesets]
sidebar_position: 9
---

The following page describes how to extend detekt and how to customize it to your domain-specific needs.
The associated **code samples** to this guide can be found in the package [detekt/detekt-sample-extensions](https://github.com/detekt/detekt/tree/main/detekt-sample-extensions).

## Custom RuleSets {#customrulesets}

_detekt_ uses the `ServiceLoader` pattern to collect all instances of the `RuleSetProvider` interface, making it possible to define rules/rule sets and enhance _detekt_ with your own flavor.

:::caution Attention

You need a `resources/META-INF/services/dev.detekt.api.RuleSetProvider` file containing the fully qualified name of
your `RuleSetProvider`. For example: 
```
dev.detekt.sample.extensions.SampleProvider
```

:::

You can use our [GitHub template](https://github.com/detekt/detekt-custom-rule-template) to have a basic scaffolding to
develop your own custom rules. Another option is to clone the provided [detekt/detekt-sample-extensions](https://github.com/detekt/detekt/tree/main/detekt-sample-extensions) project.

:::note

It's important that the dependency of `dev.detekt:detekt-api` is configured as `compileOnly` (as in the examples).
[You can read more information about this here](https://github.com/detekt/detekt/issues/7883).

:::

Custom rules must extend the `Rule` class and override the `visitXXX()` functions from the AST.
A `RuleSetProvider` must also be implemented, declaring a `RuleSet` in the `instance()` function.
To leverage the configuration mechanism of detekt you must pass the Config object from your rule set provider to your rule.
An `Issue` property defines what ID and message should be printed on the console or on any other output format.

Example of a custom rule:
```kotlin
class TooManyFunctions(config: Config) : Rule(
    config,
    "This rule reports a file with an excessive function count.",
) {
    private val threshold = 10
    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > threshold) {
            report(Finding(Entity.from(file),
                "Too many functions can make the maintainability of a file costlier"))
        }
        amount = 0
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        amount++
    }
}
```

Example of a much more precise rule, making fuller use of the `Finding` constructor and `Rule` attributes:
```kotlin
class TooManyFunctions2(config: Config) : Rule(
    config,
    "This rule reports a file with an excessive function count.",
) {
    private val threshold: Int by config(defaultValue = 10)
    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > threshold) {
            report(ThresholdedCodeSmell(issue,
                entity = Entity.from(file),
                metric = Metric(type = "SIZE", value = amount, threshold = threshold),
                message = "The file ${file.name} has $amount function declarations. " +
                        "Threshold is specified with $threshold.",
                references = emptyList())
            )
        }
        amount = 0
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        amount++
    }
}
```

If you want your rule to be configurable, write down your properties inside the `detekt.yml` file.
Please note that this will only take effect if the `Config` object is passed on by the `RuleSetProvider`
to the rule itself.

```yaml
MyRuleSet:
  TooManyFunctions2:
    active: true
    threshold: 5
  OtherRule:
    active: false
```

By specifying the rule set and rule IDs, _detekt_ will use the sub-configuration of `TooManyFunctions2`:

```val threshold = valueOrDefault("threshold", THRESHOLD)```

:::note

As of version 1.2.0 detekt now verifies whether all configured properties actually exist in a configuration created by `--generate-config`.
This means that by default detekt does not know about your new properties.
Therefore we need to mention them in the configuration under `config>excludes`.

:::

```yaml
config:
  validation: true
  # 1. exclude rule set 'sample' and all its nested members
  # 2. exclude every property in every rule under the rule set 'sample'
  excludes: "sample.*,sample>.*>.*"
```

## Testing custom rules {#testing}

To test your rules, add the `detekt-test` dependency to your project:

```kotlin
// Required
testImplementation("dev.detekt:detekt-test:[detekt_version]")

// Optional - makes use of the "assertThat" test structure 
testImplementation("dev.detekt:detekt-test-assertj:[detekt_version]")

// Optional in general, but required for the @KotlinCoreEnvironmentTest annotation
testImplementation("dev.detekt:detekt-test-junit:[detekt_version]")
```

### Basic tests

The simplest way to test a rule is with the `lint` extension function, which runs your rule against inline Kotlin code:

```kotlin
class TooManyFunctionsSpec {
    val subject = TooManyFunctions(Config.empty)

    @Test
    fun `reports files with too many functions`() {
        val code = """
            class MyClass {
                fun a() = Unit
                fun b() = Unit
                // ...
            }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report files within threshold`() {
        val code = """
            class MyClass {
                fun a() = Unit
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }
}
```

### With custom configs

To validate configurable rules, use `TestConfig` instead of `Config.empty`:

```kotlin
val subject = TooManyFunctions(
    TestConfig(
        "threshold" to 5,
        "someBooleanKey" to false,
        "someStringKey" to "abc",
    )
)
```

### With type resolution

If your rule requires type resolution (i.e. it implements `RequiresAnalysisApi`):
1. annotate the test class with `@KotlinCoreEnvironmentTest`,
1. put an instance of `KotlinEnvironmentContainer` in the test class constructor,
1. use the `lintWithContext` extension function to generate findings using full analysis:

```kotlin
@KotlinCoreEnvironmentTest
class MyTypeAwareRuleSpec(val env: KotlinEnvironmentContainer) {
    private val subject = MyTypeAwareRule(Config.empty)
    
    @Test
    fun `detects issue with type info`() {
        val code = """...""".trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }
}
```

By default, code snippets passed into `lintWithContext` are compiled against the full test classpath (kotlin-stdlib, any `testImplementation` dependencies, etc.). If your rule targets a specific third-party library, just add it as a `testImplementation` dependency in your build file and any classes in that library will be available for import/analysis in test snippets automatically.

You can also make Java source files available to your test snippets by placing them under `test/resources` and referencing them via the `@KotlinCoreEnvironmentTest` annotation, but remember that this is **Java** only - not Kotlin files.

```kotlin
@KotlinCoreEnvironmentTest(additionalJavaSourcePaths = ["myJavaSources"])
class MyRuleSpec(val env: KotlinEnvironmentContainer) {
    // Java classes under test/resources/myJavaSources/ are now importable in test snippets
}
```

### Custom assertions

The custom `assertThat` from `detekt-test-assertj` supports more idiomatic assertions on findings:

```kotlin
assertThat(findings)
    .singleElement()
    .hasMessage("Expected message")
    .hasStartSourceLocation(3, 5)
```

## Custom Processors {#customprocessors}

Custom processors can be used, for example, to implement additional project metrics.

For instance, if you want to count all loop statements in your codebase, you could write something like:

```kotlin
class NumberOfLoopsProcessor : FileProcessListener {

	override fun onProcess(file: KtFile) {
		val visitor = LoopVisitor()
		file.accept(visitor)
		file.putUserData(numberOfLoopsKey, visitor.numberOfLoops)
	}

	companion object {
		val numberOfLoopsKey = Key<Int>("number of loops")
	}

	class LoopVisitor : DetektVisitor() {

		internal var numberOfLoops = 0
		override fun visitLoopExpression(loopExpression: KtLoopExpression) {
			super.visitLoopExpression(loopExpression)
			numberOfLoops++
		}
	}
}
```

To let detekt know about the new processor, we specify a `resources/META-INF/services/dev.detekt.api.FileProcessListener` file 
with the fully qualified name of the processor as its content, e.g. `dev.detekt.sample.extensions.processors.NumberOfLoopsProcessor`.


To test the code, use the `detekt-test` module and write a JUnit 5 test case.

```kotlin
class NumberOfLoopsProcessorTest {

    @Test
    fun `should expect two loops`() {
        val code = """
            fun main() {
                for (i in 0..10) {
                    while (i < 5) {
                        println(i)
                    }
                }
            }
        """

        val ktFile = compileContentForTest(code)
        NumberOfLoopsProcessor().onProcess(ktFile)

        assertThat(ktFile.getUserData(NumberOfLoopsProcessor.numberOfLoopsKey)).isEqualTo(2)
    }
}
```

## Custom Reports {#customreports}

_detekt_ allows you to extend the console output and to create custom output formats.
If you want to customize the output, take a look at the `ConsoleReport` and `OutputReport` classes.

Each requires an implementation of the `render()` function, which takes an object with all findings and returns a string to be printed.

```kotlin
abstract fun render(detektion: Detektion): String?
```

## Integrating extensions with detekt {#configureextensions}

So you have implemented your own rules or other extensions and want to integrate them
into your `detekt` run? Great, make sure to have a `jar` with all your needed dependencies 
minus the ones `detekt` brings itself.

Take a look at our [sample project](https://github.com/detekt/detekt/tree/main/detekt-sample-extensions) on how to achieve this with gradle.

### Via the Detekt CLI

Pass your `jar` with the `--plugins` flag when calling the CLI fatjar:
```sh
detekt --input ... --plugins /path/to/my/jar
```

### Via the Detekt Gradle Plugin

For example `detekt` itself provides a wrapper over [ktlint](https://github.com/pinterest/ktlint) as a 
custom `ktlint` rule set. To enable it, we add the published dependency to `detekt` via the `detektPlugins` configuration:

```kotlin
dependencies {
    detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:[detekt_version]")
}
```

You can use the same method to apply any other custom rulesets! See the [Detekt 3rd-party Marketplace](https://detekt.dev/marketplace) for more.

### Pitfalls

- All rules are disabled by default and have to be explicitly enabled in the `detekt` yaml configuration file.
- If you do not pass the `Config` object from the `RuleSetProvider` to the rule, the rule is active, but you will not be able to use
any configuration options or disable the rule via config file.
- If your extension is part of your project and you integrate it like `detektPlugins(project(":my-rules"))` make sure that this
subproject is built before `gradle detekt` is run.
In the `kotlin-dsl` you could add something like `tasks.withType<Detekt> { dependsOn(":my-rules:assemble") }` to explicitly run `detekt` only 
after your extension subproject is built.
- If you use detekt for your Android project and if you want to integrate all your custom rules in a new module, please make sure that
you put them in a pure kotlin module with no Android dependencies. `kotlin("jvm")` is enough to make it work.
- Sometimes when you run detekt task, you may not see the violations detected by your custom rules. In this case open a terminal and run
`./gradlew --stop` to stop gradle daemons and run the task again.
- If you are configuring a custom detekt task at the root project level, you will need to apply the detektPlugins at the root project as well (not subprojects). See [this issue](https://github.com/detekt/detekt/issues/3989#issuecomment-890331512) for more.

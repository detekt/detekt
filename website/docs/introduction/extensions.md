---
id: extensions
title: "Extending detekt"
keywords: [extensions, rulesets]
sidebar_position: 9
---

The following page describes how to extend detekt and how to customize it to your domain-specific needs.
The associated **code samples** to this guide can be found in the package [detekt/detekt-sample-extensions](https://github.com/detekt/detekt/tree/main/detekt-sample-extensions).

#### <a name="customrulesets">Custom RuleSets</a>

_detekt_ uses the `ServiceLoader` pattern to collect all instances of `RuleSetProvider` interfaces. 
So it is possible to define rules/rule sets and enhance _detekt_ with your own flavor. 

:::caution Attention

You need a `resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider` file which 
has as content the fully qualified name of your `RuleSetProvider` e.g. `io.gitlab.arturbosch.detekt.sample.extensions.SampleProvider`.

:::

You can use our [GitHub template](https://github.com/detekt/detekt-custom-rule-template) to have a basic scaffolding to
develop your own custom rules. Another option is to clone the provided [detekt/detekt-sample-extensions](https://github.com/detekt/detekt/tree/main/detekt-sample-extensions) project.

:::note

It's important that the dependency of `io.gitlab.arturbosch.detekt:detekt-api` is configured as `compileOnly` (as in the examples).
You can read more information about this [here](https://github.com/detekt/detekt/issues/7883).

:::

Own rules have to extend the abstract _Rule_ class and override the `visitXXX()`-functions from the AST.  
A `RuleSetProvider` must be implemented, which declares a `RuleSet` in the `instance()`-function.
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

Example of a much preciser rule in terms of more specific Finding constructor and Rule attributes:
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

If you want your rule to be configurable, write down your properties inside the detekt.yml file.
Please note that this will only take effect, if the `Config` object is passed on by the `RuleSetProvider`
to the rule itself.

```yaml
MyRuleSet:
  TooManyFunctions2:
    active: true
    threshold: 5
  OtherRule:
    active: false
```

By specifying the rule set and rule ids, _detekt_ will use the sub configuration of `TooManyFunctions2`:

```val threshold = valueOrDefault("threshold", THRESHOLD)```

:::note

As of version 1.2.0 detekt now verifies if all configured properties actually exist in a configuration created by `--generate-config`.
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

##### <a name="testing">Testing your rules</a>

To test your rules, add the dependency on `detekt-test` to your project: `testCompile "io.gitlab.arturbosch.detekt:detekt-test:$version"`.

The easiest way to detect issues with your newly created rule is to use the `lint` extension function:
- `Rule.lint(StringContent/Path/KtFile): List<Finding>`

If you need to reuse the Kotlin file for performance reasons within similar test cases, please use one of these functions:
- `compileContentForTest(content: String): KtFile`
- `compileForTest(path: Path): KtFile`

#### <a name="customprocessors">Custom Processors</a>

Custom processors can be used for example to implement additional project metrics.

When for whatever reason you want to count all loop statements inside your code base, you could write something like:

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

To let detekt know about the new processor, we specify a `resources/META-INF/services/io.gitlab.arturbosch.detekt.api.FileProcessListener` file 
with the full qualify name of our processor as the content: `io.gitlab.arturbosch.detekt.sample.extensions.processors.NumberOfLoopsProcessor`.


To test the code we use the `detekt-test` module and write a JUnit 5 testcase.

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

#### <a name="customreports">Custom Reports</a>

_detekt_ allows you to extend the console output and to create custom output formats.
If you want to customize the output, take a look at the `ConsoleReport` and `OutputReport` classes.

All they need are an implementation of the `render()`-function which takes an object with all findings and returns a string to be printed out.

```kotlin
abstract fun render(detektion: Detektion): String?
```

#### <a name="configureextensions">Let detekt know about your extensions</a>

So you have implemented your own rules or other extensions and want to integrate them
into your `detekt` run? Great, make sure to have a `jar` with all your needed dependencies 
minus the ones `detekt` brings itself.

Take a look at our [sample project](https://github.com/detekt/detekt/tree/main/detekt-sample-extensions) on how to achieve this with gradle.

##### Integrate your extension with the detekt CLI

Mention your `jar` with the `--plugins` flag when calling the cli fatjar:
```sh
detekt --input ... --plugins /path/to/my/jar
```

##### Integrate your extension with the Detekt Gradle Plugin 

For example `detekt` itself provides a wrapper over [ktlint](https://github.com/pinterest/ktlint) as a 
custom `ktlint` rule set.
To enable it, we add the published dependency to `detekt` via the `detektPlugins` configuration:

###### Gradle (Kotlin/Groovy DSL)

```kotlin
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-ktlint-wrapper:[detekt_version]")
}
```

##### Pitfalls

- All rules are disabled by default and have to be explicitly enabled in the `detekt` yaml configuration file.
- If you do not pass the `Config` object from the `RuleSetProvider` to the rule, the rule is active, but you will not be able to use
any configuration options or disable the rule via config file.
- If your extension is part of your project and you integrate it like `detektPlugins project(":my-rules")` make sure that this
subproject is build before `gradle detekt` is run.
In the `kotlin-dsl` you could add something like `tasks.withType<Detekt> { dependsOn(":my-rules:assemble") }` to explicitly run `detekt` only 
after your extension sub project is built.
- If you use detekt for your Android project, and if you want to integrate all your custom rules in a new module, please make sure that
you created a pure kotlin module which has no Android dependencies. `apply plugin: "kotlin"` is enough to make it work.
- Sometimes when you run detekt task, you may not see the violations detected by your custom rules. In this case open a terminal and run
`./gradlew --stop` to stop gradle daemons and run the task again.
- If you are configuring a custom detekt task at the root project level, you will need to apply the detektPlugins at the root project as well (not subprojects). See [this issue](https://github.com/detekt/detekt/issues/3989#issuecomment-890331512) for more.

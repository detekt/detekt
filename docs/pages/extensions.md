---
title: "Extending detekt"
keywords: extensions rulesets 
sidebar: 
permalink: extensions.html
summary:
---

The following page describes how to extend detekt and how to customize it to your domain-specific needs.
The associated **code samples** to this guide can be found in the package [detekt/detekt-sample-extensions](https://github.com/detekt/detekt/tree/master/detekt-sample-extensions).

#### <a name="customrulesets">Custom RuleSets</a>

_detekt_ uses the `ServiceLoader` pattern to collect all instances of `RuleSetProvider` interfaces. 
So it is possible to define rules/rule sets and enhance _detekt_ with your own flavor. 

Attention: You need a `resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider` file which 
has as content the fully qualified name of your `RuleSetProvider` e.g. _io.gitlab.arturbosch.detekt.sample.extensions.SampleProvider_.

The easiest way to define a rule set is to clone the provided **detekt-sample-extensions** project.

Own rules have to extend the abstract _Rule_ class and override the `visitXXX()`-functions from the AST.  
A `RuleSetProvider` must be implemented, which declares a `RuleSet` in the `instance()`-function.
To allow your rule to be configurable, pass it a Config object from within your rule set provider.  
An `Issue` property defines what ID, severity and message should be printed on the console or on any other output format.

Example of a custom rule:
```kotlin
class TooManyFunctions : Rule() {

    override val issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports a file with an excessive function count.",
        Debt.TWENTY_MINS)

    private val threshold = 10
    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > threshold) {
            report(CodeSmell(issue, Entity.from(file), 
                "Too many functions can make the maintainability of a file costlier")
        }
        amount = 0
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        amount++
    }
}
```

Example of a much preciser rule in terms of more specific CodeSmell constructor and Rule attributes:
```kotlin
class TooManyFunctions2(config: Config) : ThresholdRule(config, THRESHOLD) {

    override val issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports a file with an excessive function count.",
        Debt.TWENTY_MINS)

    private val threshold = valueOrDefault("threshold", THRESHOLD)
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
    
    const val THRESHOLD = 10
}
```

If you want your rule to be configurable, write down your properties inside the detekt.yml file:

```yaml
MyRuleSet:
  TooManyFunctions2:
    threshold: 10
  OtherRule:
    active: false
```

By specifying the rule set and rule ids, _detekt_ will use the sub configuration of `TooManyFunctions2`:

```val threshold = valueOrDefault("threshold", THRESHOLD)```

Note: As of version 1.2.0 detekt now verifies if all configured properties actually exist in a configuration created by `--generate-config`.
This means that by default detekt does not know about your new properties.
Therefore we need to mention them in the configuration under `config>excludes`:

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


To test the code we use the `detekt-test` module and write a `Spek` testcase.

```kotlin
class NumberOfLoopsProcessorSpec : Spek({

	it("should expect two loops") {
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
})

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

Take a look at our [sample project](https://github.com/detekt/detekt/tree/master/detekt-sample-extensions) on how to achieve this with gradle.

##### Integrate your extension with the detekt CLI

Mention your `jar` with the `--plugins` flag when calling the cli fatjar:
```sh
detekt --input ... --plugins /path/to/my/jar
```

##### Integrate your extension with the detekt gradle plugin 

For example `detekt` itself provides a wrapper over [ktlint](https://github.com/pinterest/ktlint) as a 
custom `formatting` rule set.
To enable it, we add the published dependency to `detekt` via the `detektPlugins` configuration:

###### Groovy DSL
```groovy
dependencies {
    detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:{{ site.detekt_version }}"
}
```

###### Kotlin DSL
```kotlin
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:{{ site.detekt_version }}")
}
```

##### Pitfalls

- By default all rules not marked with `@active` in their `KDoc` are disabled.
That means your custom rules are also disabled if you have not explicitly enabled
them in the `detekt` yaml configuration file.
- If your extension is part of your project and you integrate it like `detektPlugins project(":my-rules")` make sure that this
subproject is build before `gradle detekt` is run.
In the `kotlin-dsl` you could add something like `tasks.withType<Detekt> { dependsOn(":my-rules:assemble") }` to explicitly run `detekt` only 
after your extension sub project is built.
- If you use detekt for your Android project, and if you want to integrate all your custom rules in a new module, please make sure that
you created a pure kotlin module which has no Android dependencies. `apply plugin: "kotlin"` is enough to make it work.
- Sometimes when you run detekt task, you may not see the violations detected by your custom rules. In this case open a terminal and run
`./gradlew --stop` to stop gradle daemons and run the task again.

#### autoCorrect property

In detekt you can write custom rules which can manipulate your code base.
For this a cli flag `--auto-correct` and the gradle plugin property `autoCorrect` exists.
Only write auto correcting code within the `Rule#withAutoCorrect()`-function.

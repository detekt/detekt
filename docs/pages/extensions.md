---
title: "Extending detekt"
keywords: extensions rulesets 
tags: 
sidebar: 
permalink: extensions.html
summary:
---

#### <a name="customrulesets">Custom RuleSets</a>

_detekt_ uses the `ServiceLoader` pattern to collect all instances of `RuleSetProvider` interfaces. 
So it is possible to define rules/rule sets and enhance _detekt_ with your own flavor. 

Attention: You need a `resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider` file which 
has as content the fully qualified name of your `RuleSetProvider` e.g. _io.gitlab.arturbosch.detekt.sampleruleset.SampleProvider_.

The easiest way to define a rule set is to clone the provided detekt-sample-extensions project.

Own rules have to extend the abstract _Rule_ class and override the `visitXXX()`-functions from the AST.  
A `RuleSetProvider` must be implemented which declares a `RuleSet` in the `instance()`-function.
To allow your rule to be configurable, pass it a Config object from within your rule set provider.  
An `Issue` property defines what ID, severity and message should be printed on the console or on any output format.

Example of a custom rule:
```kotlin
class TooManyFunctions : Rule("TooManyFunctions") {

    override val issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports a file with an excessive function count.")

    private val threshold = valueOrDefault("threshold", 10)
    private var amount: Int = 0

    override fun visitFile(file: PsiFile) {
        super.visitFile(file)
        if (amount > threshold) {
            addFindings(CodeSmell(id, Entity.from(file)))
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        amount++
    }
}
```

Example of a much preciser rule in terms of more specific CodeSmell constructor and Rule attributes:
```kotlin
class TooManyFunctions2(config: Config) : 
        Rule("TooManyFunctionsTwo", Severity.Maintainability, config) {

    override val issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports a file with an excessive function count.")

    private var amount: Int = 0

    override fun visitFile(file: PsiFile) {
        super.visitFile(file)
        if (amount > 10) {
            addFindings(CodeSmell(
                id = id, entity = Entity.from(file),
                description = "Too many functions can make the maintainability of a file costlier",
                metrics = listOf(Metric(type = "SIZE", value = amount, threshold = 10)),
                references = listOf())
            )
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        amount++
    }
}
```

If you want your rule to be configurable, write down your properties inside the detekt.yml file:

```yaml
MyRuleSet:
  MyRule:
    threshold: 10
  OtherRule:
    active: false
```

By specifying the rule set and rule ids, _detekt_ will use the sub configuration of MyRule:

```val threshold = valueOrDefault("threshold") { threshold }```


##### <a name="testing">Testing your rules</a>

To test your rules, add the dependency on `detekt-test` to your project: `testCompile "io.gitlab.arturbosch.detekt:detekt-test:$version"`.

The easiest way to detect issues with your newly created rule is to use the `lint` extension function:
- `Rule.lint(StringContent/Path/KtFile): List<Finding>`

If you need to reuse the kotlin file for performance reasons within similar test cases, please use one of these functions:
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

All they need are implementation of the `render()`-function which takes a object with all findings and returns a string.

```kotlin
abstract fun render(detektion: Detektion): String?
```

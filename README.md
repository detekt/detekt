# __detekt__

[![Join the chat at https://kotlinlang.slack.com/messages/C88E12QH4/convo/C0BQ5GZ0S-1511956674.000289/](https://img.shields.io/badge/chat-on_slack-red.svg?style=flat-square)](https://kotlinlang.slack.com/messages/C88E12QH4/convo/C0BQ5GZ0S-1511956674.000289/)
[![build status](https://travis-ci.org/arturbosch/detekt.svg?branch=master)](https://travis-ci.org/arturbosch/detekt)
[![build status windows](https://ci.appveyor.com/api/projects/status/3q9g98vveiul7yut/branch/master?svg=true)](https://ci.appveyor.com/project/arturbosch/detekt)
[ ![Download](https://api.bintray.com/packages/arturbosch/code-analysis/detekt/images/download.svg) ](https://bintray.com/arturbosch/code-analysis/detekt/_latestVersion)
[![gradle plugin](https://img.shields.io/badge/gradle_plugin-1.0.0.RC6.4-blue.svg?style=flat-square)](https://plugins.gradle.org/plugin/io.gitlab.arturbosch.detekt)

[![All Contributors](https://img.shields.io/badge/all_contributors-40-orange.svg?style=flat-square)](#contributors)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

Meet _detekt_, a static code analysis tool for the _Kotlin_ programming language.
It operates on the abstract syntax tree provided by the Kotlin compiler.

![detekt in action](img/detekt_in_action.png "detekt in action")

### Features

- code smell analysis for your kotlin projects
- complexity report based on logical lines of code, McCabe complexity and amount of code smells
- highly configurable (rule set or rule level)
- suppress findings with Kotlin's @Suppress and Java's @SuppressWarnings annotations
- specify code smell thresholds to break your build or print a warning
- code Smell baseline and ignore lists for legacy projects
- gradle tasks to use local `intellij` distribution for [formatting and inspecting](#idea) kotlin code
- optionally configure detekt for each sub module by using [profiles](#closure) (gradle-plugin)
- extensible by own rule sets and `FileProcessListener's`
- [gradle plugin](#gradleplugin) for code analysis via Gradle builds
- [sonarqube integration](https://github.com/arturbosch/sonar-kotlin)
- [intellij integration](https://github.com/arturbosch/detekt-intellij-plugin)

### Table of contents
1. [Commandline interface](#build)
2. [Gradle plugin](#gradleplugin)
    1. [in groovy dsl](#gradlegroovy)
    2. [in kotlin dsl](#gradlekotlin)
    3. [in android projects](#gradleandroid)
    4. [plugin tasks](#tasks)
    5. [detekt-closure](#closure)
3. [Standalone gradle task](#gradle)
4. [Standalone maven task](#maventask)
5. [Rule sets](#rulesets)
6. [Rule set configuration](#rulesetconfig)
7. [Suppress rules](#suppress)
7. [Build failure](#failure)
7. [Extending detekt](#extensions)
    1. [RuleSets](#customrulesets)
    2. [Processors](#customprocessors)
    3. [Reports](#customreports)
    4. [Rule testing](#testing)
10. [Black- and Whitelist code smells](#baseline)
10. [Contributors](#contributors)
10. [Mentions](#mentions)

### <a name="build">Build & use the commandline interface</a>

- `git clone https://github.com/arturbosch/detekt`
- `cd detekt`
- `./gradlew build shadowJar`
- `java -jar detekt-cli/build/libs/detekt-cli-[version]-all.jar [parameters]*`

##### Parameters for CLI
The following parameters are shown when `--help` is entered. The `--input`/`-i` option is required:

```
Usage: detekt [options]
  Options:
    --baseline, -b
      If a baseline xml file is passed in, only new code smells not in the 
      baseline are printed in the console.
    --config, -c
      Path to the config file (path/to/config.yml).
    --config-resource, -cr
      Path to the config resource on detekt's classpath (path/to/config.yml).
    --create-baseline, -cb
      Treats current analysis findings as a smell baseline for future detekt
      runs. 
      Default: false
    --debug
      Debugs given ktFile by printing its elements.
      Default: false
    --disable-default-rulesets, -dd
      Disables default rule sets.
      Default: false
    --filters, -f
      Path filters defined through regex with separator ';' (".*test.*").
    --generate-config, -gc
      Export default config to default-detekt-config.yml.
      Default: false
    --help, -h
      Shows the usage.
  * --input, -i
      Input path to analyze (path/to/project).
    --output, -o
      Directory where output reports are stored.
    --output-name, -on
      The base name for output reports is derived from this parameter.
    --parallel
      Enables parallel compilation of source files. Should only be used if the 
      analyzing project has more than ~200 kotlin files.
      Default: false
    --plugins, -p
      Extra paths to plugin jars separated by ',' or ';'.
```

`--input` can either be a directory or a single Kotlin file.
The currently only supported configuration format is yaml. `--config` should point to one. Generating a default configuration file is as easy as using the `--generate-config` parameter.
`filters` can be used for example to exclude all test directories.
With `rules` you can point to additional ruleset.jar's creating by yourself or others. 
More on this topic see section _Custom RuleSets_.

#### <a name="gradleplugin">Using the detekt-gradle-plugin</a>

Use the Groovy or Kotlin DSL of Gradle to apply the detekt Gradle Plugin. You can further configure the Plugin
using the detekt closure as described [here](#closure).

##### <a name="gradlegroovy">Configuration when using groovy dsl</a>
For gradle version >= 2.1
 
```groovy
buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    id "io.gitlab.arturbosch.detekt" version "1.0.0.[version]"
}
```

For all gradle versions:

```groovy
buildscript {
  repositories {
    jcenter()
    maven { url "https://plugins.gradle.org/m2/" }
  }
  dependencies {
    classpath "gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.0.0.[version]"
  }
}

apply plugin: "io.gitlab.arturbosch.detekt"
```

##### <a name="gradlekotlin">Configuration when using kotlin dsl</a>
For gradle version >= 4.1

```kotlin
import io.gitlab.arturbosch.detekt.DetektExtension

buildscript {
    repositories {
        jcenter()
    }
}
plugins {
    id("io.gitlab.arturbosch.detekt").version("1.0.0.[version]")
}
```

##### <a name="gradleandroid">Configuration for Android projects</a>

When using Android make sure to have detekt configured in the project level build.gradle file.
The new preferred plugin configuration way is used, the old way is commented out.

```groovy
buildscript {
    repositories {
//        maven { url "https://plugins.gradle.org/m2/" }
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.3'
//        classpath "gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.0.0.[version]"
    }

}
plugins {
    id "io.gitlab.arturbosch.detekt" version "1.0.0.[version]"
}

//apply plugin: 'io.gitlab.arturbosch.detekt'
```

#### <a name="tasks">Available plugin tasks</a>

The detekt Gradle plugin will generate `detekt` tasks for each of your source sets. For a basic project this will result
in a `detektMain` task which will check all main source code of the project. The `detektTest` task will run detekt on
the test sources of the project

- `detekt[SourceSet]` - Runs a _detekt_ analysis and complexity report on the given source set. Configure the analysis inside the `detekt` closure. By default the standard rule set is used without output report or black- and whitelist checks.
- `detektGenerateConfig` - Generates a default detekt configuration file into your project directory.
- `detektBaseline` - Similar to `detekt[SourceSet]`, but creates a code smell baseline. Further detekt runs will only feature new smells not in this list.
- `detektIdeaFormat` - Uses a local `idea` installation to format your kotlin (and other) code according to the specified `code-style.xml`.
- `detektIdeaInspect` Uses a local `idea` installation to run inspections on your kotlin (and other) code according to the specified `inspections.xml` profile.

##### <a name="closure">Options for detekt configuration closure</a>

```groovy
detekt {
    toolVersion = "1.0.0.[version]"                                  // When unspecified the latest detekt version found, will be used. Override to stay on the same version.
    parallel = false                                                 // Runs detekt in parallel. Can lead to speedups in larger projects. `false` by default.
    config = project.resources.text.fromFile("path/to/config.yml")   // Define the detekt configuration you want to use.
    configFile = file("path/to/config.yml")                          // Define the detekt configuration you want to use.
    baseline = file("path/to/baseline.xml")                          // Specifying a baseline file will ignore all findings that are saved in the baseline file.
    filters = ''                                                     // Regular expression of paths that should be excluded.
    disableDefaultRuleSets = false                                   // Disables all default detekt rulesets and will only run detekt with custom rules defined in `plugins`.
    plugins = "other/optional/ruleset.jar"                           // Jar file containing custom detekt rules.
}
```

##### <a name="gradlepluginreports">Customizing Detekt reports</a>

You can configure the reports detekt outputs with the following configuration in your `build.gradle` file:

```groovy
tasks.withType(io.gitlab.arturbosch.detekt.Detekt) {
    reports {
        xml {
            enabled true                                             // Enable/Disable XML report
            destination file("build/reports/detekt.xml")             // Path where XML report will be stored

        }
        html {
            enabled true                                             // Enable/Disable HTML report
            destination file("build/reports/detekt.html")            // Path where HTML report will be stored
        }
    }
}
```

##### <a name="customdetekttask">Defining custom detekt</a>

Custom tasks for alternative configurations or different source sets can be defined by creating a custom task that
uses the type `Detekt`.

```groovy
task customDetektTask(type: io.gitlab.arturbosch.detekt.Detekt) {
		description = "Runs a custom detekt task."

		source = sourceSets.getAt("main").allSource                              // Define the source set this task should run for
		configFile = file("${rootProject.projectDir}/reports/failfast.yml")      // Define the configuration file that should be used
	}
```


##### <a name="idea">Configure a local idea for detekt</a>

- download the community edition of [Intellij IDEA](https://www.jetbrains.com/idea/download/)
- extract the file to your preferred location eg. `~/.idea`
- let detekt know about idea inside the `detekt-closure`
- extract `code-style.xml` and `inpect.xml` from idea settings (`Settings>CodeStyle>Scheme` and `Settings>Inspections>Profile`)
- run `detektIdeaFormat` or `detektIdeaInspect`
- all parameters in the following detekt-closure are mandatory for both tasks
- make sure that current or default profile have an input path specified!

```groovy
String USER_HOME = System.getProperty("user.home")

detekt {  
    idea {
        path = "$USER_HOME/.idea"
        codeStyleScheme = "$USER_HOME/.idea/idea-code-style.xml"
        inspectionsProfile = "$USER_HOME/.idea/inspect.xml"
        report = "project.projectDir/reports"
        mask = "*.kt,"
    }
}
```

For more information on using idea as a headless formatting/inspection tool see [here](https://www.jetbrains.com/help/idea/working-with-intellij-idea-features-from-command-line.html).

#### <a name="gradle">Using _detekt_ in custom gradle projects</a>

1. Add following lines to your build.gradle file.
2. Run `gradle detekt`
3. Add `check.dependsOn detekt` if you want to run _detekt_ on every `build`

```groovy
repositories {
    jcenter()
}

configurations {
	detekt
}

task detekt(type: JavaExec) {
	main = "io.gitlab.arturbosch.detekt.cli.Main"
	classpath = configurations.detekt
	def input = "$projectDir"
	def config = "$projectDir/detekt.yml"
	def filters = ".*test.*"
	def rulesets = ""
	def params = [ '-i', input, '-c', config, '-f', filters, '-r', rulesets]
	args(params)
}

dependencies {
	detekt 'io.gitlab.arturbosch.detekt:detekt-cli:1.0.0.[version]'
}
```

`Attention Android Developers! the dependencies section must be at the bottom, after the repository, configurations and task sections!`

#### <a name="maventask">Using _detekt_ in Maven Projects</a>

1. Add following lines to your pom.xml.
2. Run `mvn verify` (when using the verify phase as I did here)


```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-antrun-plugin</artifactId>
            <version>1.8</version>
            <executions>
                <execution>
                    <!-- This can be run separately with mvn antrun:run@detekt -->
                    <id>detekt</id>
                    <phase>verify</phase>
                    <configuration>
                        <target name="detekt">
                            <java taskname="detekt" dir="${basedir}" fork="true" failonerror="true"
                                  classname="io.gitlab.arturbosch.detekt.cli.Main" classpathref="maven.plugin.classpath">
                                <arg value="-i"/>
                                <arg value="${basedir}/src"/>
                                <arg value="-f"/>
                                <arg value=".*test.*"/>
                            </java>
                        </target>
                    </configuration>
                    <goals><goal>run</goal></goals>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>io.gitlab.arturbosch.detekt</groupId>
                    <artifactId>detekt-cli</artifactId>
                    <version>1.0.0.[CURRENT_MILESTONE]</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>

<pluginRepositories>
  <pluginRepository>
    <id>arturbosch-code-analysis</id>
    <name>arturbosch-code-analysis (for detekt)</name>
    <url>https://dl.bintray.com/arturbosch/code-analysis/</url>
    <layout>default</layout>
    <releases>
      <enabled>true</enabled>
      <updatePolicy>never</updatePolicy>
    </releases>
    <snapshots>
      <enabled>false</enabled>
      <updatePolicy>never</updatePolicy>
    </snapshots>
  </pluginRepository>
</pluginRepositories>
```

### <a name="rulesets">RuleSets</a>

Currently there are eight rule sets which are used per default when running the CLI.

- [complexity](detekt-generator/docs/pages/documentation/complexity.md) - This rule set contains rules that report complex code.
- [style](detekt-generator/docs/pages/documentation/style.md) - The Style ruleset provides rules that assert the style of the code. This will help keep code in line with the given code style guidelines.
- [comments](detekt-generator/docs/pages/documentation/comments.md) - This rule set provides rules that address issues in comments and documentation of the code.
- [exceptions](detekt-generator/docs/pages/documentation/exceptions.md) - Rules in this rule set report issues related to how code throws and handles Exceptions.
- [empty-blocks](detekt-generator/docs/pages/documentation/empty-blocks.md) - The empty-blocks ruleset contains rules that will report empty blocks of code which should be avoided.
- [naming](detekt-generator/docs/pages/documentation/naming.md) - The naming ruleset contains rules which assert the naming of different parts of the codebase.
- [potential-bugs](detekt-generator/docs/pages/documentation/potential-bugs.md) - The potential-bugs rule set provides rules that detect potential bugs.
- [performance](detekt-generator/docs/pages/documentation/performance.md) - The performance rule set analyzes code for potential performance problems.

### <a name="rulesetconfig">RuleSet Configuration</a>

To turn off specific rules/rule sets or change threshold values for certain rules a yaml configuration file can be used.  There are two approaches to configuring your rulesets.

#### Copy defaults and modify

Export the default config with the `--generate-config` flag or copy and modify the [default-detekt-config.yml](./detekt-cli/src/main/resources/default-detekt-config.yml) for your needs.

#### Override defaults ([via `failFast` option](https://github.com/arturbosch/detekt/issues/179))

Set `failFast: true` in your detekt.yml configuration file.  As a result, every rule will be enabled and `maxIssues` will be set to 0.  Weights can then be ignored and left untouched.

To adjust, for example, the maxLineLength value, use this configuration file:
```
failFast: true
autoCorrect: true

style:
  MaxLineLength:
    maxLineLength: 100
```

All rules are turned on by default and the value of maxLineLength is adjusted to 100. If you don't want to have the CommentOverPrivateMethod turned on, you append:
```
comments:
  CommentOverPrivateMethod:
    active: false
```

### <a name="suppress">Suppress code smell rules</a>

_detekt_ supports the Java (`@SuppressWarnings`) and Kotlin (`@Suppress`) style suppression. If both annotations are present, only Kotlin's annotation is used! To suppress a rule, the id of the rule must be written inside the values field of the annotation e.g. `@Suppress("LongMethod", "LongParameterList", ...)`

### <a name="failure">Configure build failure thresholds</a>

_detekt_ now can throw a BuildFailure(Exception) and let the build fail with following config parameters:
```yaml
build:
  maxIssues: 10 // Ten weighted smells to fail the build
  weights:
    complexity: 2 // Whole complexity rule should add two for each finding.
    LongParameterList: 1 // The specific rule should not add two.
    comments: 0 // Comments should not fail the build at all?!
```

Every rule and rule set can be attached with an integer value which is the weight of the finding.
For example: If you have 5 findings of the category _complexity_, then your failThreshold of 10 is reached as
5 x 2 = 10. 

The formula for weights: RuleID > RuleSetID > 1. Only integer values are supported.

### <a name="extensions">Extending detekt</a>

#### <a name="customrulesets">Custom RuleSets</a>

_detekt_ uses a ServiceLoader to collect all instances of _RuleSetProvider_-interfaces. So it is possible
to define rules/rule sets and enhance _detekt_ with your own flavor. 
Attention: You need a `resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider` file which 
has as content the fully qualified name of your RuleSetProvider e.g. _io.gitlab.arturbosch.detekt.sampleruleset.SampleProvider_.

The easiest way to define a rule set is to clone the provided detekt-sample-ruleset project.

Own rules have to extend the abstract _Rule_ class and override the `visitXXX` functions from the AST.
A `RuleSetProvider` must be implemented which declares a `RuleSet` in the `instance` method.
To allow your rule to be configurable, pass it a Config object from within your rule set provider.
You can also specify a _Severity_ type for your rule.

Example of a custom rule:
```kotlin
class TooManyFunctions : Rule("TooManyFunctions") {

	private var amount: Int = 0

	override fun visitFile(file: PsiFile) {
		super.visitFile(file)
		if (amount > 10) {
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
class TooManyFunctions2(config: Config) : Rule("TooManyFunctionsTwo", Severity.Maintainability, config) {

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

If you want your rule to be configurable, write down your properties inside the detekt.yml file
 and use the `withConfig` function:

```yaml
MyRuleSet:
  MyRule:
    MyMetric: 5
    threshold: 10
  OtherRule:
    active: false
```

By specifying the rule set and rule ids, _detekt_ will use the sub configuration of MyRule:

```val threshold = withConfig { valueOrDefault("threshold") { threshold } }```

##### Maven

If your using maven to build rule sets or use _detekt_ as a dependency, you have to run the additional task `publishToMavenLocal`

#### <a name="customprocessors">Custom Processors</a>

TODO

#### <a name="customreports">Custom Reports</a>

_detekt_ allows you to extend the console output and to create custom output formats.

For example if you do not like the default printing of findings, we can ... TODO

#### <a name="testing">Testing your rules</a>

To test your rules you need a KtFile object and use its _visit_ method.
There are two predefined methods to help obtaining a KtFile:

- compileContentForTest(content: String): KtFile
- compileForTest(path: Path): KtFile

New with M3 there is a special detekt-test module, which specifies above two methods but also
Rule extension functions that allow to skip compilation, ktFile and visit procedures.

- Rule.lint(StringContent/Path/KtFile) returns just the findings for given content

### <a name="baseline">Code Smell baseline and ignore list</a>

Specify a report output with `--output` parameter and specify its format with `--output-format`.
Now you can generate a report which holds all findings of current analysis.

With `--baseline` you generate a `baseline.xml` where code smells are white- or blacklisted.

```xml
<SmellBaseline>
  <Blacklist timestamp="1483388204705">
    <ID>CatchRuntimeException:Junk.kt$e: RuntimeException</ID>
  </Blacklist>
  <Whitelist timestamp="1496432564542">
    <ID>NestedBlockDepth:Indentation.kt$Indentation$override fun procedure(node: ASTNode)</ID>
    <ID>ComplexCondition:SpacingAroundOperator.kt$SpacingAroundOperator$tokenSet.contains(node.elementType) &amp;&amp; node is LeafPsiElement &amp;&amp; !node.isPartOf(KtPrefixExpression::class) &amp;&amp; // not unary !node.isPartOf(KtTypeParameterList::class) &amp;&amp; // fun &lt;T&gt;fn(): T {} !node.isPartOf(KtTypeArgumentList::class) &amp;&amp; // C&lt;T&gt; !node.isPartOf(KtValueArgument::class) &amp;&amp; // fn(*array) !node.isPartOf(KtImportDirective::class) &amp;&amp; // import * !node.isPartOf(KtSuperExpression::class)</ID>
    <ID>TooManyFunctions:LargeClass.kt$io.gitlab.arturbosch.detekt.rules.complexity.LargeClass.kt</ID>
    <ID>ComplexMethod:DetektExtension.kt$DetektExtension$fun convertToArguments(): MutableList&lt;String&gt;</ID>
  </Whitelist>
</SmellBaseline>
```

The intention of a whitelist is that only new code smells are printed on further analysis. The blacklist can be used
to write down false positive detections. The `ID` node must be build of `<RuleID>:<Signature>`. Both values can be found
inside the report file.

### <a name="contributors">Contributors</a>

If you contributed to detekt but your name is not in the list, please feel free to add yourself to it!

- [Artur Bosch](https://github.com/arturbosch) - Maintainer
- [Marvin Ramin](https://github.com/Mauin) - Collaborator, Bunch of rules, Active on Issues, refactorings, MultiRule
- [schalks](https://github.com/schalkms) - Collaborator, Active on Issues, Bunch of rules, Project metrics
- [Niklas Baudy](https://github.com/vanniktech) - Active on Issues, Bunch of rules, Bug fixes
- [lummax](https://github.com/lummax) - Cli enhancements
- [Svyatoslav Chatchenko](https://github.com/MyDogTom) - Active on Issues, NamingConventions and UnusedImport fixes
- [Sean Flanigan](https://github.com/seanf) - Config from classpath resource
- [Sebastian Schuberth](https://github.com/sschuberth) - Active on Issues, Windows support
- [Olivier Lemasle](https://github.com/olivierlemasle) - NP-Bugfix
- [Marc Prengemann](https://github.com/winterDroid) - Support for custom output formats, prototyped Rule-Context-Issue separation
- [Sebastiano Poggi](https://github.com/rock3r) - Enhanced milestone report script, Magic number fixes
- [Ilya Tretyakov](https://github.com/jvilya) - Sonar runs should not auto correct formatting.
- [Andrey T](https://github.com/mr-procrastinator) - Readme fix
- [Ivan Balaksha](https://github.com/tagantroy) - Rules: UnsafeCast, SpreadOperator, UnsafeCallOnNullableType, LabeledExpression
- [Anna Y](https://github.com/Nevvea7) - Readme fix
- [Karol Wrótniak](https://github.com/koral--) - Treat comments as not empty blocks
- [Radim Vaculik](https://github.com/radimvaculik) - VariableMaxLength - bugfix
- [Martin Nonnenmacher](https://github.com/mnonnenmacher) - UndocumentedPublicClass - enum support
- [Dmytro Troynikov](https://github.com/DmytroTroynikov) - Updated Magic Number rule to ignore Named Arguments
- [Andrew Ochsner](https://github.com/aochsner) - Updated Readme for `failFast` option
- [Paul Merlin](https://github.com/eskatos) - Gradle build improvements
- [Konstantin Aksenov](https://github.com/vacxe) - Coding improvement
- [Matthew Haughton](https://github.com/3flex) - Started type resolution, Dependency updates, Coding + Documentation improvements
- [Janusz Bagiński](https://github.com/jbaginski) - Fixed line number reporting for MaxLineLengthRule 
- [Mike Kobit](https://github.com/mkobit) - Gradle build improvements
- [Philipp Hofmann](https://github.com/philipphofmann) - Readme improvements
- [Olivier PEREZ](https://github.com/olivierperez) - Fixed Typo in Readme
- [Sebastian Kaspari](https://github.com/pocmo) - Html-Output-Format, Documentation fix
- [Ilya Zorin](https://github.com/geralt-encore) - Rule improvement: UnnecessaryAbstractClass
- [Gesh Markov](https://github.com/markov) - Improve error message for incorrect configuration file
- [Patrick Pilch](https://github.com/patrickpilch) - Rule improvement: ReturnCount
- [Serj Lotutovici](https://github.com/serj-lotutovici) - Rule improvement: LongParameterList
- [Dmitry Primshyts](https://github.com/deeprim) - Rule improvement: MagicNumber
- [Egor Neliuba](https://github.com/egor-n) - Rule improvement: EmptyFunctionBlock, EmptyClassBlock
- [Said Tahsin Dane](https://github.com/tasomaniac/) - Gradle plugin improvements
- [Misa Torres](https://github.com/misaelmt) - Added: TrailingWhitespace and NoTabs rules
- [R.A. Porter](https://github.com/coyotesqrl) - Updated Readme links to RuleSets

### <a name="mentions">Mentions</a>

[![androidweekly](https://img.shields.io/badge/androidweekly.net-259-orange.svg?style=flat-square)](http://androidweekly.net/issues/issue-259) 
[![androidweekly](https://img.shields.io/badge/androidweekly.cn-154-orange.svg?style=flat-square)](http://androidweekly.cn/android-dev-wekly-issue-154/)

As mentioned in...

- [Codacy](https://www.codacy.com)
- [@medium/acerezoluna/static-code-analysis-tools-for-kotlin-in-android](https://medium.com/@acerezoluna/static-code-analysis-tools-for-kotlin-in-android-fa072125fd50)
- [@medium/annayan/writing-custom-lint-rules-for-your-kotlin-project-with-detekt](https://proandroiddev.com/writing-custom-lint-rules-for-your-kotlin-project-with-detekt-653e4dbbe8b9)
- [Free Continuous Integration for modern Android apps with CircleCI](https://tips.seebrock3r.me/free-continuous-integration-for-modern-android-apps-with-circleci-940e33451c83)
- [Static code analysis for Kotlin in Android](https://blog.thefuntasty.com/static-code-analysis-for-kotlin-in-android-8676c8d6a3c5)
- [The Art of Android DevOps](https://blog.undabot.com/the-art-of-android-devops-fa29396bc9ee)
- [Android Basics: Continuous Integration](https://academy.realm.io/posts/360-andev-2017-mark-scheel-continuous-integration-android/)
- [Kotlin Static Analysis — why and how?](https://proandroiddev.com/kotlin-static-analysis-why-and-how-a12042e34a98)

Integrations:
- [Gradle plugin that generates ErrorProne, Findbugs, Checkstyle, PMD, CPD, Lint, Detekt & Ktlint Tasks for every subproject](https://github.com/vanniktech/gradle-code-quality-tools-plugin)
- [Java library for parsing report files from static code analysis](https://github.com/tomasbjerre/violations-lib)
- [sputnik is a free tool for static code review and provides support for detekt](https://github.com/TouK/sputnik)
- [Novoda Gradle Static Analysis plugin](https://github.com/novoda/gradle-static-analysis-plugin)

#### Credits
- [JetBrains](https://github.com/jetbrains/) - Creating Intellij + Kotlin
- [PMD](https://github.com/pmd/pmd) & [Checkstyle](https://github.com/checkstyle/checkstyle) & [KtLint](https://github.com/shyiko/ktlint) - Ideas for threshold values and style rules

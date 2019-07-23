---
title: io.gitlab.arturbosch.detekt.api - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api](./index.html)

## Package io.gitlab.arturbosch.detekt.api

### Types

| [AnnotationExcluder](-annotation-excluder/index.html) | `class AnnotationExcluder`<br>Primary use case for an AnnotationExcluder is to decide if a KtElement should be excluded from further analysis. This is done by checking if a special annotation is present over the element. |
| [BaseConfig](-base-config/index.html) | `abstract class BaseConfig : `[`Config`](-config/index.html)<br>Convenient base configuration which parses/casts the configuration value based on the type of the default value. |
| [BaseRule](-base-rule/index.html) | `abstract class BaseRule : `[`DetektVisitor`](-detekt-visitor/index.html)`, `[`Context`](-context/index.html)<br>Defines the visiting mechanism for KtFile's. |
| [CodeSmell](-code-smell/index.html) | `open class CodeSmell : `[`Finding`](-finding/index.html)<br>A code smell indicates any possible design problem inside a program's source code. The type of a code smell is described by an [Issue](-issue/index.html). |
| [Compactable](-compactable/index.html) | `interface Compactable`<br>Provides a compact string representation. |
| [CompositeConfig](-composite-config/index.html) | `class CompositeConfig : `[`Config`](-config/index.html)<br>Wraps two different configuration which should be considered when retrieving properties. |
| [Config](-config/index.html) | `interface Config`<br>A configuration holds information about how to configure specific rules. |
| [ConfigAware](-config-aware/index.html) | `interface ConfigAware : `[`Config`](-config/index.html)<br>Interface which is implemented by each Rule class to provide utility functions to retrieve specific or generic properties from the underlying detekt configuration file. |
| [ConsoleReport](-console-report/index.html) | `abstract class ConsoleReport : `[`Extension`](-extension/index.html)<br>Extension point which describes how findings should be printed on the console. |
| [Context](-context/index.html) | `interface Context`<br>A context describes the storing and reporting mechanism of [Finding](-finding/index.html)'s inside a [Rule](-rule/index.html). Additionally it handles suppression and aliases management. |
| [Debt](-debt/index.html) | `data class Debt`<br>Debt describes the estimated amount of work needed to fix a given issue. |
| [DefaultContext](-default-context/index.html) | `open class DefaultContext : `[`Context`](-context/index.html)<br>Default [Context](-context/index.html) implementation. |
| [Detektion](-detektion/index.html) | `interface Detektion`<br>Storage for all kinds of findings and additional information which needs to be transferred from the detekt engine to the user. |
| [DetektVisitor](-detekt-visitor/index.html) | `open class DetektVisitor : KtTreeVisitorVoid`<br>Basic visitor which is used inside detekt. Guarantees a better looking name as the extended base class :). |
| [Entity](-entity/index.html) | `data class Entity : `[`Compactable`](-compactable/index.html)<br>Stores information about a specific code fragment. |
| [Extension](-extension/index.html) | `interface Extension`<br>Defines extension points in detekt. Currently supported extensions are: |
| [FileProcessListener](-file-process-listener/index.html) | `interface FileProcessListener : `[`Extension`](-extension/index.html)<br>Gather additional metrics about the analyzed kotlin file. Pay attention to the thread policy of each function! |
| [Finding](-finding/index.html) | `interface Finding : `[`Compactable`](-compactable/index.html)`, `[`HasEntity`](-has-entity/index.html)`, `[`HasMetrics`](-has-metrics/index.html)<br>Base interface of detection findings. Inherits a bunch of useful behaviour from sub interfaces. |
| [HasEntity](-has-entity/index.html) | `interface HasEntity`<br>Describes a source code position. |
| [HasMetrics](-has-metrics/index.html) | `interface HasMetrics`<br>Adds metric container behaviour. |
| [Issue](-issue/index.html) | `data class Issue`<br>An issue represents a problem in the codebase. |
| [LazyRegex](-lazy-regex/index.html) | `class LazyRegex : `[`ReadOnlyProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.properties/-read-only-property/index.html)`<`[`Rule`](-rule/index.html)`, `[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)`>`<br>LazyRegex class provides a lazy evaluation of a Regex pattern for usages inside Rules. It computes the value once when reaching the point of its usage and returns the same value when requested again. |
| [Location](-location/index.html) | `data class Location : `[`Compactable`](-compactable/index.html)<br>Specifies a position within a source code fragment. |
| [Metric](-metric/index.html) | `data class Metric`<br>Metric type, can be an integer or double value. Internally it is stored as an integer, but the conversion factor and is double attributes can be used to retrieve it as a double value. |
| [MultiRule](-multi-rule/index.html) | `abstract class MultiRule : `[`BaseRule`](-base-rule/index.html) |
| [Notification](-notification/index.html) | `interface Notification`<br>Any kind of notification which should be printed to the console. For example when using the formatting rule set, any change to your kotlin file is a notification. |
| [OutputReport](-output-report/index.html) | `abstract class OutputReport : `[`Extension`](-extension/index.html)<br>Translates detekt's result container - [Detektion](-detektion/index.html) - into an output report which is written inside a file. |
| [ProjectMetric](-project-metric/index.html) | `open class ProjectMetric`<br>Anything that can be expressed as a number value for projects. |
| [Rule](-rule/index.html) | `abstract class Rule : `[`BaseRule`](-base-rule/index.html)`, `[`ConfigAware`](-config-aware/index.html)<br>A rule defines how one specific code structure should look like. If code is found which does not meet this structure, it is considered as harmful regarding maintainability or readability. |
| [RuleSet](-rule-set/index.html) | `class RuleSet`<br>A rule set is a collection of rules and must be defined within a rule set provider implementation. |
| [RuleSetProvider](-rule-set-provider/index.html) | `interface RuleSetProvider`<br>A rule set provider, as the name states, is responsible for creating rule sets. |
| [Severity](-severity/index.html) | `enum class Severity`<br>Rules can classified into different severity grades. Maintainer can choose a grade which is most harmful to their projects. |
| [SingleAssign](-single-assign/index.html) | `class SingleAssign<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>`<br>Allows to assign a property just once. Further assignments result in [IllegalStateException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-state-exception/index.html)'s. |
| [SourceLocation](-source-location/index.html) | `data class SourceLocation`<br>Stores line and column information of a location. |
| [SplitPattern](-split-pattern/index.html) | `class SplitPattern`<br>Splits given text into parts and provides testing utilities for its elements. Basic use cases are to specify different function or class names in the detekt yaml config and test for their appearance in specific rules. |
| [TextLocation](-text-location/index.html) | `data class TextLocation`<br>Stores character start and end positions of an text file. |
| [ThresholdedCodeSmell](-thresholded-code-smell/index.html) | `open class ThresholdedCodeSmell : `[`CodeSmell`](-code-smell/index.html)<br>Represents a code smell for which a specific metric can be determined which is responsible for the existence of this rule violation. |
| [ThresholdRule](-threshold-rule/index.html) | `abstract class ThresholdRule : `[`Rule`](-rule/index.html)<br>Provides a threshold attribute for this rule, which is specified manually for default values but can be also obtained from within a configuration object. |
| [YamlConfig](-yaml-config/index.html) | `class YamlConfig : `[`BaseConfig`](-base-config/index.html)<br>Config implementation using the yaml format. SubConfigurations can return sub maps according to the yaml specification. |

### Type Aliases

| [RuleId](-rule-id.html) | `typealias RuleId = `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The type to use when referring to rule ids giving it more context then a String would. |
| [RuleSetId](-rule-set-id.html) | `typealias RuleSetId = `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Extensions for External Classes

| [org.jetbrains.kotlin.psi.KtAnnotated](org.jetbrains.kotlin.psi.-kt-annotated/index.html) |  |
| [org.jetbrains.kotlin.psi.KtElement](org.jetbrains.kotlin.psi.-kt-element/index.html) |  |

### Properties

| [DEFAULT_FLOAT_CONVERSION_FACTOR](-d-e-f-a-u-l-t_-f-l-o-a-t_-c-o-n-v-e-r-s-i-o-n_-f-a-c-t-o-r.html) | `const val DEFAULT_FLOAT_CONVERSION_FACTOR: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>To represent a value of 0.5, use the metric value 50 and the conversion factor of 100. (50 / 100 = 0.5) |
| [psiFactory](psi-factory.html) | `val psiFactory: KtPsiFactory`<br>Allows to generate different kinds of KtElement's. |
| [psiProject](psi-project.html) | `val psiProject: Project`<br>The initialized kotlin environment which is used to translate kotlin code to a Kotlin-AST. |

### Functions

| [createCompilerConfiguration](create-compiler-configuration.html) | `fun createCompilerConfiguration(classpath: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, pathsToAnalyze: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`>): CompilerConfiguration` |
| [createKotlinCoreEnvironment](create-kotlin-core-environment.html) | `fun createKotlinCoreEnvironment(configuration: CompilerConfiguration = CompilerConfiguration()): KotlinCoreEnvironment` |


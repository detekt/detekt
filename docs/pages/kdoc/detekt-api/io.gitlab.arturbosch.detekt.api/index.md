---
title: io.gitlab.arturbosch.detekt.api - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api](./index.html)

## Package io.gitlab.arturbosch.detekt.api

### Types

| [AnnotationExcluder](-annotation-excluder/index.html) | Primary use case for an AnnotationExcluder is to decide if a KtElement should be excluded from further analysis. This is done by checking if a special annotation is present over the element.`class AnnotationExcluder` |
| [CodeSmell](-code-smell/index.html) | A code smell indicates any possible design problem inside a program's source code. The type of a code smell is described by an [Issue](-issue/index.html).`open class CodeSmell : `[`Finding`](-finding/index.html) |
| [Compactable](-compactable/index.html) | Provides a compact string representation.`interface Compactable` |
| [Config](-config/index.html) | A configuration holds information about how to configure specific rules.`interface Config` |
| [ConfigAware](-config-aware/index.html) | Interface which is implemented by each Rule class to provide utility functions to retrieve specific or generic properties from the underlying detekt configuration file.`interface ConfigAware : `[`Config`](-config/index.html) |
| [ConfigValidator](-config-validator/index.html) | An extension which allows users to validate parts of the configuration.`interface ConfigValidator : `[`Extension`](-extension/index.html) |
| [ConsoleReport](-console-report/index.html) | Extension point which describes how findings should be printed on the console.`abstract class ConsoleReport : `[`Extension`](-extension/index.html) |
| [Context](-context/index.html) | A context describes the storing and reporting mechanism of [Finding](-finding/index.html)'s inside a [Rule](-rule/index.html). Additionally it handles suppression and aliases management.`interface Context` |
| [CorrectableCodeSmell](-correctable-code-smell/index.html) | Represents a code smell for that can be auto corrected.`open class CorrectableCodeSmell : `[`CodeSmell`](-code-smell/index.html) |
| [Debt](-debt/index.html) | Debt describes the estimated amount of work needed to fix a given issue.`data class Debt` |
| [DefaultContext](-default-context/index.html) | Default [Context](-context/index.html) implementation.`open class DefaultContext : `[`Context`](-context/index.html) |
| [Detektion](-detektion/index.html) | Storage for all kinds of findings and additional information which needs to be transferred from the detekt engine to the user.`interface Detektion` |
| [DetektVisitor](-detekt-visitor/index.html) | Basic visitor which is used inside detekt. Guarantees a better looking name as the extended base class :).`open class DetektVisitor : KtTreeVisitorVoid` |
| [Entity](-entity/index.html) | Stores information about a specific code fragment.`data class Entity : `[`Compactable`](-compactable/index.html) |
| [Extension](-extension/index.html) | Defines extension points in detekt. Currently supported extensions are:`interface Extension` |
| [FileProcessListener](-file-process-listener/index.html) | Gather additional metrics about the analyzed kotlin file. Pay attention to the thread policy of each function!`interface FileProcessListener : `[`Extension`](-extension/index.html) |
| [Finding](-finding/index.html) | Base interface of detection findings. Inherits a bunch of useful behaviour from sub interfaces.`interface Finding : `[`Compactable`](-compactable/index.html)`, `[`HasEntity`](-has-entity/index.html)`, `[`HasMetrics`](-has-metrics/index.html) |
| [HasEntity](-has-entity/index.html) | Describes a source code position.`interface HasEntity` |
| [HasMetrics](-has-metrics/index.html) | Adds metric container behaviour.`interface HasMetrics` |
| [Issue](-issue/index.html) | An issue represents a problem in the codebase.`data class Issue` |
| [LazyRegex](-lazy-regex/index.html) | LazyRegex class provides a lazy evaluation of a Regex pattern for usages inside Rules. It computes the value once when reaching the point of its usage and returns the same value when requested again.`class LazyRegex : `[`ReadOnlyProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.properties/-read-only-property/index.html)`<`[`Rule`](-rule/index.html)`, `[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)`>` |
| [Location](-location/index.html) | Specifies a position within a source code fragment.`data class Location : `[`Compactable`](-compactable/index.html) |
| [Metric](-metric/index.html) | Metric type, can be an integer or double value. Internally it is stored as an integer, but the conversion factor and is double attributes can be used to retrieve it as a double value.`data class Metric` |
| [MultiRule](-multi-rule/index.html) | Composite rule which delegates work to child rules. Can be used to combine different rules which do similar work like scanning the source code line by line to increase performance.`abstract class MultiRule : `[`BaseRule`](../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.html) |
| [Notification](-notification/index.html) | Any kind of notification which should be printed to the console. For example when using the formatting rule set, any change to your kotlin file is a notification.`interface Notification` |
| [OutputReport](-output-report/index.html) | Translates detekt's result container - [Detektion](-detektion/index.html) - into an output report which is written inside a file.`abstract class OutputReport : `[`Extension`](-extension/index.html) |
| [ProjectMetric](-project-metric/index.html) | Anything that can be expressed as a numeric value for projects.`open class ProjectMetric` |
| [PropertiesAware](-properties-aware/index.html) | Properties holder. Allows to store and retrieve any data.`interface PropertiesAware` |
| [ReportingExtension](-reporting-extension/index.html) | Allows to intercept detekt's result container by listening to the initial and final state and manipulate the reported findings.`interface ReportingExtension : `[`Extension`](-extension/index.html) |
| [Rule](-rule/index.html) | A rule defines how one specific code structure should look like. If code is found which does not meet this structure, it is considered as harmful regarding maintainability or readability.`abstract class Rule : `[`BaseRule`](../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.html)`, `[`ConfigAware`](-config-aware/index.html) |
| [RuleId](-rule-id.html) | The type to use when referring to rule ids giving it more context then a String would.`typealias RuleId = `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [RuleSet](-rule-set/index.html) | A rule set is a collection of rules and must be defined within a rule set provider implementation.`class RuleSet` |
| [RuleSetId](-rule-set-id.html) | `typealias RuleSetId = `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [RuleSetProvider](-rule-set-provider/index.html) | A rule set provider, as the name states, is responsible for creating rule sets.`interface RuleSetProvider` |
| [SetupContext](-setup-context/index.html) | Context providing useful processing settings to initialize extensions.`interface SetupContext : `[`PropertiesAware`](-properties-aware/index.html) |
| [Severity](-severity/index.html) | Rules can classified into different severity grades. Maintainer can choose a grade which is most harmful to their projects.`enum class Severity` |
| [SingleAssign](-single-assign/index.html) | Allows to assign a property just once. Further assignments result in [IllegalStateException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-state-exception/index.html)'s.`class SingleAssign<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>` |
| [SourceLocation](-source-location/index.html) | Stores line and column information of a location.`data class SourceLocation` |
| [SplitPattern](-split-pattern/index.html) | Splits given text into parts and provides testing utilities for its elements. Basic use cases are to specify different function or class names in the detekt yaml config and test for their appearance in specific rules.`open class SplitPattern` |
| [TextLocation](-text-location/index.html) | Stores character start and end positions of an text file.`data class TextLocation` |
| [ThresholdedCodeSmell](-thresholded-code-smell/index.html) | Represents a code smell for which a specific metric can be determined which is responsible for the existence of this rule violation.`open class ThresholdedCodeSmell : `[`CodeSmell`](-code-smell/index.html) |
| [ThresholdRule](-threshold-rule/index.html) | Provides a threshold attribute for this rule, which is specified manually for default values but can be also obtained from within a configuration object.`abstract class ThresholdRule : `[`Rule`](-rule/index.html) |

### Annotations

| [UnstableApi](-unstable-api/index.html) | Experimental detekt api which may change on minor or patch versions.`annotation class UnstableApi` |

### Extensions for External Classes

| [kotlin.String](kotlin.-string/index.html) |  |

### Properties

| [DEFAULT_FLOAT_CONVERSION_FACTOR](-d-e-f-a-u-l-t_-f-l-o-a-t_-c-o-n-v-e-r-s-i-o-n_-f-a-c-t-o-r.html) | To represent a value of 0.5, use the metric value 50 and the conversion factor of 100. (50 / 100 = 0.5)`const val DEFAULT_FLOAT_CONVERSION_FACTOR: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| [getOrNull](get-or-null.html) | Allows to retrieve stored properties in a type safe way.`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> `[`PropertiesAware`](-properties-aware/index.html)`.getOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): T?` |


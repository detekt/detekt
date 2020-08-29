---
title: alltypes - detekt-api
---

### All Types

|

##### [io.gitlab.arturbosch.detekt.api.AnnotationExcluder](../io.gitlab.arturbosch.detekt.api/-annotation-excluder/index.html)

Primary use case for an AnnotationExcluder is to decide if a KtElement should be
excluded from further analysis. This is done by checking if a special annotation
is present over the element.


|

##### [io.gitlab.arturbosch.detekt.api.internal.BaseConfig](../io.gitlab.arturbosch.detekt.api.internal/-base-config/index.html)

Convenient base configuration which parses/casts the configuration value based on the type of the default value.


|

##### [io.gitlab.arturbosch.detekt.api.internal.BaseRule](../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.html)

Defines the visiting mechanism for KtFile's.


|

##### [io.gitlab.arturbosch.detekt.api.CodeSmell](../io.gitlab.arturbosch.detekt.api/-code-smell/index.html)

A code smell indicates any possible design problem inside a program's source code.
The type of a code smell is described by an [Issue](../io.gitlab.arturbosch.detekt.api/-issue/index.html).


|

##### [io.gitlab.arturbosch.detekt.api.internal.CommaSeparatedPattern](../io.gitlab.arturbosch.detekt.api.internal/-comma-separated-pattern/index.html)


|

##### [io.gitlab.arturbosch.detekt.api.Compactable](../io.gitlab.arturbosch.detekt.api/-compactable/index.html)

Provides a compact string representation.


|

##### [io.gitlab.arturbosch.detekt.api.internal.CompilerResources](../io.gitlab.arturbosch.detekt.api.internal/-compiler-resources/index.html)

Provides compiler resources.


|

##### [io.gitlab.arturbosch.detekt.api.internal.CompositeConfig](../io.gitlab.arturbosch.detekt.api.internal/-composite-config/index.html)

Wraps two different configuration which should be considered when retrieving properties.


|

##### [io.gitlab.arturbosch.detekt.api.Config](../io.gitlab.arturbosch.detekt.api/-config/index.html)

A configuration holds information about how to configure specific rules.


|

##### [io.gitlab.arturbosch.detekt.api.ConfigAware](../io.gitlab.arturbosch.detekt.api/-config-aware/index.html)

Interface which is implemented by each Rule class to provide
utility functions to retrieve specific or generic properties
from the underlying detekt configuration file.


|

##### [io.gitlab.arturbosch.detekt.api.ConfigValidator](../io.gitlab.arturbosch.detekt.api/-config-validator/index.html)

An extension which allows users to validate parts of the configuration.


|

##### [io.gitlab.arturbosch.detekt.api.ConsoleReport](../io.gitlab.arturbosch.detekt.api/-console-report/index.html)

Extension point which describes how findings should be printed on the console.


|

##### [io.gitlab.arturbosch.detekt.api.Context](../io.gitlab.arturbosch.detekt.api/-context/index.html)

A context describes the storing and reporting mechanism of [Finding](../io.gitlab.arturbosch.detekt.api/-finding/index.html)'s inside a [Rule](../io.gitlab.arturbosch.detekt.api/-rule/index.html).
Additionally it handles suppression and aliases management.


|

##### [io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell](../io.gitlab.arturbosch.detekt.api/-correctable-code-smell/index.html)

Represents a code smell for that can be auto corrected.


|

##### [io.gitlab.arturbosch.detekt.api.Debt](../io.gitlab.arturbosch.detekt.api/-debt/index.html)

Debt describes the estimated amount of work needed to fix a given issue.


|

##### [io.gitlab.arturbosch.detekt.api.DefaultContext](../io.gitlab.arturbosch.detekt.api/-default-context/index.html)

Default [Context](../io.gitlab.arturbosch.detekt.api/-context/index.html) implementation.


|

##### [io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider](../io.gitlab.arturbosch.detekt.api.internal/-default-rule-set-provider.html)

Interface which marks sub-classes as provided by detekt via the rules sub-module.


|

##### [io.gitlab.arturbosch.detekt.api.Detektion](../io.gitlab.arturbosch.detekt.api/-detektion/index.html)

Storage for all kinds of findings and additional information
which needs to be transferred from the detekt engine to the user.


|

##### [io.gitlab.arturbosch.detekt.api.DetektVisitor](../io.gitlab.arturbosch.detekt.api/-detekt-visitor/index.html)

Basic visitor which is used inside detekt.
Guarantees a better looking name as the extended base class :).


|

##### [io.gitlab.arturbosch.detekt.api.internal.DisabledAutoCorrectConfig](../io.gitlab.arturbosch.detekt.api.internal/-disabled-auto-correct-config/index.html)


|

##### [io.gitlab.arturbosch.detekt.api.Entity](../io.gitlab.arturbosch.detekt.api/-entity/index.html)

Stores information about a specific code fragment.


|

##### [io.gitlab.arturbosch.detekt.api.Extension](../io.gitlab.arturbosch.detekt.api/-extension/index.html)

Defines extension points in detekt.
Currently supported extensions are:


|

##### [io.gitlab.arturbosch.detekt.api.internal.FailFastConfig](../io.gitlab.arturbosch.detekt.api.internal/-fail-fast-config/index.html)


|

##### [io.gitlab.arturbosch.detekt.api.FileProcessListener](../io.gitlab.arturbosch.detekt.api/-file-process-listener/index.html)

Gather additional metrics about the analyzed kotlin file.
Pay attention to the thread policy of each function!


|

##### [io.gitlab.arturbosch.detekt.api.Finding](../io.gitlab.arturbosch.detekt.api/-finding/index.html)

Base interface of detection findings. Inherits a bunch of useful behaviour
from sub interfaces.


|

##### [io.gitlab.arturbosch.detekt.api.HasEntity](../io.gitlab.arturbosch.detekt.api/-has-entity/index.html)

Describes a source code position.


|

##### [io.gitlab.arturbosch.detekt.api.HasMetrics](../io.gitlab.arturbosch.detekt.api/-has-metrics/index.html)

Adds metric container behaviour.


|

##### [io.gitlab.arturbosch.detekt.api.Issue](../io.gitlab.arturbosch.detekt.api/-issue/index.html)

An issue represents a problem in the codebase.


| (extensions in package io.gitlab.arturbosch.detekt.api.internal)

##### [org.jetbrains.kotlin.psi.KtAnnotated](../io.gitlab.arturbosch.detekt.api.internal/org.jetbrains.kotlin.psi.-kt-annotated/index.html)


| (extensions in package io.gitlab.arturbosch.detekt.api.internal)

##### [org.jetbrains.kotlin.psi.KtElement](../io.gitlab.arturbosch.detekt.api.internal/org.jetbrains.kotlin.psi.-kt-element/index.html)


|

##### [io.gitlab.arturbosch.detekt.api.LazyRegex](../io.gitlab.arturbosch.detekt.api/-lazy-regex/index.html)

LazyRegex class provides a lazy evaluation of a Regex pattern for usages inside Rules.
It computes the value once when reaching the point of its usage and returns the same
value when requested again.


|

##### [io.gitlab.arturbosch.detekt.api.Location](../io.gitlab.arturbosch.detekt.api/-location/index.html)

Specifies a position within a source code fragment.


|

##### [io.gitlab.arturbosch.detekt.api.Metric](../io.gitlab.arturbosch.detekt.api/-metric/index.html)

Metric type, can be an integer or double value. Internally it is stored as an integer,
but the conversion factor and is double attributes can be used to retrieve it as a double value.


|

##### [io.gitlab.arturbosch.detekt.api.MultiRule](../io.gitlab.arturbosch.detekt.api/-multi-rule/index.html)

Composite rule which delegates work to child rules.
Can be used to combine different rules which do similar work like
scanning the source code line by line to increase performance.


|

##### [io.gitlab.arturbosch.detekt.api.Notification](../io.gitlab.arturbosch.detekt.api/-notification/index.html)

Any kind of notification which should be printed to the console.
For example when using the formatting rule set, any change to
your kotlin file is a notification.


|

##### [io.gitlab.arturbosch.detekt.api.OutputReport](../io.gitlab.arturbosch.detekt.api/-output-report/index.html)

Translates detekt's result container - [Detektion](../io.gitlab.arturbosch.detekt.api/-detektion/index.html) - into an output report
which is written inside a file.


|

##### [io.gitlab.arturbosch.detekt.api.internal.PathFilters](../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.html)


|

##### [io.gitlab.arturbosch.detekt.api.ProjectMetric](../io.gitlab.arturbosch.detekt.api/-project-metric/index.html)

Anything that can be expressed as a numeric value for projects.


|

##### [io.gitlab.arturbosch.detekt.api.PropertiesAware](../io.gitlab.arturbosch.detekt.api/-properties-aware/index.html)

Properties holder. Allows to store and retrieve any data.


|

##### [io.gitlab.arturbosch.detekt.api.ReportingExtension](../io.gitlab.arturbosch.detekt.api/-reporting-extension/index.html)

Allows to intercept detekt's result container by listening to the initial and final state
and manipulate the reported findings.


|

##### [io.gitlab.arturbosch.detekt.api.Rule](../io.gitlab.arturbosch.detekt.api/-rule/index.html)

A rule defines how one specific code structure should look like. If code is found
which does not meet this structure, it is considered as harmful regarding maintainability
or readability.


|

##### [io.gitlab.arturbosch.detekt.api.RuleId](../io.gitlab.arturbosch.detekt.api/-rule-id.html)

The type to use when referring to rule ids giving it more context then a String would.


|

##### [io.gitlab.arturbosch.detekt.api.RuleSet](../io.gitlab.arturbosch.detekt.api/-rule-set/index.html)

A rule set is a collection of rules and must be defined within a rule set provider implementation.


|

##### [io.gitlab.arturbosch.detekt.api.RuleSetId](../io.gitlab.arturbosch.detekt.api/-rule-set-id.html)


|

##### [io.gitlab.arturbosch.detekt.api.RuleSetProvider](../io.gitlab.arturbosch.detekt.api/-rule-set-provider/index.html)

A rule set provider, as the name states, is responsible for creating rule sets.


|

##### [io.gitlab.arturbosch.detekt.api.SetupContext](../io.gitlab.arturbosch.detekt.api/-setup-context/index.html)

Context providing useful processing settings to initialize extensions.


|

##### [io.gitlab.arturbosch.detekt.api.Severity](../io.gitlab.arturbosch.detekt.api/-severity/index.html)

Rules can classified into different severity grades. Maintainer can choose
a grade which is most harmful to their projects.


|

##### [io.gitlab.arturbosch.detekt.api.internal.SimpleNotification](../io.gitlab.arturbosch.detekt.api.internal/-simple-notification/index.html)


|

##### [io.gitlab.arturbosch.detekt.api.SingleAssign](../io.gitlab.arturbosch.detekt.api/-single-assign/index.html)

Allows to assign a property just once.
Further assignments result in [IllegalStateException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-state-exception/index.html)'s.


|

##### [io.gitlab.arturbosch.detekt.api.SourceLocation](../io.gitlab.arturbosch.detekt.api/-source-location/index.html)

Stores line and column information of a location.


|

##### [io.gitlab.arturbosch.detekt.api.SplitPattern](../io.gitlab.arturbosch.detekt.api/-split-pattern/index.html)

Splits given text into parts and provides testing utilities for its elements.
Basic use cases are to specify different function or class names in the detekt
yaml config and test for their appearance in specific rules.


| (extensions in package io.gitlab.arturbosch.detekt.api)

##### [kotlin.String](../io.gitlab.arturbosch.detekt.api/kotlin.-string/index.html)


|

##### [io.gitlab.arturbosch.detekt.api.TextLocation](../io.gitlab.arturbosch.detekt.api/-text-location/index.html)

Stores character start and end positions of an text file.


|

##### [io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell](../io.gitlab.arturbosch.detekt.api/-thresholded-code-smell/index.html)

Represents a code smell for which a specific metric can be determined which is responsible
for the existence of this rule violation.


|

##### [io.gitlab.arturbosch.detekt.api.ThresholdRule](../io.gitlab.arturbosch.detekt.api/-threshold-rule/index.html)

Provides a threshold attribute for this rule, which is specified manually for default values
but can be also obtained from within a configuration object.


|

##### [io.gitlab.arturbosch.detekt.api.UnstableApi](../io.gitlab.arturbosch.detekt.api/-unstable-api/index.html)

Experimental detekt api which may change on minor or patch versions.


|

##### [io.gitlab.arturbosch.detekt.api.internal.ValidatableConfiguration](../io.gitlab.arturbosch.detekt.api.internal/-validatable-configuration/index.html)


|

##### [io.gitlab.arturbosch.detekt.api.internal.YamlConfig](../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/index.html)

Config implementation using the yaml format. SubConfigurations can return sub maps according to the
yaml specification.



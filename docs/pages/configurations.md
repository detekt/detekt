---
title: "Detekt Configuration File"
keywords: config configuration yaml
sidebar:
permalink: configurations.html
summary:
---

_detekt_ uses a [YAML style configuration](https://yaml.org/spec/1.2/spec.html) file for various things:

- rule set and rule properties
- build failure
- console reports
- output reports
- processors

See the [default-detekt-config.yml](https://github.com/detekt/detekt/blob/main/detekt-core/src/main/resources/default-detekt-config.yml)
file for all defined configuration options and their default values. 

_Note:_ When using a custom config file, the default values are ignored unless you also set the `--build-upon-default-config` flag.

## Rule sets and rules

_detekt_ allows easily to just pick the rules you want and configure them the way you like.
For example if you want to allow up to 20 functions inside a Kotlin file instead of the default threshold, write:

```yaml
complexity:
  TooManyFunctions:
    thresholdInFiles: 20
```

To read about all supported rule sets and rules, use the side navigation `Rule Sets`.

### Path Filters / Excludes / Includes

Fine grained path filters can be defined for each rule or rule set through globbing patterns.
This gives the user more freedom in analyzing only specific files
and rule authors the ability to write *library only* rules.

```yaml
complexity:
  TooManyFunctions:
    ...
    excludes: ['**/internal/**']
    includes: ['**/internal/util/NeedsToBeChecked.kt']
```

In case you want to apply the same filters for different rules, you can use
[YAML anchors and aliases](https://yaml.org/spec/1.2/spec.html#id2785586) to reapply previously defined paths.

```yaml
naming:
  ClassNaming:
    ...
    excludes: &testFolders
      - '**/test/**'
      - '**/androidTest/**'
  ConstructorParameterNaming:
    ...
    excludes: *testFolders
```

## Build failure

_Detekt_ supports the option to fail your build if a threshold of code smell issues is met.

For this the following code must be inside the detekt config:

```yaml
build:
  maxIssues: 10 # break the build if more than ten weighted issues are found
  weights:
    complexity: 2 # every rule of the complexity rule set should count as if two issues were found...
    LongParameterList: 1 # ...with the exception of the LongParameterList rule.
    comments: 0 # comment rules are just a nice to know?!
```

Every rule and rule set can be attached with an integer value which is the weight of the finding.
For example: If you have 5 findings of the category _complexity_, then your failThreshold of 10 is reached as
5 x 2 = 10. 

Weights are respected in the following priority order:
- The specified weight for a rule
- The specified weight for a rule set
- By default, the weight is 1.

## Console Reports

Uncomment the reports you don't care about.

```yaml
console-reports:
  active: true
  exclude:
  #  - 'ProjectStatisticsReport'
  #  - 'ComplexityReport'
  #  - 'NotificationReport'
  #  - 'FindingsReport'
  #  - 'FileBasedFindingsReport'
  #  - 'LiteFindingsReport'
```

**ProjectStatisticsReport** contains metrics and statistics concerning the analyzed project sorted by priority.

**ComplexityReport** contains metrics concerning the analyzed code. 
For instance the source lines of code and the McCabe complexity are calculated.

**NotificationReport** contains notifications reported by the detekt analyzer similar to push notifications. 
It's simply a way of alerting users to information that they have opted-in to.

**FindingsReport** contains all rule violations in a list format grouped by ruleset.

**FileBasedFindingsReport** is similar to the FindingsReport shown above. 
The rule violations are grouped by file location.

## Output Reports

Uncomment the reports you don't care about. The detailed description can be found in [reporting](reporting.md).

```yaml
output-reports:
  active: true
  exclude:
  #  - 'HtmlOutputReport'
  #  - 'TxtOutputReport'
  #  - 'XmlOutputReport'
  #  - 'SarifOutputReport'
```


## Processors

Count processors are used to calculate project metrics.
For example, when all count processors are enabled, a detekt html report might look like this:

![Processor metrics in html report](../images/processor_metrics_in_html_report.png)

The `'DetektProgressListener'` processor shows a progress indicator in stdout while a detekt process is running.

Uncomment the processors you don't care about.

```yaml
processors:
    active: true
    exclude:
        - 'DetektProgressListener'
        # - 'KtFileCountProcessor'
        # - 'PackageCountProcessor'
        # - 'ClassCountProcessor'
        # - 'FunctionCountProcessor'
        # - 'PropertyCountProcessor'
        # - 'ProjectComplexityProcessor'
        # - 'ProjectCognitiveComplexityProcessor'
        # - 'ProjectLLOCProcessor'
        # - 'ProjectCLOCProcessor'
        # - 'ProjectLOCProcessor'
        # - 'ProjectSLOCProcessor'
        # - 'LicenseHeaderLoaderExtension'
```

## Config JSON Schema

A JSON Schema for the config file is available on: [json.schemastore.org/detekt](https://json.schemastore.org/detekt).

You can configure your IDE (e.g. IntelliJ or Android Studio have built-in support)
to use that schema to give you **autocompletion** capabilities on your config file.
More details on the IntelliJ support are available
[on this page](https://www.jetbrains.com/help/ruby/yaml.html#remote_json).

![JSON Schema validator on IntelliJ](../images/json_schema_validator_intellij.png)

The JSON Schema is currently not automatically generated. It can be updated manually [on this repository](https://github.com/SchemaStore/schemastore).

---
title: "Detekt Configuration File"
keywords: config configuration yaml
sidebar:
permalink: configurations.html
summary:
---

_detekt_ uses a yaml style configuration file for various things:

- rule set and rule properties
- build failure
- Kotlin file processors
- console and output formats

See the [default-detekt-config.yml](https://github.com/detekt/detekt/blob/master/detekt-core/src/main/resources/default-detekt-config.yml) file for all defined configuration options and their default values. 

_Note:_ When using a custom config file, the default values are ignored unless you also set the `--build-upon-default-config` flag.

#### Rule sets and rules

_detekt_ allows easily to just pick the rules you want and configure them the way you like.
For example if you want to allow up to 20 functions inside a Kotlin file instead of the default threshold of 10, write:
```
complexity:
  TooManyFunctions:
    threshold: 20
```

To read about all supported rule sets and rules, use the side navigation `Rule Sets`.

#### Path Filters / Excludes / Includes

Starting with version **RC15** fine grained path filters can be defined for each rule or rule set:

```
complexity:
  TooManyFunctions:
    ...
    excludes: ['**/internal/**']
    includes: ['**/internal/util/NeedsToBeChecked.kt']
```

This gives the user more freedom in analyzing only specific files
and rule authors the ability to write *library only* rules.
This is the replacement for the `test-pattern` feature.

Be aware that detekt now expects globing patterns instead of regular expressions!

#### Console and Output Reports

Uncomment the reporters you don't care about.

```yaml
console-reports:
  active: true
  exclude:
  #  - 'ProjectStatisticsReport'
  #  - 'ComplexityReport'
  #  - 'NotificationReport'
  #  - 'FindingsReport'
  #  - 'FileBasedFindingsReport'
  #  - 'BuildFailureReport'

output-reports:
  active: true
  exclude:
  #  - 'HtmlOutputReport'
  #  - 'TxtOutputReport'
  #  - 'XmlOutputReport'
```

#### Processors

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
        # - 'FunctionCountProcessor'
        # - 'PropertyCountProcessor'
        # - 'ClassCountProcessor'
        # - 'PackageCountProcessor'
        # - 'KtFileCountProcessor'
```

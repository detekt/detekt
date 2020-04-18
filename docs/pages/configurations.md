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

See the [default-detekt-config.yml](https://github.com/arturbosch/detekt/blob/master/detekt-cli/src/main/resources/default-detekt-config.yml) file for all defined configuration options and their default values. 

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

Processors are usually used to raise project metrics.
Uncomment the ones you do not care about.

```yaml
processors:
  active: true
  exclude:
  # - 'FunctionCountProcessor'
  # - 'PropertyCountProcessor'
  # - 'ClassCountProcessor'
  # - 'PackageCountProcessor'
  # - 'KtFileCountProcessor'
```

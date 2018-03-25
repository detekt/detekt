---
title: "Detekt Configuration File"
keywords: config configuration yaml
tags: 
sidebar: 
permalink: configurations.html
summary:
---

_detekt_ uses a yaml style configuration file for various things:

- rule set and rule properties
- build failure
- test-pattern to exclude rule sets/rules for test classes
- processors
- console and output formats 
- autoCorrect and failfast support

See the [default-detekt-config.yml](https://github.com/arturbosch/detekt/blob/master/detekt-cli/src/main/resources/default-detekt-config.yml) file for all defined configuration options and their default values.

#### Rule sets and rules

_detekt_ allows easily to just pick the rules you want and configure them the way you like.  
For example if you want to allow 20 functions inside a kotlin file instead of the default threshold of 10, write:
```
complexity:
  TooManyFunctions:
    threshold: 20
```

To read about all supported rule sets and rules, use the side navigation `Rule Sets`.
 
#### Test-Pattern

The configuration file contains a whole section to treat test code diffently.  
Specify test patterns to detect test code and exclude rules or rule sets for them. 

```yaml
test-pattern: # Configure exclusions for test sources
  active: true
  patterns: # Test file regexes
    - '.*/test/.*'
    - '.*Test.kt'
    - '.*Spec.kt'
  exclude-rule-sets:
    - 'comments'
  exclude-rules:
    - 'NamingRules'
    - 'WildcardImport'
    - 'MagicNumber'
    - 'MaxLineLength'
    - 'LateinitUsage'
    - 'StringLiteralDuplication'
    - 'SpreadOperator'
    - 'TooManyFunctions'

```

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
  #  - 'BuildFailureReport'

output-reports:
  active: true
  exclude:
  #  - 'HtmlOutputReport'
  #  - 'PlainOutputReport'
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
 
#### failfast property

Set `failFast: true` in your detekt.yml configuration file.  
As a result, every rule will be enabled and `maxIssues` will be set to 0.  
Weights can then be ignored and left untouched.  


#### autoCorrect property

This option is still present due to legacy reasons. In the first milestone releases detekt also formatted kotlin code.
Within the detekt team we decided to not mess with user code and let other tools do the formatting eg. intellij or KtLint.
Still as detekt can be extended with custom rules, you are free to write rules which support auto correction.
Only write correcting code within the `withAutoCorrect()`-function.

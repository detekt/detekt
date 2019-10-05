# Roadmap 

This document is deprecated.
Please follow https://github.com/arturbosch/detekt/milestones
for current roadmap based on milestone feature of GitHub.

## ~ 1.0.0

### todo

- release RC15 and fix regressions

### done

- Overall improve documentation of detekt.
- We now have a website: arturbosch.github.io/detekt
- Refine threshold definition
- an output format (like checkstyle etc - thx to @winterDroid)
- Resolve `formatting` issues, integrate ktlint fixes OR `integrate ktlint` into detekt for formatting
- Resolve `java9` java.xml.bind issue
- `gradle-plugin` should support `profiles` for yaml configurations -> different rules for different source sets eg. test
- `jcenter` publishing
- Windows support!
- `sonar-plugin` (has it's own space [here](https://github.com/arturbosch/sonar-kotlin))
- Allow to exclude rules or rule sets for test sources
- figure out how kotlinc/intellij does type and symbol resolution
- rework gradle plugin to behave like other static analysis tools
- `no new complex rules` (maybe only some easy or contributed ones)

## > 1.0.0

- finish `FeatureEnvy` rule -> needs type resolution

## Ideas for new major versions

### Finished or Started

- `idea-plugin` -> https://github.com/arturbosch/detekt-intellij-plugin
- `jenkins-plugin` -> The [`Jenkins Warnings Plugin - Next Generation`](https://github.com/jenkinsci/warnings-ng-plugin) supports importing `detekt`'s xml format.

## Won't do

- implement formatting rules, instead please use:
    - `detekt-formatting`, a wrapper over KtLint
	- [KtLint](https://github.com/pinterest/ktlint)
	- [IntelliJ's format.sh](https://www.jetbrains.com/help/idea/command-line-formatter.html)
	- or `detektIdeaFormat` and `detektIdeaInspect` wrapper over a local intellij

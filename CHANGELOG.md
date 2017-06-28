# Detekt - Changelog

#### M12.1 & M12.2

- Convert Kotlin source code strings to Unix line endings - [#137](https://github.com/arturbosch/detekt/pull/137)
- Simplify reading resource files - [#136](https://github.com/arturbosch/detekt/pull/136)
- [WIP] Windows support - [#135](https://github.com/arturbosch/detekt/pull/135)
- M12.1 fails with "URISyntaxException: Illegal character in authority at index 7" under windows - [#128](https://github.com/arturbosch/detekt/issues/128)
- Rule to find `lateinit` usages - [#127](https://github.com/arturbosch/detekt/pull/127)
- Wrong link format in Changelog - [#121](https://github.com/arturbosch/detekt/issues/121)
- Setup appveyor for windows builds - [#118](https://github.com/arturbosch/detekt/issues/118)
- Duplicate main profile - [#116](https://github.com/arturbosch/detekt/issues/116)
- Referring to custom detekt.yml config results in InvalidPathExceptionon Windows - [#115](https://github.com/arturbosch/detekt/issues/115)
- MaxLineLength should allow excluding import and package statements - [#111](https://github.com/arturbosch/detekt/issues/111)
- Rule: Prohibite usage of `lateinit` - [#106](https://github.com/arturbosch/detekt/issues/106)

See all issues at: [M12.2](https://github.com/arturbosch/detekt/milestone/9)

#### M12

- Suppress for TooManyFunctions is not considered in detekt - [#108](https://github.com/arturbosch/detekt/issues/108)
- Update documentation and migration guide for M12 - [#105](https://github.com/arturbosch/detekt/issues/105)
- NoDocOverPublicClass always reported for objects - [#104](https://github.com/arturbosch/detekt/issues/104)
- Script to generate release notes from milestone - [#102](https://github.com/arturbosch/detekt/issues/102)
- Encapsulate common fields of Rule and Finding in Issue class - [#97](https://github.com/arturbosch/detekt/issues/97)
- Separate findings logic from rule logic - [#93](https://github.com/arturbosch/detekt/issues/93)
- Add support for composable configurations - [#92](https://github.com/arturbosch/detekt/issues/92)
- ClassCastException: java.lang.Integer cannot be cast to java.lang.String - [#89](https://github.com/arturbosch/detekt/issues/89)
- Baseline does not work anymore as expected due to inner restructure for the new output formats - [#83](https://github.com/arturbosch/detekt/issues/83)
- Prevent ClassCastExceptions in Configurations - [#82](https://github.com/arturbosch/detekt/issues/82)
- Migrate away from load() method in tests - [#77](https://github.com/arturbosch/detekt/issues/77)
- Support any common report file format - [#66](https://github.com/arturbosch/detekt/issues/66)
- Not all return keywords are removed when using ExpressionBodySyntax quickfix - [#58](https://github.com/arturbosch/detekt/issues/58)
- Rule: Max line length - [#56](https://github.com/arturbosch/detekt/issues/56)
- Rule: SingleExpression statements with multiple return paths - [#45](https://github.com/arturbosch/detekt/issues/45)
- Allow different naming conventions for tests - [#24](https://github.com/arturbosch/detekt/issues/24)

See all issues at: [M12](https://github.com/arturbosch/detekt/milestone/8)

#### M11

- False positive SpacingAfterKeyword - [#71](https://github.com/arturbosch/detekt/issues/71)
- Embedabble compiler - [#70](https://github.com/arturbosch/detekt/pull/70)
- Support for Android? Failed to apply plugin [id 'com.android.application'] - [#69](https://github.com/arturbosch/detekt/issues/69)
- Add gradle task to integrate idea formatting/inspection - [#67](https://github.com/arturbosch/detekt/issues/67)
- Crash when detekt.yml is empty - [#64](https://github.com/arturbosch/detekt/issues/64)
- Add Support For GSK - [#59](https://github.com/arturbosch/detekt/issues/59)
- Decouple/Rewrite/Publish smell-baseline-format - [#57](https://github.com/arturbosch/detekt/issues/57)
- Export Config with `--generate-config` - [#54](https://github.com/arturbosch/detekt/pull/54)
- Support indentation formatting in different formats (X spaces, x spaces for tabs etc) - [#53](https://github.com/arturbosch/detekt/issues/53)
- NestedBlockDepth: Elif-Structure counts as two - [#51](https://github.com/arturbosch/detekt/issues/51)
- Generate default yaml configuration on cli flag - [#48](https://github.com/arturbosch/detekt/issues/48)
- Support @Suppress("ALL") - [#47](https://github.com/arturbosch/detekt/issues/47)
- [WIP] Formatting rework - [#46](https://github.com/arturbosch/detekt/pull/46)
- Rule: Expression-syntax line breaks - [#36](https://github.com/arturbosch/detekt/issues/36)
- Rule: Expression syntax - [#35](https://github.com/arturbosch/detekt/issues/35)
- Allow Indentation check to handle "align when multiline" option - [#25](https://github.com/arturbosch/detekt/issues/25)
- SpacingAroundCurlyBraces throw IndexOutOfFound when determinating Location.of(node) as LineAndColumn calculation is simple wrong (of Idea?) - [#18](https://github.com/arturbosch/detekt/issues/18)


See all issues at: [M11](https://github.com/arturbosch/detekt/milestone/5)  
See all issues at: [Formatting](https://github.com/arturbosch/detekt/milestone/6)

#### M10

- detekt-gradle-plugin - [#16](https://github.com/arturbosch/detekt/issues/16)
- experimental migration module which can migrate imports- [#30](https://github.com/arturbosch/detekt/issues/30)
- NamingConventionViolation is now aware about backticks - [contributed by Svyatoslav Chatchenko](https://github.com/arturbosch/detekt/pull/34) 
- cli and core module refactorings, jcenter publishing, travis ci
- gradle-plugin version is not configurable, task configurations should be in `afterEvaluate` - [#41](https://api.github.com/repos/arturbosch/detekt/issues/41)
- Add Contributing Guide - [#37](https://api.github.com/repos/arturbosch/detekt/issues/37)
- NamingConventionViolation is now aware about backticks “`” - [#34](https://api.github.com/repos/arturbosch/detekt/issues/34)

See all issues at: [M10](https://github.com/arturbosch/detekt/milestone/4)  
See all issues at: [M10.1](https://api.github.com/repos/arturbosch/detekt/milestones/7)
#### M9

- Support suppressing rules (@SuppressWarnings, @Suppress) - [#6](https://github.com/arturbosch/detekt/issues/6)
- Allow easier navigation in README - [#24](https://github.com/arturbosch/detekt/issues/24)

See all issues at: [M9](https://github.com/arturbosch/detekt/milestone/2)

#### M8, M8.1

##### feature

- Introduce complexity ruleset - [#4](https://github.com/arturbosch/detekt/issues/4)
- Provide a new screenshot showing detekt in action [#13](https://github.com/arturbosch/detekt/issues/13)
- Update Readme/Rulesets for changes in code-smell/complexity rulesets [#14](https://github.com/arturbosch/detekt/issues/14)
- NamingConventionViolation should allow customization [#20](https://github.com/arturbosch/detekt/issues/20) 
<!-- - Implement FeatureEnvy rule - [#36](https://gitlab.com/arturbosch/detekt/issues/36)  -->

##### bugs fixed

- Prevent division by zero (thx @olivierlemasle)

See all issues at: [M8](https://github.com/arturbosch/detekt/milestone/1)

#### M7

##### defect

- Remove NoElseInWhenExpression rule [#10](https://github.com/arturbosch/detekt/issues/10)

##### feature

- As an user I want to give more weight to some rule sets than to others - [#83](https://gitlab.com/arturbosch/detekt/issues/83)
- Integrate debug rule set provider into cli - [#70](https://gitlab.com/arturbosch/detekt/issues/70)

##### feedback

- [Test] Analyze Kotlin project (30.000+ KtFiles) - [#85](https://gitlab.com/arturbosch/detekt/issues/85)

##### improvement

- Consider other than BodyExpression expressions inside current rules - [#1](https://gitlab.com/arturbosch/detekt/issues/1)

##### release

- As an user I want to get more often releases to try out the new stuff - [#87](https://gitlab.com/arturbosch/detekt/issues/87)
- Update to kotlin 1.0.6 - [#84](https://gitlab.com/arturbosch/detekt/issues/84)
- Update to kotlin 1.1.1 - [#2](https://github.com/arturbosch/detekt/issues/2)
- Get rid of hamkrest - [#9](https://github.com/arturbosch/detekt/issues/9)

See all issues at: [M7](https://gitlab.com/arturbosch/detekt/milestones/7)

#### M6

##### New features

- allow to fail builds on code smell thresholds (configurable), see 'Configure build failure thresholds' in README
- blacklist code smells which are false positives
- generate a baseline whitelist of code smells if your project is already old and has many smells, so only new
smells are shown in analysis (see 'Code Smell baseline and ignore list' in README)

##### Improvements

- move formatting to own rule set project as formatting works great on my kotlin projects but line/column calculation 
from within PsiElements is wrong (!?)
- OptionalUnit and -Semicolon should be auto correctable and moved to formatting
- show progress while waiting for the analysis

See all issues at: https://gitlab.com/arturbosch/detekt/milestones/6

#### M5

- Threat variables in object declarations as constants too - #67
- Normalize file content prevents some exceptions inside Psi - #66
- Fix an "Underestimated text length" problem inside Psi - #65
- Filter top level members from LargeClass and EqualsWithHashCode rules, preventing NoSuchElementExceptions - #64

In this milestone the goal was to fully be able to analyze the Kotlin project. 
All rule sets (except formatting) successfully run now. Formatting needs some fixes
as line and column values are sometimes not correctly determined by the AST.

One idea of mine is to threat formatting as an extern rule set and only allow auto correcting
the source code without an report to skip line/column problem.

More issues: https://gitlab.com/arturbosch/detekt/milestones/5

#### M4

- Rewrite UnusedImport-rule to fix deleting infix extension function imports - #60
- Cli flag: --parallel to compile all kotlin files in parallel, use when your project has more than 1000+ files,
 detection is always parallel - #46
- Cli flag: --format (--useTabs) to allow formatting of your source code without a detekt configuration file. 
All formatting rules are turned off except indentation with spaces if --useTabs is used - #54

More issues: https://gitlab.com/arturbosch/detekt/milestones/4

#### M3

- exceptions rule set (Too generic Exceptions thrown/catched)
- potential bugs rule set (EqualsWithHashCode, DuplicateCaseInWhen, ExplicitGarbageCollectionCall, NoElseInWhen)
- Ported formatting rules of KtLint (Credits to Shyiko)

More issues: https://gitlab.com/arturbosch/detekt/milestones/3

#### M2

- yaml configuration of rules
- empty rule set (empty block statements)
- include detekt in your projects

More issues: https://gitlab.com/arturbosch/detekt/milestones/2

#### M1

- Command line interface
- RuleSetProvider plugin system
- code smell rule set
- style rule set

More issues: https://gitlab.com/arturbosch/detekt/milestones/1

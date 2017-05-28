# Detekt - Changelog

#### M11 - upcoming

- formatting rework

#### M10, M10.1

- detekt-gradle-plugin - [#16](https://github.com/arturbosch/detekt/issues/16)
- experimental migration module which can migrate imports- [#30](https://github.com/arturbosch/detekt/issues/30)
- NamingConventionViolation is now aware about backticks - [contributed by Svyatoslav Chatchenko](https://github.com/arturbosch/detekt/pull/34) 
- cli and core module refactorings, jcenter publishing, travis ci

See all issues at: [M10](https://github.com/arturbosch/detekt/milestone/4)
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

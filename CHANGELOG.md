# Detekt - Changelog

#### RC1

- Allow to override the output name of output reports - [#272](https://github.com/arturbosch/detekt/pull/272)
- Rewrite sample project featuring processors, reports and rule sets - [#268](https://github.com/arturbosch/detekt/pull/268)
- Update detekt-sample-ruleset - [#257](https://github.com/arturbosch/detekt/issues/257)
- Fix input flag for standalone gradle task in README - [#256](https://github.com/arturbosch/detekt/pull/256)
- Added rule for safe cast to config.yml - [#254](https://github.com/arturbosch/detekt/pull/254)
- LateInitUsage: One annotation match is enough to let the property be ignored. - [#245](https://github.com/arturbosch/detekt/pull/245)
- Exclude extensions by id or priority - [#243](https://github.com/arturbosch/detekt/issues/243)
- Plugins & Extensions - [#242](https://github.com/arturbosch/detekt/pull/242)
- Rule: ModifierOrder - [#239](https://github.com/arturbosch/detekt/pull/239)
- Added metrics for packages and kt files - [#238](https://github.com/arturbosch/detekt/pull/238)
- Added metrics for classes, methods and fields - [#237](https://github.com/arturbosch/detekt/pull/237)
- Fix --project mention in README. - [#236](https://github.com/arturbosch/detekt/pull/236)
- Use newest detekt with failfast profile in CI - [#234](https://github.com/arturbosch/detekt/pull/234)
- Added rule for safe cast instead of if-else-null - [#233](https://github.com/arturbosch/detekt/pull/233)
- Terminal Output customization - [#171](https://github.com/arturbosch/detekt/issues/171)
- Change Main.rules to something like 'jars' or 'plugins' - [#134](https://github.com/arturbosch/detekt/issues/134)
- FileProcessListener's should be loaded through a ServiceLoader - [#101](https://github.com/arturbosch/detekt/issues/101)

See all issues at: [RC1](https://github.com/arturbosch/detekt/milestone/15)

#### M13.2

- Always use the 'main' profile as default even if 'profile' parameter set but a profile with name 'main' exists - [#231](https://github.com/arturbosch/detekt/issues/231)
- Fix DetektGenerateConfigTask to use --input instead of --project. - [#230](https://github.com/arturbosch/detekt/pull/230)
- Run Detekt on Detekt [#212](https://github.com/arturbosch/detekt/pull/212)
- Extend UndocumentedPublicClass with searchInNestedClass, searchInInnerClass & searchInInnerInterface properties. - [#210](https://github.com/arturbosch/detekt/pull/210)

See all issues at: [M13.2](https://github.com/arturbosch/detekt/milestone/14)

#### M13.1

- LateinitUsage: Add ignoreOnClassesPattern property. - [#226](https://github.com/arturbosch/detekt/pull/226)
- Implement ForbiddenComment Rule. - [#225](https://github.com/arturbosch/detekt/pull/225)
- Add Excludes to WildcardImport rule reusing logic from LateinitUsage - [#224](https://github.com/arturbosch/detekt/pull/224)
- Excluding specific imports from the WildcardImport rule - [#223](https://github.com/arturbosch/detekt/issues/223)
- Anonymous classes should not be checked for documentation - [#221](https://github.com/arturbosch/detekt/pull/221)
- Provide a test case for custom rule sets - [#220](https://github.com/arturbosch/detekt/pull/220)
- Update Detekt to 1.0.0.M13 and add usedDetektVersion to gradle.properties. - [#218](https://github.com/arturbosch/detekt/pull/218)
- Change gradle task parametr 'rulesets' to 'ruleSets' - [#216](https://github.com/arturbosch/detekt/pull/216)
- UndocumentedPublicClass for anonymous classes - [#213](https://github.com/arturbosch/detekt/issues/213)
- rename dept to debt - [#211](https://github.com/arturbosch/detekt/pull/211)
- LateInitUsage: Ignore this rule in tests - [#207](https://github.com/arturbosch/detekt/issues/207)
- change cli parameter --project (-p) to --input (-i) - [#206](https://github.com/arturbosch/detekt/pull/206)
- Meaning of Dept - [#205](https://github.com/arturbosch/detekt/issues/205)
- Add new line to each kotlin file. - [#204](https://github.com/arturbosch/detekt/pull/204)
- Fix unused import false positive in kdoc - closes#201 - [#203](https://github.com/arturbosch/detekt/pull/203)
- False positive UnusedImports - [#201](https://github.com/arturbosch/detekt/issues/201)
- Add Tests for WildcardImport and NamingConvention rules - [#200](https://github.com/arturbosch/detekt/pull/200)
- Allow package matching via excludeAnnotatedProperties in LateinitUsage Rule. - [#199](https://github.com/arturbosch/detekt/pull/199)
- UndocumentedPublicClass false positive with annotations - [#194](https://github.com/arturbosch/detekt/issues/194)
- Rule TodoComment - [#182](https://github.com/arturbosch/detekt/issues/182)
- Rule NewlineAtEndOfFile - [#181](https://github.com/arturbosch/detekt/issues/181)
- Fail fast approach / configuration - [#179](https://github.com/arturbosch/detekt/issues/179)
- Rule: SpreadOperator - [#167](https://github.com/arturbosch/detekt/issues/167)
- Not providing a detekt-closure or profile should not crash the gradle-plugin but instead just use the default profile - [#166](https://github.com/arturbosch/detekt/issues/166)

See all issues at: [M13.1](https://github.com/arturbosch/detekt/milestone/13)

#### M13

- Add missing unit test for Int.reached. - [#191](https://github.com/arturbosch/detekt/pull/191)
- Add failFast option to the configuration. - [#186](https://github.com/arturbosch/detekt/pull/186)
- Convert single line methods to Expression Bodys. - [#185](https://github.com/arturbosch/detekt/pull/185)
- Fix issue when default config should from the resources by the cli - [#178](https://github.com/arturbosch/detekt/pull/178)
- Rule: SpreadOperator - [#177](https://github.com/arturbosch/detekt/pull/177)
- Update readme, contributors, changelog, migration guide for M13 - [#176](https://github.com/arturbosch/detekt/issues/176)
- Rule: Expression with label - [#175](https://github.com/arturbosch/detekt/pull/175)
- Rule: Report unsafe call on nullable types - [#174](https://github.com/arturbosch/detekt/pull/174)
- Rule: Expression with label - [#173](https://github.com/arturbosch/detekt/issues/173)
- Rule: Report unsafe call on nullable types - [#172](https://github.com/arturbosch/detekt/issues/172)
- Fix off by one error in Int.reached regarding SmellThreshold. - [#170](https://github.com/arturbosch/detekt/pull/170)
- Set the group to verification on all gradle tasks. - [#168](https://github.com/arturbosch/detekt/pull/168)
- Fix a typo in CHANGELOG.md - [#165](https://github.com/arturbosch/detekt/pull/165)
- Update kotlin version to 1.1.3-2 - closes #124 - [#164](https://github.com/arturbosch/detekt/pull/164)
- Added missed brackets to repository name in readme - [#163](https://github.com/arturbosch/detekt/pull/163)
- Rule: Find usages of forEach on Ranges - [#161](https://github.com/arturbosch/detekt/pull/161)
- Running formatting checks without formatting the code on CI - [#159](https://github.com/arturbosch/detekt/issues/159)
- Remove lateinit usage in Main.kt - [#156](https://github.com/arturbosch/detekt/pull/156)
- Rule: Report forEach usages on Ranges - [#155](https://github.com/arturbosch/detekt/issues/155)
- More descriptions to for most rules - [#154](https://github.com/arturbosch/detekt/pull/154)
- Always show the absolute number of code smells - [#152](https://github.com/arturbosch/detekt/issues/152)
- Use the latest version in gradle-plugin as default - [#151](https://github.com/arturbosch/detekt/issues/151)
- Turn off auto correction for sonar analysis - [#147](https://github.com/arturbosch/detekt/pull/147)
- Fix AppVeyor badge in `README.md` - [#146](https://github.com/arturbosch/detekt/pull/146)
- README: Fix various issues with variables in code examples - [#142](https://github.com/arturbosch/detekt/pull/142)
- add descriptions to rules in style, empty, exceptions - [#140](https://github.com/arturbosch/detekt/pull/140)
- Remove lateinit in project property in Main class - [#132](https://github.com/arturbosch/detekt/issues/132)
- Should rule providers have their own package or live in the according package - [#131](https://github.com/arturbosch/detekt/issues/131)
- Issue descriptions for each rule (also displayed in `Detekt way` - sonar plugin) - [#110](https://github.com/arturbosch/detekt/issues/110)

See all issues at: [M13](https://github.com/arturbosch/detekt/milestone/12)

#### M12.1 & M12.2 & M12.3 & M12.4

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
- Rule: Prohibit usage of `lateinit` - [#106](https://github.com/arturbosch/detekt/issues/106)
- Use the latest version in gradle-plugin as default - [#151](https://github.com/arturbosch/detekt/issues/151)
- False positive for EmptyFunctionBlock with overriden function - [#151](https://github.com/arturbosch/detekt/issues/148)

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

# Detekt - Changelog

#### RC6

- Allow numbers in ClassNaming. - [#631](https://github.com/arturbosch/detekt/pull/631)
- Renamed MethodNameEqualsClassName to support properties - [#629](https://github.com/arturbosch/detekt/pull/629)
- Allow detekt to run on kts too. - [#628](https://github.com/arturbosch/detekt/pull/628)
- Updated kotlin compiler version - [#625](https://github.com/arturbosch/detekt/pull/625)
- Add TooManyFunctions to test-pattern exclude-rules by default. - [#624](https://github.com/arturbosch/detekt/pull/624)
- Consider adding TooManyFunctions to the test-pattern exclude-rules - [#622](https://github.com/arturbosch/detekt/issues/622)
- Added rule documentation to CONTRIBUTING.md - [#621](https://github.com/arturbosch/detekt/pull/621)
- Merged CHANGELOG.md and MIGRATION_GUIDE.md - [#620](https://github.com/arturbosch/detekt/pull/620)
- Do not depend on a specific project path in core classes - #605 - [#619](https://github.com/arturbosch/detekt/pull/619)
- Consider merging CHANGELOG.md and MIGRATION_GUIDE.md - [#618](https://github.com/arturbosch/detekt/issues/618)
- remove migration module - [#617](https://github.com/arturbosch/detekt/pull/617)
- Removed code-smell ruleset - [#616](https://github.com/arturbosch/detekt/pull/616)
- Remove CodeSmell RuleSet - [#615](https://github.com/arturbosch/detekt/issues/615)
- Remove migration module - [#614](https://github.com/arturbosch/detekt/issues/614)
- Allow checks on Kotlin script files (.kts) - [#612](https://github.com/arturbosch/detekt/issues/612)
- compliant and non-compliant code examples documentation - [#610](https://github.com/arturbosch/detekt/pull/610)
- Error message agrees with require condition - [#609](https://github.com/arturbosch/detekt/pull/609)
- False positive in ExceptionRaisedInUnexpectedLocation - [#608](https://github.com/arturbosch/detekt/pull/608)
- Update dependencies - [#607](https://github.com/arturbosch/detekt/pull/607)
- Checkout *.md, *.yml & *.html with LF line endings - [#606](https://github.com/arturbosch/detekt/pull/606)
- Allow --input to handle multiple paths - [#605](https://github.com/arturbosch/detekt/issues/605)
- update documentation after some merges to master - [#604](https://github.com/arturbosch/detekt/pull/604)
- README: Remove copy of default-detekt-config.yml - [#603](https://github.com/arturbosch/detekt/pull/603)
- HtmlOutputFormatTest tests assume Unix-style line endings - [#602](https://github.com/arturbosch/detekt/issues/602)
- add gradle task to assert config and documentation are generated up-to-date - [#601](https://github.com/arturbosch/detekt/pull/601)
- update CONTRIBUTING.md to take detekt-generator behavior into account - [#600](https://github.com/arturbosch/detekt/pull/600)
- ReturnCount augmented to ignore specified function names - [#599](https://github.com/arturbosch/detekt/pull/599)
- Add SpreadOperator to exclude-rules in test-pattern. - [#598](https://github.com/arturbosch/detekt/pull/598)
- update gradle to v4.4 - [#597](https://github.com/arturbosch/detekt/pull/597)
- DetektCli depends on kotlin-compiler-embeddable which pulls in older version of json-org-java - [#596](https://github.com/arturbosch/detekt/issues/596)
- Turn off comments over private function/property - #589 - [#595](https://github.com/arturbosch/detekt/pull/595)
- removes active mark for StringLiteralDuplication - [#594](https://github.com/arturbosch/detekt/pull/594)
- Replace default-detekt-config.yml with generated one - [#593](https://github.com/arturbosch/detekt/pull/593)
- TooManyFunctions description could be improved - [#592](https://github.com/arturbosch/detekt/issues/592)
- Automatically run detekt-generator on detekt-rules build task - [#590](https://github.com/arturbosch/detekt/pull/590)
- Add documentation to generate default config - [#589](https://github.com/arturbosch/detekt/pull/589)
- Add documentation in bugs rules - [#588](https://github.com/arturbosch/detekt/pull/588)
- Remove InterruptedException from TooGenericExceptionCaught rule - [#587](https://github.com/arturbosch/detekt/pull/587)
- InterruptedException is too generic - [#586](https://github.com/arturbosch/detekt/issues/586)
- detekt-generator code examples - [#584](https://github.com/arturbosch/detekt/pull/584)
- Add OutputReport implementation that generates an HTML report - [#583](https://github.com/arturbosch/detekt/pull/583)
- Fix my blogpost title in the README :) - [#582](https://github.com/arturbosch/detekt/pull/582)
- detekt-generator: content section - [#579](https://github.com/arturbosch/detekt/pull/579)
- README: Update description of the 'output' parameter for the Gradle plugin - [#578](https://github.com/arturbosch/detekt/pull/578)
- Output is a folder and not a file anymore - [#577](https://github.com/arturbosch/detekt/issues/577)
- add detekt-generator module to generate documentation and default config - [#563](https://github.com/arturbosch/detekt/pull/563)
- Add message to CodeSmell - [#480](https://github.com/arturbosch/detekt/pull/480)
- Generate default-detekt-config.yml according to rules - [#189](https://github.com/arturbosch/detekt/issues/189)

See all issues at: [RC6](https://github.com/arturbosch/detekt/milestone/23)

##### Migration

- We are now generating [documentation](detekt-generator/documentation) for all rule sets. They are stored as 
markdown files and will later be hosted on the official detekt website.
- rename `MethodNameEqualsClassName` to `MemberNameEqualsClassName` (rule checks also properties now)
- `CHANGELOG.md` and `MIGRATION.md` are now merged. The changelog now has also a migration subsection.
- Numbers are now allowed in class names (aligned to IntelliJ inspections) - `ClassNaming`-Rule
- If you are using the text or xml output option of detekt, consider also the new html output format.
- The `--input` cli property now supports multiple paths separated by a comma.
- `TooManyFunctions` and `SpreadOperator` rules are turned off for test files per default.

#### RC5-6

- update Kotlin to v1.1.61 - [#573](https://github.com/arturbosch/detekt/pull/573)
- rules-test-rc5 - [#571](https://github.com/arturbosch/detekt/pull/571)
- UnsafeCast: Fix in SpacingBetweenPackageImports, turn on rule, add present to baseline - [#570](https://github.com/arturbosch/detekt/pull/570)
- Rework naming rules to match intellij inspections - [#569](https://github.com/arturbosch/detekt/pull/569)
- 1.0.0-RC5-5 org.jetbrains.kotlin.psi.KtProperty cannot be cast to org.jetbrains.kotlin.psi.KtClassOrObject - [#568](https://github.com/arturbosch/detekt/issues/568)
- housekeeping_rules - [#565](https://github.com/arturbosch/detekt/pull/565)

See all issues at: [RC5-6](https://github.com/arturbosch/detekt/milestone/28)

##### Migration

- fixed a critical bug in `SpacingBetweenPackageImports`, please update if you use this rule.
- Aligned naming conventions rules to meet intellij inspections.
    - ConstantNaming got removed
    - TopLevelPropertyNaming and ObjectPropertyNaming was added
    - there are now configuration parameters for private properties

```yaml
  VariableNaming:
    active: true
    variablePattern: '[a-z][A-Za-z0-9]*'
    privateVariablePattern: '(_)?[a-z][A-Za-z0-9]*'
  ObjectPropertyNaming:
    active: true
    propertyPattern: '[A-Za-z][_A-Za-z0-9]*'
  TopLevelPropertyNaming:
    active: true
    constantPattern: '[A-Z][_A-Z0-9]*'
    propertyPattern: '[a-z][A-Za-z\d]*'
    privatePropertyPattern: '(_)?[a-z][A-Za-z0-9]*'
```

#### RC5-5

- Add --plugins option to gradle plugin - Fixes #545 - [#561](https://github.com/arturbosch/detekt/pull/561)
- Rewrite messages and issue description for TooManyFunctions rule - #552 - [#560](https://github.com/arturbosch/detekt/pull/560)
- Run built snapshot also on test sources - [#559](https://github.com/arturbosch/detekt/pull/559)
- TooManyFunctions message can be misleading - [#552](https://github.com/arturbosch/detekt/issues/552)
- Added UnnecessaryAbstractClass without body detection - [#551](https://github.com/arturbosch/detekt/pull/551)
- UnnecessaryAbstractClass: misses class without body - [#550](https://github.com/arturbosch/detekt/issues/550)
- Use newer extension syntax in example for Kotlin DSL and add in expli… - [#549](https://github.com/arturbosch/detekt/pull/549)
- improve message of PackageDeclarationStyle - [#548](https://github.com/arturbosch/detekt/pull/548)
- Update to Kotlin 1.1.60 - [#546](https://github.com/arturbosch/detekt/pull/546)
- Using plugin rulesets in Android project - [#545](https://github.com/arturbosch/detekt/issues/545)

See all issues at: [RC5-5](https://github.com/arturbosch/detekt/milestone/27)

##### Migration

- TooManyFunctions rule got a rework. Old property `threshold` was replaced with:
    - `thresholdInFiles: 10`
    - `thresholdInClasses: 10`
    - `thresholdInInterfaces: 10`
    - `thresholdInObjects: 10`
    - `thresholdInEnums: 10`

#### RC5-4

- Testcase refactoring - #527 - [#541](https://github.com/arturbosch/detekt/pull/541)
- Build improvements - [#539](https://github.com/arturbosch/detekt/pull/539)
- Allow throwing exceptions in init blocks - closes #537 - [#538](https://github.com/arturbosch/detekt/pull/538)
- ExceptionRaisedInUnexpectedLocation in constructer - [#537](https://github.com/arturbosch/detekt/issues/537)
- UnnecessaryAbstractClass takes primary constructor vals into account - [#535](https://github.com/arturbosch/detekt/pull/535)
- UnnecessaryAbstractClass does not see member variables - [#534](https://github.com/arturbosch/detekt/issues/534)

See all issues at: [RC5-4](https://github.com/arturbosch/detekt/milestone/26)

#### RC5-3

- MaxLineLengthRule line number reporting issue - [#526](https://github.com/arturbosch/detekt/pull/526)
- Fixed #522 - CollapsibleIf must not have a if-else child - [#525](https://github.com/arturbosch/detekt/pull/525)
- Implemented #523 - open function option FunctionOnlyReturningConstant - [#524](https://github.com/arturbosch/detekt/pull/524)

See all issues at: [RC5-3](https://github.com/arturbosch/detekt/milestone/25)

#### RC5-2

- Enhancement #514 - [#519](https://github.com/arturbosch/detekt/pull/519)
- Test cases RC5 - [#511](https://github.com/arturbosch/detekt/pull/511)
- Prepend project name to findings path - #171 - [#510](https://github.com/arturbosch/detekt/pull/510)
- Do not apply rule filters on main sources - [#509](https://github.com/arturbosch/detekt/pull/509)
- WildcardImport should support regex exclusions - [#506](https://github.com/arturbosch/detekt/issues/506)

See all issues at: [RC5-2](https://github.com/arturbosch/detekt/milestone/24)

##### Migration

- rule filters which are defined in the `test-pattern` were applied to main sources too, this is now fixed.

#### RC5

- Remove double check in condition - [#505](https://github.com/arturbosch/detekt/pull/505)
- Update README for failFast option - [#502](https://github.com/arturbosch/detekt/pull/502)
- Allow to suppress StringLiteralDuplication on class level - closes #442 - [#494](https://github.com/arturbosch/detekt/pull/494)
- Restrict Postfix operators to ++ and -- - closes #491 - [#493](https://github.com/arturbosch/detekt/pull/493)
- If formatting rule - [#489](https://github.com/arturbosch/detekt/pull/489)
- README: update sample output from the tool - [#488](https://github.com/arturbosch/detekt/pull/488)
- Remove stale rules from config files - [#487](https://github.com/arturbosch/detekt/pull/487)
- Minor: Use toX instead of java.lang.x.parseX - [#486](https://github.com/arturbosch/detekt/pull/486)
- Yml config test - [#484](https://github.com/arturbosch/detekt/pull/484)
- don't report UselessPostfixExpressions on fields in return expressions - [#483](https://github.com/arturbosch/detekt/pull/483)
- ignore "_" named variables for VariableNaming and VariableMinLength - [#482](https://github.com/arturbosch/detekt/pull/482)
- VariableNaming and VariableMinLength: Exclude simple Underscore from Rule - [#481](https://github.com/arturbosch/detekt/issues/481)
- Refactored all detekt-config tests - [#478](https://github.com/arturbosch/detekt/pull/478)
- Fix for #465 - [#477](https://github.com/arturbosch/detekt/pull/477)
- Rule:NestedClassVisibility update - [#474](https://github.com/arturbosch/detekt/pull/474)
- Added DetektYamlConfigTest - [#472](https://github.com/arturbosch/detekt/pull/472)
- Redundant modifier rule - [#470](https://github.com/arturbosch/detekt/pull/470)
- MethodNameEqualsClassName rule - [#469](https://github.com/arturbosch/detekt/pull/469)
- reword detekt timing message and use measureTimeMillis - [#468](https://github.com/arturbosch/detekt/pull/468)
- Processors test - [#463](https://github.com/arturbosch/detekt/pull/463)
- Added static declaration count to ComplexInterface - [#461](https://github.com/arturbosch/detekt/pull/461)
- Apply java-gradle-plugin to detekt-gradle-plugin - [#460](https://github.com/arturbosch/detekt/pull/460)
- Test pattern - #227 - [#459](https://github.com/arturbosch/detekt/pull/459)
- correcly handle imports used in KDoc tags in UnusedImports rule - [#458](https://github.com/arturbosch/detekt/pull/458)
- More false positive for unused imports - [#457](https://github.com/arturbosch/detekt/issues/457)
- Enum constant parameter flagged for MagicNumber - [#455](https://github.com/arturbosch/detekt/issues/455)
- remove default description of modifier order rule - [#454](https://github.com/arturbosch/detekt/pull/454)
- update gradle to v4.2.1 - [#453](https://github.com/arturbosch/detekt/pull/453)
- ModifierOrder has wrong description - [#452](https://github.com/arturbosch/detekt/issues/452)
- update kotlin to v1.1.51 - [#450](https://github.com/arturbosch/detekt/pull/450)
- Add AnnotationExcluder to be able to reuse exluding by annotation mechanism. - [#447](https://github.com/arturbosch/detekt/pull/447)
- Nested class, interface or enum shouldn't be publicly visible - [#446](https://github.com/arturbosch/detekt/pull/446)
- Nested class, interface or enum shouldn't be publicly visible - [#444](https://github.com/arturbosch/detekt/issues/444)
- RuleProviderTest and cleanup of test-cases - [#443](https://github.com/arturbosch/detekt/pull/443)
- Suppressing string duplication doesn't work - [#442](https://github.com/arturbosch/detekt/issues/442)
- Complexity rules: ComplexInterface + MethodOverloading - [#440](https://github.com/arturbosch/detekt/pull/440)
- Fixed #435 - false-positive for UseDataClass rule - [#439](https://github.com/arturbosch/detekt/pull/439)
- Config refactoring - [#436](https://github.com/arturbosch/detekt/pull/436)
- FalsePositive for UseDataClass on enum and annotation classes - [#435](https://github.com/arturbosch/detekt/issues/435)
- DataClassContainsFunctions - allow conversion function - [#434](https://github.com/arturbosch/detekt/issues/434)
- Exception rules modification - [#432](https://github.com/arturbosch/detekt/pull/432)
- Update FunctionNaming regex to work for sentences - [#425](https://github.com/arturbosch/detekt/pull/425)
- Added SerialVersionUIDInSerializableClass rule - [#424](https://github.com/arturbosch/detekt/pull/424)
- Update "MagicNumber" to not mark "Named Arguments" as code smells - [#415](https://github.com/arturbosch/detekt/issues/415)
- Support idea tasks also for windows - closes #413 - [#414](https://github.com/arturbosch/detekt/pull/414)
- IDEA inspections does not work under Windows - [#413](https://github.com/arturbosch/detekt/issues/413)
- Use the default-config.yml as default configuration - [#412](https://github.com/arturbosch/detekt/pull/412)
- Get warning when rule is not defined - [#407](https://github.com/arturbosch/detekt/issues/407)
- Remove formatting module - [#406](https://github.com/arturbosch/detekt/pull/406)
- Add .editorconfig - [#401](https://github.com/arturbosch/detekt/pull/401)
- Add a Gitter chat badge to README.md - [#400](https://github.com/arturbosch/detekt/pull/400)
- Correct mccabe complexity calculation - closes #396 - [#399](https://github.com/arturbosch/detekt/pull/399)
- Update to Gradle 4.1 - [#398](https://github.com/arturbosch/detekt/pull/398)
- Open gitter for easier interaction with users - [#395](https://github.com/arturbosch/detekt/issues/395)
- Fixed #389 in ProtectedMemberInFinalClass rule - [#392](https://github.com/arturbosch/detekt/pull/392)
- False positive ProtectedMemberInFinalClass - [#389](https://github.com/arturbosch/detekt/issues/389)
- Ignore sealed classes for UseDataClass rule check - [#387](https://github.com/arturbosch/detekt/pull/387)
- Use case specific descriptions in UnnecessarySuperTypeDeclaration - [#386](https://github.com/arturbosch/detekt/pull/386)
- Loop rules - [#385](https://github.com/arturbosch/detekt/pull/385)
- Test improvement - [#383](https://github.com/arturbosch/detekt/pull/383)
- Add additional FileProcessListener event - [#381](https://github.com/arturbosch/detekt/pull/381)
- Unnecessary supertype declaration - [#380](https://github.com/arturbosch/detekt/pull/380)
- Address some IntelliJ lint & Kotlin compiler warnings - [#378](https://github.com/arturbosch/detekt/pull/378)
- Do not flag empty functions which are meant to be overridden - [#376](https://github.com/arturbosch/detekt/pull/376)
- Deprecate formatting - [#326](https://github.com/arturbosch/detekt/issues/326)
- Introduce config based way of skipping certain rules for test classes - [#227](https://github.com/arturbosch/detekt/issues/227)

See all issues at: [RC5](https://github.com/arturbosch/detekt/milestone/22)

##### Migration

- Formatting rule set was removed. Use the `detektIdeaFormat` task, KtLint or wait for the official kotlin format
tool which will be released soon (Hadi mentioned it in a reply to a tweet somewhere).
- McCabe calculation was corrected and can now be slightly higher which can result in unexpected `ComplexMethod`
findings.
- Instead of using a pattern like `.*/test/.*` to filter test sources, you can now specify a `test-pattern` inside a
configuration. This allows to turn off specific rules or rule sets for test sources.

#### RC4-3 - Second bugfix release for RC4 with a bunch of new contributed rules!

- UndocumentedPublicClass: Fix enum support - [#375](https://github.com/arturbosch/detekt/pull/375)
- add rule to forbid certain class names - [#374](https://github.com/arturbosch/detekt/pull/374)
- Tests are not executed anymore but skipped! - [#373](https://github.com/arturbosch/detekt/issues/373)
- Use failfast configuration in CI - [#372](https://github.com/arturbosch/detekt/pull/372)
- Added break and continue to unreachable code rule - [#371](https://github.com/arturbosch/detekt/pull/371)
- A few more exception rules - [#370](https://github.com/arturbosch/detekt/pull/370)
- Added UnnecessaryAbstractClass rule - [#369](https://github.com/arturbosch/detekt/pull/369)
- remove spaces in default-detekt-config.yml - [#368](https://github.com/arturbosch/detekt/pull/368)
- Advanced exception rules - [#366](https://github.com/arturbosch/detekt/pull/366)
- Added PackageDeclaration style rule - [#364](https://github.com/arturbosch/detekt/pull/364)
- Implement Data class rule - [#354](https://github.com/arturbosch/detekt/pull/354)
- Feature/data class rule - [#353](https://github.com/arturbosch/detekt/pull/353)
- Extend naming ruleset - [#302](https://github.com/arturbosch/detekt/issues/302)
- Data class rule - [#263](https://github.com/arturbosch/detekt/issues/263)

See all issues at: [RC4-3](https://github.com/arturbosch/detekt/milestone/21)

#### RC4-2 - Bugfix release for RC4

- Bugfix FunctionMaxLength typo - [#367](https://github.com/arturbosch/detekt/pull/367)
- Fixed protected member report in sealed class - [#362](https://github.com/arturbosch/detekt/pull/362)
- Renamed UselessIncrement to UselessPostfixExp - [#360](https://github.com/arturbosch/detekt/pull/360)
- Bugfix/variable max length const - [#359](https://github.com/arturbosch/detekt/pull/359)
- UndocumentedPublicClass: Add searchInInnerObject configuration property. - [#358](https://github.com/arturbosch/detekt/pull/358)
- fix link to default-detekt-config.yml in migration guide for RC4 - [#357](https://github.com/arturbosch/detekt/pull/357)

See all issues at: [RC4-2](https://github.com/arturbosch/detekt/milestone/20)

#### RC4

- Do not use reflection for toString methods - closes #349 - [#351](https://github.com/arturbosch/detekt/pull/351)
- Detekt Gradle plugin breaks the Gradle properties task - [#349](https://github.com/arturbosch/detekt/issues/349)
- Decouple KtTreeCompiler from Detektor - #341 - [#348](https://github.com/arturbosch/detekt/pull/348)
- fix README to mention the renamed --plugins instead of --rules - [#346](https://github.com/arturbosch/detekt/pull/346)
- Decouple KtTreeCompiler, KtCompiler from Detektor - [#341](https://github.com/arturbosch/detekt/issues/341)
- Reimplement thrown-/catched exception rules - #95 - [#334](https://github.com/arturbosch/detekt/pull/334)
- Reimplement throw-/catch-exception-rules as MultiRules - [#332](https://github.com/arturbosch/detekt/pull/332)
- remove unnecessary parentheses in the main codebase - [#330](https://github.com/arturbosch/detekt/pull/330)
- Added packagePattern option to config - [#329](https://github.com/arturbosch/detekt/pull/329)
- add rule to detect Unnecessary Parentheses - [#328](https://github.com/arturbosch/detekt/pull/328)
- PackagePattern in naming conventions - [#327](https://github.com/arturbosch/detekt/issues/327)
- align naming patterns for enum entries in rule + default config - [#325](https://github.com/arturbosch/detekt/pull/325)
- allow suppressing `all` - [#324](https://github.com/arturbosch/detekt/pull/324)
- Combine all empty block rules into one multi rule - #95 - [#322](https://github.com/arturbosch/detekt/pull/322)
- Refactored tests and increased coverage - [#321](https://github.com/arturbosch/detekt/pull/321)
- Refactor Naming rules and add Variable/Function length rules - [#320](https://github.com/arturbosch/detekt/pull/320)
- Added protected member in final class rule - [#317](https://github.com/arturbosch/detekt/pull/317)
- [Poll] Do people care about separated EmptyXXXBlock, ThrowXXX and CatchXXX rules? - [#95](https://github.com/arturbosch/detekt/issues/95)

See all issues at: [RC4](https://github.com/arturbosch/detekt/milestone/18)

##### Migration

- CatchXXX and ThrowXXX rules were reimplemented and combined into TooGenericExceptionCatched and
TooGenericExceptionThrown rules. Own exceptions can be added to the list.
- EmptyXXXBlock rules were reimplemented and can be turned off individually
- The rule NamingConventions was reimplemented and now every case is separately configurable and new cases were added

See [default-detekt-config.yml](detekt-cli/src/main/resources/default-detekt-config.yml)

#### RC3

- Do not consider empty returns as OptionalReturnKeyword - [#314](https://github.com/arturbosch/detekt/pull/314)
- Added % to comment-source ratio output - [#309](https://github.com/arturbosch/detekt/pull/309)
- Add rules property for multirule - [#308](https://github.com/arturbosch/detekt/pull/308)
- Use function instead of method in descriptions and ids of CommentOver… - [#307](https://github.com/arturbosch/detekt/pull/307)
- update kotlin to v1.1.4 - [#306](https://github.com/arturbosch/detekt/pull/306)
- OptionalReturnKeyword: Fails with expressions - [#304](https://github.com/arturbosch/detekt/issues/304)
- Treat comment as non-empty block body - [#303](https://github.com/arturbosch/detekt/pull/303)
- Added StringLiteralDuplication rule - [#300](https://github.com/arturbosch/detekt/pull/300)
- Added naming rule for packages - [#299](https://github.com/arturbosch/detekt/pull/299)
- Update used detekt to RC2 - [#298](https://github.com/arturbosch/detekt/pull/298)
- Equals() smells - [#297](https://github.com/arturbosch/detekt/pull/297)

See all issues at: [RC3](https://github.com/arturbosch/detekt/milestone/17)

##### Migration

- MagicNumber rule has now different ignore properties

#### RC2

- Remove magic numbers and other detekt issues - [#295](https://github.com/arturbosch/detekt/pull/295)
- Ignore based on checkstyle for MagicNumber - [#289](https://github.com/arturbosch/detekt/pull/289)
- Revert implementation configuration in cli module - [#283](https://github.com/arturbosch/detekt/pull/283)
- Run cli on ci - [#282](https://github.com/arturbosch/detekt/pull/282)
- Variables/Properties declaring numbers shouldn't be flagged for MagicNumber - [#280](https://github.com/arturbosch/detekt/issues/280)
- Added UnnecessaryConversionTemporary rule - [#279](https://github.com/arturbosch/detekt/pull/279)
- More metrics - [#277](https://github.com/arturbosch/detekt/pull/277)
- MagicNumber rule throw report for null initialized variable - [#276](https://github.com/arturbosch/detekt/issues/276)
- Improve build setup - [#275](https://github.com/arturbosch/detekt/pull/275)

See all issues at: [RC2](https://github.com/arturbosch/detekt/milestone/16)

##### Migration

- Make sure to upgrade! RC2 fixes a number of MagicNumber's issues and adds properties to make this rule more configurable.

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

##### Migration

- Attention: new `MagicNumber` and `ReturnCount` rules can let your CI fail
- Sample project now reflects all possible custom extensions to detekt, see `extensions` section in README
- `--output` points to a directory now. This is due the fact that many output reports can be generated at once
- Each `OutputReport` specifies a file name and ending. The parameter `--output-name` can be used to override the
default provided file name of the `OutputReport`. Unnecessary output reports for your project can be turned off in
the configuration.

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

##### Migration

- Misspelled class `Dept` was renamed to `Debt`, if you using custom rule sets, please rebuild it
- CLI parameter `--project` was renamed to `--input` to match the input parameter of the gradle plugin

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

##### Migration

###### CLI

- No break just extra notification that you can pass now more than one configuration file within the `--config` and `--config-resource` parameters

This allows overriding certain configuration parameters in the base configuration (left-most config)

###### Gradle Plugin

- the detekt extension is now aware of `configuration profiles`
- non default or 'main' profile, needs to be specified like `gradle detektCheck -Ddetekt.profile=[profile-name]`

Instead of writing something like

```groovy
detekt {
    version = "1.0.0.M11"
    input = "$project.projectDir/src"
    filters = '.*/test/.*'
    config = "$project.projectDir/detekt-config.yml"
    output = "$project.projectDir/output.xml"
    idea {
        path = "$USER_HOME/.idea"
        codeStyleScheme = "$USER_HOME/.idea/idea-code-style.xml"
        inspectionsProfile = "$USER_HOME/.idea/inspect.xml"
        mask = "*.kt,"
    }
}
```

you have to put a `profile`-closure around the parameters

```groovy
detekt {
    profile("main") {
        version = "1.0.0.M11"
        input = "$project.projectDir/src"
        filters = '.*/test/.*'
        config = "$project.projectDir/detekt-config.yml"
        output = "$project.projectDir/output.xml"
    }
    profile("test") {
        filters = ".*/src/main/kotlin/.*"
        config = "$project.projectDir/detekt-test-config.yml"
    }
    idea {
        path = "$USER_HOME/.idea"
        codeStyleScheme = "$USER_HOME/.idea/idea-code-style.xml"
        inspectionsProfile = "$USER_HOME/.idea/inspect.xml"
        mask = "*.kt,"
    }
}
```

This allows you too configure `detekt-rules` specific for each module. Also allowing to have different configurations for production or test code.

###### Renamings

- `NoDocOverPublicClass` -> `UndocumentedPublicClass`
- `NoDocOverPublicMethod` -> `UndocumentedPublicFunction`

Rename this id's in your configuration

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

##### Migration

- `detekt` task was renamed to `detektCheck` (gradle-plugin)
- `empty` rule set was renamed to `empty-blocks` rule set

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

##### Migration

- `code-smell` rule set was renamed to `complexity` rule set (config)

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

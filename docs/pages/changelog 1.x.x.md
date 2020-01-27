---
title: Changelog and Migration Guide
sidebar: home_sidebar
keywords: changelog, release-notes, migration
permalink: changelog.html
toc: true
---

#### 1.5.0

##### Notable Changes

- detekt is now _silent_ by default. It only prints something if issues are found.
Remove following excludes if you want the old behavior back.

```yaml
console-reports:
  active: true
  exclude:
     - 'ProjectStatisticsReport'
     - 'ComplexityReport'
     - 'NotificationReport'
```

- detekt now fails the build if any issue is found. Change the `maxIssues` property to 10 for the old threshold. 

```yaml
build:
  maxIssues: 0
```

- The `HTML` report now prints the issue count per rule and rule set.
- New rules: `ExplicitCollectionElementAccessMethod` and `ForbiddenMethod`

##### Changelog

- add new mention to README.md - [#2293](https://github.com/arturbosch/detekt/pull/2293)
- Sort html report - [#2290](https://github.com/arturbosch/detekt/pull/2290)
- Number format in some report - [#2289](https://github.com/arturbosch/detekt/pull/2289)
- Show the finding count in the html report - [#2288](https://github.com/arturbosch/detekt/pull/2288)
- Keep the order of the issues in the html report - [#2287](https://github.com/arturbosch/detekt/issues/2287)
- Show issue count in the html report - [#2286](https://github.com/arturbosch/detekt/issues/2286)
- Fixing the Documentation not properly calling the superclass - [#2284](https://github.com/arturbosch/detekt/pull/2284)
- Do you have to call super in custom rules? - [#2283](https://github.com/arturbosch/detekt/issues/2283)
- Measure performance of various stages when using --debug - [#2281](https://github.com/arturbosch/detekt/pull/2281)
- Remove printing the whole config for --debug - [#2280](https://github.com/arturbosch/detekt/pull/2280)
- Introduce DefaultRuleSetProvider interface marking detekt-rules providers as default - [#2279](https://github.com/arturbosch/detekt/pull/2279)
- Simplify test dependency setup in build files - [#2278](https://github.com/arturbosch/detekt/pull/2278)
- Fix class loader memory leaks when loading services - [#2277](https://github.com/arturbosch/detekt/pull/2277)
- Always dispose Kotlin environment fixing memory leak in error cases - [#2276](https://github.com/arturbosch/detekt/pull/2276)
- Sanitize gradle build scripts and suppress unstable api usages - [#2271](https://github.com/arturbosch/detekt/pull/2271)
- Update website ruby dependencies fixing potential security vulnerability - [#2270](https://github.com/arturbosch/detekt/pull/2270)
- Fix regression not considering baseline file when calculating build failure threshold - [#2269](https://github.com/arturbosch/detekt/pull/2269)
- Turn detekt silent by default - [#2268](https://github.com/arturbosch/detekt/pull/2268)
- Remove redundant build failure message - #2264 - [#2266](https://github.com/arturbosch/detekt/pull/2266)
- Build failed with... is printed twice in the cli - [#2264](https://github.com/arturbosch/detekt/issues/2264)
- Update config:maxIssues value to 0 - [#2263](https://github.com/arturbosch/detekt/pull/2263)
- Don't flag inherited visibility in NestedClassesVisibility - [#2261](https://github.com/arturbosch/detekt/pull/2261)
- Simplify argument parsing logic, remove any exitProcess() calls from buildRunner - [#2260](https://github.com/arturbosch/detekt/pull/2260)
- Modify default behavior to not output unless errors are found.  Adding a verbose flag which will have legacy behavior - [#2258](https://github.com/arturbosch/detekt/pull/2258)
- Test some edge cases in detekt-api - [#2256](https://github.com/arturbosch/detekt/pull/2256)
- Add a new line at the end of the txt report - [#2255](https://github.com/arturbosch/detekt/pull/2255)
- Implement ExplicitCollectionElementAccessMethod rule - [#2215](https://github.com/arturbosch/detekt/pull/2215)
- ForbiddenMethod Rule - [#1954](https://github.com/arturbosch/detekt/pull/1954)
- NestedClassesVisibility(False negative): Nested class doesn't inherit visibility from parent - [#1930](https://github.com/arturbosch/detekt/issues/1930)

See all issues at: [1.5.0](https://github.com/arturbosch/detekt/milestone/56)

#### 1.4.0

##### Notable Changes

New rule set `coroutines` with two specialized rules was added.  
As always new rules must be activated by the user until they get stable.

```yaml
coroutines:
  active: true
  GlobalCoroutineUsage:
    active: false
  RedundantSuspendModifier:
    active: false
```

Wrapped `KtLint` was updated to `0.36.0` introducing two rules
- EnumEntryNameCase
- NoEmptyFirstLineInMethodBlock

##### Migration

The properties `ignoreOverriddenFunction` and `ignoreOverriddenFunctions` of some rules got deprecated and unified to a new property `ignoreOverridden`.

##### Changelog

- Refactor BuildFailure code - [#2250](https://github.com/arturbosch/detekt/pull/2250)
- Fix nested methods bug in MethodOverloading rule - [#2249](https://github.com/arturbosch/detekt/pull/2249)
- ThrowingExceptionInMain rule should consider main() function with no parameters - [#2248](https://github.com/arturbosch/detekt/issues/2248)
- MethodOverloading bug with nested overloaded methods - [#2247](https://github.com/arturbosch/detekt/issues/2247)
- Reduce complexity in FindingsReport - [#2246](https://github.com/arturbosch/detekt/pull/2246)
- Add RedundantSuspendModifier rule - [#2244](https://github.com/arturbosch/detekt/pull/2244)
- New ktlint rules - [#2243](https://github.com/arturbosch/detekt/pull/2243)
- Inline MethodOverloading case file - [#2241](https://github.com/arturbosch/detekt/pull/2241)
- ThrowingExceptionInMain: fix #2248 and add tests - [#2240](https://github.com/arturbosch/detekt/pull/2240)
- Add disposing Kotlin environment. - [#2238](https://github.com/arturbosch/detekt/pull/2238)
- OOM on multiple invocations - [#2237](https://github.com/arturbosch/detekt/issues/2237)
- Improve doc for UnusedPrivateMember - [#2236](https://github.com/arturbosch/detekt/pull/2236)
- Don't resolve dependencies during project configuration - [#2235](https://github.com/arturbosch/detekt/pull/2235)
- Revert "Introduce Pull Request Labeler" - [#2234](https://github.com/arturbosch/detekt/pull/2234)
- Fix #2230 equals() func detection - [#2233](https://github.com/arturbosch/detekt/pull/2233)
- Fix git commit-hook label and code snippet doc - [#2232](https://github.com/arturbosch/detekt/pull/2232)
- WrongEqualsTypeParameter does not ignore multi-parameter equals methods - [#2230](https://github.com/arturbosch/detekt/issues/2230)
- Introduce Pull Request Labeler - [#2228](https://github.com/arturbosch/detekt/pull/2228)
- Gradle plugin: Build upon default detekt config - [#2227](https://github.com/arturbosch/detekt/pull/2227)
- Apply ktlint formatting plugin to Gradle plugin - [#2226](https://github.com/arturbosch/detekt/pull/2226)
- Bump dependencies - [#2225](https://github.com/arturbosch/detekt/pull/2225)
- Run shadowJar & installShadowDist task with everything else - [#2220](https://github.com/arturbosch/detekt/pull/2220)
- Travis: Use consistent Java vendor - [#2219](https://github.com/arturbosch/detekt/pull/2219)
- "Property is misspelled or does not exist" error for new rules in default rulesets - [#2217](https://github.com/arturbosch/detekt/issues/2217)
- MethodOverloading false positive if every entry of an enum implement a method. - [#2216](https://github.com/arturbosch/detekt/issues/2216)
- Add Git detekt pre-commit hook doc - [#2214](https://github.com/arturbosch/detekt/pull/2214)
- Remove exclude workaround for new build property - [#2203](https://github.com/arturbosch/detekt/pull/2203)
- Add GlobalCoroutineUsage rule + coroutines ruleset - [#2174](https://github.com/arturbosch/detekt/pull/2174)
- Add rule [RedundantSuspend] to detect redundant suspend modifiers - [#2156](https://github.com/arturbosch/detekt/issues/2156)
- Deprecate ignoreOverriddenFunction/s in favor of ignoreOverridden - [#2132](https://github.com/arturbosch/detekt/pull/2132)

See all issues at: [1.4.0](https://github.com/arturbosch/detekt/milestone/55)

#### 1.3.1

- Remove old unused documentation - [#2210](https://github.com/arturbosch/detekt/pull/2210)
- Show code snippet erros in html reports - [#2209](https://github.com/arturbosch/detekt/pull/2209)
- Use compileAndLint in UnusedPrivateClassSpec - [#2208](https://github.com/arturbosch/detekt/pull/2208)
- Fix false positive in UnusedPrivateClass - [#2207](https://github.com/arturbosch/detekt/pull/2207)
- Update readme promoting new properties of the gradle plugin - [#2205](https://github.com/arturbosch/detekt/pull/2205)
- Rename default const containing _ACCEPTED_ - [#2204](https://github.com/arturbosch/detekt/pull/2204)
- Mistake From LongParameterList.kt - [#2202](https://github.com/arturbosch/detekt/issues/2202)
- Exclude yet unknown new build property - [#2201](https://github.com/arturbosch/detekt/pull/2201)
- Add comment regarding type resolution to README - [#2199](https://github.com/arturbosch/detekt/pull/2199)
- Type resolution doc - [#2198](https://github.com/arturbosch/detekt/pull/2198)
- Correct indentation for Groovy DSL doc - [#2197](https://github.com/arturbosch/detekt/pull/2197)
- Use shorthand syntax for assertThat() - [#2196](https://github.com/arturbosch/detekt/pull/2196)
- Refactor MagicNumber to use commaSeparatedPatterns - [#2195](https://github.com/arturbosch/detekt/pull/2195)
- Attach FILE_PATH_USER_DATA_KEY user data on FormattingRules (#1907) - [#2194](https://github.com/arturbosch/detekt/pull/2194)
- Handle invalid config exit code in gradle plugin - [#2193](https://github.com/arturbosch/detekt/pull/2193)
- Add tests showing how to exclude custom config properties in plugins - [#2192](https://github.com/arturbosch/detekt/pull/2192)
- Fix suppression of KtLint rules on file level - #2179 - [#2191](https://github.com/arturbosch/detekt/pull/2191)
- Mention needed kotlinx.html library from jcenter - #2146 - [#2190](https://github.com/arturbosch/detekt/pull/2190)
- UnusedPrivateClass has false positive behavior for deserialized items - [#2158](https://github.com/arturbosch/detekt/issues/2158)
- Use JDK 11 (LTS) + 13 for AppVeyor builds - [#2141](https://github.com/arturbosch/detekt/pull/2141)
- Document how to create a common baseline file for multi module gradle projects - [#2140](https://github.com/arturbosch/detekt/pull/2140)
- DetektAll with baseline fails with error - [#2100](https://github.com/arturbosch/detekt/issues/2100)
- ForbiddenMethod Rule - [#1954](https://github.com/arturbosch/detekt/pull/1954)
- Do not report auto-corrected formatting issues - [#1840](https://github.com/arturbosch/detekt/pull/1840)

See all issues at: [1.3.1](https://github.com/arturbosch/detekt/milestone/54)

#### 1.3.0

##### Notable changes

- Minimal Gradle version increased to 5.0
- New rules: 
    - `UnnecessaryAnnotationUseSiteTargetRule`
    - `MapGetWithNotNullAssertionOperator`

##### Changelog

- Add printers to the command line runner - [#2188](https://github.com/arturbosch/detekt/pull/2188)
- Fix documentation of UseArrayLiteralsInAnnotations - [#2186](https://github.com/arturbosch/detekt/pull/2186)
- Inline resolving severity label for XML reports - [#2184](https://github.com/arturbosch/detekt/pull/2184)
- Extract common jvm target value, add jvmTarget into documentation. Cl… - [#2183](https://github.com/arturbosch/detekt/pull/2183)
- Fix Detekt gradle task cache restoration issue (#2180) - [#2182](https://github.com/arturbosch/detekt/pull/2182)
- Fix exception when running ArrayPrimitive on star-projected arrays - [#2181](https://github.com/arturbosch/detekt/pull/2181)
- Gradle cache restoration issue - [#2180](https://github.com/arturbosch/detekt/issues/2180)
- Add MacOS JDK13 build job to TravisCI - [#2177](https://github.com/arturbosch/detekt/pull/2177)
- Running "ArrayPrimitive" rule on "Array<*>" causes detekt to throw exception - [#2176](https://github.com/arturbosch/detekt/issues/2176)
- Update Spek to v2.0.9 - [#2173](https://github.com/arturbosch/detekt/pull/2173)
- Create Rule: MapGetWithNotNullAssertionOperator - [#2171](https://github.com/arturbosch/detekt/pull/2171)
- EqualsAlwaysReturnsTrueOrFalse fails hard on `override fun equals(other:Any) = ...` - [#2167](https://github.com/arturbosch/detekt/issues/2167)
- Prepare 1.3.0 release - [#2165](https://github.com/arturbosch/detekt/pull/2165)
- UnsafeCast: update documentation to match new behavior - [#2164](https://github.com/arturbosch/detekt/pull/2164)
- Add jvmTarget change into documentation - [#2157](https://github.com/arturbosch/detekt/issues/2157)
- Create UnnecessaryAnnotationUseSiteTargetRule - [#2099](https://github.com/arturbosch/detekt/pull/2099)
- Gradle 6 - [#1902](https://github.com/arturbosch/detekt/pull/1902)
- Gradle 6 planning - [#1820](https://github.com/arturbosch/detekt/issues/1820)

See all issues at: [1.3.0](https://github.com/arturbosch/detekt/milestone/51)

#### 1.2.2

##### Notable Changes

- 1.2.1 introduced breaking changes for users of Gradle < 5. This was reverted.
- 1.3.0 will drop support for Gradle < 5.
- Fixed a regression in the html report.

##### Changelog

- regression updating 1.2.0 to 1.2.1, unknown property 'filters' for object of type DetektExtension - [#2163](https://github.com/arturbosch/detekt/issues/2163)
- StringIndexOutOfBoundsException when generating HTML report - [#2160](https://github.com/arturbosch/detekt/pull/2160)
- Restore KotlinExtension - [#2159](https://github.com/arturbosch/detekt/pull/2159)
- 1.2.1 breaks the build with: unresolved reference: detekt - [#2152](https://github.com/arturbosch/detekt/issues/2152)
- Updated to correct classpath documentation for Android projects. - [#2149](https://github.com/arturbosch/detekt/pull/2149)
- Update to Kotlin v1.3.61 - [#2147](https://github.com/arturbosch/detekt/pull/2147)
- Document how to exclude detekt from the check task - #1894 - [#2144](https://github.com/arturbosch/detekt/pull/2144)
- Use JDK 11 (LTS) + 13 for Travis builds - [#2142](https://github.com/arturbosch/detekt/pull/2142)
- Don't compile test snippets (bindingContext) - [#2137](https://github.com/arturbosch/detekt/pull/2137)
- StringIndexOutOfBoundsException: String index out of range: 8 when generating HTML report after update to 1.2.0 - [#2134](https://github.com/arturbosch/detekt/issues/2134)

See all issues at: [1.2.2](https://github.com/arturbosch/detekt/milestone/53)

#### 1.2.1

##### Notable changes

- Supports Kotlin 1.3.60
- Fixed a regression in configuration validation logic when using `build>weights>[RuleSet|Rule]` properties.
- Some rules got improvements (see changelog)

##### Changelog

- Exception analyzing file - [#2139](https://github.com/arturbosch/detekt/issues/2139)
- Simplify ConstructorParameterNaming:ignoreOverridden check - [#2136](https://github.com/arturbosch/detekt/pull/2136)
- Test common config sections pass through config validation - [#2135](https://github.com/arturbosch/detekt/pull/2135)
- Kotlin 1.3.60 with fix for "Unable to load JNA library" warning - [#2129](https://github.com/arturbosch/detekt/pull/2129)
- Unexpected nested config for 'build>weights'. - [#2128](https://github.com/arturbosch/detekt/issues/2128)
- Remove redundant Javadoc - [#2127](https://github.com/arturbosch/detekt/pull/2127)
- Lazy init KotlinScriptEnginePool - [#2126](https://github.com/arturbosch/detekt/pull/2126)
- Add tests for MagicNumber:ignoreLocalVariableDeclaration - [#2125](https://github.com/arturbosch/detekt/pull/2125)
- Fix NPE for EqualsAlwaysReturnsTrueOrFalse - [#2124](https://github.com/arturbosch/detekt/pull/2124)
- Add MagicNumber:ignoreLocalVariableDeclaration config - [#2123](https://github.com/arturbosch/detekt/pull/2123)
- Fix MagicNumber:ignoreConstantDeclaration doc - [#2116](https://github.com/arturbosch/detekt/pull/2116)
- Return non-nullable string in DebtSumming class - [#2113](https://github.com/arturbosch/detekt/pull/2113)
- Refactor TrailingWhitespace test - [#2112](https://github.com/arturbosch/detekt/pull/2112)
- Use inline code snippets instead of case files - [#2111](https://github.com/arturbosch/detekt/pull/2111)
- UnusedImports: False positive if referenced in @throws/@exception/@sample - [#2106](https://github.com/arturbosch/detekt/pull/2106)
- Don't compile test snippets - [#2105](https://github.com/arturbosch/detekt/pull/2105)
- MemberNameEqualsClassName should ignore overridden property names too - [#2104](https://github.com/arturbosch/detekt/pull/2104)
- EqualsAlwaysReturnsTrueOrFalse crashes on certain input - [#2103](https://github.com/arturbosch/detekt/issues/2103)
- UnusedImports: False positive if referenced only in @throws/@exception clause - [#2098](https://github.com/arturbosch/detekt/issues/2098)
- Add config flag ignoreOverridden to ConstructorParameterNaming - [#2097](https://github.com/arturbosch/detekt/pull/2097)
- compileAndLint is 2.5 times slower than lint - [#2095](https://github.com/arturbosch/detekt/issues/2095)
- Improve naming tests - [#2094](https://github.com/arturbosch/detekt/pull/2094)

See all issues at: [1.2.1](https://github.com/arturbosch/detekt/milestone/52)

#### 1.2.0

##### Notable changes

- The HTML report now includes the complexity metrics and previews of the code locations.
- Suppression by rule set id is now supported `@Suppress("detekt.[RuleSetId].[RuleId]")` 
- `parallel=true` and `--parallel` now effect both the compilation and analysis phase. 
- Users of Gradle's `--parallel` flag are encouraged to turn off the parallelism of detekt. Or turn it on otherwise.
- detekt internally does not use the `ForkJoinPool.commonPool()` anymore. When embedding detekt feel free to pass your own `ExecutionService` to the `ProcessingSettings`.

##### Migration

- Some reported positions of rules have changed, see issues starting with "Improve text location: ".
This may lead to some unexpected changes in the baseline file.
- The rule `ComplexMethod` got refactored and counts the cyclomatic complexity according to https://www.ndepend.com/docs/code-metrics#CC.
This change lead to increasing the threshold to 15 (was ten).

##### Changelog

- Update to Kotlin v1.3.60 - [#2109](https://github.com/arturbosch/detekt/pull/2109)
- UnusedPrivateClass: Fix false positive with private annotations - [#2108](https://github.com/arturbosch/detekt/pull/2108)
- Refactor ComplexMethod - [#2090](https://github.com/arturbosch/detekt/pull/2090)
- Fix NestedBlockDepth false negative - [#2086](https://github.com/arturbosch/detekt/pull/2086)
- NestedBlockDepth false negative - [#2085](https://github.com/arturbosch/detekt/issues/2085)
- Deprecate Location.locationString - [#2084](https://github.com/arturbosch/detekt/pull/2084)
- Add license badge to README - [#2080](https://github.com/arturbosch/detekt/pull/2080)
- Deploy SNAPSHOTs automatically - [#2079](https://github.com/arturbosch/detekt/pull/2079)
- Fix TrailingWhitespace reported position - [#2078](https://github.com/arturbosch/detekt/pull/2078)
- Activate more rules by default - #1911 - [#2075](https://github.com/arturbosch/detekt/pull/2075)
- Report InvalidRange for empty until range - [#2074](https://github.com/arturbosch/detekt/pull/2074)
- Deprecate properties on Entity - Closes #2014 - [#2072](https://github.com/arturbosch/detekt/pull/2072)
- Add complexity report to html output - [#2071](https://github.com/arturbosch/detekt/pull/2071)
- Use constants for config keys in tests - [#2070](https://github.com/arturbosch/detekt/pull/2070)
- Mention location adjustment in rules for 1.2.0 - [#2068](https://github.com/arturbosch/detekt/issues/2068)
- Improve text location: TooManyFunctions - [#2065](https://github.com/arturbosch/detekt/pull/2065)
- Improve text location: OptionalAbstractKeyword - [#2064](https://github.com/arturbosch/detekt/pull/2064)
- Improve text location: NestedBlockDepth - [#2063](https://github.com/arturbosch/detekt/pull/2063)
- Improve text location: MatchingDeclarationName - [#2062](https://github.com/arturbosch/detekt/pull/2062)
- Improve text location: LongMethod - [#2061](https://github.com/arturbosch/detekt/pull/2061)
- Improve text location: LargeClass - [#2060](https://github.com/arturbosch/detekt/pull/2060)
- Improve text location: ComplexMethod - [#2059](https://github.com/arturbosch/detekt/pull/2059)
- Improve text location: EmptyClassBlock - [#2058](https://github.com/arturbosch/detekt/pull/2058)
- Replace spek test hasSize(0) with isEmpty() - [#2057](https://github.com/arturbosch/detekt/pull/2057)
- Remove MacOS JDK11 CI run as discussed in #2015 - [#2056](https://github.com/arturbosch/detekt/pull/2056)
- Introduces mocking library 'mockk' - [#2055](https://github.com/arturbosch/detekt/pull/2055)
- Improve text location: InvalidPackageDeclaration - [#2052](https://github.com/arturbosch/detekt/pull/2052)
- Improve text location: MandatoryBracesIfStatements - [#2051](https://github.com/arturbosch/detekt/pull/2051)
- Improve text location: ClassNaming - [#2050](https://github.com/arturbosch/detekt/pull/2050)
- potential-bugs InvalidRange does not work in all cases - [#2044](https://github.com/arturbosch/detekt/issues/2044)
- Don't checkBuildFailureThreshold if we are creating the baseline - [#2034](https://github.com/arturbosch/detekt/pull/2034)
- gradle detektBaseline task fails - [#2033](https://github.com/arturbosch/detekt/issues/2033)
- Fix #2021 - [#2032](https://github.com/arturbosch/detekt/pull/2032)
- Update dependencies - [#2031](https://github.com/arturbosch/detekt/pull/2031)
- Dokka fix - [#2030](https://github.com/arturbosch/detekt/pull/2030)
- Simplify and refactor RuleProviderTest - [#2029](https://github.com/arturbosch/detekt/pull/2029)
- Simplify MultiRuleCollector test cases - [#2028](https://github.com/arturbosch/detekt/pull/2028)
- Dont check WrongEqualsTypeParameter if the function is topLevel - [#2027](https://github.com/arturbosch/detekt/pull/2027)
- Fix false positive at EmptyIfBlock - [#2026](https://github.com/arturbosch/detekt/pull/2026)
- Support guard clause exclusion for ThrowsCount rule - [#2025](https://github.com/arturbosch/detekt/pull/2025)
- Add ImplicitDefaultLocale rule - [#2024](https://github.com/arturbosch/detekt/pull/2024)
- Use double backtick for the in-line code - [#2022](https://github.com/arturbosch/detekt/pull/2022)
- EqualsAlwaysReturnsTrueOrFalse: The original exception message was: java.util.NoSuchElementException: Array is empty. - [#2021](https://github.com/arturbosch/detekt/issues/2021)
- Ignore sealed classes for utility class having public constructor rule - [#2016](https://github.com/arturbosch/detekt/pull/2016)
- Better handling for the Suppresion of errors - [#2013](https://github.com/arturbosch/detekt/pull/2013)
- Fix description of NoLineBreakBeforeAssignment - [#2011](https://github.com/arturbosch/detekt/pull/2011)
- Copy paste error in message in NoLineBreakBeforeAssignment.kt - [#2008](https://github.com/arturbosch/detekt/issues/2008)
- UtilityClassWithPublicConstructor should not be reported for sealed classes - [#2005](https://github.com/arturbosch/detekt/issues/2005)
- Validate yaml configurations by comparing their structure - #516 - [#1998](https://github.com/arturbosch/detekt/pull/1998)
- Allow the user to collapse the rules in the html report - [#1997](https://github.com/arturbosch/detekt/pull/1997)
- Allow detekt findings to be suppessed with rule set id - Closes #766 - [#1994](https://github.com/arturbosch/detekt/pull/1994)
- Upgrade Spek to v2.0.8 - [#1992](https://github.com/arturbosch/detekt/pull/1992)
- Reimplement parallelism internal logic - [#1991](https://github.com/arturbosch/detekt/pull/1991)
- Findings assertions - [#1978](https://github.com/arturbosch/detekt/pull/1978)
- Fix EnumNaming textLocation - [#1977](https://github.com/arturbosch/detekt/pull/1977)
- Add snippet code in html report - [#1975](https://github.com/arturbosch/detekt/pull/1975)
- Change reported element in 2 documentation rules - [#1952](https://github.com/arturbosch/detekt/pull/1952)
- Enable more rules in failfast - [#1935](https://github.com/arturbosch/detekt/pull/1935)
- Add UndocumentedPublicProperty rule - closes #1670 - [#1923](https://github.com/arturbosch/detekt/pull/1923)
- Calculate MCC (McCabe Complexity) accordingly - [#1921](https://github.com/arturbosch/detekt/issues/1921)
- UseDataClass conflicts with DataClassShouldBeImmutable - [#1920](https://github.com/arturbosch/detekt/issues/1920)
- Redesign "parallel" flag/property - [#1845](https://github.com/arturbosch/detekt/issues/1845)
- SNAPSHOT process feedback - [#1826](https://github.com/arturbosch/detekt/issues/1826)
- Initial MCC change - [#1793](https://github.com/arturbosch/detekt/pull/1793)
- @Suppress("Detekt.ruleset") feature - [#766](https://github.com/arturbosch/detekt/issues/766)
- Validate Configuration file before using it - [#516](https://github.com/arturbosch/detekt/issues/516)

See all issues at: [1.2.0](https://github.com/arturbosch/detekt/milestone/49)

#### 1.1.1

##### Changelog

- Improved test case for resolved #1971 (TrailingWhitespace and multiline string) - [#2003](https://github.com/arturbosch/detekt/pull/2003)
- Set defaults for boolean property when writing custom detekt tasks - [#1996](https://github.com/arturbosch/detekt/pull/1996)
- Generate PluginVersion.kt with newline at end - [#1993](https://github.com/arturbosch/detekt/pull/1993)
- Remove unnecessary logs from RuleProviderTest - [#1990](https://github.com/arturbosch/detekt/pull/1990)
- Use inline code snippets instead of case files - [#1989](https://github.com/arturbosch/detekt/pull/1989)
- Use config parameter for UseIfInsteadOfWhen rule - [#1987](https://github.com/arturbosch/detekt/pull/1987)
- Use inline code snippets instead of case files - [#1976](https://github.com/arturbosch/detekt/pull/1976)
- Don't flag trailing whitespaces in multiline strings - [#1971](https://github.com/arturbosch/detekt/pull/1971)

See all issues at: [1.1.1](https://github.com/arturbosch/detekt/milestone/50)

#### 1.1.0

##### Changelog

- Clarify threshold pararameter meaning in docs - [#1974](https://github.com/arturbosch/detekt/pull/1974)
- Introduce ignoreLabeled config for ReturnFromFinally - [#1973](https://github.com/arturbosch/detekt/pull/1973)
- Ignore FunctionOnlyReturningConstant for allowed annotations - [#1968](https://github.com/arturbosch/detekt/pull/1968)
- Allow regex configuration support for Forbidden Import rule - [#1963](https://github.com/arturbosch/detekt/pull/1963)
- Refactor and simplify RuleCollectorSpec - [#1959](https://github.com/arturbosch/detekt/pull/1959)
- Use inline code snippets instead of case files - [#1958](https://github.com/arturbosch/detekt/pull/1958)
- Improve UnusedPrivateMember when it's related with parameters - [#1949](https://github.com/arturbosch/detekt/pull/1949)
- Fix SpacingBetweenPackageAndImports issue for scripts without packages - [#1947](https://github.com/arturbosch/detekt/pull/1947)
- Remove ConditionalPathVisitor - [#1944](https://github.com/arturbosch/detekt/pull/1944)
- Extend UseDataClass with the allowVars configuration property - [#1942](https://github.com/arturbosch/detekt/pull/1942)
- HasPlatformType rule - [#1938](https://github.com/arturbosch/detekt/pull/1938)
- Bogus SpacingBetweenPackageAndImports violation for KTS without package - [#1937](https://github.com/arturbosch/detekt/issues/1937)
- Gradle deprecations - [#1934](https://github.com/arturbosch/detekt/pull/1934)
- UnnecessaryParentheses should be ignored for bit operations - [#1929](https://github.com/arturbosch/detekt/issues/1929)
- Prepare 1.1.0 release - [#1919](https://github.com/arturbosch/detekt/pull/1919)
- Deprecate debug in IDEA tasks - [#1918](https://github.com/arturbosch/detekt/pull/1918)
- Refactoring: use more readable functions - [#1916](https://github.com/arturbosch/detekt/pull/1916)
- Don't fail on master when test coverage decreases - [#1914](https://github.com/arturbosch/detekt/pull/1914)
- Detect deprecations - [#1913](https://github.com/arturbosch/detekt/pull/1913)
- Fix typos - [#1908](https://github.com/arturbosch/detekt/pull/1908)
- Report PreferToOverPairSyntax only for kotlin.Pair - [#1905](https://github.com/arturbosch/detekt/pull/1905)
- Fix TimeoutCancellationException - downgrade Spek - [#1903](https://github.com/arturbosch/detekt/pull/1903)
- Update dependencies - [#1901](https://github.com/arturbosch/detekt/pull/1901)
- Add RedundantExplicitType rule - [#1900](https://github.com/arturbosch/detekt/pull/1900)
- Remove unused KtAnnotated util functions - [#1899](https://github.com/arturbosch/detekt/pull/1899)
- Simplify detekt rules - [#1898](https://github.com/arturbosch/detekt/pull/1898)
- Fix shared variable in detekt-extension rules - [#1897](https://github.com/arturbosch/detekt/pull/1897)
- Make samples more idiomatic - [#1895](https://github.com/arturbosch/detekt/pull/1895)
- Update detekt extensions doc - [#1893](https://github.com/arturbosch/detekt/pull/1893)
- Clarify `WildcardImport` rule configuration - [#1888](https://github.com/arturbosch/detekt/pull/1888)
- Add configuration to allow patterns for forbidden comment - [#1887](https://github.com/arturbosch/detekt/pull/1887)
- Only report UnsafeCallOnNullableType on actual nullable types - [#1886](https://github.com/arturbosch/detekt/pull/1886)
- Minimise usage of Kotlin reflection - [#1883](https://github.com/arturbosch/detekt/pull/1883)
- Refactor KotlinCoreEnvironment test setup - [#1880](https://github.com/arturbosch/detekt/pull/1880)
- Trust Kotlin compiler to identify unsafe casts - [#1879](https://github.com/arturbosch/detekt/pull/1879)
- Replace tabs with spaces in rule KDoc - [#1876](https://github.com/arturbosch/detekt/pull/1876)
- Make all Gradle tasks cacheable - [#1875](https://github.com/arturbosch/detekt/pull/1875)
- Indentation for compliant and non-compliant code examples is missing - [#1871](https://github.com/arturbosch/detekt/issues/1871)
- Don't build twice when PR created from branch - [#1866](https://github.com/arturbosch/detekt/pull/1866)
- Print rendered report if it's null or blank - [#1862](https://github.com/arturbosch/detekt/pull/1862)
- Silence report if containing no findings - [#1860](https://github.com/arturbosch/detekt/pull/1860)
- Group console violation reports by file - [#1852](https://github.com/arturbosch/detekt/pull/1852)
- Update Kotlin to v1.3.50 - [#1841](https://github.com/arturbosch/detekt/pull/1841)
- Gradle 5.6 - [#1833](https://github.com/arturbosch/detekt/pull/1833)
- Implement rule to suggest array literal instead of arrayOf-expression in annotations - [#1823](https://github.com/arturbosch/detekt/pull/1823)
- Make UnsafeCast less aggressive - [#1601](https://github.com/arturbosch/detekt/issues/1601)
- Consider publishing artifacts to mavenCentral instead of jCenter - [#1396](https://github.com/arturbosch/detekt/issues/1396)
- false positive unnecessary parentheses in conjunction with lambdas - [#1222](https://github.com/arturbosch/detekt/issues/1222)
- False positives on UnsafeCast on AppVeyor (Windows?) only - [#1137](https://github.com/arturbosch/detekt/issues/1137)
- PreferToOverPairSyntax false positive - [#1066](https://github.com/arturbosch/detekt/issues/1066)
- Rule ForbiddenComment - regexp support - [#959](https://github.com/arturbosch/detekt/issues/959)
- UnsafeCallOnNullableType should not be reported for platform types - [#880](https://github.com/arturbosch/detekt/issues/880)
- Exclusion patterns in detekt-config - [#775](https://github.com/arturbosch/detekt/issues/775)
- Rule: OptionalTypeDeclaration - [#336](https://github.com/arturbosch/detekt/issues/336)
- Check if it is feasible to integrate ktlint as a rule set - [#38](https://github.com/arturbosch/detekt/issues/38)

See all issues at: [1.1.0](https://github.com/arturbosch/detekt/milestone/19)

#### 1.0.1

##### Notable changes

- [detekt runs can be completely silent on absence of findings](https://arturbosch.github.io/detekt/howto-silent-reports.html)
- All detekt's dependencies are now on MavenCentral. Bogus "*could not find JCommander dependency*" should be gone.

##### Changelog

- Migrate SafeCast test cases to JSR223 - [#1832](https://github.com/arturbosch/detekt/pull/1832)
- Remove unused case entries - [#1831](https://github.com/arturbosch/detekt/pull/1831)
- Migrate ComplexInterface test cases to JSR223 - [#1830](https://github.com/arturbosch/detekt/pull/1830)
- Remove <pluginrepository> from maventask.md - [#1827](https://github.com/arturbosch/detekt/pull/1827)
- Update maven-task --excludes arg - [#1825](https://github.com/arturbosch/detekt/pull/1825)
- Improve grammar on 1.0 release post - [#1822](https://github.com/arturbosch/detekt/pull/1822)
- Write guide on how to make detekt silent - [#1819](https://github.com/arturbosch/detekt/pull/1819)
- Use notifications instead of println - [#1818](https://github.com/arturbosch/detekt/pull/1818)
- JCommander 1.78 - [#1817](https://github.com/arturbosch/detekt/pull/1817)
- Fix typo in spelling of --auto-correct flag - [#1816](https://github.com/arturbosch/detekt/pull/1816)
- Update dependency versions - [#1815](https://github.com/arturbosch/detekt/pull/1815)
- Tidy the build files - [#1814](https://github.com/arturbosch/detekt/pull/1814)
- Downgrade to jcommander v1.72 - [#1809](https://github.com/arturbosch/detekt/pull/1809)
- Update docs to mention test-pattern deprecation - [#1808](https://github.com/arturbosch/detekt/pull/1808)
- Quiet mode or ability to disable all output in Gradle - [#1797](https://github.com/arturbosch/detekt/issues/1797)

See all issues at: [1.0.1](https://github.com/arturbosch/detekt/milestone/47)


#### 1.0.0

##### Migration

- Gradle Plugin: removed report consolidation. It was flawed and some users were stuck with RC14. It will be replaced in a further version.
- Gradle Plugin: `autoCorrect` property is now allowed on the detekt extension. No need to create a new task anymore.
- Formatting: updated to KtLint 0.34.2 which removed the two rules `NoItParamInMultilineLambda` and `SpacingAroundUnaryOperators`. 

##### Changelog

- Gradle plugin: Set default path for "config" parameter - [#1801](https://github.com/arturbosch/detekt/pull/1801)
- Don't use provider value that may not have been set - [#1800](https://github.com/arturbosch/detekt/pull/1800)
- Remove raw URLs from README - [#1799](https://github.com/arturbosch/detekt/pull/1799)
- Add missing autoCorrect declarations - [#1798](https://github.com/arturbosch/detekt/pull/1798)
- Docs: Missing autoCorrect option for various rules - [#1796](https://github.com/arturbosch/detekt/issues/1796)
- Update to KtLint 0.34.2 - [#1791](https://github.com/arturbosch/detekt/pull/1791)
- Add auto correct flag to detekt extension - [#1790](https://github.com/arturbosch/detekt/pull/1790)
- Gradle plugin: Fix visibility of internal properties - [#1789](https://github.com/arturbosch/detekt/pull/1789)
- Check classes and functions documented for api module - [#1788](https://github.com/arturbosch/detekt/pull/1788)
- Provide default value for ignoreFailures - [#1787](https://github.com/arturbosch/detekt/pull/1787)
- Update detekt-api documentation - [#1786](https://github.com/arturbosch/detekt/pull/1786)
- Document meanings of rule severity levels - [#1785](https://github.com/arturbosch/detekt/pull/1785)
- Remove unused code - [#1784](https://github.com/arturbosch/detekt/pull/1784)
- Fix UseDataClass false positive (delegation) - [#1783](https://github.com/arturbosch/detekt/pull/1783)
- Add ignore pattern to SwallowedException - [#1782](https://github.com/arturbosch/detekt/pull/1782)
- Prevent adding author tags in code - [#1776](https://github.com/arturbosch/detekt/pull/1776)
- Remove xml report consolidation. - [#1774](https://github.com/arturbosch/detekt/pull/1774)
- Update Complex Method doc - closes #1009 - [#1773](https://github.com/arturbosch/detekt/pull/1773)
- Implement dry-run option for detekt gradle tasks. - [#1769](https://github.com/arturbosch/detekt/pull/1769)
- Fix missing report file issue. - [#1767](https://github.com/arturbosch/detekt/pull/1767)
- Not running formatting autocorrect - [#1766](https://github.com/arturbosch/detekt/issues/1766)
- Check if file exists before considering it for report merges - [#1763](https://github.com/arturbosch/detekt/pull/1763)
- Preset ignoreFailures property with false as it is also used by Gradle - [#1762](https://github.com/arturbosch/detekt/pull/1762)
- Rearrange badges, add codefactor - [#1760](https://github.com/arturbosch/detekt/pull/1760)
- Update Kotlin to 1.3.41 - [#1759](https://github.com/arturbosch/detekt/pull/1759)
- Update EmptyClassBlock to skip classes with comments in the body - [#1758](https://github.com/arturbosch/detekt/pull/1758)
- EmptyClassBlock should consider comment as "body" (via option?) - [#1756](https://github.com/arturbosch/detekt/issues/1756)
- Remove obsolete NoWildcardImports autoCorrect param - [#1751](https://github.com/arturbosch/detekt/pull/1751)
- Kotlin language version handling - [#1748](https://github.com/arturbosch/detekt/pull/1748)
- Fix cli execution doc - [#1747](https://github.com/arturbosch/detekt/pull/1747)
- Add naming test for ForbiddenVoid rule - [#1740](https://github.com/arturbosch/detekt/pull/1740)
- ForbiddenVoid: New option 'ignoreUsageInGenerics' - [#1738](https://github.com/arturbosch/detekt/pull/1738)
- Default Gradle config path should be config/detekt/config.yml - [#1262](https://github.com/arturbosch/detekt/issues/1262)

See all issues at: [1.0.0](https://github.com/arturbosch/detekt/milestone/46)

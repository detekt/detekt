---
title: Changelog and Migration Guide
sidebar: home_sidebar
keywords: changelog, release-notes, migration
permalink: changelog.html
toc: true
---

#### 1.2.0 - unreleased

##### Notable changes

- `parallel=true` and `--parallel` now effect both the compilation and analysis phase. 
- Users of Gradle's `--parallel` flag are encouraged to turn off the parallelism of detekt. Or turn it on otherwise.
- detekt does not use the `ForkJoinPool.commonPool()` anymore internally. When embedding detekt feel free to pass your own `ExecutionService` to the `ProcessingSettings`.

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


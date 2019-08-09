---
title: Changelog and Migration Guide
sidebar: home_sidebar
keywords: changelog, release-notes, migration
permalink: changelog.html
toc: true
---

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


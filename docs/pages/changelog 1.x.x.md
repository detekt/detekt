---
title: Changelog and Migration Guide
sidebar: home_sidebar
keywords: changelog, release-notes, migration
permalink: changelog.html
toc: true
---

<!--
#### Coming up

##### Migration
-->
#### 1.0.0 (detekt-core + detekt-cli)

##### Migration from RC12

- 'failFast' inside the yaml config was deprecated. Please use the `--fail-fast` cli flag or `failFast` detekt extension property in the gradle plugin.
- `--buildUponDefaultConfig` allows to run detekt with the default config without specifying it duplicating it in your project.
All users are encouraged who use the default config and override some properties in a second config file to migrate.
- LongMethod and LargeClass rules got refactored and now count lines and not statements, you may need to change your defaults
- `.*/androidTest/.*` and `*Spek.kt` are now 

##### Changelog

- Update to Kotlin v1.3.21 - [#1460](https://github.com/arturbosch/detekt/pull/1460)
- Gradle 5.2 - [#1458](https://github.com/arturbosch/detekt/pull/1458)
- Treat androidTest directory and *Spek.kt files as test sources. - [#1456](https://github.com/arturbosch/detekt/pull/1456)
- Exclude inline classes as they are a light form of data class - Closes  #1450 - [#1454](https://github.com/arturbosch/detekt/pull/1454)
- Publish shadowed jars (-all) - [#1453](https://github.com/arturbosch/detekt/pull/1453)
- Reimplement lines of code based rules (LargeClass & LongMethod) - [#1448](https://github.com/arturbosch/detekt/pull/1448)
- Add test case for TooManyFunctons fix - #1439 - [#1447](https://github.com/arturbosch/detekt/pull/1447)
- Fix false positives for UnnecessaryApply rule - Closes #1305 - [#1446](https://github.com/arturbosch/detekt/pull/1446)
- Skip operator functions when searching for unused private members - #1354 - [#1445](https://github.com/arturbosch/detekt/pull/1445)
- Do not report annotation classes as candidates for utility class - #1428 - [#1442](https://github.com/arturbosch/detekt/pull/1442)
- Do not report unused import when alias is used by import from same package - [#1441](https://github.com/arturbosch/detekt/pull/1441)
- Update Kotlin to 1.3.20 - [#1438](https://github.com/arturbosch/detekt/pull/1438)
- Mention jcenter repository in getting started guides - [#1437](https://github.com/arturbosch/detekt/pull/1437)
- Do not report main functions args parameter in objects - [#1436](https://github.com/arturbosch/detekt/pull/1436)
- Update default package naming rule according to official style guide. Closes #1429 - [#1434](https://github.com/arturbosch/detekt/pull/1434)
- UtilityClassWithPublicConstructor on annotation class - [#1428](https://github.com/arturbosch/detekt/issues/1428)
- UnusedPrivateMember with main method - [#1427](https://github.com/arturbosch/detekt/issues/1427)
- Ignore InstanceOfCheckForException by default for tests. - [#1424](https://github.com/arturbosch/detekt/pull/1424)
- Respect @Suppress in UnusedPrivateMember - [#1423](https://github.com/arturbosch/detekt/pull/1423)
- Documentation - using Gradle 5.0, need jcenter() in top-level dependencies block, not just buildscript - [#1420](https://github.com/arturbosch/detekt/issues/1420)
- Build upon default config for cli module - [#1417](https://github.com/arturbosch/detekt/pull/1417)
- Better wording when default config isn't up to date. - [#1416](https://github.com/arturbosch/detekt/pull/1416)
- UnusedPrivateClass does not consider when class is referenced via ::class.java - [#1410](https://github.com/arturbosch/detekt/issues/1410)
- Slight tweak to lazy task configuration. - [#1407](https://github.com/arturbosch/detekt/pull/1407)
- Reformat code base before 1.0 - [#1406](https://github.com/arturbosch/detekt/issues/1406)
- Introduce script to find and compare differences between two releases - [#1405](https://github.com/arturbosch/detekt/pull/1405)
- Add a section about how to integrate custom extensions into detekt - [#1403](https://github.com/arturbosch/detekt/pull/1403)
- Run detekt on detekt-gradle-plugin - [#1402](https://github.com/arturbosch/detekt/pull/1402)
- Add LabeledExpression ignore label option - [#1399](https://github.com/arturbosch/detekt/pull/1399)
- Consider double colon references for class usages - Closes #1390 - [#1391](https://github.com/arturbosch/detekt/pull/1391)
- False positive UnusedPrivateClass when using class references - [#1390](https://github.com/arturbosch/detekt/issues/1390)
- Fix some issues reported by IntelliJ - [#1389](https://github.com/arturbosch/detekt/pull/1389)
- Minor improvements to detekt-rules - [#1388](https://github.com/arturbosch/detekt/pull/1388)
- False positive on UnusedImport when using 'as' and the same package - [#1385](https://github.com/arturbosch/detekt/issues/1385)
- Document api module for 1.0 - [#1382](https://github.com/arturbosch/detekt/pull/1382)
- false positive EmptyDefaultConstructor for annotation - [#1362](https://github.com/arturbosch/detekt/issues/1362)
- Update to Gradle v5 - [#1350](https://github.com/arturbosch/detekt/pull/1350)
- False Positive UnnecessaryApply - [#1305](https://github.com/arturbosch/detekt/issues/1305)
- LongMethod counts statements but not actual lines as its description says - [#1279](https://github.com/arturbosch/detekt/issues/1279)
- Build upon the default configuration - [#1248](https://github.com/arturbosch/detekt/issues/1248)
- Documentation for ktlint integration - [#925](https://github.com/arturbosch/detekt/issues/925)
- Config file paths should be recognized cross-platform - [#637](https://github.com/arturbosch/detekt/issues/637)
- Story: Improve documentation of detekt - [#496](https://github.com/arturbosch/detekt/issues/496)
- Example extension project should have its own build.gradle and a pom.xml - [#438](https://github.com/arturbosch/detekt/issues/438)
- Change threshold definition in detekt - [#313](https://github.com/arturbosch/detekt/issues/313)
- Update to kotlin 1.1.4 asap - [#244](https://github.com/arturbosch/detekt/issues/244)
- Prototype wiki structure - [#192](https://github.com/arturbosch/detekt/issues/192)
- Warnings are not reported at the correct line number - [#139](https://github.com/arturbosch/detekt/issues/139)
- Renaming rule names for 1.0.0 - [#109](https://github.com/arturbosch/detekt/issues/109)
- visit(root: KtFile) should be effectively final - [#107](https://github.com/arturbosch/detekt/issues/107)
- Configuration file with all possible rules and their values - [#61](https://github.com/arturbosch/detekt/issues/61)
- Idea: detekt-sonarqube-plugin - [#21](https://github.com/arturbosch/detekt/issues/21)
- As an user I want wiki pages with explanation of the common tasks I can do with detekt - [#17](https://github.com/arturbosch/detekt/issues/17)
- java 9 - reflection cannot make constructor private - [#12](https://github.com/arturbosch/detekt/issues/12)

See all issues at: [1.0.0](https://github.com/arturbosch/detekt/milestone/3)

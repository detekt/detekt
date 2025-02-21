---
id: changelog
title: Changelog and Migration Guide
keywords: [changelog, release-notes, migration]
---

# Changelog and Migration Guide

#### 1.23.8 - 2025-02-20

This is a point release for Detekt `1.23.0`, built against Kotlin `2.0.21`, with fixes for several bugs that got reported by the community.

##### Notable Changes

- fix(deps): Update AGP to v8.8.0 - [#7879](https://github.com/detekt/detekt/pull/7879)
- fix(deps): Update kotlin to 2.0.21 - [#7580](https://github.com/detekt/detekt/pull/7580)
- fix(deps): update Gradle to v8.10.2 - [#7668](https://github.com/detekt/detekt/pull/7668)

##### Changelog

- UseDataClass: do not report on `expect` classes - [#7857](https://github.com/detekt/detekt/pull/7857)
- Fix InjectDispatcher false positives - [#7797](https://github.com/detekt/detekt/pull/7797)
- [UnnecessaryParentheses] Allow float/double without integer part - [#7751](https://github.com/detekt/detekt/pull/7751)
- Fix `ThrowingExceptionsWithoutMessageOrCause` false positive - [#7715](https://github.com/detekt/detekt/pull/7715)
- Issue #7634: Make `UndocumentedPublicClass` configurable to flag `com… - [#7635](https://github.com/detekt/detekt/pull/7635)
- Fix redundant empty tags in baseline XML - [#7625](https://github.com/detekt/detekt/pull/7625)
- MatchingDeclarationName now supports platofrm suffixes - [#6426](https://github.com/detekt/detekt/pull/6426)

##### Contributors

We would like to thank the following contributors that made this release possible: @BraisGabin, @JordanLongstaff, @Nava2, @atulgpt, @eygraber, @lexa-diky, @t-kameyama

#### 1.23.7 - 2024-09-08

This is a point release for Detekt `1.23.0`, built against Kotlin `2.0.10`, with fixes for several bugs that got reported by the community.

##### Notable Changes

- fix(deps): update kotlin monorepo to v2.0.10 - [#7517](https://github.com/detekt/detekt/pull/7517)
- Update to Kotlin 2.0.0 [#6640](https://github.com/detekt/detekt/pull/6640)
- fix(deps): update kotlin monorepo to v1.9.24 - [#7264](https://github.com/detekt/detekt/pull/7264)
- fix(deps): update dependency com.android.tools.build:gradle to v8.5.2 - [#7525](https://github.com/detekt/detekt/pull/7525)
- chore(deps): update dependency gradle to v8.10 - [#7546](https://github.com/detekt/detekt/pull/7546)

##### Changelog

- Add basic support for isolated projects to 1.x - [#7526](https://github.com/detekt/detekt/pull/7526)
- ExplicitCollectionElementAccessMethod: fix false positive when Map put has 3 arguments - [#7563](https://github.com/detekt/detekt/pull/7563)
- BracesOnIfStatements: fix false-positive when chained - [#7444](https://github.com/detekt/detekt/pull/7444)
- Add enum entry check in `UndocumentedPublicProperty` - [#7426](https://github.com/detekt/detekt/pull/7426)
- Use the anchor which is already present before - [#7423](https://github.com/detekt/detekt/pull/7423)
- Fix small corner-case in "SerialVersionUIDInSerializableClass" rule, … - [#7346](https://github.com/detekt/detekt/pull/7346)
- SwallowedException: fix false positive when exception is used as a receiver - [#7288](https://github.com/detekt/detekt/pull/7288)
- NamedArguments: fix false positive on spread varargs - [#7283](https://github.com/detekt/detekt/pull/7283)
- MultilineLambdaItParameter: fix false negative with single statement on multiple lines - [#7221](https://github.com/detekt/detekt/pull/7221)
- Check for root of receiver in selector expression - [#7220](https://github.com/detekt/detekt/pull/7220)
- Check for `public companion` object for `UndocumentedPublicClass` - [#7219](https://github.com/detekt/detekt/pull/7219)
- fix: TopLevelPropertyNaming also detecting extension property name - [#7212](https://github.com/detekt/detekt/pull/7212)
- Publish detekt-compiler-plugin-all to Maven and GH Releases - [#7179](https://github.com/detekt/detekt/pull/7179)
- versioned default detekt config file link - [#7161](https://github.com/detekt/detekt/pull/7161)
- Support rangeUntil operator for UnusedImport rule - [#7104](https://github.com/detekt/detekt/pull/7104)
- Fix false positive on it usages when type parameter is specified - [#6850](https://github.com/detekt/detekt/pull/6850) 

##### Housekeeping/Docs

- [bugfix] AnnotationOnSeparateLine in snippets - [#6526](https://github.com/detekt/detekt/pull/6526)
-  Add docs about using the Compiler Plugin with the Kotlin CLI compiler - [#7184](https://github.com/detekt/detekt/pull/7184)

#### 1.23.6 - 2024-03-23

This is a point release for Detekt `1.23.0`, where we added support for Kotlin `1.9.23` and fixed several bugs that
got reported by the community.

##### Changelog

-   Don't allow invalid Source Locations - [#7030](https://github.com/detekt/detekt/pull/7030)
-   UnusedPrivateClass: don't report if private classes are used for type conversion - [#6995](https://github.com/detekt/detekt/pull/6995)
-   RedundantSuspendModifier: do not report when the function has 'actual' modifier - [#6951](https://github.com/detekt/detekt/pull/6951)
-   Update dependency gradle to v8.7 - [#7080](https://github.com/detekt/detekt/pull/7080)
-   Update kotlin monorepo to v1.9.23 - [#7027](https://github.com/detekt/detekt/pull/7027)
-   Update dependency gradle to v8.6 - [#6939](https://github.com/detekt/detekt/pull/6939)
-   Update dependency com.android.tools.build:gradle to v8.3.1 - [#7070](https://github.com/detekt/detekt/pull/7070)

##### Contributors

We would like to thank the following contributors that made this release possible: @BraisGabin, @t-kameyama

#### 1.23.5 - 2024-01-31

This is a point release for Detekt `1.23.0`, where we added support for Kotlin `1.9.22` and fixed several bugs that
got reported by the community.

##### Notable Changes

-   Test Gradle plugin with AGP 8.2.0 - [#6672](https://github.com/detekt/detekt/pull/6672)
-   chore(deps): update dependency gradle to v8.5 - [#6663](https://github.com/detekt/detekt/pull/6663)
-   fix(deps): update kotlin monorepo to v1.9.22 - [#6746](https://github.com/detekt/detekt/pull/6746)

##### Changelog

-   Report rule's default severity in sarif backport - [#6916](https://github.com/detekt/detekt/pull/6916)
-   Add ignoreAnnotatedFunctions to TooManyFunctions - [#6875](https://github.com/detekt/detekt/pull/6875)
-   Fix by checking the existence of label in previous statements - [#6671](https://github.com/detekt/detekt/pull/6671)
-   Also check `USELESS_ELVIS` in `UnreachableCode` - [#6624](https://github.com/detekt/detekt/pull/6624)
-   Fix by adding last method call to the set - [#6567](https://github.com/detekt/detekt/pull/6567)

##### Housekeeping & Refactorings

-   Opt in to ExperimentalCompilerApi in all compilations in compiler-plugin - [#6534](https://github.com/detekt/detekt/pull/6534)

##### Contributors

We would like to thank the following contributors that made this release possible: @3flex, @TWiStErRob, @atulgpt, @matejdro

#### 1.23.4 - 2023-11-26

This is a point release for Detekt `1.23.0`, where we added support for Kotlin `1.9.21` and fixed several bugs that
got reported by the community.

##### Notable Changes

-   fix(deps): update kotlin monorepo to v1.9.21 - [#6642](https://github.com/detekt/detekt/pull/6642)
-   fix(deps): update kotlin monorepo to v1.9.20 - [#6572](https://github.com/detekt/detekt/pull/6572)

##### Changelog

-   Update the ruleset regex to allow numbers - [#6635](https://github.com/detekt/detekt/pull/6635)
-   Show deprecation message - [#6614](https://github.com/detekt/detekt/pull/6614)
-   Add io.gitlab.arturbosch.detekt.generator.Main to the jar manifest - [#6613](https://github.com/detekt/detekt/pull/6613)
-   Don't report FunctionNaming when the function's name equals to the return type's name with type arguments - [#6605](https://github.com/detekt/detekt/pull/6605)
-   Fix issues related to kotlin-stdlib's Gradle module metadata in Kotlin 1.9.20 - [#6590](https://github.com/detekt/detekt/pull/6590)
-   MethodSignature - Add the condition of receiver should be null - [#6570](https://github.com/detekt/detekt/pull/6570)
-   Remove check for deprectated functions toUpperCase and toLowerCase - [#6548](https://github.com/detekt/detekt/pull/6548)
-   Fixes false positive of trailing whitespaces in kdoc - [#6370](https://github.com/detekt/detekt/pull/6370)

##### Dependency Updates

-   Update tested AGP version to 8.1.3 - [#6610](https://github.com/detekt/detekt/pull/6610)
-   Switch to kctfork for Kotlin compilation testing - [#6589](https://github.com/detekt/detekt/pull/6589)

##### Contributors

We would like to thank the following contributors that made this release possible: @3flex, @BraisGabin, @Gosunet, @atulgpt, @t-kameyama

#### 1.23.3 - 2023-10-31

This is a point release for Detekt `1.23.0`. The changelog is equivalent to `1.23.2`.

#### 1.23.2 - 2023-10-29

**Note: please use version 1.23.3 instead as Kotlin 1.9.10 support was added there**

This is a point release for Detekt `1.23.0`, where we added support for Kotlin `1.9.10` and fixed several bugs that
got reported by the community.

##### Changelog

-   Consider deprecated rules as inactive when running allRules [#6381](https://github.com/detekt/detekt/pull/6381)
-   Fix inputstream leaking file descriptor in Gradle - [#6519](https://github.com/detekt/detekt/pull/6519)
-   ForbiddenMethodCall - Handle sequence of overridden methods - [#6478](https://github.com/detekt/detekt/pull/6478)
-   Function to return supported Kotlin version - [#6472](https://github.com/detekt/detekt/pull/6472)
-   Fix false negative `IgnoredReturnValue` with scope functions - [#6446](https://github.com/detekt/detekt/pull/6446)
-   UnconditionalJumpStatementInLoop: don't report a conditional break in a single body expression - [#6443](https://github.com/detekt/detekt/pull/6443)
-   Fix reporting unused imports at file level - [#6390](https://github.com/detekt/detekt/pull/6390)
-   OutdatedDocumentation - Detect param which private property documented as property - [#6372](https://github.com/detekt/detekt/pull/6372)
-   NullableToStringCall: fix false negative in lambda - [#6352](https://github.com/detekt/detekt/pull/6352)
-   Correctly set scheme for URIs in the SARIF report output - [#6331](https://github.com/detekt/detekt/pull/6331)
-   SleepInsteadOfDelay - Find the parent for suspend check - [#6191](https://github.com/detekt/detekt/pull/6191)

##### Dependency Updates

-   Update kotlin monorepo to v1.9.10 - [#6423](https://github.com/detekt/detekt/pull/6423)
-   Update dependency gradle to v8.4 - [#6522](https://github.com/detekt/detekt/pull/6522)
-   Update dependency gradle to v8.3 - [#6406](https://github.com/detekt/detekt/pull/6406)
-   Update com.android.tools.build to 8.1.1 - [#6419](https://github.com/detekt/detekt/pull/6419)
-   update dependency org.jetbrains.dokka to v1.9.10 - [#6549](https://github.com/detekt/detekt/pull/6549)

##### Contributors

We would like to thank the following contributors that made this release possible: @3flex, @arturbosch, @atulgpt, @kkocel, @marschwar, @pablobaxter, @t-kameyama

#### 1.23.1 - 2023-07-30

This is a point release for Detekt `1.23.0`, where we added support for Kotlin `1.9.0` and fixed several bugs that
got reported by the community.

##### Notable Changes

-   Bumped Kotlin to v1.9.0 - [#6258](https://github.com/detekt/detekt/pull/6258)
-   Bumped KtLint to v0.50.0 - [#6239](https://github.com/detekt/detekt/pull/6239)
-   Updated CLI to reflect Java 20 support - [#6277](https://github.com/detekt/detekt/pull/6277)

##### Changelog

-   Add missing jdkHome and languageVersion properties to DetektCreateBaselineTask - [#6284](https://github.com/detekt/detekt/pull/6284)
-   Make InvalidRange aware of rangeUntil operator - [#6264](https://github.com/detekt/detekt/pull/6264)
-   MaxChainedCallsOnSameLine: don't count class references as chained calls - [#6224](https://github.com/detekt/detekt/pull/6224)
-   CanBeNonNullable: Fix false positive when property is defined after assignment - [#6210](https://github.com/detekt/detekt/pull/6210)
-   Add `..<` and `rangeTo` in the `ForEachOnRange` rule - [#6197](https://github.com/detekt/detekt/pull/6197)
-   Don't report `UseDataClass` if class contains non-property parameters - [#6173](https://github.com/detekt/detekt/pull/6173)
-   Allow documenting public fun name when same private variable is present - [#6165](https://github.com/detekt/detekt/pull/6165)
-   Find range call using recursion - [#6164](https://github.com/detekt/detekt/pull/6164)
-   StringShouldBeRawString: Ignore `replaceIndent` and `prependIndent` - [#6154](https://github.com/detekt/detekt/pull/6154)
-   UnusedPrivateProperty: Fix false postive by ignoring data classes - [#6151](https://github.com/detekt/detekt/pull/6151)
-   PropertyUsedBeforeDeclaration: fix false positive in nested/inner class - [#6139](https://github.com/detekt/detekt/pull/6139)

##### Dependency Updates

-   Update dependency gradle to v8.2.1 - [#6274](https://github.com/detekt/detekt/pull/6274)
-   Switch to SLF4J 2.x - [#6266](https://github.com/detekt/detekt/pull/6266)
-   Update kotlin monorepo to v1.8.22 - [#6192](https://github.com/detekt/detekt/pull/6192)

##### Contributors

We would like to thank the following contributors that made this release possible: @3flex, @Goooler, @Hexcles, @PoisonedYouth, @TWiStErRob, @VirtualParticle, @atulgpt, @cortinico, @dzirbel, @eygraber, @marschwar, @rmarquis, @segunfamisa, @severn-everett, @t-kameyama

[See all commit history here](https://github.com/detekt/detekt/compare/v1.23.0...v1.23.1)

#### 1.23.0 - 2023-05-22

##### Notable Changes

- This is the first version of Detekt that ships with the `detekt-compiler-plugin`. The [Detekt Compiler plugin](/docs/next/gettingstarted/compilerplugin) is still experimental, but we're moving it closer to Detekt to make it easier to integrate. From now on the compiler plugin will follow the same versioning schema as Detekt. We invite you to try it and provide feedback till we stabilize it. You can read more about it in the [official documentation page](/docs/next/gettingstarted/compilerplugin) - [#5492](https://github.com/detekt/detekt/pull/5492)
-   We added **25** new Rules to detekt
    -   `BracesOnIfStatements` - [#5700](https://github.com/detekt/detekt/pull/5700)
    -   `BracesOnWhenStatements` - [#5838](https://github.com/detekt/detekt/pull/5838)
    -   `CastNullableToNonNullableType` - [#5653](https://github.com/detekt/detekt/pull/5653)
    -   `DoubleNegativeLambda` - [#5937](https://github.com/detekt/detekt/pull/5937)
    -   `ForbiddenAnnotation` - [#5515](https://github.com/detekt/detekt/pull/5515)
    -   `PropertyUsedBeforeDeclaration` - [#6062](https://github.com/detekt/detekt/pull/6062)
    -   `StringShouldBeRawString` - [#5705](https://github.com/detekt/detekt/pull/5705)
    -   `SuspendFunSwallowedCancellation` - [#5666](https://github.com/detekt/detekt/pull/5666)
    -   `UnusedParameter` - [#5722](https://github.com/detekt/detekt/pull/5722)
    -   `UnusedPrivateProperty` - [#5722](https://github.com/detekt/detekt/pull/5722)
    -   `UseLet` - [#6091](https://github.com/detekt/detekt/pull/6091)
    -   `UnnecessaryBracesAroundTrailingLambda` - [#6029](https://github.com/detekt/detekt/pull/6029)
    -   Plus the bump to KtLint 0.49.1 added the following rules to the `detekt-formatting` ruleset:
        -   `ClassName` - [#6037](https://github.com/detekt/detekt/pull/6037)
        -   `EnumWrapping` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `FunctionName` - [#6037](https://github.com/detekt/detekt/pull/6037)
        -   `IfElseBracing` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `IfElseWrapping` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `MultilineExpressionWrapping` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `NoBlankLineInList` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `NoConsecutiveComments` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `NoEmptyFirstLineInClassBody` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `NoSingleLineBlockCommentRule` - [#6104](https://github.com/detekt/detekt/pull/6104)
        -   `ParameterWrapping` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `PropertyName` - [#6037](https://github.com/detekt/detekt/pull/6037)
        -   `PropertyWrapping` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `StringTemplateIndent` - [#6028](https://github.com/detekt/detekt/pull/6028)
        -   `TryCatchFinallySpacing` - [#6028](https://github.com/detekt/detekt/pull/6028)
-   Notable changes to existing rules:
    -   `UnnecessaryAbstractClass` now only runs with type resolution - [#5829](https://github.com/detekt/detekt/pull/5829)
    -   `UnusedPrivateMember` has been refactored with some of its logic moved to `UnusedParameter` and `UnusedPrivateProperty` - [#5722](https://github.com/detekt/detekt/pull/5722)
    -   Removed the `ignoreOverridden` config from `BooleanPropertyNaming`, `ConstructorParameterNaming`, `FunctionNaming`, `VariableNaming` and `FunctionParameterNaming` as not useful for those rules - [#5718](https://github.com/detekt/detekt/pull/5718)
    -   Added `ignoredSubjectTypes` to rule `ElseCaseInsteadOfExhaustiveWhen` to specify types that should be ignored by the rule (#5623) - [#5634](https://github.com/detekt/detekt/pull/5634)
    -   Added `allowOperators` to rule `DataClassContainsFunctions` - [#5658](https://github.com/detekt/detekt/pull/5658)
    -   `MandatoryBracesIfStatements` has been removed in favor of `BracesOnIfStatements` - [#5700](https://github.com/detekt/detekt/pull/5700)
    -   Added `ignoreWhenContainingVariableDeclaration` to `UseIfInsteadOfWhen` to ignore captured variables - [#5681](https://github.com/detekt/detekt/pull/5681)
    -   Several rules in the `detekt-formatting` ruleset now accepts an `indentSize` parameter (see the [full list here](https://github.com/detekt/detekt/pull/6028/files#diff-2486d56e6f1bbfcb160b923d15266691f5776948cf5bb31c8fc102bd3cf9513d)).
    -   We followed the KtLint decisions on turning on some rules by default, so several rules in the `detekt-formatting` ruleset are now turned on by default (see the [full list here](https://github.com/detekt/detekt/pull/6028/files#diff-2486d56e6f1bbfcb160b923d15266691f5776948cf5bb31c8fc102bd3cf9513d)).
-   We added support for Gradle's Worker API inside Detekt Gradle Plugin, for faster execution on bigger projects. - [#4128](https://github.com/detekt/detekt/pull/4128)
-   We fixed the `includes`/`excludes` logic on the config file as they were overriding each other - [#5782](https://github.com/detekt/detekt/pull/5782)
-   We fully removed support for Spek from `detekt-test-utils`. The recommended testing framework is JUnit - [#5785](https://github.com/detekt/detekt/pull/5785)
-   The minimum supported Gradle version is now `v6.8.3` - [#5616](https://github.com/detekt/detekt/pull/5616)
-   This version of detekt is built with Gradle `v8.1`, AGP `8.0.1`, Kotlin `1.8.21` and KtLint `0.49.1` (see [#5893](https://github.com/detekt/detekt/pull/5893) [#5723](https://github.com/detekt/detekt/pull/5723) [#5877](https://github.com/detekt/detekt/pull/5877) [#6028](https://github.com/detekt/detekt/pull/6028) [#6043](https://github.com/detekt/detekt/pull/6043) [#5995](https://github.com/detekt/detekt/pull/5995) [#5996](https://github.com/detekt/detekt/pull/5996))
-   We now added a [Code of Conduct](https://github.com/detekt/detekt/blob/main/.github/CODE_OF_CONDUCT.md) to our repo. Please read it and follow it when interacting with our community on our channels.

##### Changelog

-   SerialVersionUIDInSerializableClass - Update the error location - [#6114](https://github.com/detekt/detekt/pull/6114)
-   Reduce LoopWithTooManyJumpStatements finding scope - [#6110](https://github.com/detekt/detekt/pull/6110)
-   Add alias for SuspendFunWithCoroutineScopeReceiver - [#6089](https://github.com/detekt/detekt/pull/6089)
-   CastNullableToNonNullableType - Check the SimpleType instead of typeElement - [#6071](https://github.com/detekt/detekt/pull/6071)
-   Update plugin com.gradle.enterprise to v3.13.1 - [#6069](https://github.com/detekt/detekt/pull/6069)
-   CanBeNonNullable: Check parent condition for checking if nullability info is used or not - [#6064](https://github.com/detekt/detekt/pull/6064)
-   Add configuration to add alternate trimming methods - [#6063](https://github.com/detekt/detekt/pull/6063)
-   Check if property is documented at class header - [#6061](https://github.com/detekt/detekt/pull/6061)
-   OutdatedDocumentation - Check if only public property is documented - [#6057](https://github.com/detekt/detekt/pull/6057)
-   UnnecessaryLet: fix false positive in call chains - [#6052](https://github.com/detekt/detekt/pull/6052)
-   Add `comments` with a list of regexes to `ForbiddenComment` - [#5981](https://github.com/detekt/detekt/pull/5981)
-   Fix incomplete `requireRootInDeclaration` check in `InvalidPackageDeclaration` - [#6045](https://github.com/detekt/detekt/pull/6045)
-   BracesOnWhenStatements: fix false positive for necessary braces - [#6042](https://github.com/detekt/detekt/pull/6042)
-   Fix redundant ClassOrdering violations using maximum increasing section - [#6003](https://github.com/detekt/detekt/pull/6003)
-   UseIsNullOrEmpty: fix false negative with chained call - [#6027](https://github.com/detekt/detekt/pull/6027)
-   Create docs for Gradle Worker API - [#6016](https://github.com/detekt/detekt/pull/6016)
-   Compile compiler plugin against kotlin-compiler-embeddable - [#6012](https://github.com/detekt/detekt/pull/6012)
-   Update intro.mdx to use setFrom() on detekt config - [#6010](https://github.com/detekt/detekt/pull/6010)
-   Use `detekt-versions.properties` instead of `versions.properties` - [#6006](https://github.com/detekt/detekt/pull/6006)
-   Implement parentPath accessor in concrete config implementations - [#6002](https://github.com/detekt/detekt/pull/6002)
-   Increase ALIASES_LIMIT to 100 for YamlConfig - [#5986](https://github.com/detekt/detekt/pull/5986)
-   Fix elvis to +/- case in case UnnecessaryParentheses when precedence is unclear - [#5983](https://github.com/detekt/detekt/pull/5983)
-   jdkHome as @Internal - [#5978](https://github.com/detekt/detekt/pull/5978)
-   Fix broken POM file for detetk-compiler-plugin - [#5971](https://github.com/detekt/detekt/pull/5971)
-   Fix broken publishToMavenLocal - [#5970](https://github.com/detekt/detekt/pull/5970)
-   Add `allowOmitUnit` to rule `LibraryCodeMustSpecifyReturnType` - [#5861](https://github.com/detekt/detekt/pull/5861)
-   Remove dependency that creates a cycle. - [#5777](https://github.com/detekt/detekt/pull/5777)
-   Update dependency org.jetbrains:annotations to v24 - [#5969](https://github.com/detekt/detekt/pull/5969)
-   Update github/codeql-action digest to 8c8d71d - [#5966](https://github.com/detekt/detekt/pull/5966)
-   Add functions to ExitOutsideMain rule - [#5963](https://github.com/detekt/detekt/pull/5963)
-   Update README.md - [#5954](https://github.com/detekt/detekt/pull/5954)
-   Prevent import statements from counting as references for UnusedPrivateProperty - [#5942](https://github.com/detekt/detekt/pull/5942)
-   Fix ExpressionBodySyntax not checking property getters/setters - [#5938](https://github.com/detekt/detekt/pull/5938)
-   Improve correctness of UnusedPrivateProperty - [#5935](https://github.com/detekt/detekt/pull/5935)
-   Fix documentation for deprecated 'reports' object (Issue #5908) - [#5924](https://github.com/detekt/detekt/pull/5924)
-   Print file path report as link file - [#5921](https://github.com/detekt/detekt/pull/5921)
-   "detekt" or "Detekt" - [#5898](https://github.com/detekt/detekt/issues/5898)
-   Update JSON schema URL - [#5881](https://github.com/detekt/detekt/pull/5881)
-   Add support for local suppression inside formatting - [#5876](https://github.com/detekt/detekt/pull/5876)
-   Fix checkExhaustiveness for formatting and third party rules - [#5869](https://github.com/detekt/detekt/pull/5869)
-   Allow newline style for MaxChainedCallsOnSameLine - [#5865](https://github.com/detekt/detekt/pull/5865)
-   Declare inputs and outputs to support incremental build for testPluginKotlinc - [#5862](https://github.com/detekt/detekt/pull/5862)
-   Use code syntax for `Unit` for ImplicitUnitReturnType rule - [#5857](https://github.com/detekt/detekt/pull/5857)
-   MatchingDeclarationName. KtFilesSpec also remove .common.kt suffix from kotlin files - [#5851](https://github.com/detekt/detekt/pull/5851)
-   Fix double mutability issues with Gradle plugin's use of ConfigurableFileCollection - [#5850](https://github.com/detekt/detekt/pull/5850)
-   MagicNumber - Make ignoreNamedArgument catch more complex expression - [#5837](https://github.com/detekt/detekt/pull/5837)
-   Exclude super call when generating guard clauses - [#5835](https://github.com/detekt/detekt/pull/5835)
-   Enable test retry for all our modules - [#5825](https://github.com/detekt/detekt/pull/5825)
-   Stop configuring report merge tasks while configuring Detekt tasks - [#5813](https://github.com/detekt/detekt/pull/5813)
-   FunctionMatcher support for fully qualified function names - [#5812](https://github.com/detekt/detekt/pull/5812)
-   Boy Scout - [#5808](https://github.com/detekt/detekt/pull/5808)
-   Simplify `TestConfig` usages - [#5801](https://github.com/detekt/detekt/pull/5801)
-   Reduce configuration of UnusedPrivateMember's split rules - [#5800](https://github.com/detekt/detekt/pull/5800)
-   Force SerialVerionUID to be private - [#5798](https://github.com/detekt/detekt/pull/5798)
-   Exclude the KMP test folders for android - [#5797](https://github.com/detekt/detekt/pull/5797)
-   Add aliases for PackageNaming and InvalidPackageDeclaration - [#5795](https://github.com/detekt/detekt/pull/5795)
-   Forbid using Jupiter Kotlin assertions - [#5794](https://github.com/detekt/detekt/pull/5794)
-   ModifierOrder: fix false positive with block comments - [#5791](https://github.com/detekt/detekt/pull/5791)
-   Fixed ProtectedMemberInFinalClass rule reporting valid JVM finalize - [#5788](https://github.com/detekt/detekt/pull/5788)
-   Remove unnecessary usage of BeforeAll in tests - [#5781](https://github.com/detekt/detekt/pull/5781)
-   Run Kotlin compiler plugin CLI test as part of standard build - [#5766](https://github.com/detekt/detekt/pull/5766)
-   Refactor to remove java.util.Array\* imports - [#5761](https://github.com/detekt/detekt/pull/5761)
-   Replace java.util.Stack with Kotlin's ArrayDeque implementation - [#5760](https://github.com/detekt/detekt/pull/5760)
-   Replace java.time.Duration with kotlin.time.Duration - [#5759](https://github.com/detekt/detekt/pull/5759)
-   Add NixOS installation method to doc - [#5757](https://github.com/detekt/detekt/pull/5757)
-   Remove & forbid usage of java.util.stream - [#5756](https://github.com/detekt/detekt/pull/5756)
-   Use stdlib functions for file & path operations - [#5754](https://github.com/detekt/detekt/pull/5754)
-   Enable UnnecessaryBackticks in detekt project - [#5753](https://github.com/detekt/detekt/pull/5753)
-   Update docusaurus monorepo to v2.3.1 - [#5752](https://github.com/detekt/detekt/pull/5752)
-   Resolve runtime classpaths consistently with compile classpaths - [#5730](https://github.com/detekt/detekt/pull/5730)
-   Exclude new Kotlin Test directories in default config - [#5727](https://github.com/detekt/detekt/issues/5727)
-   Fail when unexpected version of kotlin-compiler-embeddable is on runtime classpath - [#5726](https://github.com/detekt/detekt/pull/5726)
-   Fix IgnoredReturnValue rule crash in parallel mode - [#5724](https://github.com/detekt/detekt/pull/5724)
-   Use `name` that return name after backticks - [#5719](https://github.com/detekt/detekt/pull/5719)
-   UnusedPrivateMember - Fix false positive in case of invoke operator - [#5717](https://github.com/detekt/detekt/pull/5717)
-   Fix false positive for `CanBeNonNullable` rule - [#5714](https://github.com/detekt/detekt/pull/5714)
-   IgnoredReturnValue: fix false negative when annotation is on the package - [#5706](https://github.com/detekt/detekt/pull/5706)
-   Check Thread.sleep for block expression - [#5699](https://github.com/detekt/detekt/pull/5699)
-   Fix false positive of in UnnecessaryParentheses - [#5684](https://github.com/detekt/detekt/pull/5684)
-   Fix url and kotlin reference support in kdoc - [#5683](https://github.com/detekt/detekt/pull/5683)
-   Add config for variable expression in when - [#5681](https://github.com/detekt/detekt/pull/5681)
-   Enable NoSemicolons rule - [#5663](https://github.com/detekt/detekt/pull/5663)
-   Use correct resolvable/consumable flags on detekt's configurations - [#5657](https://github.com/detekt/detekt/pull/5657)
-   Prepare for Gradle 8 - [#5656](https://github.com/detekt/detekt/pull/5656)
-   ExplicitCollectionElementAccessMethod rule update - [#5654](https://github.com/detekt/detekt/pull/5654)
-   Cast nullable to non nullable type - [#5653](https://github.com/detekt/detekt/pull/5653)
-   Enable verbose mode for Codecov GH Action - [#5652](https://github.com/detekt/detekt/pull/5652)
-   Fail build when issues found with JVM target compatibility of related compile tasks - [#5651](https://github.com/detekt/detekt/pull/5651)
-   Don't silently use Kotlin compiler fallback strategy - [#5650](https://github.com/detekt/detekt/pull/5650)
-   Documentation tweaks - [#5639](https://github.com/detekt/detekt/pull/5639)
-   Have consistent compile-test-snippets between project and system property - [#5630](https://github.com/detekt/detekt/pull/5630)
-   Exclude operator functions in function min/max length - [#5618](https://github.com/detekt/detekt/pull/5618)
-   Broken link at EmptyFunctionBlock - [#5604](https://github.com/detekt/detekt/pull/5604)
-   Update rule description for errorprone rules - [#5603](https://github.com/detekt/detekt/pull/5603)
-   FunctionMaxLength false positive for overridden methods #5590 - [#5599](https://github.com/detekt/detekt/pull/5599)
-   Report proper code position in `MaxLineLength` - [#5583](https://github.com/detekt/detekt/pull/5583)
-   Allow access to nonpublic members of public types in java.base/java.lang package - [#5579](https://github.com/detekt/detekt/pull/5579)
-   NonBooleanPropertyPrefixedWithIs: Allow AtomicBoolean - [#5577](https://github.com/detekt/detekt/pull/5577)
-   Pass compilation output classes to detekt analysis classpath - [#5556](https://github.com/detekt/detekt/pull/5556)
-   Fix KDocReferencesNonPublicProperty false positive - [#5534](https://github.com/detekt/detekt/pull/5534)

##### Dependency Updates

-   Update dependency io.github.detekt.sarif4k:sarif4k to v0.4.0 - [#6113](https://github.com/detekt/detekt/pull/6113)
-   Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.7.1 - [#6097](https://github.com/detekt/detekt/pull/6097)
-   Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.7.0 - [#6074](https://github.com/detekt/detekt/pull/6074)
-   Update com.android.tools.build - [#6065](https://github.com/detekt/detekt/pull/6065)
-   Update JaCoCo to v0.8.10 - [#6044](https://github.com/detekt/detekt/pull/6044)
-   Update plugin pluginPublishing to v1.2.0 - [#5975](https://github.com/detekt/detekt/pull/5975)
-   Update ktlint to v0.48.1 - [#5661](https://github.com/detekt/detekt/pull/5661)
-   Update dependency com.android.tools.build:gradle to v7.4.0 - [#5693](https://github.com/detekt/detekt/pull/5693)
-   JaCoCo 0.8.9 - [#5959](https://github.com/detekt/detekt/pull/5959)
-   Update dependency com.github.tschuchortdev:kotlin-compile-testing to v1.5.0 - [#5882](https://github.com/detekt/detekt/pull/5882)
-   Update dependency org.jetbrains.dokka to v1.8.10 - [#5878](https://github.com/detekt/detekt/pull/5878)
-   Compile detekt-compiler-plugin against standard Kotlin compiler artifact - [#5765](https://github.com/detekt/detekt/pull/5765)
-   Migrate to SnakeYAML Engine - [#5751](https://github.com/detekt/detekt/pull/5751)
-   Update kotlin monorepo to v1.8.10 - [#5745](https://github.com/detekt/detekt/pull/5745)
-   Update ktlint to v0.48.0 - [#5625](https://github.com/detekt/detekt/pull/5625)
-   Migrate to Gradle Nexus Publish Plugin - [#5554](https://github.com/detekt/detekt/pull/5554)
-   Update dependency io.github.detekt.sarif4k:sarif4k to v0.2.0 - [#5496](https://github.com/detekt/detekt/pull/5496)

##### Housekeeping & Refactorings

-   Inline Cases enum and inline other external test code into the test classes - [#6107](https://github.com/detekt/detekt/pull/6107)
-   Update codecov/codecov-action digest to eaaf4be - [#6102](https://github.com/detekt/detekt/pull/6102)
-   Remove unnecessary baselines - [#6092](https://github.com/detekt/detekt/pull/6092)
-   Remove unused `dependenciesAsNames` - [#6059](https://github.com/detekt/detekt/pull/6059)
-   Reduce eager POM task creation - [#6041](https://github.com/detekt/detekt/pull/6041)
-   Improve our configuration of `ClassNaming` and `FunctionNaming` - [#6019](https://github.com/detekt/detekt/pull/6019)
-   Comment text in the Issue/PR Template - [#5992](https://github.com/detekt/detekt/pull/5992)
-   Fix typo: Github -> GitHub - [#5956](https://github.com/detekt/detekt/pull/5956)
-   Review all Detekt and Detekt Gradle Plugin usages. - [#5955](https://github.com/detekt/detekt/pull/5955)
-   Review all Detekt and Detekt Gradle Plugin usages. - [#5953](https://github.com/detekt/detekt/pull/5953)
-   Add a test for catching undocumented public interfaces - [#5951](https://github.com/detekt/detekt/pull/5951)
-   Execute tests in parallel - [#5944](https://github.com/detekt/detekt/pull/5944)
-   Make GeneratorSpec use resources - [#5932](https://github.com/detekt/detekt/pull/5932)
-   Cleanup detekt-formatting to use detekt's own assertThat function - [#5911](https://github.com/detekt/detekt/pull/5911)
-   Stale any issue with support tag in one month - [#5904](https://github.com/detekt/detekt/pull/5904)
-   Remove instances of double mutability - [#5899](https://github.com/detekt/detekt/pull/5899)
-   Handle todo in LinesOfCode logic - [#5897](https://github.com/detekt/detekt/pull/5897)
-   Boy scout - `detekt-generator` - [#5854](https://github.com/detekt/detekt/pull/5854)
-   Tweak GHA configs a bit - [#5852](https://github.com/detekt/detekt/pull/5852)
-   Create `generateWebsite` - [#5849](https://github.com/detekt/detekt/pull/5849)
-   Remove dependency between check and jacocoMergedReport - [#5846](https://github.com/detekt/detekt/pull/5846)
-   Sort deprecation properties - [#5845](https://github.com/detekt/detekt/pull/5845)
-   Simplify generate documentation - [#5844](https://github.com/detekt/detekt/pull/5844)
-   Remove unnecessary `@Suppress("ReturnCount")` - [#5841](https://github.com/detekt/detekt/pull/5841)
-   Cleaner merging of Gradle blocks for functionalTests - [#5830](https://github.com/detekt/detekt/pull/5830)
-   Replace trimMargin usages - [#5827](https://github.com/detekt/detekt/pull/5827)
-   Disable PTS from local and enable it for PRs - [#5826](https://github.com/detekt/detekt/pull/5826)
-   Activate MultilineRawStringIndentation on detekt - [#5819](https://github.com/detekt/detekt/pull/5819)
-   Remove single-use `times` method, use stdlib's `repeat` instead. - [#5774](https://github.com/detekt/detekt/pull/5774)
-   Remove redundant build config - [#5617](https://github.com/detekt/detekt/pull/5617)
-   Migrate to AGP namespaces - [#5569](https://github.com/detekt/detekt/pull/5569)
-   Fix typo - [#5557](https://github.com/detekt/detekt/pull/5557)

##### Contributors

We would like to thank the following contributors that made this release possible: @3flex, @BeBAKE, @BraisGabin, @Goooler, @SaumyaBhushan, @TWiStErRob, @VitalyVPinchuk, @adef145, @asomov, @atulgpt, @chao2zhang, @cketti, @cortinico, @drawers, @dzirbel, @igorwojda, @lexa-diky, @luanpotter, @marschwar, @mjovanc, @mmorozkov, @ncteisen, @osipxd, @ov7a, @schalkms, @t-kameyama, @tresni

See all issues at: [1.23.0](https://github.com/detekt/detekt/milestone/88)

#### 1.22.0 - 2022-11-20

##### Notable Changes

-   We're introducing the [**Detekt Marketplace**](https://detekt.dev/marketplace), a place where you can add your own 3rd party extension such as rule, plugins, custom reporter, etc. - [#5191](https://github.com/detekt/detekt/pull/5191)
-   Our website is now versioned. You can find the changes for each version using the dropdown menu on the top bar. Documentation for the upcoming version (next) [can be found here](https://detekt.dev/docs/next/intro).
-   We added **16** new Rules to detekt
    -   `AlsoCouldBeApply` - [#5333](https://github.com/detekt/detekt/pull/5333)
    -   `MultilineRawStringIndentation` - [#5058](https://github.com/detekt/detekt/pull/5058)
    -   `TrimMultilineRawString` - [#5051](https://github.com/detekt/detekt/pull/5051)
    -   `UnnecessaryNotNullCheck` - [#5218](https://github.com/detekt/detekt/pull/5218)
    -   `UnnecessaryPartOfBinaryExpression` - [#5203](https://github.com/detekt/detekt/pull/5203)
    -   `UseSumOfInsteadOfFlatMapSize` - [#5405](https://github.com/detekt/detekt/pull/5405)
    -   `FunctionReturnTypeSpacing` from KtLint - [#5256](https://github.com/detekt/detekt/pull/5256)
    -   `FunctionSignature` from KtLint - [#5256](https://github.com/detekt/detekt/pull/5256)
    -   `FunctionStartOfBodySpacing` from KtLint - [#5256](https://github.com/detekt/detekt/pull/5256)
    -   `NullableTypeSpacing` from KtLint - [#5256](https://github.com/detekt/detekt/pull/5256)
    -   `ParameterListSpacing` from KtLint - [#5256](https://github.com/detekt/detekt/pull/5256)
    -   `SpacingBetweenFunctionNameAndOpeningParenthesis` from KtLint - [#5256](https://github.com/detekt/detekt/pull/5256)
    -   `TrailingCommaOnCallSite` from KtLint - [#5312](https://github.com/detekt/detekt/pull/5312)
    -   `TrailingCommaOnDeclarationSite` from KtLint - [#5312](https://github.com/detekt/detekt/pull/5312)
    -   `TypeParameterListSpacing` from KtLint - [#5256](https://github.com/detekt/detekt/pull/5256)
-   We added a new ruleset called `detekt-rules-ruleauthors` containing rules for Rule Authors to enforce best practices on detekt rules such as the new `ViolatesTypeResolutionRequirements` - [#5129](https://github.com/detekt/detekt/pull/5129) [#5182](https://github.com/detekt/detekt/pull/5182)
-   We added a new ruleset called `detekt-rules-libraries` containing rules mostly useful for Library Authors - We moved the following rules inside `ForbiddenPublicDataClass`, `LibraryCodeMustSpecifyReturnType`, `LibraryEntitiesShouldNotBePublic` this new ruleset - See Migration below on how to migrate [#5360](https://github.com/detekt/detekt/pull/5360)
-   We added support for JVM toolchain. This means that detekt will now respect the JDK toolchain you specify on your Gradle configuration. You will also be able to specify a custom JDK home with the `--jdk-home` CLI parameter - [#5269](https://github.com/detekt/detekt/pull/5269)
-   Improvement for Type Resolution
    -   We will now skip rules annotated with `@RequiresTypeResolution` when without Type Resolution - [#5176](https://github.com/detekt/detekt/pull/5176)
    -   We will warn users if they run rules requiring Type Resolution when Type Resolution is disabled, so they're not silently skipped - [#5226](https://github.com/detekt/detekt/pull/5226)
-   Improvement for Config Management
    -   We added exhaustiveness check during config validation. You can enable it `checkExhaustiveness: true` in your config file. This is disabled by default. - [#5089](https://github.com/detekt/detekt/pull/5089)
    -   We added support for generating custom configuration for rule authors - [#5080](https://github.com/detekt/detekt/pull/5080)
-   Deprecations & Removals
    -   We deprecated the MultiRule class as it was overly complicated. The suggested approach is to just provide separated rules. - [#5161](https://github.com/detekt/detekt/pull/5161)
    -   The `--fail-fast` CLI flag (and `failFast` Gradle property) has been removed. It was deprecated since 1.16.x - [#5290](https://github.com/detekt/detekt/pull/5290)
    -   We **deprecated** the following rules `DuplicateCaseInWhenExpression`, `MissingWhenCase`, `RedundantElseInWhen` as the Kotlin Compiler is already reporting errors for those scenarios - [#5309](https://github.com/detekt/detekt/pull/5309)
    -   We removed the `--print-ast` CLI flag as [PsiViewer](https://www.jetbrains.com/help/idea/psi-viewer.html) provides the same features - [#5418](https://github.com/detekt/detekt/pull/5418)
-   Notable changes to existing rules
    -   `ArrayPrimitive` is now working only with Type Resolution - [#5175](https://github.com/detekt/detekt/pull/5175)
    -   `WildcardImport` is now running also on tests by default - [#5121](https://github.com/detekt/detekt/pull/5121)
    -   `ForbiddenImport` allows now to specify a reason for every forbidden import - [#4909](https://github.com/detekt/detekt/pull/4909)
    -   `IgnoredReturnValue`: option `restrictToAnnotatedMethods` is now deprecated in favor of `restrictToConfig` - [#4922](https://github.com/detekt/detekt/pull/4922)
-   This version of detekt is built with Gradle `v7.5.1`, AGP `7.3.1`, Kotlin `1.7.21` and KtLint `0.47.1` (see [#5363](https://github.com/detekt/detekt/pull/5363) [#5189](https://github.com/detekt/detekt/pull/5189) [#5411](https://github.com/detekt/detekt/pull/5411) [#5312](https://github.com/detekt/detekt/pull/5312) [#5519](https://github.com/detekt/detekt/pull/5519))
-   The minimum supported Gradle version is now `v6.7.1` - [#4964](https://github.com/detekt/detekt/pull/4964)

##### Migration

We deprecated a number of rules in this release.

You should update your config file as follows:

```diff
  potential-bugs:
    active: true
    ...
-   DuplicateCaseInWhenExpression:
-     active: true
    ...
-   MissingWhenCase:
-     active: true
-     allowElseExpression: true
    ...
-   RedundantElseInWhen:
-     active: true

  style:
    active: true
    ...
-   ForbiddenPublicDataClass:
-     active: true
-     excludes: ['**']
-     ignorePackages:
-       - '*.internal'
-       - '*.internal.*'
    ...
-   LibraryCodeMustSpecifyReturnType:
-     active: true
-     excludes: ['**']
    ...
-   LibraryEntitiesShouldNotBePublic:
-     active: true
-     excludes: ['**']
```

If you wish to use the `libraries` ruleset we introduced you should add the following to your config file:

```diff
+ libraries:
+   active: true
+   ForbiddenPublicDataClass:
+     active: false
+   LibraryEntitiesShouldNotBePublic:
+     active: false
+   LibraryCodeMustSpecifyReturnType:
+     active: true
```

and add the following to your `build.gradle` file:

```kotlin
detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:$version")
```

If you're using our KtLint wrapper (i.e. `detekt-formatting`) you should also update your config file as follows:

```diff
formatting:
  active: true
  ...
- TrailingComma:
-   active: false
-   autoCorrect: true
-   allowTrailingComma: false
-   allowTrailingCommaOnCallSite: false
  ...
+ TrailingCommaOnCallSite:
+   active: false
+   autoCorrect: true
+   useTrailingCommaOnCallSite: false
+ TrailingCommaOnDeclarationSite:
+   active: false
+   autoCorrect: true
+   useTrailingCommaOnDeclarationSite: false
```

##### Changelog

-   ReturnCount: correctly count assignment expressions with elvis return as guard clauses - [#5539](https://github.com/detekt/detekt/pull/5539)
-   UnnecessaryPartOfBinaryExpression: fix false positive with pair creation - [#5516](https://github.com/detekt/detekt/pull/5516)
-   False positive at `UnnecessaryPartOfBinaryExpression` - [#5514](https://github.com/detekt/detekt/issues/5514)
-   Update documentation for TrailingComma rules - [#5513](https://github.com/detekt/detekt/pull/5513)
-   `TrimMultilineRawString` false-positive on annotation parameters - [#5476](https://github.com/detekt/detekt/issues/5476)
-   detekt 1.22.0-RC1 -> 1.22.0-RC2 breaks UnreachableCode - [#5435](https://github.com/detekt/detekt/issues/5435)
-   detekt 1.22.0-RC1 -> 1.22.0-RC2 breaks ignoreAnnotated - [#5427](https://github.com/detekt/detekt/issues/5427)
-   Fix issues introduced by #5152 - [#5508](https://github.com/detekt/detekt/pull/5508)
-   MultilineLambdaItParameter: fix false positive for one-line statements with a lambda argument - [#5505](https://github.com/detekt/detekt/pull/5505)
-   UseArrayLiteralsInAnnotations: fix false negative with primitive array factory calls - [#5482](https://github.com/detekt/detekt/pull/5482)
-   TrimMultilineRawString: fix false positive when it's expected as constant - [#5480](https://github.com/detekt/detekt/pull/5480)
-   Fix false negative `SafeCast` with no braces - [#5479](https://github.com/detekt/detekt/pull/5479)
-   Update gradle/wrapper-validation-action digest to 55e685c - [#5472](https://github.com/detekt/detekt/pull/5472)
-   Grant permission for type resolution Gradle job - [#5470](https://github.com/detekt/detekt/pull/5470)
-   Fix ObjectPropertyNaming Rule false positive - [#5466](https://github.com/detekt/detekt/pull/5466)
-   Fix LambdaParameterNaming rule false positive - [#5465](https://github.com/detekt/detekt/pull/5465)
-   Fix ReturnCount false positive when excludeReturnFromLambda is enabled - [#5459](https://github.com/detekt/detekt/pull/5459)
-   CognitiveComplexity: count else/else-if as one complexity - [#5458](https://github.com/detekt/detekt/pull/5458)
-   Fix false positive MultilineRawStringIndentation with tab indentation - [#5453](https://github.com/detekt/detekt/pull/5453)
-   Don't show the number of issues generating the BindingContext - [#5449](https://github.com/detekt/detekt/pull/5449)
-   Make detekt less noisy - [#5448](https://github.com/detekt/detekt/pull/5448)
-   New ruleauthors rule for Entity.from(x.nameIdentifier ?: x) -> Entity.atName(x) - [#5444](https://github.com/detekt/detekt/pull/5444)
-   Separating ComplexMethod rule into CyclomaticComplexMethod and CognitiveComplexMethod - [#5442](https://github.com/detekt/detekt/pull/5442)
-   Improve error reporting for CascadingCallWrapping - [#5439](https://github.com/detekt/detekt/pull/5439)
-   TrimMultilineRawString: fix false positive with not a raw string - [#5438](https://github.com/detekt/detekt/pull/5438)
-   BooleanPropertyNaming highlight only the name of the variable - [#5431](https://github.com/detekt/detekt/pull/5431)
-   Deprecate `TrailingComma` as it's now split in two rules - [#5423](https://github.com/detekt/detekt/pull/5423)
-   Remove unused constant - [#5421](https://github.com/detekt/detekt/pull/5421)
-   Report if/else as issue location instead of block - [#5407](https://github.com/detekt/detekt/pull/5407)
-   Remove some unnecessary suppressions - [#5400](https://github.com/detekt/detekt/pull/5400)
-   Check FormattingRule is auto-correctable by information provided by ktlint - [#5398](https://github.com/detekt/detekt/pull/5398)
-   Fix false negative MultilineLambdaItParameter on complex multiline single statement - [#5397](https://github.com/detekt/detekt/pull/5397)
-   ObjectPropertyNaming: fix false positive with top level properties - [#5390](https://github.com/detekt/detekt/pull/5390)
-   Remove usage of MPP targets function for JVM-only projects - [#5383](https://github.com/detekt/detekt/pull/5383)
-   UnnecessaryNotNullCheck: fix false negative with smart casted arguments - [#5380](https://github.com/detekt/detekt/pull/5380)
-   Add missing overlapping info & fix rules URLs - [#5378](https://github.com/detekt/detekt/pull/5378)
-   AlsoCouldBeApply: fix false positive when all statements are not `it`-started expressions - [#5376](https://github.com/detekt/detekt/pull/5376)
-   UnusedPrivateMember: fix false negative with named arguments - [#5374](https://github.com/detekt/detekt/pull/5374)
-   Change requires type resolution rule warning to debug level to not spam the user console - [#5353](https://github.com/detekt/detekt/pull/5353)
-   Report UseDataClass findings on class name - [#5352](https://github.com/detekt/detekt/pull/5352)
-   Report LabeledExpression as the label instead of the whole expression - [#5351](https://github.com/detekt/detekt/pull/5351)
-   Report CastToNullableType at the cast operator instead of the whole expression - [#5350](https://github.com/detekt/detekt/pull/5350)
-   Convert previously known string property to list based on default value - [#5347](https://github.com/detekt/detekt/pull/5347)
-   CastToNullableType: highlights too much - [#5346](https://github.com/detekt/detekt/issues/5346)
-   UseDataClass flags the whole class body, not just the name - [#5338](https://github.com/detekt/detekt/issues/5338)
-   CanBeNonNullable: explain why the rule does what it does. - [#5332](https://github.com/detekt/detekt/pull/5332)
-   Differentiate between correctable and non-correctable KtLint rules - [#5324](https://github.com/detekt/detekt/pull/5324)
-   ReturnCount 1.22.0 crashes on valid 1.21.0 config property excludedFunctions when using --all-rules cli flag - [#5323](https://github.com/detekt/detekt/issues/5323)
-   LabeledExpression to highlight only label - [#5316](https://github.com/detekt/detekt/issues/5316)
-   Use the correct source directory set on JVM - [#5163](https://github.com/detekt/detekt/pull/5163)
-   Get Android variant compile classpath from compileConfiguration - [#5152](https://github.com/detekt/detekt/pull/5152)
-   Use list config for `FunctionOnlyReturningConstant>excludedFunctions` - [#5120](https://github.com/detekt/detekt/pull/5120)
-   MaxLineLength: raw typo and test cleanup - [#5315](https://github.com/detekt/detekt/pull/5315)
-   EndOfSentenceFormat: fix HTML tag heuristic - [#5313](https://github.com/detekt/detekt/pull/5313)
-   Fix EndOfSentenceFormat highlight - [#5311](https://github.com/detekt/detekt/pull/5311)
-   Introduce configFile property on DetektGenerateTask - [#5308](https://github.com/detekt/detekt/pull/5308)
-   Improve debug suggestion message - [#5300](https://github.com/detekt/detekt/pull/5300)
-   Fat-Jar version of detekt-generator module - [#5297](https://github.com/detekt/detekt/pull/5297)
-   Toolchains docs - [#5293](https://github.com/detekt/detekt/pull/5293)
-   Adopt new AGP dsl - [#5288](https://github.com/detekt/detekt/pull/5288)
-   NonBooleanPropertyPrefixedWithIs: Allow boolean functions - [#5285](https://github.com/detekt/detekt/pull/5285)
-   Provide the current classpath inside `KotlinEnvironmentResolver` - [#5275](https://github.com/detekt/detekt/pull/5275)
-   Fix false-positive on `NestedScopeFunctions` - [#5274](https://github.com/detekt/detekt/pull/5274)
-   Use convention method to set task property defaults - [#5272](https://github.com/detekt/detekt/pull/5272)
-   Update docusaurus monorepo to v2.1.0 - [#5270](https://github.com/detekt/detekt/pull/5270)
-   detektVersionReplace.js plugin is not replacing all [detekt_version] tags on website - [#5266](https://github.com/detekt/detekt/pull/5266)
-   Update ktlint rule doc links - [#5258](https://github.com/detekt/detekt/pull/5258)
-   Remove redundant rule config for rules enabled by default - [#5257](https://github.com/detekt/detekt/pull/5257)
-   UnusedPrivateMember: fix false positive with backtick parameters - [#5252](https://github.com/detekt/detekt/pull/5252)
-   Improve MultilineRawStringIndentation - [#5245](https://github.com/detekt/detekt/pull/5245)
-   UnnecessaryLet: fix false positive with with invoke operator calls - [#5240](https://github.com/detekt/detekt/pull/5240)
-   Introduce baseline tooling api - [#5239](https://github.com/detekt/detekt/pull/5239)
-   Allow secondary constructors to reference CoroutineDispatchers - [#5227](https://github.com/detekt/detekt/pull/5227)
-   Update `UnnecessaryAbstractClass` issue description to be less verbose - [#5224](https://github.com/detekt/detekt/pull/5224)
-   Update plugin com.gradle.common-custom-user-data-gradle-plugin to v1.8.0 - [#5223](https://github.com/detekt/detekt/pull/5223)
-   Pin dependencies - [#5222](https://github.com/detekt/detekt/pull/5222)
-   Remove rule from NamingRules multi rule - [#5212](https://github.com/detekt/detekt/pull/5212)
-   Run all rules from EmptyBlocks multi rule individually - [#5208](https://github.com/detekt/detekt/pull/5208)
-   Run all rules from KDocStyle multi rule individually - [#5207](https://github.com/detekt/detekt/pull/5207)
-   Docs: GitHub - Add link to configure Sarif severity alert level - [#5206](https://github.com/detekt/detekt/pull/5206)
-   Fix errors with `detektGenerateConfig` - [#5199](https://github.com/detekt/detekt/pull/5199)
-   Forbid constructors with `ForbiddenMethodCall` - [#5195](https://github.com/detekt/detekt/pull/5195)
-   Update github/codeql-action digest to 2ca79b6 - [#5177](https://github.com/detekt/detekt/pull/5177)
-   Allow to ignore overloaded methods for the complex interface rule (#5165) - [#5173](https://github.com/detekt/detekt/pull/5173)
-   Add excludesRawStrings in MaxLineLength - [#5171](https://github.com/detekt/detekt/pull/5171)
-   Enable Predictive Test Selection for local builds - [#5170](https://github.com/detekt/detekt/pull/5170)
-   Update dependency org.kohsuke:github-api to v1.307 - [#5168](https://github.com/detekt/detekt/pull/5168)
-   Update dependency com.github.ajalt:clikt to v2.8.0 - [#5167](https://github.com/detekt/detekt/pull/5167)
-   Update docusaurus monorepo to v2.0.1 - [#5166](https://github.com/detekt/detekt/pull/5166)
-   Run build-logic Kotlin compilation out of process on CI - [#5162](https://github.com/detekt/detekt/pull/5162)
-   Add information about exhaustiveness check to documentation - [#5160](https://github.com/detekt/detekt/pull/5160)
-   Use getter when determining whether custom config path is set in DetektGenerateConfigTask - [#5157](https://github.com/detekt/detekt/pull/5157)
-   Limit Kotlin version warning suppression scope in build - [#5156](https://github.com/detekt/detekt/pull/5156)
-   Re-enable warnings as errors for detekt-gradle-plugin - [#5155](https://github.com/detekt/detekt/pull/5155)
-   Bundle slf4j-nop in detekt-formatting JAR - [#5153](https://github.com/detekt/detekt/pull/5153)
-   Fix false negative for UseRequire when thrown in conditional block - [#5147](https://github.com/detekt/detekt/pull/5147)
-   Allow parentheses for unclear precedence with range operator - [#5143](https://github.com/detekt/detekt/pull/5143)
-   Mark apiDump task as incompatible with configuration cache - [#5134](https://github.com/detekt/detekt/pull/5134)
-   Improve binding context management - [#5130](https://github.com/detekt/detekt/pull/5130)
-   `RedundantExplicitType` add annotation `@RequiresFullAnalysis` - [#5128](https://github.com/detekt/detekt/pull/5128)
-   Disable `ExitOutsideMain` if `contextBinding` is empty - [#5127](https://github.com/detekt/detekt/pull/5127)
-   Use list config for `DataClassContainsFunctions>conversionFunctionPrefix` - [#5119](https://github.com/detekt/detekt/pull/5119)
-   Support proper globbing in `ReturnCount` - [#5118](https://github.com/detekt/detekt/pull/5118)
-   Improve finding message of ExplicitItLambdaParameter - [#5117](https://github.com/detekt/detekt/pull/5117)
-   Update JamesIves/github-pages-deploy-action digest to 13046b6 - [#5110](https://github.com/detekt/detekt/pull/5110)
-   UnusedUnaryOperator: fix false positive with var assignment and if expression - [#5106](https://github.com/detekt/detekt/pull/5106)
-   Tag publishPlugins task as incompatible with configuration cache - [#5101](https://github.com/detekt/detekt/pull/5101)
-   Make verifyGeneratorOutput task configuration cache compatible - [#5100](https://github.com/detekt/detekt/pull/5100)
-   Remove obsolete FeatureInAlphaState opt in - [#5099](https://github.com/detekt/detekt/pull/5099)
-   Remove explicit RequiresOptIn compiler flag - [#5098](https://github.com/detekt/detekt/pull/5098)
-   Use Gradle's configuration cache by default - [#5095](https://github.com/detekt/detekt/pull/5095)
-   Detect undocumented protected classes, properties, and functions - [#5083](https://github.com/detekt/detekt/pull/5083)
-   ReturnCount.excludedFunctions should be a `List<String>` - [#5081](https://github.com/detekt/detekt/pull/5081)
-   Make ForbiddenMethodCall to support property getters/setters and method references - [#5078](https://github.com/detekt/detekt/pull/5078)
-   Refactor Gradle tasks to use Gradle's managed properties - [#4966](https://github.com/detekt/detekt/pull/4966)
-   Add option to add a reason to `ForbiddenMethodCall` - [#4910](https://github.com/detekt/detekt/pull/4910)
-   UnnecessaryParentheses: add options to allow in ambiguous cases - [#4881](https://github.com/detekt/detekt/pull/4881)

##### Dependency Updates

-   Update dependency com.android.tools.build:gradle to v7.3.1 - [#5411](https://github.com/detekt/detekt/pull/5411)
-   Update plugin com.gradle.enterprise to v3.11.2 - [#5406](https://github.com/detekt/detekt/pull/5406)
-   Update dependency org.jetbrains.dokka to v1.7.20 - [#5401](https://github.com/detekt/detekt/pull/5401)
-   Update dependency org.yaml:snakeyaml to v1.33 - [#5354](https://github.com/detekt/detekt/pull/5354)
-   Update dependency org.spekframework.spek2:spek-dsl-jvm to v2.0.19 - [#5237](https://github.com/detekt/detekt/pull/5237)
-   Update dependency com.android.tools.build:gradle to v7.2.2 - [#5178](https://github.com/detekt/detekt/pull/5178)
-   Update org.jetbrains.kotlinx - [#5072](https://github.com/detekt/detekt/pull/5072)
-   Update dependency org.jetbrains.dokka to v1.7.10 - [#5070](https://github.com/detekt/detekt/pull/5070)
-   Bump ktlint to version 0.46.1 - [#5044](https://github.com/detekt/detekt/pull/5044)
-   AssertJ 3.23.1 - [#4265](https://github.com/detekt/detekt/pull/4265)

##### Housekeeping & Refactorings

-   Document and test edge cases for ForbiddenMethodCall function signatures - [#5495](https://github.com/detekt/detekt/pull/5495)
-   Fix invalid syntaxes in test code - [#5446](https://github.com/detekt/detekt/pull/5446)
-   Improve raw strings format - [#5244](https://github.com/detekt/detekt/pull/5244)
-   Enable trim multiline raw string - [#5243](https://github.com/detekt/detekt/pull/5243)
-   Remove old configurations - [#5198](https://github.com/detekt/detekt/pull/5198)
-   Improve tests in UnnecessaryParenthesesSpec - [#5197](https://github.com/detekt/detekt/pull/5197)
-   Remove multi rule FileParsingRule - [#5193](https://github.com/detekt/detekt/pull/5193)
-   Remove unused dry run properties from baseline/config tasks - [#5158](https://github.com/detekt/detekt/pull/5158)
-   remove SimpleGlob in favor of String.simplePatternToRegex() - [#5144](https://github.com/detekt/detekt/pull/5144)
-   Remove unused property - [#5135](https://github.com/detekt/detekt/pull/5135)
-   Assert end source locations - [#5116](https://github.com/detekt/detekt/pull/5116)
-   Forbid usage of DiagnosticUtils.getLineAndColumnInPsiFile - [#5109](https://github.com/detekt/detekt/pull/5109)
-   Configure 'ForbiddenImport' to use value and reason - [#5105](https://github.com/detekt/detekt/pull/5105)
-   Enable Kotlin's new approach to incremental compilation - [#5092](https://github.com/detekt/detekt/pull/5092)
-   Fix current indentation - [#5059](https://github.com/detekt/detekt/pull/5059)

See all issues at: [1.22.0](https://github.com/detekt/detekt/milestone/87)

#### 1.21.0 - 2022-07-16

We're delighted to announce the next upcoming stable release of detekt: `1.21.0` 🎉
This release is coming with 6 new rules, new API and functionalities and several stability improvements.

We want to thank you very much [our Sponsors](https://github.com/sponsors/detekt) for the support in those last months. The work behind detekt is all happening on a voluntary basis, and we're more than grateful for all the support we get from the Open Source Ecosystem.

We're also excited to announce that we're now having an [Open Source Gradle Enterprise](https://ge.detekt.dev) instance. When building the detekt projects, you'll benefit from the Gradle Remote Cache that this instance is providing!

Finally, we want to take the opportunity to thank our contributors for testing, bug reporting and helping
us release this new version of detekt. You're more than welcome to join our community on the [#detekt](https://kotlinlang.slack.com/archives/C88E12QH4) channel on KotlinLang's Slack (you can [get an invite here](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)).

##### Notable Changes

-   We enabled ~30 new rules by default which we believe are now stable enough. - [#4875](https://github.com/detekt/detekt/pull/4875)
-   We added **7** new Rules to detekt
    -   `NullableBooleanCheck` - [#4872](https://github.com/detekt/detekt/pull/4872)
    -   `CouldBeSequence` - [#4855](https://github.com/detekt/detekt/pull/4855)
    -   `UnnecessaryBackticks` - [#4764](https://github.com/detekt/detekt/pull/4764)
    -   `ForbiddenSuppress` - [#4899](https://github.com/detekt/detekt/pull/4899)
    -   `MaxChainedCallsOnSameLine` - [#4985](https://github.com/detekt/detekt/pull/4985)
    -   `CascadingCallWrapping` - [#4979](https://github.com/detekt/detekt/pull/4979)
    -   `NestedScopeFunctions` - [#4788](https://github.com/detekt/detekt/pull/4788)
-   We added support for Markdown reports - [#4858](https://github.com/detekt/detekt/pull/4858)
-   We now allow users and rule authors to specify a **reason** for every value in the config file - [#4611](https://github.com/detekt/detekt/pull/4611) Please note that this feature requires a rule to be extended to support it. If you're a rule author you can start using it right away in your rule. We're looking into using this feature in some first party rule starting from detekt `1.22.0`.
-   We now report as warnings the Strings in the config file that can be converted to be an array - [#4793](https://github.com/detekt/detekt/pull/4793)
-   We added a dependency on **ConTester** to help us verify concurrency scenarios for detekt - [#4672](https://github.com/detekt/detekt/pull/4672)
-   For contributors: we restructured our build setup to be use **Gradle composite build** - [#4751](https://github.com/detekt/detekt/pull/4751)

##### Migration

We fixed a bug related to function with KDocs and how their location in the source code was calculated (see [#4961](https://github.com/detekt/detekt/pull/4961) and [#4887](https://github.com/detekt/detekt/issues/4887)).

Because of this, some users might have to **recreate their baseline** as the location of such functions are not matched anymore against the baseline. You can do so by deleting your old baseline and invoking the `detektBaseline` task (or the corresponding task, based on your configuration).

##### Changelog

-   ReturnCount: Make configuration parameter more explicit - [#5062](https://github.com/detekt/detekt/pull/5062)
-   Remove redundant null check - [#5061](https://github.com/detekt/detekt/pull/5061)
-   Drop redundant Gradle workaround - [#5057](https://github.com/detekt/detekt/pull/5057)
-   Update ktlint links from website to readme - [#5056](https://github.com/detekt/detekt/pull/5056)
-   Improve extensions.doc format with admonitions - [#5055](https://github.com/detekt/detekt/pull/5055)
-   Update docusaurus monorepo to v2.0.0-beta.22 - [#5050](https://github.com/detekt/detekt/pull/5050)
-   Enable strict Kotlin DSL precompiled script plugins accessors generation - [#5048](https://github.com/detekt/detekt/pull/5048)
-   MaxChainedCallsOnSameLine: don't count package references as chained calls - [#5036](https://github.com/detekt/detekt/pull/5036)
-   Xml Report Merger now merges duplicate smells across input report files - [#5033](https://github.com/detekt/detekt/pull/5033)
-   Add ending line and column to Location.kt - [#5032](https://github.com/detekt/detekt/pull/5032)
-   Fix type resolution link in Contributing.md - [#5027](https://github.com/detekt/detekt/pull/5027)
-   #5014 Fix MaxChainedCallsOnSameLine false positives - [#5020](https://github.com/detekt/detekt/pull/5020)
-   Add endColumn/endLine to SARIF region - [#5011](https://github.com/detekt/detekt/pull/5011)
-   Removed UnnecessaryAbstractClass if it inherits from a abstract class - [#5009](https://github.com/detekt/detekt/pull/5009)
-   Only recommend using index accessors for Java classes that are known collections - [#4994](https://github.com/detekt/detekt/pull/4994)
-   UnusedImports: fix false positive for unresolved imports - [#4882](https://github.com/detekt/detekt/pull/4882)
-   Fix Signatures.kt:buildFunctionSignature - [#4961](https://github.com/detekt/detekt/pull/4961)
-   Loading a specific resource from a module must use class from module - [#5008](https://github.com/detekt/detekt/pull/5008)
-   Update github/codeql-action digest to 3f62b75 - [#5007](https://github.com/detekt/detekt/pull/5007)
-   Show finding at declaration name instead of the whole declaration - [#5003](https://github.com/detekt/detekt/pull/5003)
-   NamedArguments: don't count trailing lambda argument - [#5002](https://github.com/detekt/detekt/pull/5002)
-   Address TextLocation for Wrapping - [#4998](https://github.com/detekt/detekt/pull/4998)
-   Support markdown report in Gradle plugin - [#4995](https://github.com/detekt/detekt/pull/4995)
-   Fix false-negative for CanBeNonNullable - [#4993](https://github.com/detekt/detekt/pull/4993)
-   Give a better error message for --jvm-target - [#4978](https://github.com/detekt/detekt/pull/4978)
-   Fix rule code samples to be valid Kotlin code - [#4969](https://github.com/detekt/detekt/pull/4969)
-   Use plain ASCII output in standard reports - [#4968](https://github.com/detekt/detekt/pull/4968)
-   UnnecessaryApply: fix false negative for assignment - [#4948](https://github.com/detekt/detekt/pull/4948)
-   Support disabling config validation via tooling spec - [#4937](https://github.com/detekt/detekt/pull/4937)
-   UnusedPrivateMember: highlight declaration name - [#4928](https://github.com/detekt/detekt/pull/4928)
-   Provide a priority field for DetektProvider - [#4923](https://github.com/detekt/detekt/pull/4923)
-   CastToNullableType: allow casting null keyword - [#4907](https://github.com/detekt/detekt/pull/4907)
-   Update plugin com.gradle.common-custom-user-data-gradle-plugin to v1.7.2 - [#4897](https://github.com/detekt/detekt/pull/4897)
-   Set strict dependency on tested Kotlin compiler version - [#4822](https://github.com/detekt/detekt/pull/4822)
-   Simplify regular expressions - [#4893](https://github.com/detekt/detekt/pull/4893)
-   Remove redundant character escape in RegExp - [#4892](https://github.com/detekt/detekt/pull/4892)
-   Reformat Markdown files to comply with the spec - [#4891](https://github.com/detekt/detekt/pull/4891)
-   UnnecessaryInnerClass: fix false negative with `this` references - [#4884](https://github.com/detekt/detekt/pull/4884)
-   UselessCallOnNotNull: fix false positive for unresolved types - [#4880](https://github.com/detekt/detekt/pull/4880)
-   Update MagicNumber rule to exclude .kts files - [#4877](https://github.com/detekt/detekt/pull/4877)
-   CanBeNonNullable: fix false positives for parameterized types - [#4870](https://github.com/detekt/detekt/pull/4870)
-   UnnecessaryInnerClass: fix false positives labeled expression to outer class - [#4865](https://github.com/detekt/detekt/pull/4865)
-   UnnecessaryInnerClass: add test for safe qualified expressions - [#4864](https://github.com/detekt/detekt/pull/4864)
-   Fix a confusing Regex in the Compose webpage - [#4852](https://github.com/detekt/detekt/pull/4852)
-   Fix edit URLs for the website - [#4850](https://github.com/detekt/detekt/pull/4850)
-   detektGenerateConfig adds the configuration of plugins - [#4844](https://github.com/detekt/detekt/pull/4844)
-   Update dependency prism-react-renderer to v1.3.3 - [#4833](https://github.com/detekt/detekt/pull/4833)
-   Search in all versions.properties, not just the first one #4830 - [#4831](https://github.com/detekt/detekt/pull/4831)
-   Improve exception message - [#4823](https://github.com/detekt/detekt/pull/4823)
-   Fix ValShouldBeVar false positive inside unknown type - [#4820](https://github.com/detekt/detekt/pull/4820)
-   Add a recent conference talk link - [#4819](https://github.com/detekt/detekt/pull/4819)
-   False positive for unused imports #4815 - [#4818](https://github.com/detekt/detekt/pull/4818)
-   Revert "Display dynamic --jvm-target values when using --help flag (#4694)" - [#4816](https://github.com/detekt/detekt/pull/4816)
-   UnnecessaryAbstractClass: report only the class name - [#4808](https://github.com/detekt/detekt/pull/4808)
-   Fix wrong replacement suggestion for UnnecessaryFilter - [#4807](https://github.com/detekt/detekt/pull/4807)
-   UseOrEmpty: fix false positive for indexing operator calls with type parameters - [#4804](https://github.com/detekt/detekt/pull/4804)
-   ExplicitCollectionElementAccessMethod: fix false positive for get operators with type parameters - [#4803](https://github.com/detekt/detekt/pull/4803)
-   Add tests for #4786 - [#4801](https://github.com/detekt/detekt/pull/4801)
-   Add documentation link for rules in html report - [#4799](https://github.com/detekt/detekt/pull/4799)
-   Improve rule documentaion and smell message of NamedArguments - [#4796](https://github.com/detekt/detekt/pull/4796)
-   Improve issue description and smell message of DestructuringDeclarationWithTooManyEntries - [#4795](https://github.com/detekt/detekt/pull/4795)
-   NestedScopeFunctions - Add rule for nested scope functions - [#4788](https://github.com/detekt/detekt/pull/4788)
-   Partially drop redundant usage of "dry run" in Gradle plugin tests - [#4776](https://github.com/detekt/detekt/pull/4776)
-   Allow additionalJavaSourceRootPaths to be defined on @KotlinCoreEnvironmentTest - [#4771](https://github.com/detekt/detekt/pull/4771)
-   Report KDoc comments that refer to non-public properties of a class - [#4768](https://github.com/detekt/detekt/pull/4768)
-   Self-inspect the detekt-gradle-plugin - [#4765](https://github.com/detekt/detekt/pull/4765)
-   Pass args to DetektInvoker as `List<String>` - [#4762](https://github.com/detekt/detekt/pull/4762)
-   Cleanup Gradle Plugin Publications - [#4752](https://github.com/detekt/detekt/pull/4752)
-   Break a dependency between `detekt-gradle-plugin` and `detekt-utils` - [#4748](https://github.com/detekt/detekt/pull/4748)
-   Remove suspend lambda rule with CoroutineScope receiver due to not de… - [#4747](https://github.com/detekt/detekt/pull/4747)
-   `VarCouldBeVal`: Add configuration flag `ignoreLateinitVar` - [#4745](https://github.com/detekt/detekt/pull/4745)
-   UnnecessaryInnerClass: fix false positive with references to function type variables - [#4738](https://github.com/detekt/detekt/pull/4738)
-   Fix false positive on VarCouldBeVal in generic classes - [#4733](https://github.com/detekt/detekt/pull/4733)
-   OutdatedDocumentation: fix false positive with no primary constructor - [#4728](https://github.com/detekt/detekt/pull/4728)
-   Android Gradle: add javac intermediates to classpath - [#4723](https://github.com/detekt/detekt/pull/4723)
-   OptionalWhenBraces: fix false negative when the single statement has comments inside - [#4722](https://github.com/detekt/detekt/pull/4722)
-   Document pre-commit hook for staged files - [#4711](https://github.com/detekt/detekt/pull/4711)
-   Enable rules by default for `1.21` - [#4643](https://github.com/detekt/detekt/issues/4643)

##### Dependency Updates

-   Update plugin binaryCompatibilityValidator to v0.12.0 - [#5456](https://github.com/detekt/detekt/pull/5456)
-   Update dependency gradle to v7.5 - [#5074](https://github.com/detekt/detekt/pull/5074)
-   Update plugin binaryCompatibilityValidator to v0.11.0 - [#5069](https://github.com/detekt/detekt/pull/5069)
-   Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.6.3 - [#4976](https://github.com/detekt/detekt/pull/4976)
-   Update dependency org.jetbrains.dokka to v1.7.0 - [#4974](https://github.com/detekt/detekt/pull/4974)
-   Update plugin binaryCompatibilityValidator to v0.10.1 - [#4954](https://github.com/detekt/detekt/pull/4954)
-   Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.6.2 - [#4868](https://github.com/detekt/detekt/pull/4868)
-   Update dependency com.android.tools.build:gradle to v7.2.1 - [#4861](https://github.com/detekt/detekt/pull/4861)
-   Update plugin binaryCompatibilityValidator to v0.10.0 - [#4837](https://github.com/detekt/detekt/pull/4837)
-   Update dependency io.mockk:mockk to v1.12.4 - [#4829](https://github.com/detekt/detekt/pull/4829)
-   Update dependency com.android.tools.build:gradle to v7.2.0 - [#4824](https://github.com/detekt/detekt/pull/4824)
-   Add dependency-analysis plugin and implement some recommendations - [#4798](https://github.com/detekt/detekt/pull/4798)
-   Add dependency on slf4j-nop to silence warning - [#4775](https://github.com/detekt/detekt/pull/4775)
-   Update plugin dokka to v1.6.21 - [#4770](https://github.com/detekt/detekt/pull/4770)
-   Update org.jetbrains.kotlin to v1.6.21 - [#4737](https://github.com/detekt/detekt/pull/4737)
-   Update dependency com.github.breadmoirai:github-release to v2.3.7 - [#4734](https://github.com/detekt/detekt/pull/4734)
-   Update plugin binaryCompatibilityValidator to v0.9.0 - [#4729](https://github.com/detekt/detekt/pull/4729)

##### Housekeeping & Refactorings

-   Fix `ComplexMethod` debt and refactor code - [#5029](https://github.com/detekt/detekt/pull/5029)
-   Fix ReturnCount debt and refactor code - [#5026](https://github.com/detekt/detekt/pull/5026)
-   Add test for ForbiddenMethodCall with getters - [#5018](https://github.com/detekt/detekt/pull/5018)
-   Measure flakyness on Windows CI - [#4742](https://github.com/detekt/detekt/pull/4742)
-   Declare nested test classes as non-static - [#4894](https://github.com/detekt/detekt/pull/4894)
-   Remove deprecated usages in gradle-plugin test - [#4889](https://github.com/detekt/detekt/pull/4889)
-   Remove reference to contributor list - [#4871](https://github.com/detekt/detekt/pull/4871)
-   Add missing image - [#4834](https://github.com/detekt/detekt/pull/4834)
-   Upgrade to GE enterprise 3.10 - [#4802](https://github.com/detekt/detekt/pull/4802)
-   Fix broken snapshot publishing - [#4783](https://github.com/detekt/detekt/pull/4783)
-   Remove pending Gradle version milestones from comments - [#4777](https://github.com/detekt/detekt/pull/4777)
-   Add more tests for Annotation Suppressor - [#4774](https://github.com/detekt/detekt/pull/4774)
-   fix: add test case that fails if environment is not properly set up - [#4769](https://github.com/detekt/detekt/pull/4769)
-   Disable UnusedImports for the detekt project - [#4741](https://github.com/detekt/detekt/pull/4741)
-   Remove Unnecesary @Nested - [#4740](https://github.com/detekt/detekt/pull/4740)
-   Update the argsfile to unblock `runWithArgsFile` failing locally - [#4718](https://github.com/detekt/detekt/pull/4718)

See all issues at: [1.21.0](https://github.com/detekt/detekt/milestone/86)

#### 1.20.0 - 2022-04-14

We're extremely excited to share with you all the next upcoming stable release of detekt: `1.20.0` 🎉
This release is coming with 16 new rules, new API and functionalities and several stability improvements.

First, much thanks to our sponsors ❤️ as we were able to buy a domain and move our website to [https://detekt.dev/](https://detekt.dev/).

As for the feature shipped, we work a lot on the Reporting side: we added a new type of reporting, improved the styling of the existing one and
generally reduced the unnecessary warnings of run with type resolution.

For rules like `ForbiddenMethod` where you can configure a signature of a method you want to use in your rule, we added a new syntax
that allows to reference generic methods & extension functions.

We update a lot of the libraries we depend on bringing detekt up to the ecosystem: KtLint 0.45.2, Kotlin 1.6.20 and Gradle 7.4.2 to name a few.

Finally, we also migrated all of our tests **from Spek to JUnit**. This was a huge effort that will hopefully make easier for contributors
to be involved with detekt.

As always, we want to take the opportunity to thank our contributors for testing, bug reporting and helping
us release this new version of detekt. You're more than welcome to join our community on the [#detekt](https://kotlinlang.slack.com/archives/C88E12QH4) channel on KotlinLang's Slack (you can [get an invite here](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)).

##### Notable Changes

-   With this detekt version, rule authors **can define the default configuration** for their custom rules. This default configuration will be merged together with the user configuration and can be overridden by the user if they wish. More on this here [#4315](https://github.com/detekt/detekt/pull/4315). The `formatting` ruleset provided by detekt is updated to use this new mechanism - [#4352](https://github.com/detekt/detekt/pull/4352)
-   We've added **16 new rules**:
    -   UnnecessaryInnerClass - [#4394](https://github.com/detekt/detekt/pull/4394)
    -   CanBeNonNullableProperty - [#4379](https://github.com/detekt/detekt/pull/4379)
    -   NullCheckOnMutableProperty - [#4353](https://github.com/detekt/detekt/pull/4353)
    -   SuspendFunWithCoroutineScopeReceiver - [#4616](https://github.com/detekt/detekt/pull/4616)
    -   ElseCaseInsteadOfExhaustiveWhen - [#4632](https://github.com/detekt/detekt/pull/4632)
    -   TrailingComma - From KtLint - [#4227](https://github.com/detekt/detekt/pull/4227)
    -   UnnecessaryParenthesesBeforeTrailingLambda - From KtLint - [#4630](https://github.com/detekt/detekt/pull/4630)
    -   BlockCommentInitialStarAlignment - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
    -   CommentWrapping - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
    -   DiscouragedCommentLocation - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
    -   FunKeywordSpacing - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
    -   FunctionTypeReferenceSpacing - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
    -   KdocWrapping - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
    -   ModifierListSpacing - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
    -   TypeArgumentListSpacing - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
    -   Wrapping - From KtLint - [#4645](https://github.com/detekt/detekt/pull/4645)
-   We've made several improvements to the **console reporting**:
    -   The HTML report has now a better CSS styling - [#4447](https://github.com/detekt/detekt/pull/4447)
    -   The default reporting format is now `LiteFindingsReport` (which is more compact reporting and similar to other tools in the ecosystem. [You can see an example here](https://github.com/detekt/detekt/pull/4027)) - [#4449](https://github.com/detekt/detekt/pull/4449).
    -   We've added issue details to findings on `FindingsReport` and `FileBasedFindingsReporter` - [#4464](https://github.com/detekt/detekt/pull/4464)
    -   We suppressed several warnings reported when running with type resolution - [#4423](https://github.com/detekt/detekt/pull/4423)
-   We fixed a **regression** introduced in `1.19.0` for users using `ignoreAnnotated` running **without type resolution** - [#4570](https://github.com/detekt/detekt/pull/4570)
-   For rules like `ForbiddenMethod` where you can specify a method name in the config file, now we added support for:
    -   Matching functions with generics - [#4460](https://github.com/detekt/detekt/pull/4460)
    -   Matching extension functions - [#4459](https://github.com/detekt/detekt/pull/4459)
-   We've fixed a security vulnerability related to XML parsing - [#4499](https://github.com/detekt/detekt/pull/4499)
-   We've changed the behavior of the baseline task. Now the baseline is always update, even if you fixed all the issues in your codebase - [#4445](https://github.com/detekt/detekt/pull/4445)
-   We now enable the naming ruleset by default also on tests. Previously they were excluded - [#4438](https://github.com/detekt/detekt/pull/4438)
-   This version of detekt is built with Gradle `v7.4.2`, AGP `7.1.3` and Kotlin `1.6.20` (see [#4530](https://github.com/detekt/detekt/pull/4530) [#4573](https://github.com/detekt/detekt/pull/4573) [#4133](https://github.com/detekt/detekt/pull/4133) [#4277](https://github.com/detekt/detekt/pull/4277) [#4665](https://github.com/detekt/detekt/pull/4665))
-   This version of detekt is wrapping KtLint version `0.45.2` (see [#4227](https://github.com/detekt/detekt/pull/4227) [#4630](https://github.com/detekt/detekt/pull/4630) [#4645](https://github.com/detekt/detekt/pull/4645) [#4690](https://github.com/detekt/detekt/pull/4690))
-   For contributors: we migrated all our tests **from Spek to JUnit** due to better support and tooling [#4670](https://github.com/detekt/detekt/pull/4670).

##### Changelog

-   Display dynamic --jvm-target values when using --help flag - [#4694](https://github.com/detekt/detekt/pull/4694)
-   CanBeNonNullable shouldn't consider abstract properties - [#4686](https://github.com/detekt/detekt/pull/4686)
-   NonBooleanPropertyPrefixedWithIs: Allow boolean function reference - [#4684](https://github.com/detekt/detekt/pull/4684)
-   [VarCouldBeVal] fix overrides false positives - [#4664](https://github.com/detekt/detekt/pull/4664)
-   Add ignoreOverridden support for BooleanPropertyNaming rule - [#4654](https://github.com/detekt/detekt/pull/4654)
-   Fix regression generating configuration - [#4646](https://github.com/detekt/detekt/pull/4646)
-   Fix concurrency issue when creating PomModel (#4609) - [#4631](https://github.com/detekt/detekt/pull/4631)
-   UnnecessaryAbstractClass: fix false positive when the abstract class has properties in the primary constructor - [#4628](https://github.com/detekt/detekt/pull/4628)
-   Properly set toolVersion on DetektExtension - [#4623](https://github.com/detekt/detekt/pull/4623)
-   NamedArguments: Ignore when argument values are the same as the parameter name - [#4613](https://github.com/detekt/detekt/pull/4613)
-   Parallel invocation of AnalysisFacade fails spuriously in 1.20.0-RC1 - [#4609](https://github.com/detekt/detekt/issues/4609)
-   NoSuchElementException after updating to 1.20.0-RC1 - [#4604](https://github.com/detekt/detekt/issues/4604)
-   Better error classification in Gradle Enterprise. - [#4586](https://github.com/detekt/detekt/pull/4586)
-   Fix for missing /kotlin folder when running on Android projects - [#4554](https://github.com/detekt/detekt/pull/4554)
-   Deprecate continuationIndentSize from the Indentation rule - [#4551](https://github.com/detekt/detekt/pull/4551)
-   Fix performance issue for regexp in Reporting.kt - [#4550](https://github.com/detekt/detekt/pull/4550)
-   Revert "trim values when parsing the baseline (#4335)" - [#4548](https://github.com/detekt/detekt/pull/4548)
-   Fix AutoCorrection crashing with Missing extension point - [#4545](https://github.com/detekt/detekt/pull/4545)
-   Make DoubleMutabilityForCollection configurable and set a DoubleMutability alias - [#4541](https://github.com/detekt/detekt/pull/4541)
-   Fix `AnnotationExcluder` - [#4518](https://github.com/detekt/detekt/pull/4518)
-   Fix false positive of UnnecessaryInnerClass - [#4509](https://github.com/detekt/detekt/pull/4509)
-   [MaxLineLength] Fix signature in for blank characters in the Baseline - [#4504](https://github.com/detekt/detekt/pull/4504)
-   Fix overridden function reporting for CanBeNonNullable rule - [#4497](https://github.com/detekt/detekt/pull/4497)
-   Set the name of functions and paramenters between ` to improve the readability - [#4488](https://github.com/detekt/detekt/pull/4488)
-   update InvalidPackageDeclaration to report if rootPackage is not present - [#4484](https://github.com/detekt/detekt/pull/4484)
-   [VarCouldBeVal] Override vars will not be flagged if bindingContext is not set - [#4477](https://github.com/detekt/detekt/pull/4477)
-   Document the overlapping rules from `formatting` - [#4473](https://github.com/detekt/detekt/pull/4473)
-   Match functions signatures with lambdas on it - [#4458](https://github.com/detekt/detekt/pull/4458)
-   Add option for OutdatedDocumentation to allow param in constructor pr… - [#4453](https://github.com/detekt/detekt/pull/4453)
-   Ignore private operators when we don't have ContextBingding in UnusedPrivateMember - [#4441](https://github.com/detekt/detekt/pull/4441)
-   Add documentation for `Suppressor`s - [#4440](https://github.com/detekt/detekt/issues/4440)
-   [FunctionNaming] Don't allow the usage of ` in function names - [#4439](https://github.com/detekt/detekt/pull/4439)
-   Add list of functions to skip in IgnoredReturnValue rule - [#4434](https://github.com/detekt/detekt/pull/4434)
-   Extend CanBeNonNullable rule to check function params - [#4431](https://github.com/detekt/detekt/pull/4431)
-   Extend VarCouldBeVal to include analysis of file- and class-level properties - [#4424](https://github.com/detekt/detekt/pull/4424)
-   Formulate rule/sample-extensions descriptions consistently - [#4412](https://github.com/detekt/detekt/pull/4412)
-   Fix false-positive on ExplicitCollectionElementAccessMethod - [#4400](https://github.com/detekt/detekt/pull/4400)
-   Fixes false negatives in `UnnecessaryAbstractClass` - [#4399](https://github.com/detekt/detekt/pull/4399)
-   Add first draft of a rule description style guide - [#4386](https://github.com/detekt/detekt/pull/4386)
-   Forbid usage of java.lang.ClassLoader.getResourceAsStream - [#4381](https://github.com/detekt/detekt/pull/4381)
-   Update Sponsor button to detekt's one - [#4378](https://github.com/detekt/detekt/pull/4378)
-   [OptionalUnit] Allow a function to declare a Unit return type when it uses a generic function initializer - [#4371](https://github.com/detekt/detekt/pull/4371)
-   Completely-empty abstract classes will now be flagged by UnnecessaryAbstractClass - [#4370](https://github.com/detekt/detekt/pull/4370)
-   Fix false positive in RethrowCaughtException for try with more than one catch (#4367) - [#4369](https://github.com/detekt/detekt/pull/4369)
-   Testing and rule improvement for EmptyElseBlock - [#4349](https://github.com/detekt/detekt/pull/4349)
-   UnusedPrivateMember should not report external classes/interfaces - [#4347](https://github.com/detekt/detekt/pull/4347)
-   [UseDataClass] Do not report on `inner` classes - [#4344](https://github.com/detekt/detekt/pull/4344)
-   Support jvmTarget 17 - [#4287](https://github.com/detekt/detekt/issues/4287)
-   UnderscoresInNumericLiterals: Allow numbers with non standard groupings - [#4280](https://github.com/detekt/detekt/pull/4280)
-   Introduce DefaultValue type - [#3928](https://github.com/detekt/detekt/pull/3928)

##### Dependency Updates

-   Update plugin dokka to v1.6.20 - [#4717](https://github.com/detekt/detekt/pull/4717)
-   Update dependency com.android.tools.build:gradle to v7.1.3 - [#4695](https://github.com/detekt/detekt/pull/4695)
-   JaCoCo 0.8.8 - [#4680](https://github.com/detekt/detekt/pull/4680)
-   Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.6.1 - [#4673](https://github.com/detekt/detekt/pull/4673)
-   Update dependency gradle to v7.4.2 - [#4658](https://github.com/detekt/detekt/pull/4658)
-   Update dependency org.jetbrains.kotlinx:kotlinx-html-jvm to v0.7.5 - [#4657](https://github.com/detekt/detekt/pull/4657)
-   Update dependency gradle to v7.4.1 - [#4622](https://github.com/detekt/detekt/pull/4622)
-   Update dependency com.android.tools.build:gradle to v7.1.2 - [#4594](https://github.com/detekt/detekt/pull/4594)
-   Update dependency com.android.tools.build:gradle to v7.1.1 - [#4561](https://github.com/detekt/detekt/pull/4561)
-   Update plugin pluginPublishing to v0.20.0 - [#4502](https://github.com/detekt/detekt/pull/4502)
-   Update JamesIves/github-pages-deploy-action action to v4.2.1 - [#4475](https://github.com/detekt/detekt/pull/4475)
-   Update JamesIves/github-pages-deploy-action action to v4.1.9 - [#4455](https://github.com/detekt/detekt/pull/4455)
-   Update plugin gradleVersions to v0.41.0 - [#4454](https://github.com/detekt/detekt/pull/4454)
-   Revert "Update plugin pluginPublishing to v0.19.0 (#4429)" - [#4452](https://github.com/detekt/detekt/pull/4452)
-   Update plugin pluginPublishing to v0.19.0 - [#4429](https://github.com/detekt/detekt/pull/4429)
-   Update dependency io.mockk:mockk to v1.12.2 - [#4427](https://github.com/detekt/detekt/pull/4427)
-   Shadow 7.1.2 - [#4422](https://github.com/detekt/detekt/pull/4422)
-   Update plugin dokka to v1.6.10 - autoclosed - [#4407](https://github.com/detekt/detekt/pull/4407)
-   Update dependency org.jetbrains.dokka:jekyll-plugin to v1.6.10 - [#4406](https://github.com/detekt/detekt/pull/4406)
-   Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.6.0 - [#4393](https://github.com/detekt/detekt/pull/4393)
-   Update dependency gradle to v7.3.3 - [#4392](https://github.com/detekt/detekt/pull/4392)
-   Update dependency org.yaml:snakeyaml to v1.30 - [#4375](https://github.com/detekt/detekt/pull/4375)
-   Update dependency gradle to v7.3.2 - [#4374](https://github.com/detekt/detekt/pull/4374)
-   Update plugin shadow to v7.1.1 - [#4373](https://github.com/detekt/detekt/pull/4373)
-   Update dependency gradle to v7.3.1 - [#4350](https://github.com/detekt/detekt/pull/4350)
-   Update plugin dokka to v1.6.0 - [#4328](https://github.com/detekt/detekt/pull/4328)

##### Housekeeping & Refactorings

-   Add missing Test annotations - [#4699](https://github.com/detekt/detekt/pull/4699)
-   Add failure message assertions to Gradle's "expect failure" tests - [#4693](https://github.com/detekt/detekt/pull/4693)
-   Drop (most) Groovy DSL tests - [#4687](https://github.com/detekt/detekt/pull/4687)
-   Check detekt-gradle-plugin functionalTest source when running detekt task - [#4681](https://github.com/detekt/detekt/pull/4681)
-   Fix typo in AvoidReferentialEquality rule description - [#4644](https://github.com/detekt/detekt/pull/4644)
-   Housekeep Gradle scripts - [#4589](https://github.com/detekt/detekt/pull/4589)
-   Refactor config printer to improve testability - [#4580](https://github.com/detekt/detekt/pull/4580)
-   avoid usage of java stream for parameterized tests - [#4579](https://github.com/detekt/detekt/pull/4579)
-   split rule documentation printer to improve testability - [#4578](https://github.com/detekt/detekt/pull/4578)
-   Make VERSION_CATALOGS stable - [#4577](https://github.com/detekt/detekt/pull/4577)
-   Enable Gradle's configuration cache by default - [#4576](https://github.com/detekt/detekt/pull/4576)
-   Migrate detekt-rules-performance tests to JUnit - [#4569](https://github.com/detekt/detekt/pull/4569)
-   Migrate detekt-rules-complexity tests to JUnit - [#4566](https://github.com/detekt/detekt/pull/4566)
-   Drop Groovy DSL testing in DetektTaskDslSpec - [#4563](https://github.com/detekt/detekt/pull/4563)
-   Reuse `setReportOutputConventions` - [#4546](https://github.com/detekt/detekt/pull/4546)
-   Code cleanups - [#4542](https://github.com/detekt/detekt/pull/4542)
-   Fix MaxLineLength violation on detekt main inside IgnoredReturnValue rule - [#4539](https://github.com/detekt/detekt/pull/4539)
-   Use Java 17 for all CI jobs - [#4526](https://github.com/detekt/detekt/pull/4526)
-   Migrate tests in detekt-rules-errorprone to junit - [#4523](https://github.com/detekt/detekt/pull/4523)
-   Drop unused dependencies - [#4506](https://github.com/detekt/detekt/pull/4506)
-   Update JUnit dependencies - [#4505](https://github.com/detekt/detekt/pull/4505)
-   Fixes test for LiteFindingsReport - [#4479](https://github.com/detekt/detekt/pull/4479)
-   Remove outdated detekt suppression - [#4468](https://github.com/detekt/detekt/pull/4468)
-   Add test cases to RedundantSuspendModifier rule - [#4430](https://github.com/detekt/detekt/pull/4430)
-   Refactor MultilineLambdaItParameter rule - [#4428](https://github.com/detekt/detekt/pull/4428)
-   Formulate rule/naming descriptions consistently - [#4419](https://github.com/detekt/detekt/pull/4419)
-   Formulate rule/bugs descriptions consistently - [#4418](https://github.com/detekt/detekt/pull/4418)
-   Formulate rule/complexity descriptions consistently - [#4417](https://github.com/detekt/detekt/pull/4417)
-   Formulate rule/documentation descriptions consistently - [#4416](https://github.com/detekt/detekt/pull/4416)
-   Formulate rule/coroutines descriptions consistently - [#4415](https://github.com/detekt/detekt/pull/4415)
-   Formulate rule/style descriptions consistently - [#4414](https://github.com/detekt/detekt/pull/4414)
-   Formulate rule/exceptions descriptions consistently - [#4413](https://github.com/detekt/detekt/pull/4413)
-   Formulate rule/performance descriptions consistently - [#4411](https://github.com/detekt/detekt/pull/4411)
-   Make MultiRuleCollector.kt consistent with the DoubleMutabilityForCollection rule - [#4405](https://github.com/detekt/detekt/pull/4405)
-   Add test for nested SwallowedException - [#4404](https://github.com/detekt/detekt/pull/4404)
-   Disable CI for Windows & JDK8 - [#4403](https://github.com/detekt/detekt/pull/4403)
-   Improve test description in ForEachOnRangeSpec.kt - [#4402](https://github.com/detekt/detekt/pull/4402)
-   Don't define classes on default package - [#4401](https://github.com/detekt/detekt/pull/4401)
-   Config file in directory test - [#4398](https://github.com/detekt/detekt/pull/4398)
-   Remove unnecessary map lambda in test code - [#4397](https://github.com/detekt/detekt/pull/4397)
-   Improve `AnnotationExcluder` tests - [#4368](https://github.com/detekt/detekt/pull/4368)
-   Enable UseAnyOrNoneInsteadOfFind - [#4362](https://github.com/detekt/detekt/pull/4362)
-   Enable ForbiddenMethodCall - [#4334](https://github.com/detekt/detekt/pull/4334)

See all issues at: [1.20.0](https://github.com/detekt/detekt/milestone/85)

#### 1.19.0 - 2021-11-29

Please welcome the next upcoming stable release of detekt: `1.19.0` 🎉
This release is coming with a lot of new features, new rules, evolution in the API and stability improvements.

Specifically, we've shipped some features that will allow you to better adapt detekt to run on codebases
that are using JetPack compose with features such as [`ignoreAnnotated` and `ignoreFunction`](/docs/introduction/suppressors).

As always, we want to take the opportunity to thank our contributors for testing, bug reporting and helping
us release this new version of detekt. You're more than welcome to join our community on the [#detekt](https://kotlinlang.slack.com/archives/C88E12QH4) channel on KotlinLang's Slack (you can [get an invite here](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)).

##### Notable Changes

-   We now offer an `ignoreAnnotated` configuration key that you can use on all your rules to suppress findings if inside an annotated block (e.g. `@Composable`) - [#4102](https://github.com/detekt/detekt/pull/4102) and [#4241](https://github.com/detekt/detekt/pull/4241)
-   Similarly, we now offer also an `ignoreFunction` configuration key that you can use to suppress findings if inside a function with a given name - [#4148](https://github.com/detekt/detekt/pull/4148)
-   Report configuration is changing in the Gradle plugin. The `reports` extension on the `detekt` extension has been
    deprecated. See the Migration section below for steps to migrate to the new recommended configuration - [#3687](https://github.com/detekt/detekt/pull/3687)
-   The `ExplicitCollectionElementAccessMethod` rule is now a type-resolution only rule - [#4201](https://github.com/detekt/detekt/pull/4201)
-   The `InvalidPackageDeclaration` rule has been split to create the `MissingPackageDeclaration` rule - [#4149](https://github.com/detekt/detekt/pull/4149)
-   The `ForbiddenComment` rule now offers a `customMessage` configuration key - [#4126](https://github.com/detekt/detekt/pull/4126)
-   We bumped ktlint and updated the default enabled rules to mirror what ktlint is doing - [#4179](https://github.com/detekt/detekt/pull/4179)
-   Added a new `LambdaParameterNaming` rule, to enforce a naming convention of parameter inside lambdas - [#4147](https://github.com/detekt/detekt/pull/4147)
-   Added a new `InjectDispatcher` rule, to check if dispatchers are injectable - [#4222](https://github.com/detekt/detekt/pull/4222)
-   Added a new `ConsoleReport` format - [#4027](https://github.com/detekt/detekt/pull/4027)
-   Gradle: We added the `--auto-correct` cmdline option to gradle tasks - [#4202](https://github.com/detekt/detekt/pull/4202)
-   Gradle: We removed the `afterEvaluate` wrapper from the Android and KMM plugin - [#4159](https://github.com/detekt/detekt/pull/4159) and [#4271](https://github.com/detekt/detekt/pull/4271)
-   We now test against Java 17 and stopped testing against Java 16 - [#4136](https://github.com/detekt/detekt/pull/4136)
-   Remove library specific configurations like Jetpack Compose and Dagger from the default config - [#4101](https://github.com/detekt/detekt/pull/4101)
-   Remove detekt-bom module - [#4043](https://github.com/detekt/detekt/pull/4043)
-   Use reference in fallback property delegate - [#3982](https://github.com/detekt/detekt/pull/3982)

##### Migration

Configuring reports in the Gradle plugin should be done at the task level instead of at the extension (or global) level.
The previous recommendation resulted in the report output for multiple tasks overwriting each other when multiple detekt
tasks were executed in the same Gradle run.

Before this release the recommended way to configure reports was using the `detekt` extension:

```kotlin
detekt {
    reports {
        xml {
            enabled = true
            destination = file("build/reports/detekt/detekt.xml")
        }
    }
}
```

This meant all detekt tasks would output the report to the same destination. From this detekt release you should enable
and disable reports for all tasks using the `withType` Gradle method:

```kotlin
// Kotlin DSL
tasks.withType<Detekt>().configureEach {
    reports {
        xml.required.set(true)
    }
}
```

```groovy
// Groovy DSL
tasks.withType(Detekt).configureEach {
    reports {
        xml.required.set(true)
    }
}
```

To customize the report output location configure the task individually:

```kotlin
tasks.detektMain {
    reports {
        xml {
            outputLocation.set(file("build/reports/detekt/customPath.xml"))
            required.set(true) // reports can also be enabled and disabled at the task level as needed
        }
    }
}
```

##### Changelog

-   trim values when parsing the baseline - [#4335](https://github.com/detekt/detekt/pull/4335)
-   Fix #4332 by widening the scope to all JDKs - [#4333](https://github.com/detekt/detekt/pull/4333)
-   Bugfix provided by #4225 needs wider scope - [#4332](https://github.com/detekt/detekt/issues/4332)
-   Avoid false positives in MemberNameEqualsClassName - [#4329](https://github.com/detekt/detekt/pull/4329)
-   Add two new config steps for Compose - [#4322](https://github.com/detekt/detekt/pull/4322)
-   Set DetektJvm task source with SourceDirectorySet instead of file list - [#4151](https://github.com/detekt/detekt/pull/4151)
-   Add documentation about how to configure Baseline task with type resolution - [#4285](https://github.com/detekt/detekt/pull/4285)
-   Remove kotlin-gradle-plugin-api from runtime classpath - [#4275](https://github.com/detekt/detekt/pull/4275)
-   Use appropriate annotations on source properties in Gradle tasks - [#4264](https://github.com/detekt/detekt/pull/4264)
-   Replace usage of deprecated ConfigureUtil - [#4263](https://github.com/detekt/detekt/pull/4263)
-   Fix test failure of ReportMergeSpec - [#4262](https://github.com/detekt/detekt/pull/4262)
-   Revert "Remove afterEvaluate wrapper (#4159)" - [#4259](https://github.com/detekt/detekt/pull/4259)
-   ExplicitCollectionElementAccessMethodSpec: does not report methods that is called on implicit receiver - [#4256](https://github.com/detekt/detekt/pull/4256)
-   UnusedPrivateMember: fix false positive with operator `in` - [#4249](https://github.com/detekt/detekt/pull/4249)
-   Introduce UseAnyOrNoneInsteadOfFind rule - [#4247](https://github.com/detekt/detekt/pull/4247)
-   OptionalWhenBraces: fix false negative for nested when - [#4246](https://github.com/detekt/detekt/pull/4246)
-   Handle MultiRules in Suppressors - [#4239](https://github.com/detekt/detekt/pull/4239)
-   Fix UselessCallOnNotNull rule - [#4237](https://github.com/detekt/detekt/pull/4237)
-   Make detekt a bit less noisy when mixing java and kotlin files - [#4231](https://github.com/detekt/detekt/pull/4231)
-   Workaround for JDK 8 instability when reading config - [#4225](https://github.com/detekt/detekt/pull/4225)
-   Define FunctionSignature - [#4176](https://github.com/detekt/detekt/pull/4176)
-   ForbiddenMethodCall: report overriding method calls - [#4205](https://github.com/detekt/detekt/pull/4205)
-   ObjectLiteralToLambda: fix false positive when using Java interfaces with default methods - [#4203](https://github.com/detekt/detekt/pull/4203)
-   Unit tests for TooGenericExceptionThrown - [#4198](https://github.com/detekt/detekt/pull/4198)
-   Display correct --jvm-target values when using --help flag - [#4195](https://github.com/detekt/detekt/pull/4195)
-   Improved `MaximumLineLength` documentation - [#4188](https://github.com/detekt/detekt/pull/4188)
-   Report NewLineAtEndOfFile source location at end of file - [#4187](https://github.com/detekt/detekt/pull/4187)
-   #4169 OutdatedDocumentation rule - [#4185](https://github.com/detekt/detekt/pull/4185)
-   Don't report on platform types in NullableToStringCall - [#4180](https://github.com/detekt/detekt/pull/4180)
-   Fix #4140: Allow Bazel based tests to run with string test input - [#4170](https://github.com/detekt/detekt/pull/4170)
-   Improve ForbiddenMethodCall documentation - [#4166](https://github.com/detekt/detekt/pull/4166)
-   Report SwallowedException on catchParameter - [#4158](https://github.com/detekt/detekt/pull/4158)
-   Enable binary compatibility validator for detekt-test and detekt-test-api - [#4157](https://github.com/detekt/detekt/pull/4157)
-   Fix issues with Elvis operator in UnconditionalJumpStatementInLoop - [#4150](https://github.com/detekt/detekt/pull/4150)
-   Improve documentation for naming rules - [#4146](https://github.com/detekt/detekt/pull/4146)
-   Disable `UnsafeCallOnNullableType` on tests - [#4123](https://github.com/detekt/detekt/pull/4123)
-   Remove annotations from LateinitUsage noncompliant block - [#4100](https://github.com/detekt/detekt/pull/4100)
-   UnnecessaryAbstractClass: false positive when the abstract class has internal/protected abstract members - [#4099](https://github.com/detekt/detekt/pull/4099)
-   Deprecate DefaultContext - [#4098](https://github.com/detekt/detekt/pull/4098)
-   Fix confusing message when breaking the MultilineLambdaItParameter rule - [#4089](https://github.com/detekt/detekt/pull/4089)
-   Remove deprecated KotlinExtension - [#4063](https://github.com/detekt/detekt/pull/4063)
-   Add an alias for FunctionMinLength/FunctionMaxLength rules to be more descriptive - [#4050](https://github.com/detekt/detekt/pull/4050)
-   fix report path, default path is reports/detekt/... - [#4034](https://github.com/detekt/detekt/pull/4034)
-   Fix TextLocation of Indentation rule - [#4030](https://github.com/detekt/detekt/pull/4030)
-   detekt-bom is going away after 1.18.0 - [#3988](https://github.com/detekt/detekt/issues/3988)
-   UnderscoresInNumericLiterals acceptableDecimalLength is off by one - [#3972](https://github.com/detekt/detekt/pull/3972)
-   Create rule set configurations in a safe way - [#3964](https://github.com/detekt/detekt/pull/3964)
-   Remove UnnecessarySafeCall safeguard against ErrorType - [#3439](https://github.com/detekt/detekt/pull/3439)

##### Dependency Updates

-   Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.5.2 - [#4302](https://github.com/detekt/detekt/pull/4302)
-   Update dependency io.mockk:mockk to v1.12.1 - [#4297](https://github.com/detekt/detekt/pull/4297)
-   Update dependency com.android.tools.build:gradle to v4.2.2 - [#4296](https://github.com/detekt/detekt/pull/4296)
-   Gradle Publishing Plugin 0.17.0 - [#4270](https://github.com/detekt/detekt/pull/4270)
-   Shadow 7.1.0 - [#4269](https://github.com/detekt/detekt/pull/4269)
-   Dokka 1.5.31 - [#4268](https://github.com/detekt/detekt/pull/4268)
-   Binary Compatibility Validator 0.8.0 - [#4267](https://github.com/detekt/detekt/pull/4267)
-   Reflections 0.10.2 - [#4266](https://github.com/detekt/detekt/pull/4266)
-   Upgrade to Gradle 7.3 - [#4254](https://github.com/detekt/detekt/pull/4254)
-   Dokka 1.5.30 - [#4114](https://github.com/detekt/detekt/pull/4114)
-   Kotlin 1.5.31 - [#4113](https://github.com/detekt/detekt/pull/4113)
-   Update dependencies - [#4065](https://github.com/detekt/detekt/pull/4065)

##### Housekeeping & Refactorings

-   Simplify YamlConfig - [#4316](https://github.com/detekt/detekt/pull/4316)
-   Move tests to the correct module - [#4314](https://github.com/detekt/detekt/pull/4314)
-   Don't hide null issues - [#4313](https://github.com/detekt/detekt/pull/4313)
-   Add functional test for type resolution for JVM - [#4307](https://github.com/detekt/detekt/pull/4307)
-   Minor typo fix and code refactoring - [#4284](https://github.com/detekt/detekt/pull/4284)
-   Improve Tests of UnnecesaryLet - [#4282](https://github.com/detekt/detekt/pull/4282)
-   Fix typo in UnnecessaryLet - [#4281](https://github.com/detekt/detekt/pull/4281)
-   Fix typo in Gradle lib definition - [#4255](https://github.com/detekt/detekt/pull/4255)
-   Rename DoubleMutabilityInCollectionSpec to DoubleMutabilityForCollectionSpec - [#4251](https://github.com/detekt/detekt/pull/4251)
-   Simplify conditional checks to improve coverage - [#4221](https://github.com/detekt/detekt/pull/4221)
-   Refactor NoTabs to remove DetektVisitor - [#4220](https://github.com/detekt/detekt/pull/4220)
-   Fix typos and grammar in rule descriptions - [#4219](https://github.com/detekt/detekt/pull/4219)
-   Use Kotlin's ArrayDeque implementation - [#4218](https://github.com/detekt/detekt/pull/4218)
-   Update Kotlin docs URL - [#4217](https://github.com/detekt/detekt/pull/4217)
-   Report UntilInsteadOfRangeTo for 'rangeTo' calls - [#4212](https://github.com/detekt/detekt/pull/4212)
-   Add tests for merging reports - [#4199](https://github.com/detekt/detekt/pull/4199)
-   Setup Gradle functional tests - [#4074](https://github.com/detekt/detekt/pull/4074)
-   GitHub Actions cache fixes - [#3723](https://github.com/detekt/detekt/pull/3723)
-   Simplify where casts used unnecessarily - [#4213](https://github.com/detekt/detekt/pull/4213)
-   Don't specify Gradle Enterprise Gradle Plugin version - [#4210](https://github.com/detekt/detekt/pull/4210)
-   Fix baserule import in tests - [#4189](https://github.com/detekt/detekt/pull/4189)
-   Run CLI sanity checks with Gradle - [#4186](https://github.com/detekt/detekt/pull/4186)
-   Use Codecov GitHub Action to upload coverage - [#4184](https://github.com/detekt/detekt/pull/4184)
-   Enable ParameterListWrapping rule on detekt codebase - [#4178](https://github.com/detekt/detekt/pull/4178)
-   Add test cases for MagicNumber - [#4152](https://github.com/detekt/detekt/pull/4152)
-   Fix FunctionParameterNamingSpec - [#4145](https://github.com/detekt/detekt/pull/4145)
-   Address feedback on #4139 - [#4143](https://github.com/detekt/detekt/pull/4143)
-   Don't skip tests that now pass - [#4142](https://github.com/detekt/detekt/pull/4142)
-   Fixes for Kotlin 1.6.0-M1 - [#4139](https://github.com/detekt/detekt/pull/4139)
-   Don't unnecessarily propogate opt-in requirement - [#4116](https://github.com/detekt/detekt/pull/4116)
-   Drop junit-platform-launcher dependency - [#4115](https://github.com/detekt/detekt/pull/4115)
-   Ensure detekt-tooling public API is stable - [#4112](https://github.com/detekt/detekt/pull/4112)
-   Fix globing typo - [#4107](https://github.com/detekt/detekt/pull/4107)
-   Rename and split ValidateConfig files - [#4105](https://github.com/detekt/detekt/pull/4105)
-   Dynamic deprecation - [#4104](https://github.com/detekt/detekt/pull/4104)
-   Fix indent issues with continuation indent - [#4103](https://github.com/detekt/detekt/pull/4103)
-   Refactor so detekt-gradle-plugin can be added as an included build - [#4094](https://github.com/detekt/detekt/pull/4094)
-   Migrate buildSrc to composite build - [#4090](https://github.com/detekt/detekt/pull/4090)
-   Fix broken `applySelfAnalysisVersion` task - [#4082](https://github.com/detekt/detekt/pull/4082)
-   Convert DetektJvmSpec to use ProjectBuilder - [#4075](https://github.com/detekt/detekt/pull/4075)
-   Upscale JVM settings - [#4057](https://github.com/detekt/detekt/pull/4057)
-   Gradle 7.2 - [#4056](https://github.com/detekt/detekt/pull/4056)
-   Verify at compile time that issue id matches rule name - [#4047](https://github.com/detekt/detekt/pull/4047)

See all issues at: [1.19.0](https://github.com/detekt/detekt/milestone/83)

#### 1.18.1 - 2021-08-30

This is a point release for detekt `1.18.0` containing bugfixes for problems that got discovered just after the release.

##### Notable Changes

-   MultiRule should pass correctly the BindingContext - [#4071](https://github.com/detekt/detekt/pull/4071)
-   Allow active, excludes and includes in the rule-set configuration - [#4045](https://github.com/detekt/detekt/pull/4045)
-   Remove Error from ThrowingExceptionsWithoutMessageOrCause because is a common name - [#4046](https://github.com/detekt/detekt/pull/4046)
-   Fix issue IDs for ReferentialEquality and DoubleMutability - [#4040](https://github.com/detekt/detekt/pull/4040)

See all issues at: [1.18.1](https://github.com/detekt/detekt/milestone/84)

#### 1.18.0 - 2021-08-12

We're more than excited to introduce you a next stable release of detekt: `1.18.0` 🎉
This release is coming with a lot of changes, new rules, evolution in the API and stability improvements.

We want to take the opportunity to thank our contributors for testing, bug reporting and helping
us release this new version of detekt.

##### Notable Changes

-   We've added two new rules: `AvoidReferentialEquality` and `BooleanPropertyNaming` (see [#3924](https://github.com/detekt/detekt/pull/3924) and [#3795](https://github.com/detekt/detekt/pull/3795))
-   This version of detekt ships with Kotlin `1.5.21`, and we're compiling with `apiVersion` set to `1.4` - [#3956](https://github.com/detekt/detekt/pull/3956) and [#3852](https://github.com/detekt/detekt/pull/3852)
-   The minimum version of Gradle to use Detekt Gradle Plugin is now `6.1` - [#3830](https://github.com/detekt/detekt/pull/3830)
-   This version of detekt has been tested against Java 16 - [#3698](https://github.com/detekt/detekt/pull/3698)
-   We fixed a long-standing bug related to parallel execution (#3248) - [#3799](https://github.com/detekt/detekt/pull/3799) and [#3822](https://github.com/detekt/detekt/pull/3822)
-   We now use multi-line format for list options in the default detekt config file - [#3827](https://github.com/detekt/detekt/pull/3827)
-   The rule `VarCouldBeVal` has been updated and now works only with type resolution to provide more precise findings - [#3880](https://github.com/detekt/detekt/pull/3880)
-   We removed all the references to `Extensions.getRootArea` that is now deprecated from our codebase. This was affecting users with sporadic crashes. - [#3848](https://github.com/detekt/detekt/pull/3848)
-   For _detekt_ rule authors: We created a Github Template that you can use to bootstrap your custom rule project: [detekt-custom-rule-template](https://github.com/detekt/detekt-custom-rule-template). You can use JitPack to host it and share your rule easily with other members of the community.
-   For _detekt_ rule authors: We finished the rework to use the annotations instead of kdoc tags in rules. Specifically configurations must be configured using `@Configuration` while auto-correction capability should be specified with the `@AutoCorrectable` annotation [#3820](https://github.com/detekt/detekt/pull/3820).

##### Migration

-   We renamed the `input` property inside the `detekt{}` extension of the Gradle plugin to `source`. The `input` property has been deprecated, and we invite you to migrate to the new property (see [#3951](https://github.com/detekt/detekt/pull/3951))

```
// BEFORE
detekt {
    input = files(...)
}

// AFTER
detekt {
    source = files(...)
}
```

-   For all rule authors: When accessing a config value within a rule, using `valueOrDefault` and `valueOrDefaultCommaSeparated` is no longer recommended. While both will remain part of the public api, they should be replaced by one of the config delegates (see [#3891](https://github.com/detekt/detekt/pull/3891)). The key that is used to lookup the configured value is derived from the property name.

```kotlin
/* simple property */
// BEFORE
val ignoreDataClasses = valueOrDefault("ignoreDataClasses", true)
// AFTER
val ignoreDataClasses: Boolean by config(true)

/* transformed simple property */
// BEFORE
val ignoredName = valueOrDefault("ignoredName", "").trim()
// AFTER
val ignoredName: String by config("", String::trim)

/* transformed list property */
// BEFORE
val ignoreAnnotated = valueOrDefaultCommaSeparated("ignoreAnnotated", listOf("Inject", "Value"))
        .map(String::trim)
// AFTER
val ignoreAnnotated: List<String> by config(listOf("Inject", "Value")) { list ->
    list.map(String::trim)
}
```

-   For all rule authors: The types `ThresholdRule` and `LazyRegex` have been marked as deprecated and will be removed in a future release. Please migrate to config delegates.

```kotlin
/* ThresholdRule */
// BEFORE
class MyRule(config: Config, threshold: Int = 10) : ThresholdRule(config, threshold) {
    // ...
}
// AFTER
class MyRule(config: Config) : Rule(config) {
    private val threshold: Int by config(10)
    // ...
}

/* LazyRegex */
// BEFORE
private val allowedPattern: Regex by LazyRegex("allowedPatterns", "")
// AFTER
private val allowedPattern: Regex by config("", String::toRegex)
```

-   For custom rule authors: This will be the last version of detekt where we publish the `detekt-bom` artifact. This change should not affect anyone. If it affects you, [please let us know](https://github.com/detekt/detekt/issues/3988).

##### Changelog

-   [KMP] Fix resolution of Android test classpaths - [#4026](https://github.com/detekt/detekt/pull/4026)
-   Sort config lists - [#4014](https://github.com/detekt/detekt/pull/4014)
-   Multiplatform tasks should not depend on check - [#4025](https://github.com/detekt/detekt/pull/4025)
-   mark configWithFallback as unstable - [#4028](https://github.com/detekt/detekt/pull/4028)
-   UseDataClass: fix false positive on value classes - [#4016](https://github.com/detekt/detekt/pull/4016)
-   ImplicitUnitReturnType: don't report when expression body is 'Unit' - [#4011](https://github.com/detekt/detekt/pull/4011)
-   Fix false positive with UnusedPrivateMember on parameter of a protected function - [#4007](https://github.com/detekt/detekt/pull/4007)
-   ClassNaming: Don't treat Kotlin syntax ` as part of class name - [#3977](https://github.com/detekt/detekt/pull/3977)
-   IgnoredReturnValue: fix false negative when annotation is on the class - [#3979](https://github.com/detekt/detekt/pull/3979)
-   NoNameShadowing: fix false positive with nested lambda has implicit parameter - [#3991](https://github.com/detekt/detekt/pull/3991)
-   UnusedPrivateMember - added handling of overloaded array get operator - [#3666](https://github.com/detekt/detekt/pull/3666)
-   Publish bundled/Shadow JAR artifact to Maven repos - [#3986](https://github.com/detekt/detekt/pull/3986)
-   EmptyDefaultConstructor false positive with expect and actual classes - [#3970](https://github.com/detekt/detekt/pull/3970)
-   FunctionNaming - Allow factory function names - fix #1639 - [#3973](https://github.com/detekt/detekt/pull/3973)
-   EndOfSentenceFormat - Fix #3893 by only calling super.visit once - [#3904](https://github.com/detekt/detekt/pull/3904)
-   UndocumentedPublicFunction: don't report when nested class is inside not public class [#3962](https://github.com/detekt/detekt/pull/3962)
-   Fail with a meaningful error message for invalid boolean - [#3931](https://github.com/detekt/detekt/pull/3931)
-   UndocumentedPublicProperty and UndocumentedPublicFunction should include objects - [#3940](https://github.com/detekt/detekt/pull/3940)
-   Fix exclusion pattern for InvalidPackageDeclaration - [#3907](https://github.com/detekt/detekt/pull/3907)
-   Allow else when `{...}` in MandatoryBracesIfStatements rule - [#3905](https://github.com/detekt/detekt/pull/3905)
-   Remove unnecessary constant declaration - [#3903](https://github.com/detekt/detekt/pull/3903)
-   Check bindingContext only once in MemberNameEqualsClassName - [#3899](https://github.com/detekt/detekt/pull/3899)
-   LongMethod: add 'ignoreAnnotated' configuration option - [#3892](https://github.com/detekt/detekt/pull/3892)
-   Fix Deprecation rule message - [#3885](https://github.com/detekt/detekt/pull/3885)
-   Improve LongParameterList rule by supporting ignoring annotated parameters - [#3879](https://github.com/detekt/detekt/pull/3879)
-   OptionalUnit: fix false positive when function initializer is Nothing type - [#3876](https://github.com/detekt/detekt/pull/3876)
-   UnnecessaryParentheses: fix false positive for delegated expressions - [#3858](https://github.com/detekt/detekt/pull/3858)
-   Fix UnnecessaryLet false positive in inner lambdas - [#3841](https://github.com/detekt/detekt/pull/3841)
-   Fix false positive for UnusedPrivateMember - Backtick identifiers - [#3828](https://github.com/detekt/detekt/pull/3828)
-   Properly apply test excludes for comments - [#3815](https://github.com/detekt/detekt/pull/3815)
-   Fix generation issues around (deprecated) list properties - [#3813](https://github.com/detekt/detekt/pull/3813)
-   Update the implementation of ClassOrdering to handle false negatives - [#3810](https://github.com/detekt/detekt/pull/3810)
-   [comments] Do not exclude tests globally - [#3801](https://github.com/detekt/detekt/pull/3801)
-   UnnecessaryLet: report when implicit parameter isn't used - [#3794](https://github.com/detekt/detekt/pull/3794)
-   NoNameShadowing: don't report when implicit 'it' parameter isn't used - [#3793](https://github.com/detekt/detekt/pull/3793)
-   Fix ModifierOrder to support value class - [#3719](https://github.com/detekt/detekt/pull/3719)
-   Remove inline value class to stay compatible with Kotlin 1.4 API - [#3871](https://github.com/detekt/detekt/pull/3871)
-   [FunctionNaming] Revert annotations that are ignored by default - [#3948](https://github.com/detekt/detekt/pull/3948)
-   Android: add javac intermediates to classpath - [#3867]((https://github.com/detekt/detekt/pull/3867)
-   Revert "Android: add javac intermediates to classpath (#3867)" - [#3958]((https://github.com/detekt/detekt/pull/3958)
-   Use annotations to configure rules in detekt-rules-exceptions - [#3798](https://github.com/detekt/detekt/pull/3798)
-   Use @Configuration in detekt-rules-style - [#3774](https://github.com/detekt/detekt/pull/3774)
-   Use annotations to configure rules in custom-checks - [#3773](https://github.com/detekt/detekt/pull/3773)
-   Use @Configuration for rules-errorprone - [#3772](https://github.com/detekt/detekt/pull/3772)
-   Use annotation to configure rules in rules-empty - [#3771](https://github.com/detekt/detekt/pull/3771)
-   Use annotation to configure rules in rules-documentation - [#3770](https://github.com/detekt/detekt/pull/3770)
-   Use annotations to configure rules in rules-naming - [#3769](https://github.com/detekt/detekt/pull/3769)
-   Use annotations to configure rules in rules-complexity - [#3768](https://github.com/detekt/detekt/pull/3768)
-   Move formatting rules to @Configuration - [#3847](https://github.com/detekt/detekt/pull/3847)

##### Dependency Updates

-   Bump Kotlin to 1.5.21 - [#3956](https://github.com/detekt/detekt/pull/3956)
-   Revert "Bump Kotlin to v1.5.20" - [#3941](https://github.com/detekt/detekt/pull/3941)
-   Bump Kotlin to v1.5.20 - [#3921](https://github.com/detekt/detekt/pull/3921)
-   Kotlin 1.5.10 - [#3826](https://github.com/detekt/detekt/pull/3826)
-   Update assertj to v3.20.2 - [#3912](https://github.com/detekt/detekt/pull/3912)
-   Update snakeyaml to v1.29 - [#3911](https://github.com/detekt/detekt/pull/3911)
-   Bump byte-buddy from 1.11.2 to 1.11.5 - [#3886](https://github.com/detekt/detekt/pull/3886)
-   Bump byte-buddy from 1.11.1 to 1.11.2 - [#3872](https://github.com/detekt/detekt/pull/3872)
-   Bump byte-buddy from 1.11.0 to 1.11.1 - [#3861](https://github.com/detekt/detekt/pull/3861)
-   Update mockk to 1.12.0 - [#3937](https://github.com/detekt/detekt/pull/3937)

##### Housekeeping & Refactorings

-   Enable UnnecessaryLet rule for detekt code base - [#4024](https://github.com/detekt/detekt/pull/4024)
-   enable PreferToOverPairSyntax rule for detekt code base - [#4023](https://github.com/detekt/detekt/pull/4023)
-   Add IllegalArgumentException and IllegalStateException to ThrowingExceptionsWithoutMessageOrCause - [#4013](https://github.com/detekt/detekt/pull/4013)
-   enable more potential-bugs rules for detekt code base - [#3997](https://github.com/detekt/detekt/pull/3997)
-   enable more exception rules for detekt code base - [#3995](https://github.com/detekt/detekt/pull/3995)
-   Enable UseOrEmpty for detekt code base - [#3999](https://github.com/detekt/detekt/pull/3999)
-   enable those rules from the style rule set that have not violation or obvious fixes - [#3998](https://github.com/detekt/detekt/pull/3998)
-   Enable more rules from naming rule set for detekt code base - [#3996](https://github.com/detekt/detekt/pull/3996)
-   Enable UseEmptyCounterpart for detekt code base - [#4000](https://github.com/detekt/detekt/pull/4000)
-   enable coroutine rules for detekt code base - [#3994](https://github.com/detekt/detekt/pull/3994)
-   Remove "plugin" suffix from version catalog aliases - [#3987](https://github.com/detekt/detekt/pull/3987)
-   Fix ClassCastException in test on java 11 openjdk9 - [#3984](https://github.com/detekt/detekt/pull/3984)
-   Activate IgnoredReturnValue on detekt code base - [#3974](https://github.com/detekt/detekt/pull/3974)
-   Add missing test in FunctionNaming - [#3976](https://github.com/detekt/detekt/pull/3976)
-   Fix trunk compilation - [#3968](https://github.com/detekt/detekt/pull/3968)
-   Reformat internal detekt.yml using multi line lists - [#3936](https://github.com/detekt/detekt/pull/3936)
-   Increase memory available to gradle integration test daemon - [#3938](https://github.com/detekt/detekt/pull/3938)
-   Avoid empty lines when running detekt with type resolution - [#3909](https://github.com/detekt/detekt/pull/3909)
-   Fix java.lang.ClassCastException is reading default yaml config - [#3920](https://github.com/detekt/detekt/pull/3920)
-   Refactor + rename util function inside MandatoryBracesIfStatement rule - [#3908](https://github.com/detekt/detekt/pull/3908)
-   Rename Tests to Spec - [#3906](https://github.com/detekt/detekt/pull/3906)
-   verify that no rule is configured with kdoc tags - [#3870](https://github.com/detekt/detekt/pull/3870)
-   Setup FOSSA - [#3836](https://github.com/detekt/detekt/pull/3836)
-   jvmTarget can't be null - [#3818](https://github.com/detekt/detekt/pull/3818)
-   Add test for ruleset provider configuration - [#3814](https://github.com/detekt/detekt/pull/3814)
-   Merge JaCoCo coverage reports the "right" way - [#3650](https://github.com/detekt/detekt/pull/3650)
-   Update outdated Gradle plugin documentation regarding source files - [#3883](https://github.com/detekt/detekt/pull/3883)
-   Make documentation more precise about how rules are enabled - [#3889](https://github.com/detekt/detekt/pull/3889)
-   Rename MapGetWithNotNullAsserSpec to follow test convention - [#3878](https://github.com/detekt/detekt/pull/3878)
-   Remove custom assertions that check kdoc of rules - [#3859](https://github.com/detekt/detekt/pull/3859)
-   Avoid overlapping outputs - [#3790](https://github.com/detekt/detekt/pull/3790)
-   Revert "Avoid overlapping outputs" - [#3943](https://github.com/detekt/detekt/pull/3943)

See all issues at: [1.18.0](https://github.com/detekt/detekt/milestone/82)

#### 1.17.1 - 2021-05-19

##### Notable Changes

This is a patch release for detekt `1.17.0` including fixes that we considered worth a point release.

Specifically, we're reverting a change on our Gradle Plugin. The original change [#3655](https://github.com/detekt/detekt/pull/3655) resulted in several false positives when using rules with Type Resolution on Java/Kotlin mixed codebases.

Moreover we included a couple of false positive fixes for `NoNameShadowing` and `UnnecessaryLet`

##### Changelog

-   Revert "Noisy gradle (#3655)" - [#3792](https://github.com/detekt/detekt/pull/3792)
-   NoNameShadowing: don't report when implicit 'it' parameter isn't used - [#3793](https://github.com/detekt/detekt/pull/3793)
-   UnnecessaryLet: report when implicit parameter isn't used - [#3794](https://github.com/detekt/detekt/pull/3794)

#### 1.17.0 - 2021-05-15

##### Notable Changes

-   We're introducing our new Project logo :). See [#3726](https://github.com/detekt/detekt/pull/3726)
-   This release allows you to replace your `jcenter()` dependencies with `mavenCentral()` given that our dependency on `kotlinx.html` migrated to Maven Central - See [#3455](https://github.com/detekt/detekt/pull/3455)
-   We now introduced the `src/test/java` and `src/test/kotlin` by default for the plain `detekt` Gradle task. If you use that task, you might notice rule reports in your test sourceset. See [#3649](https://github.com/detekt/detekt/pull/3649)
-   We now default the baseline file to `detekt-baseline.xml` so you don't have to specify it manually. You can revert the previous behavior by setting the baseline to `null` - See [#3619](https://github.com/detekt/detekt/pull/3619) and [#3745](https://github.com/detekt/detekt/pull/3745)
-   We enabled the SARIF output format by default - See [#3543](https://github.com/detekt/detekt/pull/3543)
-   We're introducing annotations to provide metadata to rules, such as `@ActiveByDefault`, `@Configuration` and `@RequiresFullAnalysis` - See [#3637](https://github.com/detekt/detekt/pull/3637) [#3592](https://github.com/detekt/detekt/pull/3592) and [#3579](https://github.com/detekt/detekt/pull/3579)

##### Changelog

-   Fix crash for DontDowncastCollectionTypes on Synthetic types - [#3776](https://github.com/detekt/detekt/pull/3776)
-   We don't need to talk about jcenter anymore at our docs - [#3755](https://github.com/detekt/detekt/pull/3755)
-   Skip publishing for detekt-cli shadowRuntimeElements variant - [#3747](https://github.com/detekt/detekt/pull/3747)
-   Set the org.gradle.dependency.bundling attribute to external - [#3738](https://github.com/detekt/detekt/pull/3738)
-   Support triple quoted strings in default value of config delegate - [#3733](https://github.com/detekt/detekt/pull/3733)
-   Properly populate versions.properties - [#3730](https://github.com/detekt/detekt/pull/3730)
-   We have a logo :) - [#3726](https://github.com/detekt/detekt/pull/3726)
-   [UndocumentedPublicProperty] Allow inline comments for properties in primary constructor as documentation - [#3722](https://github.com/detekt/detekt/pull/3722)
-   MultilineLambdaItParameter: don't report when lambda has no implicit parameter references - [#3696](https://github.com/detekt/detekt/pull/3696)
-   Fix false positives for UnnecessaryFilter - [#3695](https://github.com/detekt/detekt/pull/3695)
-   Add support for transformer function in config property delegate - [#3676](https://github.com/detekt/detekt/pull/3676)
-   Add support for fallback property - [#3675](https://github.com/detekt/detekt/pull/3675)
-   Ignore actual members in UnusedPrivateMember - [#3669](https://github.com/detekt/detekt/pull/3669)
-   NamedArguments rule: fix false positive with trailing lambda - [#3661](https://github.com/detekt/detekt/pull/3661)
-   Add DeprecatedBlockTag rule - [#3660](https://github.com/detekt/detekt/pull/3660)
-   Noisy gradle - [#3655](https://github.com/detekt/detekt/pull/3655)
-   Drop support to Gradle 5 - [#3647](https://github.com/detekt/detekt/pull/3647)
-   Add MayBeConstant as alias for MayBeConst - [#3644](https://github.com/detekt/detekt/pull/3644)
-   [ThrowingExceptionInMain] [ExitOutsideMainfix] fix for KtNamedFunction.isMainFunction() - [#3641](https://github.com/detekt/detekt/pull/3641)
-   Fixing IllegalArgumentException in ForbiddenMethodCall rule for Intersection type parameters - [#3626](https://github.com/detekt/detekt/pull/3626)
-   Replace getJetTypeFqName with fqNameOrNull extension - [#3613](https://github.com/detekt/detekt/pull/3613)
-   New Rule: ObjectLiteralToLambda - [#3599](https://github.com/detekt/detekt/pull/3599)
-   [MemberNameEqualsClassName] Support factory exemption for generic classes - [#3595](https://github.com/detekt/detekt/pull/3595)
-   Refactor Analyzer so that RuleSetProvider.instance is only called once - [#3585](https://github.com/detekt/detekt/pull/3585)
-   SarifOutputReportSpec: Correctly detect Windows root directory on local development machine - [#3584](https://github.com/detekt/detekt/pull/3584)
-   Replace @since KDoc tag with @SinceDetekt - [#3582](https://github.com/detekt/detekt/pull/3582)
-   Simplify code in RedundantSuspendModifier rule - [#3580](https://github.com/detekt/detekt/pull/3580)
-   Revert "Refactor Analyzer so that RuleSetProvider.instance is only called once" - [#3578](https://github.com/detekt/detekt/pull/3578)
-   fix error message -> buildUponDefaultConfig instead of buildOnDefaultConfig - [#3572](https://github.com/detekt/detekt/pull/3572)
-   UnnecessaryApply: fix false positive when lambda has multiple member references - [#3564](https://github.com/detekt/detekt/pull/3564)
-   Switch SARIF report off jackson - [#3557](https://github.com/detekt/detekt/pull/3557)
-   Fix rules not appearing in the sarif output - [#3556](https://github.com/detekt/detekt/pull/3556)
-   Refactor Analyzer so that RuleSetProvider.instance is only called once - [#3555](https://github.com/detekt/detekt/pull/3555)
-   New Rule: DoubleMutabilityForCollection - [#3553](https://github.com/detekt/detekt/pull/3553)
-   Adds a ForbiddenSingleExpressionSyntax rule - [#3550](https://github.com/detekt/detekt/pull/3550)

##### Dependency Updates

-   Update to Gradle 7.0.1 - [#3760](https://github.com/detekt/detekt/pull/3760)
-   Update Shadow plugin to 7.0.0 - [#3759](https://github.com/detekt/detekt/pull/3759)
-   Upgrade to AGP 4.2.0 - [#3744](https://github.com/detekt/detekt/pull/3744)
-   JaCoCo 0.8.7 - [#3739](https://github.com/detekt/detekt/pull/3739)
-   Upgrade to GitHub-native Dependabot - [#3716](https://github.com/detekt/detekt/pull/3716)
-   Upgrade to Gradle 7 - [#3689](https://github.com/detekt/detekt/pull/3689)
-   Bump com.gradle.plugin-publish from 0.13.0 to 0.14.0 - [#3654](https://github.com/detekt/detekt/pull/3654)
-   Bump kotlin-reflect from 1.4.0 to 1.4.32 - [#3627](https://github.com/detekt/detekt/pull/3627)
-   Upgrade to ktlint 0.41.0 - [#3624](https://github.com/detekt/detekt/pull/3624)
-   Update to Kotlin 1.4.32 - [#3606](https://github.com/detekt/detekt/pull/3606)
-   Bump AGP from 4.1.2 to 4.1.3 - [#3589](https://github.com/detekt/detekt/pull/3589)
-   Bump mockk from 1.10.6 to 1.11.0 - [#3588](https://github.com/detekt/detekt/pull/3588)

##### Housekeeping & Refactorings

-   Fix document - [#3765](https://github.com/detekt/detekt/pull/3765)
-   Fix kdoc link on blog navigation - [#3761](https://github.com/detekt/detekt/pull/3761)
-   Upload any heap dumps produced during CI build - [#3758](https://github.com/detekt/detekt/pull/3758)
-   Always run warningsAsErrors on CI - [#3754](https://github.com/detekt/detekt/pull/3754)
-   Clean ci - [#3753](https://github.com/detekt/detekt/pull/3753)
-   Revert "Set the org.gradle.dependency.bundling attribute to external" - [#3750](https://github.com/detekt/detekt/pull/3750)
-   Enable Gradle's type-safe project accessors - [#3742](https://github.com/detekt/detekt/pull/3742)
-   Enable Gradle's version catalogs - [#3741](https://github.com/detekt/detekt/pull/3741)
-   Ignore gradle plugin in codecov - [#3740](https://github.com/detekt/detekt/pull/3740)
-   Update config file due to invalid argument - [#3735](https://github.com/detekt/detekt/pull/3735)
-   Skip Multiplatform iOS tests if XCode is not configured - [#3734](https://github.com/detekt/detekt/pull/3734)
-   Specify Java language level in module plugin - [#3732](https://github.com/detekt/detekt/pull/3732)
-   Don't run unnecesary tasks - [#3725](https://github.com/detekt/detekt/pull/3725)
-   Remove --stacktrace now that we have scan - [#3724](https://github.com/detekt/detekt/pull/3724)
-   Drop JCenter usage from detekt's own build - [#3711](https://github.com/detekt/detekt/pull/3711)
-   Publish build scans for all CI builds - [#3710](https://github.com/detekt/detekt/pull/3710)
-   Remove deprecated kotlin-dsl Gradle config option - [#3709](https://github.com/detekt/detekt/pull/3709)
-   Update to setup-java@v2 - [#3704](https://github.com/detekt/detekt/pull/3704)
-   (Try to) improve CI build reliability - [#3703](https://github.com/detekt/detekt/pull/3703)
-   Simplify UpdateVersionInFileTask - [#3693](https://github.com/detekt/detekt/pull/3693)
-   Fix compilation issue in `:detekt-rules-style:compileTestKotlin` - [#3691](https://github.com/detekt/detekt/pull/3691)
-   Fix detekt failure in CI - [#3674](https://github.com/detekt/detekt/pull/3674)
-   Refactor UnusedPrivateMemberSpec - [#3667](https://github.com/detekt/detekt/pull/3667)
-   Warnings as errors - [#3646](https://github.com/detekt/detekt/pull/3646)
-   Skip ios tests if no ci - [#3635](https://github.com/detekt/detekt/pull/3635)
-   Fix tests - [#3634](https://github.com/detekt/detekt/pull/3634)
-   Include detekt-rules on CLI runtime classpath - [#3625](https://github.com/detekt/detekt/pull/3625)
-   Improve tests from :detekt-gradle-plugin - [#3623](https://github.com/detekt/detekt/pull/3623)
-   Improve generator test coverage - [#3622](https://github.com/detekt/detekt/pull/3622)
-   Improve tests - [#3618](https://github.com/detekt/detekt/pull/3618)
-   Apply more formatting rules to our code - [#3615](https://github.com/detekt/detekt/pull/3615)
-   Add negative test case for `requiresFullAnalysis` - [#3614](https://github.com/detekt/detekt/pull/3614)
-   Simplify Gradle config - [#3612](https://github.com/detekt/detekt/pull/3612)
-   Decouple Gradle projects - [#3611](https://github.com/detekt/detekt/pull/3611)
-   Add --stacktrace to help triage CI flakiness - [#3604](https://github.com/detekt/detekt/pull/3604)
-   Fix CI failure for deploy-snapshot - [#3598](https://github.com/detekt/detekt/pull/3598)
-   Improve Deprecation and Documentation for allRules - [#3596](https://github.com/detekt/detekt/pull/3596)
-   Update files to support `main` branch in order to remove oppressive language - [#3586](https://github.com/detekt/detekt/pull/3586)
-   Format test code for RedundantSuspendModifierSpec - [#3581](https://github.com/detekt/detekt/pull/3581)
-   Gradle tweaks - [#3575](https://github.com/detekt/detekt/pull/3575)
-   Support Gradle config cache in detekt's build - [#3574](https://github.com/detekt/detekt/pull/3574)
-   Show information from @active in the website - [#3569](https://github.com/detekt/detekt/pull/3569)
-   Update rule doc for SwallowedException config - [#3547](https://github.com/detekt/detekt/pull/3547)
-   Markdown: Reintroduce double-backticks for inline code rendering - [#3545](https://github.com/detekt/detekt/pull/3545)

See all issues at: [1.17.0](https://github.com/detekt/detekt/milestone/81)

#### 1.16.0 - 2021-03-10

##### Changelog

-   Bump jcommander from 1.78 to 1.81 - [#3530](https://github.com/detekt/detekt/pull/3530)
-   Swallow exception fixes - [#3525](https://github.com/detekt/detekt/pull/3525)
-   Merge SARIF reports - [#3522](https://github.com/detekt/detekt/pull/3522)
-   Revert Update Kotlin to version 1.4.31 - [#3521](https://github.com/detekt/detekt/pull/3521)
-   Fix not being able to override `insertFinalNewLine` - [#3515](https://github.com/detekt/detekt/pull/3515)
-   Allow opt-out configuring detekt android and multiplatform - [#3511](https://github.com/detekt/detekt/pull/3511)
-   Gradle Plugin tests should access also Maven Local - [#3510](https://github.com/detekt/detekt/pull/3510)
-   Update Kotlin to version 1.4.31 - [#3509](https://github.com/detekt/detekt/pull/3509)
-   Fix SARIF validation failure - [#3507](https://github.com/detekt/detekt/pull/3507)
-   Remove off importing android util - [#3506](https://github.com/detekt/detekt/pull/3506)
-   Adding support for full method signatures in ForbiddenMethodCall - [#3505](https://github.com/detekt/detekt/pull/3505)
-   Add UnusedUnaryOperator rule - [#3499](https://github.com/detekt/detekt/pull/3499)
-   New rule: disallow to cast to nullable type - [#3497](https://github.com/detekt/detekt/pull/3497)
-   Merge XML report output - [#3491](https://github.com/detekt/detekt/pull/3491)
-   Allow using regular expressions when defining license header templates - [#3486](https://github.com/detekt/detekt/pull/3486)
-   Add UnreachableCatchBlock rule - [#3478](https://github.com/detekt/detekt/pull/3478)
-   Add NoNameShadowing rule - [#3477](https://github.com/detekt/detekt/pull/3477)
-   Fix false negative "UselessCallOnNotNull" with `list.isNullOrEmpty()` - [#3475](https://github.com/detekt/detekt/pull/3475)
-   Add UseOrEmpty rule - [#3470](https://github.com/detekt/detekt/pull/3470)
-   Add UseIsNullOrEmpty rule - [#3469](https://github.com/detekt/detekt/pull/3469)
-   Add support to Kotlin Multiplatform Projects - [#3453](https://github.com/detekt/detekt/pull/3453)
-   Fix false positives for MultilineLambdaItParameter.kt - [#3451](https://github.com/detekt/detekt/pull/3451)
-   Dont generate baseline if empty - [#3450](https://github.com/detekt/detekt/pull/3450)
-   Silence IndexOutOfBoundsException in getLineAndColumnInPsiFile() - [#3446](https://github.com/detekt/detekt/pull/3446)
-   Add new ObjectExtendsThrowable rule - [#3443](https://github.com/detekt/detekt/pull/3443)
-   Add allRules and deprecate failFast in gradle tasks - [#3431](https://github.com/detekt/detekt/pull/3431)
-   Add two missing ktlint rules - [#3430](https://github.com/detekt/detekt/pull/3430)
-   Don't fail if baseline doesn't exist in PlainDetekt - [#3429](https://github.com/detekt/detekt/pull/3429)
-   Fix False Positive on `UnnecessarySafeCall` - [#3419](https://github.com/detekt/detekt/pull/3419)
-   Fix false positive for `UnusedPrivateMember` with expect on objects - [#3417](https://github.com/detekt/detekt/pull/3417)
-   Fix code samples for `UnnecessarySafeCall` - [#3416](https://github.com/detekt/detekt/pull/3416)
-   New rule: DontDowncastCollectionTypes - [#3413](https://github.com/detekt/detekt/pull/3413)
-   Fix documentation in UnnecessarySafeCall - [#3412](https://github.com/detekt/detekt/pull/3412)
-   Bump gradle from 4.1.1 to 4.1.2 - [#3405](https://github.com/detekt/detekt/pull/3405)
-   Introduce --max-issues flag for cli - #2267 - [#3391](https://github.com/detekt/detekt/pull/3391)
-   Ignore actual functions in FunctionOnlyReturningConstant (#3388) - [#3390](https://github.com/detekt/detekt/pull/3390)
-   Fix hyperlink for elements of the type 'KtFile' - [#3386](https://github.com/detekt/detekt/pull/3386)
-   Update gradle doc to show table of contents correctly - [#3383](https://github.com/detekt/detekt/pull/3383)
-   Update cli doc to show table of contents correctly - [#3382](https://github.com/detekt/detekt/pull/3382)
-   Fix EmptyConfig making all rules active in production - [#3380](https://github.com/detekt/detekt/pull/3380)
-   Empty custom config enables rules disabled by default - [#3379](https://github.com/detekt/detekt/issues/3379)
-   Add `setup-detekt` action to README - [#3373](https://github.com/detekt/detekt/pull/3373)
-   GlobalClassLoaderCache: move call to getFiles out of synchronized block - [#3370](https://github.com/detekt/detekt/pull/3370)
-   Check for === instead of == - [#3363](https://github.com/detekt/detekt/issues/3363)
-   Filter existing files in classpath - [#3361](https://github.com/detekt/detekt/pull/3361)
-   Suppress RedundantVisibilityModifierRule if explicit API mode enabled - [#3358](https://github.com/detekt/detekt/pull/3358)
-   Add the final new line in the baseline again - [#3351](https://github.com/detekt/detekt/pull/3351)
-   [Security] Bump nokogiri from 1.10.10 to 1.11.1 in /docs - [#3348](https://github.com/detekt/detekt/pull/3348)
-   Remove trailing newline after ending IndentingXMLStreamWriter - [#3347](https://github.com/detekt/detekt/pull/3347)
-   [Security] Bump nokogiri from 1.10.10 to 1.11.0 in /docs - [#3343](https://github.com/detekt/detekt/pull/3343)
-   Add UnnecessaryFilter rule - [#3341](https://github.com/detekt/detekt/pull/3341)
-   Reorganize docs for the configuration file - [#3337](https://github.com/detekt/detekt/pull/3337)
-   Add new rule SleepInsteadOfDelay - [#3335](https://github.com/detekt/detekt/pull/3335)
-   Update Android Gradle Plugin to 4.1.1 - [#3328](https://github.com/detekt/detekt/pull/3328)
-   Support relative output paths - [#3319](https://github.com/detekt/detekt/pull/3319)
-   Fix `runLastOnRoot` being empty in KtLintMultiRule - [#3318](https://github.com/detekt/detekt/pull/3318)
-   Ensure binary-compatibility with previous versrions - [#3315](https://github.com/detekt/detekt/issues/3315)
-   Fix reports not propagated to detekt task with type resolution - [#3313](https://github.com/detekt/detekt/pull/3313)
-   Support configurable severity per ruleset/rule in XML and Sarif output - [#3310](https://github.com/detekt/detekt/pull/3310)
-   Configure default excludes for InvalidPackageDeclaration - [#3305](https://github.com/detekt/detekt/pull/3305)
-   Remove exceptions of Library rules - [#3304](https://github.com/detekt/detekt/pull/3304)
-   Move the questions to discussions - [#3300](https://github.com/detekt/detekt/pull/3300)
-   Magic number extension functions - [#3299](https://github.com/detekt/detekt/pull/3299)
-   NamedArguments: fix false positive with varargs - [#3294](https://github.com/detekt/detekt/pull/3294)
-   NamedArguments rule: false positive with varargs - [#3291](https://github.com/detekt/detekt/issues/3291)
-   NamedArguments with java code false positive - [#3289](https://github.com/detekt/detekt/issues/3289)
-   Upgrade ktlint to 0.40.0 - [#3281](https://github.com/detekt/detekt/pull/3281)
-   False positive "Unconditional loop jump" - [#3280](https://github.com/detekt/detekt/issues/3280)
-   ForbiddenComments don't report TODO: in KDoc - [#3273](https://github.com/detekt/detekt/issues/3273)
-   Add MultilineLambdaItParameter rule - [#3259](https://github.com/detekt/detekt/pull/3259)
-   Update Kotlin to 1.4.21 - [#3254](https://github.com/detekt/detekt/pull/3254)
-   Introduce --all-rules flag - [#3253](https://github.com/detekt/detekt/pull/3253)
-   Enable more rules by default - [#3229](https://github.com/detekt/detekt/pull/3229)
-   Running multiple Detekt tasks concurrently may cause deadlock - [#3047](https://github.com/detekt/detekt/issues/3047)
-   detektMain is noisy "Ignoring a file detekt cannot handle" - [#3019](https://github.com/detekt/detekt/issues/3019)
-   Configure default excludes for InvalidPackageDeclaration - [#2539](https://github.com/detekt/detekt/issues/2539)
-   Hyperlink to error inside Android studio - [#2340](https://github.com/detekt/detekt/issues/2340)
-   Align cli flags and defaults with other analysis tools - [#2267](https://github.com/detekt/detekt/issues/2267)

##### Housekeeping & Refactorings

-   SwallowedException: Slightly improve the documentation - [#3527](https://github.com/detekt/detekt/pull/3527)
-   Fix Markdown rendering of multiple default values - [#3526](https://github.com/detekt/detekt/pull/3526)
-   Move gradle testkit test back to test/ - [#3504](https://github.com/detekt/detekt/pull/3504)
-   Add documentation on suppressing formatting rules - [#3503](https://github.com/detekt/detekt/pull/3503)
-   Change DetektMultiplatform from unit test to gradle testkit integrati… - [#3500](https://github.com/detekt/detekt/pull/3500)
-   Bump com.gradle.plugin-publish from 0.12.0 to 0.13.0 - [#3494](https://github.com/detekt/detekt/pull/3494)
-   Refactor Gradle integration tests - [#3489](https://github.com/detekt/detekt/pull/3489)
-   Refactor gradle integration test - [#3487](https://github.com/detekt/detekt/pull/3487)
-   Prepare detekt 1.16.0-RC2 - [#3485](https://github.com/detekt/detekt/pull/3485)
-   Bump mockk from 1.10.5 to 1.10.6 - [#3473](https://github.com/detekt/detekt/pull/3473)
-   Upgrade to Gradle 6.8.2 - [#3468](https://github.com/detekt/detekt/pull/3468)
-   Correct `maxIssues` documentation - [#3456](https://github.com/detekt/detekt/pull/3456)
-   Bump junit-platform-launcher from 1.7.0 to 1.7.1 - [#3454](https://github.com/detekt/detekt/pull/3454)
-   Don't use deprecated functions - [#3452](https://github.com/detekt/detekt/pull/3452)
-   Bump github-pages from 210 to 211 in /docs - [#3434](https://github.com/detekt/detekt/pull/3434)
-   Add documentation for SARIF, severity and relative path - [#3433](https://github.com/detekt/detekt/pull/3433)
-   Refactor uploading SARIF to report without overriding the previous step - [#3432](https://github.com/detekt/detekt/pull/3432)
-   Fix `githubRelease` skipping assets - [#3427](https://github.com/detekt/detekt/pull/3427)
-   Prompt bug reporters to attach gradle scan - [#3422](https://github.com/detekt/detekt/pull/3422)
-   Fix invalid link in detekt html report - [#3421](https://github.com/detekt/detekt/pull/3421)
-   Prepare 1.16.0-rc1 release - [#3411](https://github.com/detekt/detekt/pull/3411)
-   Add full qualified name in documentation - [#3410](https://github.com/detekt/detekt/pull/3410)
-   Bump kotlinx-coroutines-core from 1.3.8 to 1.4.1 - [#3407](https://github.com/detekt/detekt/pull/3407)
-   Fix deploy website on master - [#3406](https://github.com/detekt/detekt/pull/3406)
-   Bump mockk from 1.10.4 to 1.10.5 - [#3404](https://github.com/detekt/detekt/pull/3404)
-   Bump assertj-core from 3.18.1 to 3.19.0 - [#3403](https://github.com/detekt/detekt/pull/3403)
-   Bump github-pages from 209 to 210 in /docs - [#3401](https://github.com/detekt/detekt/pull/3401)
-   Update dangling URLs pointing to the old website - [#3400](https://github.com/detekt/detekt/pull/3400)
-   Auto generate CLI options in docs - [#3399](https://github.com/detekt/detekt/pull/3399)
-   Update documentations on snapshots - [#3393](https://github.com/detekt/detekt/pull/3393)
-   Fix maven publish - [#3392](https://github.com/detekt/detekt/pull/3392)
-   Fix build script to avoid jvm plugin applied - [#3389](https://github.com/detekt/detekt/pull/3389)
-   Disable parallel test discovery; we already use Grade workers for max parallelism - [#3387](https://github.com/detekt/detekt/pull/3387)
-   Use more fluent assertions - [#3381](https://github.com/detekt/detekt/pull/3381)
-   Refactor orders of repositories - [#3376](https://github.com/detekt/detekt/pull/3376)
-   Add a test for UndocumentedPublicClass and fun interfaces - [#3374](https://github.com/detekt/detekt/pull/3374)
-   Refactor build.gradle.kts in detekt-gradle-plugin - [#3371](https://github.com/detekt/detekt/pull/3371)
-   Gradle to 6.8 - [#3362](https://github.com/detekt/detekt/pull/3362)
-   Integrate SARIF report with Github code scanning - [#3359](https://github.com/detekt/detekt/pull/3359)
-   Refactor integration test for detekt-gradle-plugin - [#3356](https://github.com/detekt/detekt/pull/3356)
-   Improve gradle plugin - [#3354](https://github.com/detekt/detekt/pull/3354)
-   Remove checkNotNull - [#3352](https://github.com/detekt/detekt/pull/3352)
-   Generate API validation for `detekt-psi-utils` - [#3338](https://github.com/detekt/detekt/pull/3338)
-   recover binary compatibility with 1.15.0 - [#3336](https://github.com/detekt/detekt/pull/3336)
-   Refactor tests in detekt-gradle-plugin - [#3333](https://github.com/detekt/detekt/pull/3333)
-   Fix failing website deployment on master - [#3332](https://github.com/detekt/detekt/pull/3332)
-   The output of updateVersion should not depend on the OS that executes it - [#3330](https://github.com/detekt/detekt/pull/3330)
-   Reduce visibility - [#3326](https://github.com/detekt/detekt/pull/3326)
-   Refactor XmlOutputFormatSpec - [#3325](https://github.com/detekt/detekt/pull/3325)
-   Simplify our buildSrc - [#3322](https://github.com/detekt/detekt/pull/3322)
-   Apply binary compatibility plugin to detekt - [#3320](https://github.com/detekt/detekt/pull/3320)
-   Add KDoc for convoluted PathFilters.isIgnored - [#3312](https://github.com/detekt/detekt/pull/3312)
-   Don't mix kotlin 1.3 and 1.4 - [#3309](https://github.com/detekt/detekt/pull/3309)
-   Allow to overwrite in the task moveJarForIntegrationTest - [#3308](https://github.com/detekt/detekt/pull/3308)
-   Remove unnecessary .trimIndent() - housekeeping - [#3307](https://github.com/detekt/detekt/pull/3307)
-   Fix typo - [#3301](https://github.com/detekt/detekt/pull/3301)
-   General housekeeping - [#3298](https://github.com/detekt/detekt/pull/3298)
-   Inline UnconditionalJumpStatementInLoop case files - [#3296](https://github.com/detekt/detekt/pull/3296)

See all issues at: [1.16.0](https://github.com/detekt/detekt/milestone/80)

#### 1.15.0 - 2020-12-18

##### Notable Changes

detekt 1.15.0 bundles Kotlin 1.4.10.  
You may experience some known issues when your project already uses 1.4.20

-   [#3248](https://github.com/detekt/detekt/issues/3248) and [#3282](https://github.com/detekt/detekt/issues/3282).

In addition to many rule improvements, there are also new ones:

-   RedundantHigherOrderMapUsage
-   UseIfEmptyOrIfBlank

We added documentation on how to configure [type resolution](/docs/gettingstarted/type-resolution).  
Only the rules marked with `Requires Type Resolution` (on the website documentation or `@requiresFullAnalysis` in code) are executed (see [here for example](/docs/rules/style#forbiddenmethodcall)).

detekt now supports [SARIF](https://sarifweb.azurewebsites.net/) as an output format. In the future you will be able to
upload this format to GitHub and see detekt issues right in your pull requests.

##### Migration

We removed implementations of the `Config` interface from the public api.  
It was first deprecated and then moved to `internal` package earlier this year.  
Rule authors can use `TestConfig(Map)` or `yamlConfig(String)` from `detekt-test` to test their rules.

-   Move internal config api to core module - [#3163](https://github.com/detekt/detekt/pull/3163)

##### Changelog

-   NamedArguments: fix false positive with java method call - [#3290](https://github.com/detekt/detekt/pull/3290)
-   Prepare 1.15.0 rc2 - [#3286](https://github.com/detekt/detekt/pull/3286)
-   UnconditionalJumpStatementInLoop: don't report a return after a conditional jump - [#3285](https://github.com/detekt/detekt/pull/3285)
-   Add MuseDev to the list of integrations - [#3284](https://github.com/detekt/detekt/pull/3284)
-   Fix ForbiddenComment rule not checking for KDoc - [#3275](https://github.com/detekt/detekt/pull/3275)
-   ForbiddenComments don't report TODO: in KDoc - [#3273](https://github.com/detekt/detekt/issues/3273)
-   Add IntelliJ platform plugin template integration to readme - [#3270](https://github.com/detekt/detekt/pull/3270)
-   Bundle new sarif output format by default - [#3268](https://github.com/detekt/detekt/pull/3268)
-   Add a test for UnusedImports with annotations used as attributes #3246 - [#3255](https://github.com/detekt/detekt/pull/3255)
-   Add documentation page on type resolution - [#3225](https://github.com/detekt/detekt/pull/3225)
-   ThrowsCount rule: fix false positive with nested function - [#3223](https://github.com/detekt/detekt/pull/3223)
-   False positive in ThrowsCount rule - [#3222](https://github.com/detekt/detekt/issues/3222)
-   Refactor UnsafeCallOnNullableType rule - [#3221](https://github.com/detekt/detekt/pull/3221)
-   Fix false negatives in UnreachableCode rule - [#3220](https://github.com/detekt/detekt/pull/3220)
-   False negatives in UnreachableCode rule - [#3219](https://github.com/detekt/detekt/issues/3219)
-   Refactor RedundantElseInWhen to use compiler warning - [#3214](https://github.com/detekt/detekt/pull/3214)
-   NullableToStringCall: fix false negative with safe qualified expression - [#3213](https://github.com/detekt/detekt/pull/3213)
-   False negative in NullableToStringCall - [#3211](https://github.com/detekt/detekt/issues/3211)
-   NullableToStringCall: fix false negatives with qualified expression - [#3198](https://github.com/detekt/detekt/pull/3198)
-   False negatives in NullableToStringCall - [#3196](https://github.com/detekt/detekt/issues/3196)
-   Check for presence of null case in MissingWhenCase rule - [#3194](https://github.com/detekt/detekt/pull/3194)
-   Throw error instead of logging as error in analysis phase - [#3193](https://github.com/detekt/detekt/pull/3193)
-   Make kotlinc adapted rule comments internal - [#3190](https://github.com/detekt/detekt/issues/3190)
-   MissingWhenCase false negative with nulls - [#3189](https://github.com/detekt/detekt/issues/3189)
-   Check for static imports in unused imports rule - [#3188](https://github.com/detekt/detekt/pull/3188)
-   Add allowElseExpression configuration for MissingWhenCase rule - [#3187](https://github.com/detekt/detekt/pull/3187)
-   Add UseIfEmptyOrIfBlank rule - [#3186](https://github.com/detekt/detekt/pull/3186)
-   Fix detektBaseline task filtering .java files - [#3185](https://github.com/detekt/detekt/pull/3185)
-   Internal exception should fail the gradle task - [#3183](https://github.com/detekt/detekt/issues/3183)
-   Add RedundantHigherOrderMapUsage rule - [#3182](https://github.com/detekt/detekt/pull/3182)
-   Fix false negative in IgnoredReturnValue - [#3179](https://github.com/detekt/detekt/pull/3179)
-   Fix false positive when `to` is used to create a pair within a function - [#3178](https://github.com/detekt/detekt/pull/3178)
-   False Positive PreferToOverPairSyntax - [#3177](https://github.com/detekt/detekt/issues/3177)
-   Suppress RedundantVisibilityModifierRule if explicit API mode enabled - [#3175](https://github.com/detekt/detekt/pull/3175)
-   Hardcode default values - [#3171](https://github.com/detekt/detekt/pull/3171)
-   False negative in IgnoredReturnValue - [#3170](https://github.com/detekt/detekt/issues/3170)
-   Fix false positive in IgnoredReturnValue - [#3169](https://github.com/detekt/detekt/pull/3169)
-   Duplicate deprecated KtLint methods - [#3168](https://github.com/detekt/detekt/pull/3168)
-   Introduce NamedArguments rule - [#3167](https://github.com/detekt/detekt/pull/3167)
-   Add JSON Schema documentation - [#3166](https://github.com/detekt/detekt/pull/3166)
-   Fix MaxLineLengthSuppressed ignoring @Suppress annotation on class - [#3164](https://github.com/detekt/detekt/pull/3164)
-   Use the properties syntax in Gradle docs - #3158 - [#3161](https://github.com/detekt/detekt/pull/3161)
-   Fix rule LibraryCodeMustSpecifyReturnType - [#3155](https://github.com/detekt/detekt/pull/3155)
-   Update README to mention config auto-complete - [#3143](https://github.com/detekt/detekt/issues/3143)
-   @Suppress("MaxLineLength") not working for simple block comment inside class - [#3136](https://github.com/detekt/detekt/issues/3136)
-   Support sarif as a report type - #3045 - [#3132](https://github.com/detekt/detekt/pull/3132)
-   UnusedImports false positive for enums in annotation attributes (with type resolution) - [#3131](https://github.com/detekt/detekt/issues/3131)
-   Unable to generate detektMain baseline for UnsafeCallOnNullableType violations in Android (mixed Kotlin + Java) modules - [#3130](https://github.com/detekt/detekt/issues/3130)
-   Suppress RedundantVisibilityModifierRule if explicit API mode enabled - [#3125](https://github.com/detekt/detekt/issues/3125)
-   SARIF export support - [#3045](https://github.com/detekt/detekt/issues/3045)
-   IgnoredReturnValue false positives - [#3043](https://github.com/detekt/detekt/issues/3043)
-   Offset calculation in KtLint deprecated/made private - [#3021](https://github.com/detekt/detekt/issues/3021)
-   Map `{ it }` must return an error - [#2975](https://github.com/detekt/detekt/issues/2975)
-   Upload detekt-formatting plugin to Github releases next to precompiled cli binary - [#2927](https://github.com/detekt/detekt/issues/2927)
-   Add a rule to flag places where `ifBlank` and `ifEmpty` can be used - [#2840](https://github.com/detekt/detekt/issues/2840)
-   Remove hardcoded default values from rules - [#2597](https://github.com/detekt/detekt/issues/2597)
-   Doc: type and symbol solving - [#2259](https://github.com/detekt/detekt/issues/2259)
-   Suggestion: LongParameterList rule but on method call if named argument is not used - [#1007](https://github.com/detekt/detekt/issues/1007)

##### Housekeeping & Refactorings

-   Standardize "active" constant - [#3292](https://github.com/detekt/detekt/pull/3292)
-   Update Spek to v2.0.15 - [#3287](https://github.com/detekt/detekt/pull/3287)
-   Reformat code indentation in ReturnFromFinallySpec.kt - [#3278](https://github.com/detekt/detekt/pull/3278)
-   Inline ReturnFromFinally report message text - [#3277](https://github.com/detekt/detekt/pull/3277)
-   Simplify ReturnFromFinally check for finally expressions - [#3276](https://github.com/detekt/detekt/pull/3276)
-   CI with Java 15 - [#3262](https://github.com/detekt/detekt/pull/3262)
-   Enabled publishing of sha256 and sha512 signatures - [#3249](https://github.com/detekt/detekt/pull/3249)
-   Remove default config entries in detekt.yml - [#3239](https://github.com/detekt/detekt/pull/3239)
-   Fix grammar in configuration guide - [#3238](https://github.com/detekt/detekt/pull/3238)
-   Exclude detekt:LargeClass rule in test sources - [#3237](https://github.com/detekt/detekt/pull/3237)
-   Release 1.15.0 rc1 - [#3236](https://github.com/detekt/detekt/pull/3236)
-   Remove unused format function in RuleExtensions - [#3234](https://github.com/detekt/detekt/pull/3234)
-   Update spek to v2.0.14 - [#3231](https://github.com/detekt/detekt/pull/3231)
-   Remove already activated rules from detekt.yml - [#3230](https://github.com/detekt/detekt/pull/3230)
-   Fix broken website redirects - [#3227](https://github.com/detekt/detekt/pull/3227)
-   Remove unused resources from the website - [#3226](https://github.com/detekt/detekt/pull/3226)
-   Simplify EqualsOnSignatureLine rule - [#3224](https://github.com/detekt/detekt/pull/3224)
-   Remove unnecessary suppression in main - [#3217](https://github.com/detekt/detekt/pull/3217)
-   Simplify MissingWhenCase by removing an unnecessary alternative path - [#3216](https://github.com/detekt/detekt/pull/3216)
-   Refactor HasPlatformType rule - [#3210](https://github.com/detekt/detekt/pull/3210)
-   Remove Suppress annotation from ArrayPrimitive - [#3209](https://github.com/detekt/detekt/pull/3209)
-   Refactor UselessCallOnNotNull rule - [#3208](https://github.com/detekt/detekt/pull/3208)
-   Refactor MissingWhenCase - [#3207](https://github.com/detekt/detekt/pull/3207)
-   Refactor NullableToStringCall - [#3206](https://github.com/detekt/detekt/pull/3206)
-   Refactor RedundantElseInWhen - [#3205](https://github.com/detekt/detekt/pull/3205)
-   Refactor PreferToOverPairSyntax - [#3204](https://github.com/detekt/detekt/pull/3204)
-   Remove Suppress annotation from MagicNumber - [#3203](https://github.com/detekt/detekt/pull/3203)
-   Remove Suppress annotation from UnusedImports - [#3202](https://github.com/detekt/detekt/pull/3202)
-   Refactor FunctionNaming rule - [#3201](https://github.com/detekt/detekt/pull/3201)
-   Setup the website publishing pipeline - [#3199](https://github.com/detekt/detekt/pull/3199)
-   Improve code coverage for DefaultCliInvoker testing happy and error path - [#3195](https://github.com/detekt/detekt/pull/3195)
-   Make kotlinc adapted rule comments internal - [#3192](https://github.com/detekt/detekt/pull/3192)
-   Improve PreferToOverPairSyntax - [#3181](https://github.com/detekt/detekt/pull/3181)
-   Simplify PreferToOverPairSyntax check - [#3180](https://github.com/detekt/detekt/pull/3180)
-   Improves in IgnoredReturnValue - [#3174](https://github.com/detekt/detekt/pull/3174)
-   Move KtFileContent to FileParsingRule - [#3173](https://github.com/detekt/detekt/pull/3173)
-   Don't use deprecated onStart - [#3172](https://github.com/detekt/detekt/pull/3172)

See all issues at: [1.15.0](https://github.com/detekt/detekt/milestone/79)

#### 1.14.2 - 2020-10-20

##### Changelog

-   Do not report vararg arguments which are just passed to a vararg parameter - [#3157](https://github.com/detekt/detekt/pull/3157)
-   Simplify documentation - [#3156](https://github.com/detekt/detekt/pull/3156)
-   Respect inspection aliases ClassName, PackageDirectoryMismatch, RedundantVisibilityModifier - [#3153](https://github.com/detekt/detekt/pull/3153)
-   Change spek and assertj to compile only dependencies for detekt-test consumers - [#3152](https://github.com/detekt/detekt/pull/3152)
-   False positive performance SpreadOperator in case of pass-through vararg - [#3145](https://github.com/detekt/detekt/issues/3145)
-   ClassOrdering rule reports a list of errors - [#3142](https://github.com/detekt/detekt/pull/3142)
-   ClassOrdering only reports first misorder, not all misorders - [#3141](https://github.com/detekt/detekt/issues/3141)
-   UnusedPrivateMember: fix false positive with getValue/setValue operator functions - [#3139](https://github.com/detekt/detekt/pull/3139)
-   ClassOrdering reports a message describing the misorder - [#3138](https://github.com/detekt/detekt/pull/3138)
-   False positive UnusedPrivateMember for getValue, setValue operator functions - [#3128](https://github.com/detekt/detekt/issues/3128)
-   Add aliases to class and package Naming rules - [#3124](https://github.com/detekt/detekt/issues/3124)
-   Artifact detekt-test forces dependency constraining - [#3082](https://github.com/detekt/detekt/issues/3082)

##### Housekeeping & Refactorings

-   Fix two eager Regex creations in rules which may confuse users if this rules are inactive but evaluated - [#3154](https://github.com/detekt/detekt/pull/3154)
-   Update Gradle to 6.7 - [#3148](https://github.com/detekt/detekt/pull/3148)
-   Bump github-pages from 208 to 209 in /docs - [#3135](https://github.com/detekt/detekt/pull/3135)

See all issues at: [1.14.2](https://github.com/detekt/detekt/milestone/78)

#### 1.14.1 - 2020-09-30

##### Changelog

-   Write recipe to compare user and default config - #3065 - [#3114](https://github.com/detekt/detekt/pull/3114)
-   Improve description of AbsentOrWrongFileLicense rule - [#3109](https://github.com/detekt/detekt/pull/3109)
-   Report Unit returned by method implementations in interfaces - [#3108](https://github.com/detekt/detekt/pull/3108)
-   Change OptionalUnit to report Unit returned by method implementations in interfaces - [#3099](https://github.com/detekt/detekt/issues/3099)
-   1.13.0 changes to detektGenerateConfig - [#3065](https://github.com/detekt/detekt/issues/3065)
-   detekt 1.12.0 and org.springframework.boot 2.2.6.RELEASE clash - [#3058](https://github.com/detekt/detekt/issues/3058)

##### Housekeeping & Refactorings

-   Update mockk to 1.10.2 - [#3113](https://github.com/detekt/detekt/pull/3113)
-   Refactoring: Use indices syntax in loop - [#3112](https://github.com/detekt/detekt/pull/3112)
-   Specify type explicitly in DetektCreateBaselineTask - [#3111](https://github.com/detekt/detekt/pull/3111)
-   Refactoring: Class member can have private visibility - [#3110](https://github.com/detekt/detekt/pull/3110)
-   Fix kotlin-reflect problem for idea plugin - [#3107](https://github.com/detekt/detekt/pull/3107)

See all issues at: [1.14.1](https://github.com/detekt/detekt/milestone/77)

#### 1.14.0 - 2020-09-26

##### Notable Changes

-   New standard rules: `ReplaceSafeCallChainWithRun`, `ClassOrdering`, `SuspendFunWithFlowReturnType`
-   New experimental KtLint rules: `AnnotationSpacing`, `ArgumentListWrapping`

##### Changelog

-   Add additional classpaths to KtTestCompiler - [#3101](https://github.com/detekt/detekt/pull/3101)
-   New rule: SuspendFunWithFlowReturnType - [#3098](https://github.com/detekt/detekt/pull/3098)
-   Fix formatting issues have no absolute path - #3063 - [#3097](https://github.com/detekt/detekt/pull/3097)
-   Fix false positive (+= overload) in UnusedPrivateMember - [#3094](https://github.com/detekt/detekt/pull/3094)
-   Update RedundantVisibilityModifierRule to find redundant internal modifiers - [#3092](https://github.com/detekt/detekt/pull/3092)
-   New rule: ReplaceSafeCallChainWithRun - [#3089](https://github.com/detekt/detekt/pull/3089)
-   New rule: ClassOrdering - [#3088](https://github.com/detekt/detekt/pull/3088)
-   Use File.pathSeparator to split entries in classpath - [#3084](https://github.com/detekt/detekt/pull/3084)
-   Wrap new KtLint rules - [#3083](https://github.com/detekt/detekt/pull/3083)
-   Wrap KtLint AnnotationSpacingRule - [#3081](https://github.com/detekt/detekt/issues/3081)
-   Wrap KtLint ArgumentListWrappingRule - [#3080](https://github.com/detekt/detekt/issues/3080)
-   UnusedImports rule: fix false positives when type resolution is enabled - [#3079](https://github.com/detekt/detekt/pull/3079)
-   Move SNAPSHOT from artifactory to sonatype - [#3076](https://github.com/detekt/detekt/pull/3076)
-   Update KtLint to 0.39.0 - [#3075](https://github.com/detekt/detekt/pull/3075)
-   UnusedImports: Lots of false positives - [#3074](https://github.com/detekt/detekt/issues/3074)
-   Update dependency badge to maven central - [#3072](https://github.com/detekt/detekt/pull/3072)
-   Simplify reporting message for IgnoredReturnValue - [#3068](https://github.com/detekt/detekt/pull/3068)
-   Add ability to pass rootclasspaths to KtTestCompiler - [#3066](https://github.com/detekt/detekt/issues/3066)
-   MagicNumber rule: don't check Character, Boolean and null - [#3064](https://github.com/detekt/detekt/pull/3064)
-   KtLint formatting checks do not include full path in report - [#3063](https://github.com/detekt/detekt/issues/3063)
-   IgnoredReturnValue should only report name of method/function called - [#3052](https://github.com/detekt/detekt/issues/3052)

##### Housekeeping & Refactorings

-   Update jacoco with Java 15 support - [#3105](https://github.com/detekt/detekt/pull/3105)
-   Update project dependencies - [#3104](https://github.com/detekt/detekt/pull/3104)
-   Enable parallel spek test discovery - [#3090](https://github.com/detekt/detekt/pull/3090)
-   Update Gradle to 6.7-rc-2 - [#3071](https://github.com/detekt/detekt/pull/3071)
-   Update spek framework to 2.0.13 - [#3070](https://github.com/detekt/detekt/pull/3070)
-   Update kramdown dependency due to a vulnerability - [#3069](https://github.com/detekt/detekt/pull/3069)
-   Add test asserting the report message - [#3061](https://github.com/detekt/detekt/pull/3061)

See all issues at: [1.14.0](https://github.com/detekt/detekt/milestone/76)

#### 1.13.1 - 2020-09-13

##### Notable Changes

We now publish directly to maven central.
The 1.13.0 release got corrupted and should not be used.

#### 1.13.0 - 2020-09-07

##### Migration

-   This release drops several deprecations in `detekt-api`.

##### Changelog

-   Set modifier order based on Kotlin coding conventions - [#3056](https://github.com/detekt/detekt/pull/3056)
-   FunctionNaming: allow anonymous functions - [#3055](https://github.com/detekt/detekt/pull/3055)
-   FunctionNaming should ignore anonymous functions - [#3054](https://github.com/detekt/detekt/issues/3054)
-   Fix ModifierOrder for false positive reported by ModifierOrder when using fun interfaces - [#3051](https://github.com/detekt/detekt/pull/3051)
-   False positive reported by ModifierOrder when using fun interfaces - [#3050](https://github.com/detekt/detekt/issues/3050)
-   Fix PreferToOverPairSyntax exception - [#3046](https://github.com/detekt/detekt/pull/3046)
-   PreferToOverPairSyntax throws exceptions - [#3044](https://github.com/detekt/detekt/issues/3044)
-   Remove deprecated entity and location members - [#3037](https://github.com/detekt/detekt/pull/3037)
-   Respect configured config file when running the detektCreateConfig task - [#3036](https://github.com/detekt/detekt/pull/3036)
-   Remove last years api deprecations - [#3035](https://github.com/detekt/detekt/pull/3035)
-   UnusedImports rule: fix false negative when same name identifiers are imported and used - [#3033](https://github.com/detekt/detekt/pull/3033)
-   ForbiddenMethodCall: report operator calls - [#3032](https://github.com/detekt/detekt/pull/3032)
-   Improve documentation for Gradle repositories setup - [#3030](https://github.com/detekt/detekt/pull/3030)
-   how to use the type resolution to make ForbiddenMethodCall to check bigdecimal.equals using bigdecimal == bigdecimal? - [#3029](https://github.com/detekt/detekt/issues/3029)
-   Use lazy gradle APIs in docs - [#3028](https://github.com/detekt/detekt/pull/3028)
-   Implement option to turn config property deprecation warnings as errors - [#3026](https://github.com/detekt/detekt/pull/3026)
-   Fix RedundantSuspendModifier message - [#3025](https://github.com/detekt/detekt/pull/3025)
-   Print filtered paths in debug mode for easier bug tracing - [#3022](https://github.com/detekt/detekt/pull/3022)
-   Unused import not detected - [#3020](https://github.com/detekt/detekt/issues/3020)
-   Include reports and formatting features to the system test - [#3018](https://github.com/detekt/detekt/pull/3018)
-   detektGenerateConfig ignores detekt.config paramenter - [#2565](https://github.com/detekt/detekt/issues/2565)
-   Treat config property deprecations as errors - [#2545](https://github.com/detekt/detekt/issues/2545)
-   Print filtered paths in debug mode - [#869](https://github.com/detekt/detekt/issues/869)

##### Housekeeping & Refactorings

-   Remove confusing annotation for UnusedImports rule - [#3039](https://github.com/detekt/detekt/pull/3039)
-   Remove obsolete signature workaround for formatting plugin - [#3038](https://github.com/detekt/detekt/pull/3038)
-   Upgrade to new dokka - [#2931](https://github.com/detekt/detekt/pull/2931)

See all issues at: [1.13.0](https://github.com/detekt/detekt/milestone/75)

#### 1.12.0 - 2020-08-25

##### Notable Changes

-   Kotlin 1.4 support
-   New rules: `UseCheckNotNull`, `UseRequireNotNull`, `NonBooleanPropertyPrefixedWithIs`
-   The Gradle plugin now requires at least Gradle 5.4. This aligns with Kotlin's Gradle plugin.
-   The Gradle plugin now supports configuration avoidance
-   The Gradle plugin now generates additional detekt tasks for Android projects
-   KtLint 0.38.1 support (via detekt-formatting)

##### Migration

-   `ArrayPrimitive` rule is more precise but requires type resolution now.
-   The deprecated `customReports` property of the Gradle plugin got removed.
-   Deprecated properties `xmlReportFile`, `htmlReportFile`, `txtReportFile` are now internal.

Note the default `gradle detekt` task does not run over test sources.
To include test sources following setup can be used:

```kt
detekt {
    // ...
    input = objects.fileCollection().from(
        "src/main/java",
        "src/test/java",
        "src/main/kotlin",
        "src/test/kotlin"
    )
}
```

This does not apply to `gradle detektTest`.
We encourage to also check test sources.  
We will include test sources by default in detekt `1.15.0` or later.

##### Changelog

-   Consolidate working for Type Resolution - [#3011](https://github.com/detekt/detekt/pull/3011)
-   ArrayPrimitive rule: report variable/receiver types and factory methods - [#3009](https://github.com/detekt/detekt/pull/3009)
-   Add requiresFullAnalysis annotation to UseCheckNotNull/UseRequireNotNull - [#3008](https://github.com/detekt/detekt/pull/3008)
-   Update kotlinx.html dependencies - [#3007](https://github.com/detekt/detekt/pull/3007)
-   Do not overwrite bintray publication - [#3006](https://github.com/detekt/detekt/pull/3006)
-   Update Ktlint to 0.38.0 - [#3004](https://github.com/detekt/detekt/pull/3004)
-   Add UseRequireNotNull/UseCheckNotNull rules - [#3003](https://github.com/detekt/detekt/pull/3003)
-   Workaround config - [#3001](https://github.com/detekt/detekt/pull/3001)
-   Replace `require(x != null)` with `requireNotNull(x)` - [#2998](https://github.com/detekt/detekt/issues/2998)
-   Minor Gradle plugin cleanup - [#2997](https://github.com/detekt/detekt/pull/2997)
-   detekt-gradle-plugin has two publishing configuration - [#2996](https://github.com/detekt/detekt/issues/2996)
-   Introduce @requiresFullAnalysis to KDoc for rules - [#2993](https://github.com/detekt/detekt/pull/2993)
-   Fix support for Gradle configuration cache - [#2992](https://github.com/detekt/detekt/pull/2992)
-   Fix false positive for UnnecessaryLet with disabled type resolution - [#2991](https://github.com/detekt/detekt/pull/2991)
-   Deprecation: False positive with Kotlin 1.4.0 - [#2990](https://github.com/detekt/detekt/issues/2990)
-   False positive in UnnecessaryLet - [#2987](https://github.com/detekt/detekt/issues/2987)
-   Gradle plugin fixes - [#2986](https://github.com/detekt/detekt/pull/2986)
-   Remove deprecated customReports detekt task property - #2811 - [#2944](https://github.com/detekt/detekt/pull/2944)
-   Rename IsPropertyNaming to NonBooleanPropertyPrefixedWithIs? - [#2819](https://github.com/detekt/detekt/pull/2819)
-   Add automatic detekt tasks for Android Plugins - [#2787](https://github.com/detekt/detekt/pull/2787)

##### Housekeeping & Refactorings

-   Update Gradle to 6.6.1 - [#3016](https://github.com/detekt/detekt/pull/3016)
-   Run detekt with type resolution analysis on CI - [#3015](https://github.com/detekt/detekt/pull/3015)
-   Run Gradle Android tests conditionally when sdk path is defined - [#3014](https://github.com/detekt/detekt/pull/3014)
-   Fix documentation for NonBooleanPropertyPrefixedWithIs - [#3012](https://github.com/detekt/detekt/pull/3012)
-   Run detekt on all test sources - [#3010](https://github.com/detekt/detekt/pull/3010)
-   Do not publish test fixture; removing warning - [#3005](https://github.com/detekt/detekt/pull/3005)
-   Implement custom rule to check spek test discovery performance issues - [#2954](https://github.com/detekt/detekt/pull/2954)

See all issues at: [1.12.0](https://github.com/detekt/detekt/milestone/71)

#### 1.12.0-RC1 - 2020-08-20

##### Notable Changes

The Gradle plugin now requires at least Gradle 5.4. This aligns with Kotlin's Gradle plugin.

##### Changelog

-   Support Kotlin 1.4 - [#2981](https://github.com/detekt/detekt/pull/2981)
-   1.10.0 to 1.11.0 | Could not find org.spekframework.spek2:spek-dsl-jvm - [#2976](https://github.com/detekt/detekt/issues/2976)
-   Make a release built against Kotlin 1.4 - [#2974](https://github.com/detekt/detekt/issues/2974)
-   Roll back to non snapshot spek version - [#2922](https://github.com/detekt/detekt/pull/2922)

See all issues at: [1.12.0-RC1](https://github.com/detekt/detekt/milestone/73)

#### 1.11.2 - 2020-08-19

##### Changelog

-   Fix formatting regression where issues printed the whole filename - [#2988](https://github.com/detekt/detekt/pull/2988)
-   Baseline adds file path instead package - [#2985](https://github.com/detekt/detekt/issues/2985)

See all issues at: [1.11.2](https://github.com/detekt/detekt/milestone/74)

#### 1.11.1 - 2020-08-18

##### Changelog

-   Fix regression separating classpath entries - #2961 - [#2977](https://github.com/detekt/detekt/pull/2977)
-   Show groovy and kotlin dsl in the same page - [#2971](https://github.com/detekt/detekt/pull/2971)
-   Fix typo - [#2969](https://github.com/detekt/detekt/pull/2969)
-   UnnecessaryLet: fix false positive when let is used for destructuring - [#2968](https://github.com/detekt/detekt/pull/2968)
-   UnnecessaryLet false positive when let is used for destructuring - [#2966](https://github.com/detekt/detekt/issues/2966)
-   Merge gradle groovy dsl and kotlin dsl documentation in the same page - [#2846](https://github.com/detekt/detekt/issues/2846)
-   Run detekt as Kotlin Compiler Plugin - [#2119](https://github.com/detekt/detekt/issues/2119)

See all issues at: [1.11.1](https://github.com/detekt/detekt/milestone/72)

#### 1.11.0 - 2020-08-13

##### Changelog

-   Fix false positive for UnnecessaryApply with disabled type resolution - [#2963](https://github.com/detekt/detekt/pull/2963)
-   Add new rule: LibraryEntitiesCannotBePublic - [#2959](https://github.com/detekt/detekt/pull/2959)
-   Suggest rule: Class cannot be public. - [#2943](https://github.com/detekt/detekt/issues/2943)
-   False positive in UnnecessaryApply - [#2938](https://github.com/detekt/detekt/issues/2938)
-   Add NullableToStringCall rule - [#2903](https://github.com/detekt/detekt/pull/2903)
-   New rule: toString over a nullable value - [#2901](https://github.com/detekt/detekt/issues/2901)
-   Introduce tooling-api - [#2860](https://github.com/detekt/detekt/issues/2860)

See all issues at: [1.11.0](https://github.com/detekt/detekt/milestone/70)  
See all issues at: [1.11.0-RC2](https://github.com/detekt/detekt/milestone/68)  
See all issues at: [1.11.0-RC1](https://github.com/detekt/detekt/milestone/69)

#### 1.11.0-RC2 - 2020-08-09

##### Changelog

-   Actually print the invalid properties - [#2955](https://github.com/detekt/detekt/pull/2955)
-   Update FindingsReport doc - [#2942](https://github.com/detekt/detekt/pull/2942)
-   Document reports on homepage - [#2941](https://github.com/detekt/detekt/pull/2941)
-   Set missing code languages in doc - [#2935](https://github.com/detekt/detekt/pull/2935)
-   Fix sitemap.xml - [#2933](https://github.com/detekt/detekt/pull/2933)
-   Flag listOfNotNull if all arguments are non-nullable - [#2932](https://github.com/detekt/detekt/pull/2932)
-   Add LanguageVersionSettings and DataFlowValueFactory to BaseRule - [#2929](https://github.com/detekt/detekt/pull/2929)
-   Generate release date to the changelog entries - [#2924](https://github.com/detekt/detekt/pull/2924)
-   Add informative message to UselessCallOnNotNull report - [#2920](https://github.com/detekt/detekt/pull/2920)
-   Flag listOfNotNull if all arguments are non-nullable - [#2916](https://github.com/detekt/detekt/issues/2916)

##### Housekeeping & Refactorings

-   Reduce test discovery for metrics module - [#2953](https://github.com/detekt/detekt/pull/2953)
-   Reduce test discovery for rules-style module - [#2952](https://github.com/detekt/detekt/pull/2952)
-   Reduce test discovery for rules-naming module - [#2951](https://github.com/detekt/detekt/pull/2951)
-   Reduce test discovery for rules-exceptions module - [#2950](https://github.com/detekt/detekt/pull/2950)
-   Reduce test discovery for rules-complexity module - [#2949](https://github.com/detekt/detekt/pull/2949)
-   Reduce test discovery for formatting module - [#2948](https://github.com/detekt/detekt/pull/2948)
-   Reduce test discovery for core module - [#2947](https://github.com/detekt/detekt/pull/2947)
-   Reduce test discovery for cli module - [#2946](https://github.com/detekt/detekt/pull/2946)
-   Reduce test discovery for api module - [#2945](https://github.com/detekt/detekt/pull/2945)
-   Update kramdown to remedy CVE vulnerability - [#2940](https://github.com/detekt/detekt/pull/2940)

See all issues at: [1.11.0-RC2](https://github.com/detekt/detekt/milestone/68)

#### 1.11.0-RC1 - 2020-08-02

##### Changelog

-   Add non-affirming test cases for UselessCallOnNotNull - [#2918](https://github.com/detekt/detekt/pull/2918)
-   Add Github Action: Detekt All to readme - [#2915](https://github.com/detekt/detekt/pull/2915)
-   Remove deprecated BuildFailureReport from doc - [#2914](https://github.com/detekt/detekt/pull/2914)
-   Document Console Reports code - [#2913](https://github.com/detekt/detekt/pull/2913)
-   Update docs in ProjectMetric class - [#2912](https://github.com/detekt/detekt/pull/2912)
-   Document Output Reports code - [#2911](https://github.com/detekt/detekt/pull/2911)
-   Document Output Reports on the home page - [#2910](https://github.com/detekt/detekt/pull/2910)
-   Add doc to UseDataClass - [#2909](https://github.com/detekt/detekt/pull/2909)
-   Generate output report config parts for config validation to know the properties - [#2907](https://github.com/detekt/detekt/pull/2907)
-   Fix UseDataClass to accept classes that implement interfaces - [#2905](https://github.com/detekt/detekt/pull/2905)
-   UseDataClass does not report for classes that implement interfaces - [#2904](https://github.com/detekt/detekt/issues/2904)
-   Add bindingContext function to FileProcessListener - #2872 - [#2900](https://github.com/detekt/detekt/pull/2900)
-   IgnoredReturnValue: Fix false positive on chained statements - [#2895](https://github.com/detekt/detekt/pull/2895)
-   Setup local PGP signing of published artifacts - [#2893](https://github.com/detekt/detekt/pull/2893)
-   error in output-reports config - [#2891](https://github.com/detekt/detekt/issues/2891)
-   OptionalUnit: fix false positive with 'else if' - [#2888](https://github.com/detekt/detekt/pull/2888)
-   OptionalUnit: don't report it if Unit is used as an expression - [#2886](https://github.com/detekt/detekt/pull/2886)
-   Preserve original exception when loading an invalid config - [#2884](https://github.com/detekt/detekt/pull/2884)
-   Sign artifacts and publish checksums - [#2883](https://github.com/detekt/detekt/issues/2883)
-   Check string arguments with TR enabled - [#2879](https://github.com/detekt/detekt/pull/2879)
-   Detekt Deployment is missing Gradle Module files - [#2878](https://github.com/detekt/detekt/issues/2878)
-   Better Support for Guard Clauses in ThrowsCount Rule - [#2876](https://github.com/detekt/detekt/pull/2876)
-   Make KtTestCompiler internal - [#2874](https://github.com/detekt/detekt/pull/2874)
-   Don't mention the old performance impact - [#2873](https://github.com/detekt/detekt/pull/2873)
-   Support for BindingContext in FileProcessListener - [#2872](https://github.com/detekt/detekt/issues/2872)
-   Hide KtTestCompiler as internal - [#2871](https://github.com/detekt/detekt/issues/2871)
-   Document Console and Output Reports - [#2869](https://github.com/detekt/detekt/issues/2869)
-   Split rules module into a module per rule set - [#2865](https://github.com/detekt/detekt/pull/2865)
-   Add new rule UseEmptyCounterpart - [#2864](https://github.com/detekt/detekt/pull/2864)
-   Introduce tooling api module - [#2861](https://github.com/detekt/detekt/pull/2861)
-   Extend docs on Processors - [#2854](https://github.com/detekt/detekt/pull/2854)
-   Add a rule to flag places where `emptyList` (and similar) can be used - [#2850](https://github.com/detekt/detekt/issues/2850)
-   Clarify / document processors and console reports in config - [#2833](https://github.com/detekt/detekt/issues/2833)
-   Fix UnnecessaryLet false negatives - [#2828](https://github.com/detekt/detekt/pull/2828)
-   UnnecessaryLet false negatives - [#2826](https://github.com/detekt/detekt/issues/2826)
-   False positive in OptionalUnit rule - [#2452](https://github.com/detekt/detekt/issues/2452)

##### Housekeeping & Refactorings

-   Test performance improvements - [#2921](https://github.com/detekt/detekt/pull/2921)
-   Remove redundant guard clauses - [#2919](https://github.com/detekt/detekt/pull/2919)
-   The tests are slow - [#2902](https://github.com/detekt/detekt/issues/2902)
-   Drop dependency on detekt test utils for Gradle plugin - [#2899](https://github.com/detekt/detekt/pull/2899)
-   Use KtFile.name instead of storing an absolute path key - [#2898](https://github.com/detekt/detekt/pull/2898)
-   Fix testcase with early access jdk's - [#2897](https://github.com/detekt/detekt/pull/2897)
-   Publish to Bintray using maven-publish - [#2885](https://github.com/detekt/detekt/pull/2885)
-   Simplify internal parsing to KtFile's - [#2875](https://github.com/detekt/detekt/pull/2875)
-   Mention bazel integration and a new blog article - [#2867](https://github.com/detekt/detekt/pull/2867)
-   Mention bazel and github actions integration - [#2866](https://github.com/detekt/detekt/pull/2866)
-   Cleanup code - [#2862](https://github.com/detekt/detekt/pull/2862)
-   Make sure to always target JVM 8 bytecode - [#2853](https://github.com/detekt/detekt/pull/2853)
-   Refactor - [#2849](https://github.com/detekt/detekt/pull/2849)
-   Clear findings even if we are not going to visit the file - [#2848](https://github.com/detekt/detekt/pull/2848)
-   Update Spek to 2.0.12 - [#2847](https://github.com/detekt/detekt/pull/2847)
-   Speed up generateDocumentation - [#2832](https://github.com/detekt/detekt/pull/2832)
-   Simplify pre-merge.yaml - [#2823](https://github.com/detekt/detekt/pull/2823)

See all issues at: [1.11.0-RC1](https://github.com/detekt/detekt/milestone/69)

#### 1.10.0

##### Notable Changes

-   New rules: `IgnoredReturnValue`, `ImplictUnitReturnType`
-   The complexity report (console/html) now calculates the [cognitive complexity metric](https://www.sonarsource.com/docs/CognitiveComplexity.pdf) for your project.
-   Issues at functions and classes are now reported at the identifiers. This is especially helpful in the IntelliJ plugin.
-   Extension authors can now manipulate the findings with the new [ReportingExtension](https://github.com/detekt/detekt/blob/main/detekt-api/src/main/kotlin/io/gitlab/arturbosch/detekt/api/ReportingExtension.kt).
-   `detekt-formatting` was updated to use KtLint 0.37.2 which includes a lot of improvements and changes. Please see their [changelog](https://github.com/pinterest/ktlint/releases/tag/0.37.0).
    -   New wrapper rules: `SpacingAroundDoubleColon`, `SpacingBetweenDeclarationsWithCommentsRule`, `SpacingBetweenDeclarationsWithAnnotationsRule`
    -   You can configure the [layoutPattern](https://github.com/pinterest/ktlint/blob/0.37.0/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/ImportOrderingRule.kt#L18) for `ImportOrdering` in detekt's configuration file.
    -   `Indentation` rule was replaced with a new [implementation](https://github.com/pinterest/ktlint/pull/758).
-   The `default-detekt-config` moved to `detekt-core/src/main/resources/default-detekt-config.yml`.
    Please update your links if you used it for references.

##### Migration

-   We fixed a regression in baseline signatures. This breaks baseline ids for reported issues at functions for some rules. You may need to regenerate the baseline file or manually change the ids. We are very sorry for this inconvenience.
-   KtLint replaced their default `Indentation` rule. Expect new formatting issues here!
-   We removed `HierachicalConfig` interface. It was deprecated in 1.7.0 and could lead to OOM errors when reusing `Config`
    embedding detekt.
    If you used `Config.parent.key` in your code, `Config.parentPath` is an alternative for this interface.
-   We are dropping the thin wrapper tasks over Intellij's `inspections.sh` and `format.sh`.
    They were broken for some time and in general sparingly used.
    There are alternative ways to run these scripts: https://www.jetbrains.com/help/idea/command-line-formatter.html or https://github.com/bentolor/idea-cli-inspector.
-   XML-tags in the baseline file now have more meaningful names:
    -   `Whitelist` -> `ManuallySuppressedIssues`
    -   `Blacklist` -> `CurrentIssues`
    -   Note: the old names will work until a new major release

##### Changelog

-   UnusedPrivateClass: fix false negative with import directives - [#2817](https://github.com/detekt/detekt/pull/2817)
-   UnusedPrivateClass: false negative with import directives - [#2816](https://github.com/detekt/detekt/issues/2816)
-   Publish detekt-bom module - [#2814](https://github.com/detekt/detekt/pull/2814)
-   [Gradle] Configuration cache support - [#2813](https://github.com/detekt/detekt/pull/2813)
-   UnusedPrivateClass: don't report imported classes - [#2812](https://github.com/detekt/detekt/pull/2812)
-   False positive UnusedPrivateClass when importing private enum constants directly. - [#2809](https://github.com/detekt/detekt/issues/2809)
-   Upgrade ktlint to version 0.37.2 - [#2807](https://github.com/detekt/detekt/pull/2807)
-   LongMethod: disregard params in method definition - [#2806](https://github.com/detekt/detekt/pull/2806)
-   LongMethod should not consider parameters while calculating the number of lines - [#2804](https://github.com/detekt/detekt/issues/2804)
-   Local vars inside object literals can't be const - Closes #2794 - [#2799](https://github.com/detekt/detekt/pull/2799)
-   MayBeConst should not report properties in local anonymous object declarations - [#2794](https://github.com/detekt/detekt/issues/2794)
-   Fix false positive for UnusedPrivateClass on generics - [#2793](https://github.com/detekt/detekt/pull/2793)
-   Report ComplexInterface issues at identifiers - [#2786](https://github.com/detekt/detekt/pull/2786)
-   Construct signatures based on named declaration instead of just the identifier - [#2785](https://github.com/detekt/detekt/pull/2785)
-   KtLint to 0.37.1 - [#2783](https://github.com/detekt/detekt/pull/2783)
-   Implement new rule ImplictUnitReturnTypet - [#2781](https://github.com/detekt/detekt/pull/2781)
-   Feature/restrict is properties - [#2779](https://github.com/detekt/detekt/pull/2779)
-   Rename Blacklist and Whitelist to be self explanatory - [#2778](https://github.com/detekt/detekt/pull/2778)
-   Remove deprecated HierarchicalConfig which could lead to OOM when reusing Config objects - [#2768](https://github.com/detekt/detekt/pull/2768)
-   Support layout property for ImportOrdering rule - [#2763](https://github.com/detekt/detekt/pull/2763)
-   Wrap three new experimental KtLint rules - [#2762](https://github.com/detekt/detekt/pull/2762)
-   Upgrade to ktlint 0.37.0 - [#2760](https://github.com/detekt/detekt/pull/2760)
-   Introduce reporting extensions - [#2755](https://github.com/detekt/detekt/pull/2755)
-   Add default print methods to ForbiddenMethodCall - [#2753](https://github.com/detekt/detekt/pull/2753)
-   Add the `ignoreAnnotated` array parameter to the FunctionNaming rule - [#2734](https://github.com/detekt/detekt/pull/2734)
-   FunctionNaming: Needs "ignoreAnnotated" - [#2733](https://github.com/detekt/detekt/issues/2733)
-   State that speeding the detekt task just applies to version < 1.7.0 - [#2730](https://github.com/detekt/detekt/pull/2730)
-   Add cognitive complexity in complexity report - [#2727](https://github.com/detekt/detekt/pull/2727)
-   add better documentation for the LongParameterList ignoreAnnotated - [#2714](https://github.com/detekt/detekt/pull/2714)
-   IgnoreReturnValue: config options - [#2712](https://github.com/detekt/detekt/pull/2712)
-   Use experimental indentation rule set instead of the unused from the standard rule set - [#2709](https://github.com/detekt/detekt/pull/2709)
-   Remove idea integration - [#2706](https://github.com/detekt/detekt/pull/2706)
-   Improve issue reporting/report at identifiers and package declarations - #2699 - [#2702](https://github.com/detekt/detekt/pull/2702)
-   Feature request - limit number of lines for an issue to 1 - [#2699](https://github.com/detekt/detekt/issues/2699)
-   New Rule: IgnoredReturnValue - [#2698](https://github.com/detekt/detekt/pull/2698)
-   New rule: NoPrintStatement - [#2678](https://github.com/detekt/detekt/issues/2678)
-   Add default values to SwallowedException rule - [#2661](https://github.com/detekt/detekt/pull/2661)
-   [V1.6.0 -> V1.7.4] Error reading configuration file, java.util.zip.ZipException: invalid code lengths set. - [#2582](https://github.com/detekt/detekt/issues/2582)
-   New rule: Warn on ignored return value - [#2239](https://github.com/detekt/detekt/issues/2239)
-   File 'C\...\.idea' specified for property 'ideaExtension.path' is not a file. - [#2172](https://github.com/detekt/detekt/issues/2172)
-   ktlint integration does not report most errors - [#2161](https://github.com/detekt/detekt/issues/2161)
-   Non deterministic output. False positives on Indentation rule - [#1633](https://github.com/detekt/detekt/issues/1633)

##### Housekeeping & Refactorings

-   use parallel build options - [#2808](https://github.com/detekt/detekt/pull/2808)
-   Compile Test Snippets on Java 14 - [#2803](https://github.com/detekt/detekt/pull/2803)
-   Cleanup MayBeConst tests - [#2802](https://github.com/detekt/detekt/pull/2802)
-   Refactor compare_releases script to use clikt - [#2801](https://github.com/detekt/detekt/pull/2801)
-   Simplify the Code Coverage workflow - [#2798](https://github.com/detekt/detekt/pull/2798)
-   Run compile-test-snippets in a isolated job - [#2797](https://github.com/detekt/detekt/pull/2797)
-   Run verifyGeneratorOutput in a isolated job - [#2796](https://github.com/detekt/detekt/pull/2796)
-   Introduce BoM to manage our own dependencies - [#2792](https://github.com/detekt/detekt/pull/2792)
-   Smarter Caching on Github Actions - [#2788](https://github.com/detekt/detekt/pull/2788)
-   Fix config generation directory - [#2782](https://github.com/detekt/detekt/pull/2782)
-   Disable Gradle daemon on CI - [#2780](https://github.com/detekt/detekt/pull/2780)
-   Convert github-milestone-report.groovy to main.kts - [#2777](https://github.com/detekt/detekt/pull/2777)
-   Prepare 1.10.0-RC1 release - [#2776](https://github.com/detekt/detekt/pull/2776)
-   Fix memory leak with not closing processing settings - [#2775](https://github.com/detekt/detekt/pull/2775)
-   Do not print passing tests on the console - [#2774](https://github.com/detekt/detekt/pull/2774)
-   Run in parallel by default - [#2773](https://github.com/detekt/detekt/pull/2773)
-   Remove core module dependency for detekt-test - [#2771](https://github.com/detekt/detekt/pull/2771)
-   Unify extension debug printing - [#2770](https://github.com/detekt/detekt/pull/2770)
-   Package editorconfig dependency into the jar for formatting module - [#2769](https://github.com/detekt/detekt/pull/2769)
-   Update spek to 2.0.11 disabling timeouts - [#2767](https://github.com/detekt/detekt/pull/2767)
-   Introduce additional changelog section filtering developing/refactoring noise for the users - [#2766](https://github.com/detekt/detekt/pull/2766)
-   Move config validation from cli to core - [#2764](https://github.com/detekt/detekt/pull/2764)
-   Improve the performance of tests which use type resolution - [#2756](https://github.com/detekt/detekt/pull/2756)
-   Move reporting logic to core module - [#2754](https://github.com/detekt/detekt/pull/2754)
-   Cleanup tests in ProtectedMemberInFinalClass - [#2752](https://github.com/detekt/detekt/pull/2752)
-   Add referential equality test case in EqualsAlwaysReturnsTrueOrFalse - [#2751](https://github.com/detekt/detekt/pull/2751)
-   Extract xml and html reports to own modules - [#2750](https://github.com/detekt/detekt/pull/2750)
-   Separate console and output report loading - [#2749](https://github.com/detekt/detekt/pull/2749)
-   Bump actions/cache to v2 - [#2746](https://github.com/detekt/detekt/pull/2746)
-   Fix EqualsAlwaysReturnsTrueOrFalse doc - [#2744](https://github.com/detekt/detekt/pull/2744)
-   Simplify core facade class - [#2743](https://github.com/detekt/detekt/pull/2743)
-   Mark some well known cli functions as implicit unsupported api - [#2742](https://github.com/detekt/detekt/pull/2742)
-   Move baseline feature to core module - [#2741](https://github.com/detekt/detekt/pull/2741)
-   Make baseline entities internal - [#2740](https://github.com/detekt/detekt/pull/2740)
-   Simplify baseline data structures - [#2739](https://github.com/detekt/detekt/pull/2739)
-   Move baseline utils to the baseline package - [#2738](https://github.com/detekt/detekt/pull/2738)
-   Bump github-pages from 204 to 206 in /docs - [#2737](https://github.com/detekt/detekt/pull/2737)
-   Update gradle scan plugin - [#2736](https://github.com/detekt/detekt/pull/2736)
-   Update test dependencies - [#2735](https://github.com/detekt/detekt/pull/2735)
-   Move three core-related tests to core module - [#2731](https://github.com/detekt/detekt/pull/2731)
-   Update to Gradle 6.4.1 - [#2729](https://github.com/detekt/detekt/pull/2729)
-   Migrate to resource function of test-utils - [#2728](https://github.com/detekt/detekt/pull/2728)
-   Remove own collectByType function as Kotlin's does not crash anymore - [#2726](https://github.com/detekt/detekt/pull/2726)
-   Move processors to metrics module - [#2725](https://github.com/detekt/detekt/pull/2725)
-   Create publish tasks lazily - [#2723](https://github.com/detekt/detekt/pull/2723)
-   Faster documentation generation - [#2722](https://github.com/detekt/detekt/pull/2722)
-   Modularize test module - [#2720](https://github.com/detekt/detekt/pull/2720)
-   Introduce parser and psi module - [#2716](https://github.com/detekt/detekt/pull/2716)
-   Clean up code by using builtin associateBy function - [#2715](https://github.com/detekt/detekt/pull/2715)
-   [Security] Bump activesupport from 6.0.2.1 to 6.0.3.1 in /docs - [#2708](https://github.com/detekt/detekt/pull/2708)
-   Correct formatting issues - [#2707](https://github.com/detekt/detekt/pull/2707)
-   [Gradle plugin/rule authors]: Invalidate jars on modified date changes - [#2703](https://github.com/detekt/detekt/pull/2703)

See all issues at: [1.10.0](https://github.com/detekt/detekt/milestone/67)

#### 1.9.1

##### Changelog

-   Add negative tests to UnreachableCode rule - [#2697](https://github.com/detekt/detekt/pull/2697)
-   Inline test cases of CollapsibleIfStatements - [#2696](https://github.com/detekt/detekt/pull/2696)
-   Inline unreachable code - [#2695](https://github.com/detekt/detekt/pull/2695)
-   Fix wrong test cases for CollapsibleIfs rule - [#2694](https://github.com/detekt/detekt/pull/2694)
-   Print loaded rule set classes on debug - [#2691](https://github.com/detekt/detekt/pull/2691)
-   Fix wrong test cases for MandatoryBracesIfStatements - [#2689](https://github.com/detekt/detekt/pull/2689)
-   Simplify LongParameterList tests - [#2688](https://github.com/detekt/detekt/pull/2688)
-   Fix serialVersionUID false positive in UnderscoresInNumericLiteral - [#2687](https://github.com/detekt/detekt/pull/2687)
-   False positive UnderscoresInNumericLiterals in Serializable - [#2686](https://github.com/detekt/detekt/issues/2686)
-   Report a better message for LongParameterList - [#2685](https://github.com/detekt/detekt/pull/2685)
-   Report a better message for LongParameterList - [#2684](https://github.com/detekt/detekt/pull/2684)
-   Test against the newest Java version - [#2682](https://github.com/detekt/detekt/pull/2682)

See all issues at: [1.9.1](https://github.com/detekt/detekt/milestone/66)

#### 1.9.0

##### Changelog

-   Extra documentation added to pitfalls section - [#2675](https://github.com/detekt/detekt/pull/2675)
-   Use configuration avoidance for withType usages - [#2672](https://github.com/detekt/detekt/pull/2672)
-   GuardClause also matches if-with-body that contains a return - [#2671](https://github.com/detekt/detekt/pull/2671)
-   Simplify patterns - [#2668](https://github.com/detekt/detekt/pull/2668)
-   Include Kotlin multiplatform test folders to default exclude config - [#2667](https://github.com/detekt/detekt/pull/2667)
-   Remove duplicate MandatoryBracesLoops rule tests - [#2665](https://github.com/detekt/detekt/pull/2665)
-   Remove print statement in MandatoryBracesLoops - [#2664](https://github.com/detekt/detekt/pull/2664)
-   Remove $ as a valid char in class and method names - [#2662](https://github.com/detekt/detekt/pull/2662)
-   Build detekt executable before publishing it - #2654 - [#2659](https://github.com/detekt/detekt/pull/2659)
-   Add style rule for mandatory braces in for loop bodies - [#2658](https://github.com/detekt/detekt/pull/2658)
-   Asset in release 1.8.0 is not up-to-date - [#2654](https://github.com/detekt/detekt/issues/2654)
-   Rule: Mandatory braces for single-line for loop bodies - [#2652](https://github.com/detekt/detekt/issues/2652)
-   Use task configuration avoidance for detekt plugin - [#2651](https://github.com/detekt/detekt/pull/2651)
-   Add detekt GitHub action to readme - [#2650](https://github.com/detekt/detekt/pull/2650)
-   Don't report unused private properties in expect class - [#2646](https://github.com/detekt/detekt/pull/2646)
-   UnusedPrivateMember: don't report parameters in expect/actual functions - [#2643](https://github.com/detekt/detekt/pull/2643)
-   UnusedPrivateMembers on expect class - [#2636](https://github.com/detekt/detekt/issues/2636)
-   Include Kotlin multiplatform test folders to default exclude configuration - [#2608](https://github.com/detekt/detekt/issues/2608)

See all issues at: [1.9.0](https://github.com/detekt/detekt/milestone/65)

#### 1.8.0

##### Notable Changes

-   Most rule properties now support yaml lists next to string's with comma-separated-entries.
-   Standalone `detekt` executable on GitHub release pages
-   New rules: `UnnecessaryNotNullOperator` and `UnnecessarySafeCall`

##### Migration

-   Formatting rules get reported now on the correct lines. The baseline file may need to be adjusted/regenerated.
-   Issues concerning classes and objects are now reported at the identifier. The baseline file may need to be adjusted/regenerated.

##### Changelog

-   Use yaml lists in our own configuration and tests - [#2623](https://github.com/detekt/detekt/pull/2623)
-   Run code coverage as an own action - [#2622](https://github.com/detekt/detekt/pull/2622)
-   Modularize build script by introducing buildSrc module - [#2621](https://github.com/detekt/detekt/pull/2621)
-   \*>excludes allow yaml list - [#2620](https://github.com/detekt/detekt/pull/2620)
-   Kotlin to 1.3.72 - [#2619](https://github.com/detekt/detekt/pull/2619)
-   Set failfast to false for pre-merge - [#2618](https://github.com/detekt/detekt/pull/2618)
-   Update documentation - [#2617](https://github.com/detekt/detekt/pull/2617)
-   ThrowingExceptionsWithoutMessageOrCause>exceptions allow yaml list - [#2616](https://github.com/detekt/detekt/pull/2616)
-   SwallowedException>ignoredExceptionTypes allow yaml list - [#2615](https://github.com/detekt/detekt/pull/2615)
-   ForbiddenPublicDataClass>ignorePackages allow yaml list - [#2614](https://github.com/detekt/detekt/pull/2614)
-   LabeledExpression>ignoredLabels allow yaml list - [#2613](https://github.com/detekt/detekt/pull/2613)
-   ForbiddenMethodCall>methods allow yaml list - [#2612](https://github.com/detekt/detekt/pull/2612)
-   Generate the cli as a stand alone executable - [#2607](https://github.com/detekt/detekt/pull/2607)
-   Report class and object violations at the identifier - [#2606](https://github.com/detekt/detekt/pull/2606)
-   Fix formatting line reporting - [#2604](https://github.com/detekt/detekt/pull/2604)
-   Correct documentation/recommendation of EmptyCatchBlock rule - [#2603](https://github.com/detekt/detekt/pull/2603)
-   Incorrect (or unclear) EmptyCatchBlock rule - [#2602](https://github.com/detekt/detekt/issues/2602)
-   Use more lintAndCompile - [#2601](https://github.com/detekt/detekt/pull/2601)
-   MagicNumber>ignoredNumbers allow yaml list - [#2600](https://github.com/detekt/detekt/pull/2600)
-   Remove unnecesary symbolic link - [#2598](https://github.com/detekt/detekt/pull/2598)
-   WildcardImport>excludeImports allow yaml list - [#2596](https://github.com/detekt/detekt/pull/2596)
-   ForbiddenClassName>forbiddenName allow yaml list - [#2595](https://github.com/detekt/detekt/pull/2595)
-   Fix false positives in UndocumentedPublicProperty - [#2591](https://github.com/detekt/detekt/pull/2591)
-   Fix false positive in UndocumentedPublicClass - [#2588](https://github.com/detekt/detekt/pull/2588)
-   *>*Annotated\* allow yaml lists - [#2587](https://github.com/detekt/detekt/pull/2587)
-   ForbiddenComment>values allow yaml list - [#2585](https://github.com/detekt/detekt/pull/2585)
-   ExceptionRaisedInUnexpectedLocation>methodNames allow yaml list - [#2584](https://github.com/detekt/detekt/pull/2584)
-   ComplexMethod>nestingFunctions allow yaml list - [#2583](https://github.com/detekt/detekt/pull/2583)
-   UndocumentedPublicClass false positive for inner types - [#2580](https://github.com/detekt/detekt/issues/2580)
-   New Rule: UnnecessaryNotNullOperator - [#2578](https://github.com/detekt/detekt/pull/2578)
-   New Rule: UnnecessarySafeCall - [#2574](https://github.com/detekt/detekt/pull/2574)
-   New Rule: UnnecessarySafeCall - [#2572](https://github.com/detekt/detekt/issues/2572)
-   Add ignoreAnnotated option to LongParameterList - [#2570](https://github.com/detekt/detekt/pull/2570)
-   Rule configuration request: Ignore based on annotations - [#2563](https://github.com/detekt/detekt/issues/2563)
-   codecov test coverage report doesn't work correctly - [#2558](https://github.com/detekt/detekt/issues/2558)
-   Support yaml lists in the configuration values - [#2498](https://github.com/detekt/detekt/issues/2498)
-   Baseline ignoring MaxLineLength: on 1.0.1 - [#1906](https://github.com/detekt/detekt/issues/1906)
-   Formatting rules are reported at wrong line (e.g. MaximumLineLength) - [#1843](https://github.com/detekt/detekt/issues/1843)

See all issues at: [1.8.0](https://github.com/detekt/detekt/milestone/63)

#### 1.7.4

##### Notable Changes

The last Gradle plugin version was referencing 1.7.2 instead of 1.7.3.
This was fixed and specifying the `toolVersion` is not necessary anymore.

```kotlin
detekt {
    toolversion = "1.7.3"
}
```

##### Changelog

-   Revert warning: jcenter is missing (#2550) - [#2576](https://github.com/detekt/detekt/pull/2576)
-   Add additional task input for plugin version task - [#2575](https://github.com/detekt/detekt/pull/2575)
-   jcenter() requirement - [#2571](https://github.com/detekt/detekt/issues/2571)

See all issues at: [1.7.4](https://github.com/detekt/detekt/milestone/64)

#### 1.7.3

##### Changelog

-   OptionalWhenBraces: fix false positive with lambda which has no arrow - [#2568](https://github.com/detekt/detekt/pull/2568)
-   OptionalWhenBraces: false positive with lambda which has no arrow - [#2567](https://github.com/detekt/detekt/issues/2567)
-   valueOrDefaultCommaSeparated throws a ClassCastException - [#2566](https://github.com/detekt/detekt/pull/2566)
-   1.6.0 -> 1.7.2: java.lang.String cannot be cast to java.util.List - [#2561](https://github.com/detekt/detekt/issues/2561)
-   Display actual and threshold values for several metric rules - [#2559](https://github.com/detekt/detekt/pull/2559)
-   Return the actual values for complexity rules - [#2557](https://github.com/detekt/detekt/issues/2557)
-   UseCheckOrError/UseRequire: fix false positive with a non-String argument - [#2556](https://github.com/detekt/detekt/pull/2556)
-   InstanceOfCheckForException: do not report when catch blocks do not check for the subtype of an exception - [#2555](https://github.com/detekt/detekt/pull/2555)
-   Include statistics for our custom detekt tasks - [#2554](https://github.com/detekt/detekt/pull/2554)
-   Remove unnecesary parentheses - [#2553](https://github.com/detekt/detekt/pull/2553)
-   Fix console output indentation - [#2552](https://github.com/detekt/detekt/pull/2552)
-   Console output is not indented correctly - [#2551](https://github.com/detekt/detekt/issues/2551)
-   Check jcenter repository present in Gradle plugin - [#2550](https://github.com/detekt/detekt/pull/2550)
-   Adding detekt to a project from scratch, detektGenerateConfig is broken - [#2549](https://github.com/detekt/detekt/issues/2549)
-   Validate Wrapper only on push to master - [#2548](https://github.com/detekt/detekt/pull/2548)
-   Cleanup Gradle Folders - [#2547](https://github.com/detekt/detekt/pull/2547)
-   Require CI to pass to run codecov - [#2546](https://github.com/detekt/detekt/pull/2546)
-   Export bintray key to publish snapshots - [#2544](https://github.com/detekt/detekt/pull/2544)
-   Activate UseErrorOrCheck rule - [#2542](https://github.com/detekt/detekt/pull/2542)
-   Refactor build file to sections - [#2541](https://github.com/detekt/detekt/pull/2541)
-   False positive: UseCheckOrError - [#2514](https://github.com/detekt/detekt/issues/2514)
-   InstanceOfCheckForException should be ignored for catch blocks that is more than an if expression - [#1927](https://github.com/detekt/detekt/issues/1927)

See all issues at: [1.7.3](https://github.com/detekt/detekt/milestone/62)

#### 1.7.2

##### Changelog

-   Remove ignore:AppVeyor from codecov config - [#2540](https://github.com/detekt/detekt/pull/2540)
-   Use kotlin-stdlib-jdk8 to reduce dependency conflicts - #2527 - [#2538](https://github.com/detekt/detekt/pull/2538)
-   Update Gradle plugin-publish to 0.11.0 - [#2537](https://github.com/detekt/detekt/pull/2537)
-   Delete created temp dir automatically on JVM exit - [#2536](https://github.com/detekt/detekt/pull/2536)
-   Delete created temp file automatically on JVM exit - [#2535](https://github.com/detekt/detekt/pull/2535)
-   Refactor MemberNameEqualsClassNameSpec tests - [#2534](https://github.com/detekt/detekt/pull/2534)
-   Fix UnnecessaryAbstractClass false-positive - [#2533](https://github.com/detekt/detekt/pull/2533)
-   Update GroovyDSL doc to mention input - [#2532](https://github.com/detekt/detekt/pull/2532)
-   Update KotlinDSL doc to mention input - [#2531](https://github.com/detekt/detekt/pull/2531)
-   Fix report for documented data classes property - [#2530](https://github.com/detekt/detekt/pull/2530)
-   UndocumentedPublicProperty reported for documented data classes property - [#2529](https://github.com/detekt/detekt/issues/2529)
-   detekt using Gradle documentation out of date - [#2528](https://github.com/detekt/detekt/issues/2528)
-   1.7.1 does not depend on Kotlin 1.3.71 - [#2527](https://github.com/detekt/detekt/issues/2527)
-   UnnecessaryAbstractClass false positive for abstractproperties - [#2526](https://github.com/detekt/detekt/issues/2526)
-   Do not fail the build on config property warnings/deprecations - #2523 - [#2525](https://github.com/detekt/detekt/pull/2525)
-   regression 1.7.1, threshhold does not exist - [#2523](https://github.com/detekt/detekt/issues/2523)
-   Setup GitHub Actions - [#2512](https://github.com/detekt/detekt/pull/2512)

See all issues at: [1.7.2](https://github.com/detekt/detekt/milestone/61)

#### 1.7.1

##### Changelog

-   UnnecessaryAbstractClass: fix false positive when abstract class has abstract inherited members - [#2513](https://github.com/detekt/detekt/pull/2513)
-   UnusedPrivateMember: report unused overloaded operators - [#2510](https://github.com/detekt/detekt/pull/2510)
-   Fix build compile error resulted from merging two conflicting PRs - [#2508](https://github.com/detekt/detekt/pull/2508)
-   Update Kotlin to 1.3.71 - [#2507](https://github.com/detekt/detekt/pull/2507)
-   Remove duplicated test task setup for gradle plugin - [#2506](https://github.com/detekt/detekt/pull/2506)
-   Add LicenceHeaderExtension test to verify resource path loading is supported - [#2505](https://github.com/detekt/detekt/pull/2505)
-   Ensure filesystems are created so paths can be gotten - [#2504](https://github.com/detekt/detekt/pull/2504)
-   FileSystemNotFoundException in detekt 1.7.0 when using --config-resource - [#2503](https://github.com/detekt/detekt/issues/2503)
-   MemberNameEqualsClassName: fix false negative when function has no explicit return type - [#2502](https://github.com/detekt/detekt/pull/2502)
-   Use the system-dependent line separator in NotificationReport - [#2497](https://github.com/detekt/detekt/pull/2497)
-   Remove default print stream - [#2496](https://github.com/detekt/detekt/pull/2496)
-   Don't use System.out in the ProgressListeners - [#2495](https://github.com/detekt/detekt/pull/2495)
-   Make the gradle plugin a sub project again - [#2493](https://github.com/detekt/detekt/pull/2493)
-   Move test factory outside the main code - [#2491](https://github.com/detekt/detekt/pull/2491)
-   Single runner - [#2490](https://github.com/detekt/detekt/pull/2490)
-   ProcessingSettings - [#2489](https://github.com/detekt/detekt/pull/2489)
-   Don't use println :detekt-generator - [#2488](https://github.com/detekt/detekt/pull/2488)
-   Simplify tests with StringPrintStream - [#2487](https://github.com/detekt/detekt/pull/2487)
-   Improve tests - [#2486](https://github.com/detekt/detekt/pull/2486)
-   Use PrinterStream in AstPrinter - [#2485](https://github.com/detekt/detekt/pull/2485)
-   Errors running detekt in the detekt project - [#2484](https://github.com/detekt/detekt/issues/2484)
-   Unify yml strings - [#2482](https://github.com/detekt/detekt/pull/2482)
-   Use yml arrays to list the ForbiddenImports - [#2474](https://github.com/detekt/detekt/pull/2474)
-   Add date, detekt-version + link to HtmlReport - [#2470](https://github.com/detekt/detekt/pull/2470)
-   Refactor FindingsReport + FileBasedFindingsReport - [#2454](https://github.com/detekt/detekt/pull/2454)
-   The detekt HTML report should include date, version number, website link (Feature Request) - [#2416](https://github.com/detekt/detekt/issues/2416)
-   Added validation of constructors to LongParameterList - [#2410](https://github.com/detekt/detekt/pull/2410)
-   UnusedPrivateMember doesn't report the correct warning count - [#1981](https://github.com/detekt/detekt/issues/1981)
-   UnusedPrivateMember should consider overloaded operators - [#1444](https://github.com/detekt/detekt/issues/1444)
-   False positive on rule UnnecessaryAbstractClass - [#727](https://github.com/detekt/detekt/issues/727)
-   MemberNameEqualsClassName ignores functions which return a single expression - [#655](https://github.com/detekt/detekt/issues/655)

See all issues at: [1.7.1](https://github.com/detekt/detekt/milestone/60)

#### 1.7.0

##### Notable Changes

-   The Gradle plugin should be on par with the CLI performance-wise
-   Good to know: detekt CLI always supported argument files (`java -jar detekt-cli.jar @argsfile`)
-   New CLI `--version` flag
-   New Rule `AbsentOrWrongFileLicense` and other rule improvements

##### Changelog

-   Update Travis to use the newest xcode11.3 image - [#2480](https://github.com/detekt/detekt/pull/2480)
-   Synchronize use of root extension point - [#2479](https://github.com/detekt/detekt/pull/2479)
-   Extend ComplexInterface to support exclusion of private member/functions - [#2478](https://github.com/detekt/detekt/pull/2478)
-   Minor refactors around --create-baseline - [#2477](https://github.com/detekt/detekt/pull/2477)
-   Check primary ctor in UndocumentedPublicProperty - [#2475](https://github.com/detekt/detekt/pull/2475)
-   Unify yaml strings - [#2472](https://github.com/detekt/detekt/pull/2472)
-   Running detektBaseline generates too general exceptions, disabling rules - [#2471](https://github.com/detekt/detekt/issues/2471)
-   UndocumentedPublicProperty doesn't check primary constructor - [#2468](https://github.com/detekt/detekt/issues/2468)
-   Remove default implementations of toString() - [#2467](https://github.com/detekt/detekt/pull/2467)
-   Remove tags from core documentation sites - [#2466](https://github.com/detekt/detekt/pull/2466)
-   Validate boolean config values - Closes #2045 - [#2465](https://github.com/detekt/detekt/pull/2465)
-   Add test for BaselineHandler - [#2462](https://github.com/detekt/detekt/pull/2462)
-   Refactor BaselineFormatSpec.kt - [#2461](https://github.com/detekt/detekt/pull/2461)
-   Add tests for BaselineFacade - [#2460](https://github.com/detekt/detekt/pull/2460)
-   Fix toString() output for Whitelist class - [#2459](https://github.com/detekt/detekt/pull/2459)
-   Remove unused functions in XmlExtensions.kt - [#2458](https://github.com/detekt/detekt/pull/2458)
-   Add test cases for invalid yaml config - [#2457](https://github.com/detekt/detekt/pull/2457)
-   Add edge test cases for ComplexityReportGenerator - [#2456](https://github.com/detekt/detekt/pull/2456)
-   Simplify anonymous detektion object creation - [#2455](https://github.com/detekt/detekt/pull/2455)
-   Update trim logic in Debt:toString() - [#2453](https://github.com/detekt/detekt/pull/2453)
-   Refactor FindingsReport + FileBasedFindingsReport - [#2451](https://github.com/detekt/detekt/pull/2451)
-   Rename tests with \_Test prefix to \_Spec - [#2450](https://github.com/detekt/detekt/pull/2450)
-   Refactor HtmlOutputReport - [#2449](https://github.com/detekt/detekt/pull/2449)
-   Simplify UnnecessaryApplySpec test cases - [#2448](https://github.com/detekt/detekt/pull/2448)
-   Link code samples package for extension guide - [#2447](https://github.com/detekt/detekt/pull/2447)
-   ComplexInterface - should it also count private methods? - [#2446](https://github.com/detekt/detekt/issues/2446)
-   Update AppVeyor branch conf to comply with Travis - [#2445](https://github.com/detekt/detekt/pull/2445)
-   Add PsiViewer plugin to Contributing guide - [#2444](https://github.com/detekt/detekt/pull/2444)
-   UnnecessaryApply: fix false positive when it's used as an expression - [#2442](https://github.com/detekt/detekt/pull/2442)
-   Add detekt-hint to integrations - [#2441](https://github.com/detekt/detekt/pull/2441)
-   SwallowedException: fix false negative when using variables - [#2436](https://github.com/detekt/detekt/pull/2436)
-   UnnecessaryApply false positive cases - [#2435](https://github.com/detekt/detekt/issues/2435)
-   Prepare 1.7.0-beta2 release - [#2434](https://github.com/detekt/detekt/pull/2434)
-   Parallel invocation of DetektFacade fails spuriously - [#2433](https://github.com/detekt/detekt/issues/2433)
-   Deprecate BaseRule and state that it will be make sealed - #2365 - [#2432](https://github.com/detekt/detekt/pull/2432)
-   Add License rule - [#2429](https://github.com/detekt/detekt/pull/2429)
-   Skip all guard clauses at function start for ReturnCount - Closes #2342 - [#2428](https://github.com/detekt/detekt/pull/2428)
-   Fail if `--baseline` file not found - [#2427](https://github.com/detekt/detekt/pull/2427)
-   Add tests for YamlConfig - [#2426](https://github.com/detekt/detekt/pull/2426)
-   Choose a minimum codecov patch coverage ratio - [#2425](https://github.com/detekt/detekt/pull/2425)
-   Include ruleset-suppression feature doc - [#2424](https://github.com/detekt/detekt/pull/2424)
-   Add Groovy DSL example for jvmTarget to homepage - [#2423](https://github.com/detekt/detekt/pull/2423)
-   Add groovy dsl example for jvmTarget - Closes #2408 - [#2422](https://github.com/detekt/detekt/pull/2422)
-   Deprecate hierarchical config - [#2421](https://github.com/detekt/detekt/pull/2421)
-   Deprecate implementations of Config - [#2420](https://github.com/detekt/detekt/pull/2420)
-   Remove YAML Extension Requirement - [#2419](https://github.com/detekt/detekt/pull/2419)
-   Update Spek version to 2.0.10 - [#2418](https://github.com/detekt/detekt/pull/2418)
-   file does not end with .yml! - [#2417](https://github.com/detekt/detekt/issues/2417)
-   Disable autoCorrect property for all rules if global flag is set to false - [#2413](https://github.com/detekt/detekt/pull/2413)
-   Release 1.7.0 beta - [#2409](https://github.com/detekt/detekt/pull/2409)
-   Stop using kotlin-reflect - [#2405](https://github.com/detekt/detekt/pull/2405)
-   Stop compiling with experimental flag - [#2404](https://github.com/detekt/detekt/pull/2404)
-   Only post a code coverage patch status to PRs - [#2402](https://github.com/detekt/detekt/pull/2402)
-   ExplicitCollectionElementAccessMethod: Don't report on nullable collection - [#2401](https://github.com/detekt/detekt/pull/2401)
-   Discourage new Case files - [#2399](https://github.com/detekt/detekt/pull/2399)
-   Use argsfiles for CI - [#2397](https://github.com/detekt/detekt/pull/2397)
-   Update to Kotlin v1.3.70 - [#2396](https://github.com/detekt/detekt/pull/2396)
-   Fix typo in VersionPrinter test - [#2395](https://github.com/detekt/detekt/pull/2395)
-   Add documentation for cli --version flag - [#2392](https://github.com/detekt/detekt/pull/2392)
-   ExplicitCollectionElementAccessMethod: Do not report map?.get("foo") - [#2391](https://github.com/detekt/detekt/issues/2391)
-   Update Gradle to 6.2.1 - [#2390](https://github.com/detekt/detekt/pull/2390)
-   Do not report conditional elvis continue statements - Closes#2388 - [#2389](https://github.com/detekt/detekt/pull/2389)
-   False positive: UnconditionalJumpStatementInLoop with elvis operator ?: - [#2388](https://github.com/detekt/detekt/issues/2388)
-   Refactor getting the detekt version for readability - [#2387](https://github.com/detekt/detekt/pull/2387)
-   Create less objects and sets when creating findings id map - [#2385](https://github.com/detekt/detekt/pull/2385)
-   Add --version to cli - [#2383](https://github.com/detekt/detekt/pull/2383)
-   Add `--version` CLI option - [#2382](https://github.com/detekt/detekt/issues/2382)
-   Add test cases for cli/runners package - [#2381](https://github.com/detekt/detekt/pull/2381)
-   Sort and run correctable rules first - #2341 - [#2378](https://github.com/detekt/detekt/pull/2378)
-   Should fail if `--baseline` file does not found? - [#2374](https://github.com/detekt/detekt/issues/2374)
-   Deprecate rule set methods which expose implementation details of detekt-core - [#2366](https://github.com/detekt/detekt/pull/2366)
-   Deprecate api's exposing detekt-core implementation details - [#2365](https://github.com/detekt/detekt/issues/2365)
-   ReturnCount excludeGuardClauses not working - [#2342](https://github.com/detekt/detekt/issues/2342)
-   Mixing autocorrectable and non correctable rules results in obsolete issue locations for reports - [#2341](https://github.com/detekt/detekt/issues/2341)
-   Allow Detekt CLI to take an args file. - [#2318](https://github.com/detekt/detekt/issues/2318)
-   Homepage doesn't show Jekyll tags correctly - [#2309](https://github.com/detekt/detekt/issues/2309)
-   Call detekt reflectively - [#2282](https://github.com/detekt/detekt/pull/2282)
-   Copyright header rule for Kotlin files - #1515 - [#2077](https://github.com/detekt/detekt/pull/2077)
-   SwallowedException false negative - [#2049](https://github.com/detekt/detekt/issues/2049)
-   Invalid boolean config values are evaluated silently to false - [#2045](https://github.com/detekt/detekt/issues/2045)
-   Detekt Gradle Plugin is much slower than the jar - [#2035](https://github.com/detekt/detekt/issues/2035)
-   Cases files are super rigorous to work with - [#1089](https://github.com/detekt/detekt/issues/1089)

See all issues at: [1.7.0](https://github.com/detekt/detekt/milestone/59)

#### 1.6.0

##### Migration

Rule set authors do not need to exclude their rule set from config validation anymore.
This will be done by default now.

If you need to include further config property checks, give the new `ConfigValidator` api a try.

##### Changelog

-   Check-in ConfigValidator documentation - [#2368](https://github.com/detekt/detekt/pull/2368)
-   Remove obsolete PathFilter class - [#2367](https://github.com/detekt/detekt/pull/2367)
-   Reference coroutines doc for homepage - [#2363](https://github.com/detekt/detekt/pull/2363)
-   [Documentation Request] Coroutine Ruleset - [#2362](https://github.com/detekt/detekt/issues/2362)
-   Extend AnnotationExcluder to catch fully qualified annotations - [#2361](https://github.com/detekt/detekt/pull/2361)
-   UnnecessaryAbstractClass excludeAnnotatedClasses not working - [#2360](https://github.com/detekt/detekt/issues/2360)
-   Replace LinkedList with ArrayDeque - [#2358](https://github.com/detekt/detekt/pull/2358)
-   Fix detection of CRLF line endings - [#2357](https://github.com/detekt/detekt/pull/2357)
-   Introduce new ConfigValidator extensions - #2285 - [#2356](https://github.com/detekt/detekt/pull/2356)
-   Include options to better meet requirements for utility files - [#2355](https://github.com/detekt/detekt/pull/2355)
-   Don't normalize file content twice - [#2354](https://github.com/detekt/detekt/pull/2354)
-   Remove unused code in rules/Case.kt - [#2351](https://github.com/detekt/detekt/pull/2351)
-   Add blog post about speeding up a detekt task - [#2349](https://github.com/detekt/detekt/pull/2349)
-   Add tests for setting KtLint's config - [#2348](https://github.com/detekt/detekt/pull/2348)
-   add details into internal validation - [#2347](https://github.com/detekt/detekt/pull/2347)
-   Minor change in the HTML report: Remove extra space when you copy the file path - [#2344](https://github.com/detekt/detekt/pull/2344)
-   detekt does not always inject the required EDITOR_CONFIG_USER_DATA_KEY for ktlint - [#2339](https://github.com/detekt/detekt/issues/2339)
-   Remove duplicate tests for TooManyFunctions rule - [#2338](https://github.com/detekt/detekt/pull/2338)
-   Add EmptyTryBlock rule - [#2337](https://github.com/detekt/detekt/pull/2337)
-   Use requireNotNull for arguments checking - [#2336](https://github.com/detekt/detekt/pull/2336)
-   Refactor and add tests to Ast- and ElementPrinter - [#2335](https://github.com/detekt/detekt/pull/2335)
-   Add test case for disabled reports - [#2334](https://github.com/detekt/detekt/pull/2334)
-   Refactor ReportPath:load() - [#2333](https://github.com/detekt/detekt/pull/2333)
-   Add test case for FileProcessorLocator - [#2332](https://github.com/detekt/detekt/pull/2332)
-   Add test cases for Kt(Tree)Compiler - [#2331](https://github.com/detekt/detekt/pull/2331)
-   Add equality test cases for PathFiler - [#2330](https://github.com/detekt/detekt/pull/2330)
-   Add local function test case for MethodOverloading rule - [#2328](https://github.com/detekt/detekt/pull/2328)
-   Add anonymous object expression test case for MethodOverloading rule - [#2327](https://github.com/detekt/detekt/pull/2327)
-   Validate Gradle Wrapper - [#2326](https://github.com/detekt/detekt/pull/2326)
-   Use more consistent metrics in HtmlReport - [#2325](https://github.com/detekt/detekt/pull/2325)
-   Fix ClassCastException in ExplicitCollectionElementAccessMethod - [#2323](https://github.com/detekt/detekt/pull/2323)
-   ExplicitCollectionElementAccessMethod crashes - [#2322](https://github.com/detekt/detekt/issues/2322)
-   use same behaviour for valueOrNull as for valueOrDefault - [#2319](https://github.com/detekt/detekt/pull/2319)
-   FailFastConfig.valueOrNull should return specified value for active and maxIssues - [#2316](https://github.com/detekt/detekt/issues/2316)
-   Bugfix: MagicNumber with ignoreNamedArgument and a negative value - [#2315](https://github.com/detekt/detekt/pull/2315)
-   More consistent reports - [#2291](https://github.com/detekt/detekt/issues/2291)
-   Change MatchingDeclarationName to handle utility files - [#1500](https://github.com/detekt/detekt/issues/1500)
-   False-positive MagicNumber issue reported when using negative numbers - [#530](https://github.com/detekt/detekt/issues/530)

See all issues at: [1.6.0](https://github.com/detekt/detekt/milestone/57)

#### 1.5.1

-   Update intro page to deprecate input property - [#2311](https://github.com/detekt/detekt/pull/2311)
-   Update codecov run settings to after_n_builds=4 - [#2305](https://github.com/detekt/detekt/pull/2305)
-   Update GroovyDSL doc to deprecate input property - [#2304](https://github.com/detekt/detekt/pull/2304)
-   Update KotlinDSL doc to deprecate input property - [#2303](https://github.com/detekt/detekt/pull/2303)
-   Fix ExplicitCollectionElementAccessMethod crash - [#2302](https://github.com/detekt/detekt/pull/2302)
-   ExplicitCollectionElementAccessMethod crashes - [#2301](https://github.com/detekt/detekt/issues/2301)

See all issues at: [1.5.1](https://github.com/detekt/detekt/milestone/58)

#### 1.5.0

##### Notable Changes

-   detekt is now _silent_ by default. It only prints something if issues are found.
    Remove the following excludes if you want the old behavior back.

```yaml
console-reports:
    active: true
    exclude:
        - "ProjectStatisticsReport"
        - "ComplexityReport"
        - "NotificationReport"
```

-   detekt now fails the build if any issue is found. Change the `maxIssues` property to 10 for the old threshold.

```yaml
build:
    maxIssues: 0
```

-   The `HTML` report now prints the issue count per rule and rule set.
-   New rules: `ExplicitCollectionElementAccessMethod` and `ForbiddenMethod`

##### Changelog

-   add new mention to README.md - [#2293](https://github.com/detekt/detekt/pull/2293)
-   Sort html report - [#2290](https://github.com/detekt/detekt/pull/2290)
-   Number format in some report - [#2289](https://github.com/detekt/detekt/pull/2289)
-   Show the finding count in the html report - [#2288](https://github.com/detekt/detekt/pull/2288)
-   Keep the order of the issues in the html report - [#2287](https://github.com/detekt/detekt/issues/2287)
-   Show issue count in the html report - [#2286](https://github.com/detekt/detekt/issues/2286)
-   Fixing the Documentation not properly calling the superclass - [#2284](https://github.com/detekt/detekt/pull/2284)
-   Do you have to call super in custom rules? - [#2283](https://github.com/detekt/detekt/issues/2283)
-   Measure performance of various stages when using --debug - [#2281](https://github.com/detekt/detekt/pull/2281)
-   Remove printing the whole config for --debug - [#2280](https://github.com/detekt/detekt/pull/2280)
-   Introduce DefaultRuleSetProvider interface marking detekt-rules providers as default - [#2279](https://github.com/detekt/detekt/pull/2279)
-   Simplify test dependency setup in build files - [#2278](https://github.com/detekt/detekt/pull/2278)
-   Fix class loader memory leaks when loading services - [#2277](https://github.com/detekt/detekt/pull/2277)
-   Always dispose Kotlin environment fixing memory leak in error cases - [#2276](https://github.com/detekt/detekt/pull/2276)
-   Sanitize gradle build scripts and suppress unstable api usages - [#2271](https://github.com/detekt/detekt/pull/2271)
-   Update website ruby dependencies fixing potential security vulnerability - [#2270](https://github.com/detekt/detekt/pull/2270)
-   Fix regression not considering baseline file when calculating build failure threshold - [#2269](https://github.com/detekt/detekt/pull/2269)
-   Turn detekt silent by default - [#2268](https://github.com/detekt/detekt/pull/2268)
-   Remove redundant build failure message - #2264 - [#2266](https://github.com/detekt/detekt/pull/2266)
-   Build failed with... is printed twice in the cli - [#2264](https://github.com/detekt/detekt/issues/2264)
-   Update config:maxIssues value to 0 - [#2263](https://github.com/detekt/detekt/pull/2263)
-   Don't flag inherited visibility in NestedClassesVisibility - [#2261](https://github.com/detekt/detekt/pull/2261)
-   Simplify argument parsing logic, remove any exitProcess() calls from buildRunner - [#2260](https://github.com/detekt/detekt/pull/2260)
-   Modify default behavior to not output unless errors are found. Adding a verbose flag which will have legacy behavior - [#2258](https://github.com/detekt/detekt/pull/2258)
-   Test some edge cases in detekt-api - [#2256](https://github.com/detekt/detekt/pull/2256)
-   Add a new line at the end of the txt report - [#2255](https://github.com/detekt/detekt/pull/2255)
-   Implement ExplicitCollectionElementAccessMethod rule - [#2215](https://github.com/detekt/detekt/pull/2215)
-   ForbiddenMethod Rule - [#1954](https://github.com/detekt/detekt/pull/1954)
-   NestedClassesVisibility(False negative): Nested class doesn't inherit visibility from parent - [#1930](https://github.com/detekt/detekt/issues/1930)

See all issues at: [1.5.0](https://github.com/detekt/detekt/milestone/56)

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

-   EnumEntryNameCase
-   NoEmptyFirstLineInMethodBlock

##### Migration

The properties `ignoreOverriddenFunction` and `ignoreOverriddenFunctions` of some rules got deprecated and unified to a new property `ignoreOverridden`.

##### Changelog

-   Refactor BuildFailure code - [#2250](https://github.com/detekt/detekt/pull/2250)
-   Fix nested methods bug in MethodOverloading rule - [#2249](https://github.com/detekt/detekt/pull/2249)
-   ThrowingExceptionInMain rule should consider main() function with no parameters - [#2248](https://github.com/detekt/detekt/issues/2248)
-   MethodOverloading bug with nested overloaded methods - [#2247](https://github.com/detekt/detekt/issues/2247)
-   Reduce complexity in FindingsReport - [#2246](https://github.com/detekt/detekt/pull/2246)
-   Add RedundantSuspendModifier rule - [#2244](https://github.com/detekt/detekt/pull/2244)
-   New ktlint rules - [#2243](https://github.com/detekt/detekt/pull/2243)
-   Inline MethodOverloading case file - [#2241](https://github.com/detekt/detekt/pull/2241)
-   ThrowingExceptionInMain: fix #2248 and add tests - [#2240](https://github.com/detekt/detekt/pull/2240)
-   Add disposing Kotlin environment. - [#2238](https://github.com/detekt/detekt/pull/2238)
-   OOM on multiple invocations - [#2237](https://github.com/detekt/detekt/issues/2237)
-   Improve doc for UnusedPrivateMember - [#2236](https://github.com/detekt/detekt/pull/2236)
-   Don't resolve dependencies during project configuration - [#2235](https://github.com/detekt/detekt/pull/2235)
-   Revert "Introduce Pull Request Labeler" - [#2234](https://github.com/detekt/detekt/pull/2234)
-   Fix #2230 equals() func detection - [#2233](https://github.com/detekt/detekt/pull/2233)
-   Fix git commit-hook label and code snippet doc - [#2232](https://github.com/detekt/detekt/pull/2232)
-   WrongEqualsTypeParameter does not ignore multi-parameter equals methods - [#2230](https://github.com/detekt/detekt/issues/2230)
-   Introduce Pull Request Labeler - [#2228](https://github.com/detekt/detekt/pull/2228)
-   Gradle plugin: Build upon default detekt config - [#2227](https://github.com/detekt/detekt/pull/2227)
-   Apply ktlint formatting plugin to Gradle plugin - [#2226](https://github.com/detekt/detekt/pull/2226)
-   Bump dependencies - [#2225](https://github.com/detekt/detekt/pull/2225)
-   Run shadowJar & installShadowDist task with everything else - [#2220](https://github.com/detekt/detekt/pull/2220)
-   Travis: Use consistent Java vendor - [#2219](https://github.com/detekt/detekt/pull/2219)
-   "Property is misspelled or does not exist" error for new rules in default rulesets - [#2217](https://github.com/detekt/detekt/issues/2217)
-   MethodOverloading false positive if every entry of an enum implement a method. - [#2216](https://github.com/detekt/detekt/issues/2216)
-   Add Git detekt pre-commit hook doc - [#2214](https://github.com/detekt/detekt/pull/2214)
-   Remove exclude workaround for new build property - [#2203](https://github.com/detekt/detekt/pull/2203)
-   Add GlobalCoroutineUsage rule + coroutines ruleset - [#2174](https://github.com/detekt/detekt/pull/2174)
-   Add rule [RedundantSuspend] to detect redundant suspend modifiers - [#2156](https://github.com/detekt/detekt/issues/2156)
-   Deprecate ignoreOverriddenFunction/s in favor of ignoreOverridden - [#2132](https://github.com/detekt/detekt/pull/2132)

See all issues at: [1.4.0](https://github.com/detekt/detekt/milestone/55)

#### 1.3.1

-   Remove old unused documentation - [#2210](https://github.com/detekt/detekt/pull/2210)
-   Show code snippet erros in html reports - [#2209](https://github.com/detekt/detekt/pull/2209)
-   Use compileAndLint in UnusedPrivateClassSpec - [#2208](https://github.com/detekt/detekt/pull/2208)
-   Fix false positive in UnusedPrivateClass - [#2207](https://github.com/detekt/detekt/pull/2207)
-   Update readme promoting new properties of the gradle plugin - [#2205](https://github.com/detekt/detekt/pull/2205)
-   Rename default const containing _ACCEPTED_ - [#2204](https://github.com/detekt/detekt/pull/2204)
-   Mistake From LongParameterList.kt - [#2202](https://github.com/detekt/detekt/issues/2202)
-   Exclude yet unknown new build property - [#2201](https://github.com/detekt/detekt/pull/2201)
-   Add comment regarding type resolution to README - [#2199](https://github.com/detekt/detekt/pull/2199)
-   Type resolution doc - [#2198](https://github.com/detekt/detekt/pull/2198)
-   Correct indentation for Groovy DSL doc - [#2197](https://github.com/detekt/detekt/pull/2197)
-   Use shorthand syntax for assertThat() - [#2196](https://github.com/detekt/detekt/pull/2196)
-   Refactor MagicNumber to use commaSeparatedPatterns - [#2195](https://github.com/detekt/detekt/pull/2195)
-   Attach FILE_PATH_USER_DATA_KEY user data on FormattingRules (#1907) - [#2194](https://github.com/detekt/detekt/pull/2194)
-   Handle invalid config exit code in gradle plugin - [#2193](https://github.com/detekt/detekt/pull/2193)
-   Add tests showing how to exclude custom config properties in plugins - [#2192](https://github.com/detekt/detekt/pull/2192)
-   Fix suppression of KtLint rules on file level - #2179 - [#2191](https://github.com/detekt/detekt/pull/2191)
-   Mention needed kotlinx.html library from jcenter - #2146 - [#2190](https://github.com/detekt/detekt/pull/2190)
-   UnusedPrivateClass has false positive behavior for deserialized items - [#2158](https://github.com/detekt/detekt/issues/2158)
-   Use JDK 11 (LTS) + 13 for AppVeyor builds - [#2141](https://github.com/detekt/detekt/pull/2141)
-   Document how to create a common baseline file for multi module gradle projects - [#2140](https://github.com/detekt/detekt/pull/2140)
-   DetektAll with baseline fails with error - [#2100](https://github.com/detekt/detekt/issues/2100)
-   ForbiddenMethod Rule - [#1954](https://github.com/detekt/detekt/pull/1954)
-   Do not report auto-corrected formatting issues - [#1840](https://github.com/detekt/detekt/pull/1840)

See all issues at: [1.3.1](https://github.com/detekt/detekt/milestone/54)

#### 1.3.0

##### Notable changes

-   Minimal Gradle version increased to 5.0
-   New rules:
    -   `UnnecessaryAnnotationUseSiteTargetRule`
    -   `MapGetWithNotNullAssertionOperator`

##### Changelog

-   Add printers to the command line runner - [#2188](https://github.com/detekt/detekt/pull/2188)
-   Fix documentation of UseArrayLiteralsInAnnotations - [#2186](https://github.com/detekt/detekt/pull/2186)
-   Inline resolving severity label for XML reports - [#2184](https://github.com/detekt/detekt/pull/2184)
-   Extract common jvm target value, add jvmTarget into documentation. Cl… - [#2183](https://github.com/detekt/detekt/pull/2183)
-   Fix Detekt Gradle task cache restoration issue (#2180) - [#2182](https://github.com/detekt/detekt/pull/2182)
-   Fix exception when running ArrayPrimitive on star-projected arrays - [#2181](https://github.com/detekt/detekt/pull/2181)
-   Gradle cache restoration issue - [#2180](https://github.com/detekt/detekt/issues/2180)
-   Add MacOS JDK13 build job to TravisCI - [#2177](https://github.com/detekt/detekt/pull/2177)
-   Running `ArrayPrimitive` rule on `Array` causes detekt to throw exception - [#2176](https://github.com/detekt/detekt/issues/2176)
-   Update Spek to v2.0.9 - [#2173](https://github.com/detekt/detekt/pull/2173)
-   Create Rule: MapGetWithNotNullAssertionOperator - [#2171](https://github.com/detekt/detekt/pull/2171)
-   EqualsAlwaysReturnsTrueOrFalse fails hard on `override fun equals(other:Any) = ...` - [#2167](https://github.com/detekt/detekt/issues/2167)
-   Prepare 1.3.0 release - [#2165](https://github.com/detekt/detekt/pull/2165)
-   UnsafeCast: update documentation to match new behavior - [#2164](https://github.com/detekt/detekt/pull/2164)
-   Add jvmTarget change into documentation - [#2157](https://github.com/detekt/detekt/issues/2157)
-   Create UnnecessaryAnnotationUseSiteTargetRule - [#2099](https://github.com/detekt/detekt/pull/2099)
-   Gradle 6 - [#1902](https://github.com/detekt/detekt/pull/1902)
-   Gradle 6 planning - [#1820](https://github.com/detekt/detekt/issues/1820)

See all issues at: [1.3.0](https://github.com/detekt/detekt/milestone/51)

#### 1.2.2

##### Notable Changes

-   1.2.1 introduced breaking changes for users of Gradle < 5. This was reverted.
-   1.3.0 will drop support for Gradle < 5.
-   Fixed a regression in the html report.

##### Changelog

-   regression updating 1.2.0 to 1.2.1, unknown property 'filters' for object of type DetektExtension - [#2163](https://github.com/detekt/detekt/issues/2163)
-   StringIndexOutOfBoundsException when generating HTML report - [#2160](https://github.com/detekt/detekt/pull/2160)
-   Restore KotlinExtension - [#2159](https://github.com/detekt/detekt/pull/2159)
-   1.2.1 breaks the build with: unresolved reference: detekt - [#2152](https://github.com/detekt/detekt/issues/2152)
-   Updated to correct classpath documentation for Android projects. - [#2149](https://github.com/detekt/detekt/pull/2149)
-   Update to Kotlin v1.3.61 - [#2147](https://github.com/detekt/detekt/pull/2147)
-   Document how to exclude detekt from the check task - #1894 - [#2144](https://github.com/detekt/detekt/pull/2144)
-   Use JDK 11 (LTS) + 13 for Travis builds - [#2142](https://github.com/detekt/detekt/pull/2142)
-   Don't compile test snippets (bindingContext) - [#2137](https://github.com/detekt/detekt/pull/2137)
-   StringIndexOutOfBoundsException: String index out of range: 8 when generating HTML report after update to 1.2.0 - [#2134](https://github.com/detekt/detekt/issues/2134)

See all issues at: [1.2.2](https://github.com/detekt/detekt/milestone/53)

#### 1.2.1

##### Notable changes

-   Supports Kotlin 1.3.60
-   Fixed a regression in configuration validation logic when using `build>weights>[RuleSet|Rule]` properties.
-   Some rules got improvements (see changelog)

##### Changelog

-   Exception analyzing file - [#2139](https://github.com/detekt/detekt/issues/2139)
-   Simplify ConstructorParameterNaming:ignoreOverridden check - [#2136](https://github.com/detekt/detekt/pull/2136)
-   Test common config sections pass through config validation - [#2135](https://github.com/detekt/detekt/pull/2135)
-   Kotlin 1.3.60 with fix for "Unable to load JNA library" warning - [#2129](https://github.com/detekt/detekt/pull/2129)
-   Unexpected nested config for 'build>weights'. - [#2128](https://github.com/detekt/detekt/issues/2128)
-   Remove redundant Javadoc - [#2127](https://github.com/detekt/detekt/pull/2127)
-   Lazy init KotlinScriptEnginePool - [#2126](https://github.com/detekt/detekt/pull/2126)
-   Add tests for MagicNumber:ignoreLocalVariableDeclaration - [#2125](https://github.com/detekt/detekt/pull/2125)
-   Fix NPE for EqualsAlwaysReturnsTrueOrFalse - [#2124](https://github.com/detekt/detekt/pull/2124)
-   Add MagicNumber:ignoreLocalVariableDeclaration config - [#2123](https://github.com/detekt/detekt/pull/2123)
-   Fix MagicNumber:ignoreConstantDeclaration doc - [#2116](https://github.com/detekt/detekt/pull/2116)
-   Return non-nullable string in DebtSumming class - [#2113](https://github.com/detekt/detekt/pull/2113)
-   Refactor TrailingWhitespace test - [#2112](https://github.com/detekt/detekt/pull/2112)
-   Use inline code snippets instead of case files - [#2111](https://github.com/detekt/detekt/pull/2111)
-   UnusedImports: False positive if referenced in @throws/@exception/@sample - [#2106](https://github.com/detekt/detekt/pull/2106)
-   Don't compile test snippets - [#2105](https://github.com/detekt/detekt/pull/2105)
-   MemberNameEqualsClassName should ignore overridden property names too - [#2104](https://github.com/detekt/detekt/pull/2104)
-   EqualsAlwaysReturnsTrueOrFalse crashes on certain input - [#2103](https://github.com/detekt/detekt/issues/2103)
-   UnusedImports: False positive if referenced only in @throws/@exception clause - [#2098](https://github.com/detekt/detekt/issues/2098)
-   Add config flag ignoreOverridden to ConstructorParameterNaming - [#2097](https://github.com/detekt/detekt/pull/2097)
-   compileAndLint is 2.5 times slower than lint - [#2095](https://github.com/detekt/detekt/issues/2095)
-   Improve naming tests - [#2094](https://github.com/detekt/detekt/pull/2094)

See all issues at: [1.2.1](https://github.com/detekt/detekt/milestone/52)

#### 1.2.0

##### Notable changes

-   The HTML report now includes the complexity metrics and previews of the code locations.
-   Suppression by rule set id is now supported `@Suppress("detekt.[RuleSetId].[RuleId]")`
-   `parallel=true` and `--parallel` now effect both the compilation and analysis phase.
-   Users of Gradle's `--parallel` flag are encouraged to turn off the parallelism of detekt. Or turn it on otherwise.
-   detekt internally does not use the `ForkJoinPool.commonPool()` anymore. When embedding detekt feel free to pass your own `ExecutionService` to the `ProcessingSettings`.

##### Migration

-   Some reported positions of rules have changed, see issues starting with "Improve text location: ".
    This may lead to some unexpected changes in the baseline file.
-   The rule `ComplexMethod` got refactored and counts the cyclomatic complexity according to https://www.ndepend.com/docs/code-metrics#CC.
    This change lead to increasing the threshold to 15 (was ten).

##### Changelog

-   Update to Kotlin v1.3.60 - [#2109](https://github.com/detekt/detekt/pull/2109)
-   UnusedPrivateClass: Fix false positive with private annotations - [#2108](https://github.com/detekt/detekt/pull/2108)
-   Refactor ComplexMethod - [#2090](https://github.com/detekt/detekt/pull/2090)
-   Fix NestedBlockDepth false negative - [#2086](https://github.com/detekt/detekt/pull/2086)
-   NestedBlockDepth false negative - [#2085](https://github.com/detekt/detekt/issues/2085)
-   Deprecate Location.locationString - [#2084](https://github.com/detekt/detekt/pull/2084)
-   Add license badge to README - [#2080](https://github.com/detekt/detekt/pull/2080)
-   Deploy SNAPSHOTs automatically - [#2079](https://github.com/detekt/detekt/pull/2079)
-   Fix TrailingWhitespace reported position - [#2078](https://github.com/detekt/detekt/pull/2078)
-   Activate more rules by default - #1911 - [#2075](https://github.com/detekt/detekt/pull/2075)
-   Report InvalidRange for empty until range - [#2074](https://github.com/detekt/detekt/pull/2074)
-   Deprecate properties on Entity - Closes #2014 - [#2072](https://github.com/detekt/detekt/pull/2072)
-   Add complexity report to html output - [#2071](https://github.com/detekt/detekt/pull/2071)
-   Use constants for config keys in tests - [#2070](https://github.com/detekt/detekt/pull/2070)
-   Mention location adjustment in rules for 1.2.0 - [#2068](https://github.com/detekt/detekt/issues/2068)
-   Improve text location: TooManyFunctions - [#2065](https://github.com/detekt/detekt/pull/2065)
-   Improve text location: OptionalAbstractKeyword - [#2064](https://github.com/detekt/detekt/pull/2064)
-   Improve text location: NestedBlockDepth - [#2063](https://github.com/detekt/detekt/pull/2063)
-   Improve text location: MatchingDeclarationName - [#2062](https://github.com/detekt/detekt/pull/2062)
-   Improve text location: LongMethod - [#2061](https://github.com/detekt/detekt/pull/2061)
-   Improve text location: LargeClass - [#2060](https://github.com/detekt/detekt/pull/2060)
-   Improve text location: ComplexMethod - [#2059](https://github.com/detekt/detekt/pull/2059)
-   Improve text location: EmptyClassBlock - [#2058](https://github.com/detekt/detekt/pull/2058)
-   Replace spek test hasSize(0) with isEmpty() - [#2057](https://github.com/detekt/detekt/pull/2057)
-   Remove MacOS JDK11 CI run as discussed in #2015 - [#2056](https://github.com/detekt/detekt/pull/2056)
-   Introduces mocking library 'mockk' - [#2055](https://github.com/detekt/detekt/pull/2055)
-   Improve text location: InvalidPackageDeclaration - [#2052](https://github.com/detekt/detekt/pull/2052)
-   Improve text location: MandatoryBracesIfStatements - [#2051](https://github.com/detekt/detekt/pull/2051)
-   Improve text location: ClassNaming - [#2050](https://github.com/detekt/detekt/pull/2050)
-   potential-bugs InvalidRange does not work in all cases - [#2044](https://github.com/detekt/detekt/issues/2044)
-   Don't checkBuildFailureThreshold if we are creating the baseline - [#2034](https://github.com/detekt/detekt/pull/2034)
-   gradle detektBaseline task fails - [#2033](https://github.com/detekt/detekt/issues/2033)
-   Fix #2021 - [#2032](https://github.com/detekt/detekt/pull/2032)
-   Update dependencies - [#2031](https://github.com/detekt/detekt/pull/2031)
-   Dokka fix - [#2030](https://github.com/detekt/detekt/pull/2030)
-   Simplify and refactor RuleProviderTest - [#2029](https://github.com/detekt/detekt/pull/2029)
-   Simplify MultiRuleCollector test cases - [#2028](https://github.com/detekt/detekt/pull/2028)
-   Dont check WrongEqualsTypeParameter if the function is topLevel - [#2027](https://github.com/detekt/detekt/pull/2027)
-   Fix false positive at EmptyIfBlock - [#2026](https://github.com/detekt/detekt/pull/2026)
-   Support guard clause exclusion for ThrowsCount rule - [#2025](https://github.com/detekt/detekt/pull/2025)
-   Add ImplicitDefaultLocale rule - [#2024](https://github.com/detekt/detekt/pull/2024)
-   Use double backtick for the in-line code - [#2022](https://github.com/detekt/detekt/pull/2022)
-   EqualsAlwaysReturnsTrueOrFalse: The original exception message was: java.util.NoSuchElementException: Array is empty. - [#2021](https://github.com/detekt/detekt/issues/2021)
-   Ignore sealed classes for utility class having public constructor rule - [#2016](https://github.com/detekt/detekt/pull/2016)
-   Better handling for the Suppresion of errors - [#2013](https://github.com/detekt/detekt/pull/2013)
-   Fix description of NoLineBreakBeforeAssignment - [#2011](https://github.com/detekt/detekt/pull/2011)
-   Copy paste error in message in NoLineBreakBeforeAssignment.kt - [#2008](https://github.com/detekt/detekt/issues/2008)
-   UtilityClassWithPublicConstructor should not be reported for sealed classes - [#2005](https://github.com/detekt/detekt/issues/2005)
-   Validate yaml configurations by comparing their structure - #516 - [#1998](https://github.com/detekt/detekt/pull/1998)
-   Allow the user to collapse the rules in the html report - [#1997](https://github.com/detekt/detekt/pull/1997)
-   Allow detekt findings to be suppessed with rule set id - Closes #766 - [#1994](https://github.com/detekt/detekt/pull/1994)
-   Upgrade Spek to v2.0.8 - [#1992](https://github.com/detekt/detekt/pull/1992)
-   Reimplement parallelism internal logic - [#1991](https://github.com/detekt/detekt/pull/1991)
-   Findings assertions - [#1978](https://github.com/detekt/detekt/pull/1978)
-   Fix EnumNaming textLocation - [#1977](https://github.com/detekt/detekt/pull/1977)
-   Add snippet code in html report - [#1975](https://github.com/detekt/detekt/pull/1975)
-   Change reported element in 2 documentation rules - [#1952](https://github.com/detekt/detekt/pull/1952)
-   Enable more rules in failfast - [#1935](https://github.com/detekt/detekt/pull/1935)
-   Add UndocumentedPublicProperty rule - closes #1670 - [#1923](https://github.com/detekt/detekt/pull/1923)
-   Calculate MCC (McCabe Complexity) accordingly - [#1921](https://github.com/detekt/detekt/issues/1921)
-   UseDataClass conflicts with DataClassShouldBeImmutable - [#1920](https://github.com/detekt/detekt/issues/1920)
-   Redesign "parallel" flag/property - [#1845](https://github.com/detekt/detekt/issues/1845)
-   SNAPSHOT process feedback - [#1826](https://github.com/detekt/detekt/issues/1826)
-   Initial MCC change - [#1793](https://github.com/detekt/detekt/pull/1793)
-   @Suppress("Detekt.ruleset") feature - [#766](https://github.com/detekt/detekt/issues/766)
-   Validate Configuration file before using it - [#516](https://github.com/detekt/detekt/issues/516)

See all issues at: [1.2.0](https://github.com/detekt/detekt/milestone/49)

#### 1.1.1

##### Changelog

-   Improved test case for resolved #1971 (TrailingWhitespace and multiline string) - [#2003](https://github.com/detekt/detekt/pull/2003)
-   Set defaults for boolean property when writing custom detekt tasks - [#1996](https://github.com/detekt/detekt/pull/1996)
-   Generate PluginVersion.kt with newline at end - [#1993](https://github.com/detekt/detekt/pull/1993)
-   Remove unnecessary logs from RuleProviderTest - [#1990](https://github.com/detekt/detekt/pull/1990)
-   Use inline code snippets instead of case files - [#1989](https://github.com/detekt/detekt/pull/1989)
-   Use config parameter for UseIfInsteadOfWhen rule - [#1987](https://github.com/detekt/detekt/pull/1987)
-   Use inline code snippets instead of case files - [#1976](https://github.com/detekt/detekt/pull/1976)
-   Don't flag trailing whitespaces in multiline strings - [#1971](https://github.com/detekt/detekt/pull/1971)

See all issues at: [1.1.1](https://github.com/detekt/detekt/milestone/50)

#### 1.1.0

##### Changelog

-   Clarify threshold pararameter meaning in docs - [#1974](https://github.com/detekt/detekt/pull/1974)
-   Introduce ignoreLabeled config for ReturnFromFinally - [#1973](https://github.com/detekt/detekt/pull/1973)
-   Ignore FunctionOnlyReturningConstant for allowed annotations - [#1968](https://github.com/detekt/detekt/pull/1968)
-   Allow regex configuration support for Forbidden Import rule - [#1963](https://github.com/detekt/detekt/pull/1963)
-   Refactor and simplify RuleCollectorSpec - [#1959](https://github.com/detekt/detekt/pull/1959)
-   Use inline code snippets instead of case files - [#1958](https://github.com/detekt/detekt/pull/1958)
-   Improve UnusedPrivateMember when it's related with parameters - [#1949](https://github.com/detekt/detekt/pull/1949)
-   Fix SpacingBetweenPackageAndImports issue for scripts without packages - [#1947](https://github.com/detekt/detekt/pull/1947)
-   Remove ConditionalPathVisitor - [#1944](https://github.com/detekt/detekt/pull/1944)
-   Extend UseDataClass with the allowVars configuration property - [#1942](https://github.com/detekt/detekt/pull/1942)
-   HasPlatformType rule - [#1938](https://github.com/detekt/detekt/pull/1938)
-   Bogus SpacingBetweenPackageAndImports violation for KTS without package - [#1937](https://github.com/detekt/detekt/issues/1937)
-   Gradle deprecations - [#1934](https://github.com/detekt/detekt/pull/1934)
-   UnnecessaryParentheses should be ignored for bit operations - [#1929](https://github.com/detekt/detekt/issues/1929)
-   Prepare 1.1.0 release - [#1919](https://github.com/detekt/detekt/pull/1919)
-   Deprecate debug in IDEA tasks - [#1918](https://github.com/detekt/detekt/pull/1918)
-   Refactoring: use more readable functions - [#1916](https://github.com/detekt/detekt/pull/1916)
-   Don't fail on master when test coverage decreases - [#1914](https://github.com/detekt/detekt/pull/1914)
-   Detect deprecations - [#1913](https://github.com/detekt/detekt/pull/1913)
-   Fix typos - [#1908](https://github.com/detekt/detekt/pull/1908)
-   Report PreferToOverPairSyntax only for kotlin.Pair - [#1905](https://github.com/detekt/detekt/pull/1905)
-   Fix TimeoutCancellationException - downgrade Spek - [#1903](https://github.com/detekt/detekt/pull/1903)
-   Update dependencies - [#1901](https://github.com/detekt/detekt/pull/1901)
-   Add RedundantExplicitType rule - [#1900](https://github.com/detekt/detekt/pull/1900)
-   Remove unused KtAnnotated util functions - [#1899](https://github.com/detekt/detekt/pull/1899)
-   Simplify detekt rules - [#1898](https://github.com/detekt/detekt/pull/1898)
-   Fix shared variable in detekt-extension rules - [#1897](https://github.com/detekt/detekt/pull/1897)
-   Make samples more idiomatic - [#1895](https://github.com/detekt/detekt/pull/1895)
-   Update detekt extensions doc - [#1893](https://github.com/detekt/detekt/pull/1893)
-   Clarify `WildcardImport` rule configuration - [#1888](https://github.com/detekt/detekt/pull/1888)
-   Add configuration to allow patterns for forbidden comment - [#1887](https://github.com/detekt/detekt/pull/1887)
-   Only report UnsafeCallOnNullableType on actual nullable types - [#1886](https://github.com/detekt/detekt/pull/1886)
-   Minimise usage of Kotlin reflection - [#1883](https://github.com/detekt/detekt/pull/1883)
-   Refactor KotlinCoreEnvironment test setup - [#1880](https://github.com/detekt/detekt/pull/1880)
-   Trust Kotlin compiler to identify unsafe casts - [#1879](https://github.com/detekt/detekt/pull/1879)
-   Replace tabs with spaces in rule KDoc - [#1876](https://github.com/detekt/detekt/pull/1876)
-   Make all Gradle tasks cacheable - [#1875](https://github.com/detekt/detekt/pull/1875)
-   Indentation for compliant and non-compliant code examples is missing - [#1871](https://github.com/detekt/detekt/issues/1871)
-   Don't build twice when PR created from branch - [#1866](https://github.com/detekt/detekt/pull/1866)
-   Print rendered report if it's null or blank - [#1862](https://github.com/detekt/detekt/pull/1862)
-   Silence report if containing no findings - [#1860](https://github.com/detekt/detekt/pull/1860)
-   Group console violation reports by file - [#1852](https://github.com/detekt/detekt/pull/1852)
-   Update Kotlin to v1.3.50 - [#1841](https://github.com/detekt/detekt/pull/1841)
-   Gradle 5.6 - [#1833](https://github.com/detekt/detekt/pull/1833)
-   Implement rule to suggest array literal instead of arrayOf-expression in annotations - [#1823](https://github.com/detekt/detekt/pull/1823)
-   Make UnsafeCast less aggressive - [#1601](https://github.com/detekt/detekt/issues/1601)
-   Consider publishing artifacts to mavenCentral instead of jCenter - [#1396](https://github.com/detekt/detekt/issues/1396)
-   false positive unnecessary parentheses in conjunction with lambdas - [#1222](https://github.com/detekt/detekt/issues/1222)
-   False positives on UnsafeCast on AppVeyor (Windows?) only - [#1137](https://github.com/detekt/detekt/issues/1137)
-   PreferToOverPairSyntax false positive - [#1066](https://github.com/detekt/detekt/issues/1066)
-   Rule ForbiddenComment - regexp support - [#959](https://github.com/detekt/detekt/issues/959)
-   UnsafeCallOnNullableType should not be reported for platform types - [#880](https://github.com/detekt/detekt/issues/880)
-   Exclusion patterns in detekt-config - [#775](https://github.com/detekt/detekt/issues/775)
-   Rule: OptionalTypeDeclaration - [#336](https://github.com/detekt/detekt/issues/336)
-   Check if it is feasible to integrate ktlint as a rule set - [#38](https://github.com/detekt/detekt/issues/38)

See all issues at: [1.1.0](https://github.com/detekt/detekt/milestone/19)

#### 1.0.1

##### Notable changes

-   detekt runs can be completely silent on absence of findings](/blog/2019/08/14/custom-console-reports)
-   All detekt's dependencies are now on MavenCentral. Bogus "_could not find JCommander dependency_" should be gone.

##### Changelog

-   Migrate SafeCast test cases to JSR223 - [#1832](https://github.com/detekt/detekt/pull/1832)
-   Remove unused case entries - [#1831](https://github.com/detekt/detekt/pull/1831)
-   Migrate ComplexInterface test cases to JSR223 - [#1830](https://github.com/detekt/detekt/pull/1830)
-   Remove pluginrepository from maventask.md - [#1827](https://github.com/detekt/detekt/pull/1827)
-   Update maven-task --excludes arg - [#1825](https://github.com/detekt/detekt/pull/1825)
-   Improve grammar on 1.0 release post - [#1822](https://github.com/detekt/detekt/pull/1822)
-   Write guide on how to make detekt silent - [#1819](https://github.com/detekt/detekt/pull/1819)
-   Use notifications instead of println - [#1818](https://github.com/detekt/detekt/pull/1818)
-   JCommander 1.78 - [#1817](https://github.com/detekt/detekt/pull/1817)
-   Fix typo in spelling of --auto-correct flag - [#1816](https://github.com/detekt/detekt/pull/1816)
-   Update dependency versions - [#1815](https://github.com/detekt/detekt/pull/1815)
-   Tidy the build files - [#1814](https://github.com/detekt/detekt/pull/1814)
-   Downgrade to jcommander v1.72 - [#1809](https://github.com/detekt/detekt/pull/1809)
-   Update docs to mention test-pattern deprecation - [#1808](https://github.com/detekt/detekt/pull/1808)
-   Quiet mode or ability to disable all output in Gradle - [#1797](https://github.com/detekt/detekt/issues/1797)

See all issues at: [1.0.1](https://github.com/detekt/detekt/milestone/47)

#### 1.0.0

##### Migration

-   Gradle Plugin: removed report consolidation. It was flawed and some users were stuck with RC14. It will be replaced in a further version.
-   Gradle Plugin: `autoCorrect` property is now allowed on the detekt extension. No need to create a new task anymore.
-   Formatting: updated to KtLint 0.34.2 which removed the two rules `NoItParamInMultilineLambda` and `SpacingAroundUnaryOperators`.

##### Changelog

-   Gradle plugin: Set default path for "config" parameter - [#1801](https://github.com/detekt/detekt/pull/1801)
-   Don't use provider value that may not have been set - [#1800](https://github.com/detekt/detekt/pull/1800)
-   Remove raw URLs from README - [#1799](https://github.com/detekt/detekt/pull/1799)
-   Add missing autoCorrect declarations - [#1798](https://github.com/detekt/detekt/pull/1798)
-   Docs: Missing autoCorrect option for various rules - [#1796](https://github.com/detekt/detekt/issues/1796)
-   Update to KtLint 0.34.2 - [#1791](https://github.com/detekt/detekt/pull/1791)
-   Add auto correct flag to detekt extension - [#1790](https://github.com/detekt/detekt/pull/1790)
-   Gradle plugin: Fix visibility of internal properties - [#1789](https://github.com/detekt/detekt/pull/1789)
-   Check classes and functions documented for api module - [#1788](https://github.com/detekt/detekt/pull/1788)
-   Provide default value for ignoreFailures - [#1787](https://github.com/detekt/detekt/pull/1787)
-   Update detekt-api documentation - [#1786](https://github.com/detekt/detekt/pull/1786)
-   Document meanings of rule severity levels - [#1785](https://github.com/detekt/detekt/pull/1785)
-   Remove unused code - [#1784](https://github.com/detekt/detekt/pull/1784)
-   Fix UseDataClass false positive (delegation) - [#1783](https://github.com/detekt/detekt/pull/1783)
-   Add ignore pattern to SwallowedException - [#1782](https://github.com/detekt/detekt/pull/1782)
-   Prevent adding author tags in code - [#1776](https://github.com/detekt/detekt/pull/1776)
-   Remove xml report consolidation. - [#1774](https://github.com/detekt/detekt/pull/1774)
-   Update Complex Method doc - closes #1009 - [#1773](https://github.com/detekt/detekt/pull/1773)
-   Implement dry-run option for detekt gradle tasks. - [#1769](https://github.com/detekt/detekt/pull/1769)
-   Fix missing report file issue. - [#1767](https://github.com/detekt/detekt/pull/1767)
-   Not running formatting autocorrect - [#1766](https://github.com/detekt/detekt/issues/1766)
-   Check if file exists before considering it for report merges - [#1763](https://github.com/detekt/detekt/pull/1763)
-   Preset ignoreFailures property with false as it is also used by Gradle - [#1762](https://github.com/detekt/detekt/pull/1762)
-   Rearrange badges, add codefactor - [#1760](https://github.com/detekt/detekt/pull/1760)
-   Update Kotlin to 1.3.41 - [#1759](https://github.com/detekt/detekt/pull/1759)
-   Update EmptyClassBlock to skip classes with comments in the body - [#1758](https://github.com/detekt/detekt/pull/1758)
-   EmptyClassBlock should consider comment as "body" (via option?) - [#1756](https://github.com/detekt/detekt/issues/1756)
-   Remove obsolete NoWildcardImports autoCorrect param - [#1751](https://github.com/detekt/detekt/pull/1751)
-   Kotlin language version handling - [#1748](https://github.com/detekt/detekt/pull/1748)
-   Fix cli execution doc - [#1747](https://github.com/detekt/detekt/pull/1747)
-   Add naming test for ForbiddenVoid rule - [#1740](https://github.com/detekt/detekt/pull/1740)
-   ForbiddenVoid: New option 'ignoreUsageInGenerics' - [#1738](https://github.com/detekt/detekt/pull/1738)
-   Default Gradle config path should be config/detekt/config.yml - [#1262](https://github.com/detekt/detekt/issues/1262)

See all issues at: [1.0.0](https://github.com/detekt/detekt/milestone/46)

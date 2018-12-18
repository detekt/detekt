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

- 'failFast' inside the yaml config was deprecated. Please use the `--fail-fast` cli flag or `failFast` detekt extension property in the gradle plugin.

-->

#### RC12

##### Changelog

- Actually print the exception message next to the stacktrace - [#1378](https://github.com/arturbosch/detekt/pull/1378)
- Added support for JSR test infrastructure in documentation ruleset - [#1377](https://github.com/arturbosch/detekt/pull/1377)
- Actually load the manifest to report detekt's version - [#1376](https://github.com/arturbosch/detekt/pull/1376)
- fix typo - [#1374](https://github.com/arturbosch/detekt/pull/1374)
- Drop JDK 9 & 10 from CI - [#1371](https://github.com/arturbosch/detekt/pull/1371)
- Add EqualsOnSignatureLine - [#1370](https://github.com/arturbosch/detekt/pull/1370)
- Do not add +1 complexity for nested functions inside functions - [#1365](https://github.com/arturbosch/detekt/pull/1365)
- Do not report expect'ed annotation classes with an empty constructor … - [#1364](https://github.com/arturbosch/detekt/pull/1364)
- Add more talks mentioning detekt - [#1363](https://github.com/arturbosch/detekt/pull/1363)
- false positive EmptyDefaultConstructor for annotation - [#1362](https://github.com/arturbosch/detekt/issues/1362)
- Mention published url change in the migration guide - [#1361](https://github.com/arturbosch/detekt/pull/1361)
- Support multiple it refs in UnnecessaryLet (#1359) - [#1360](https://github.com/arturbosch/detekt/pull/1360)
- Allow multiple 'it' references in UnnecessaryLet - [#1359](https://github.com/arturbosch/detekt/issues/1359)
- Fix link to contributing guideline in PR template - [#1358](https://github.com/arturbosch/detekt/pull/1358)
- Updated groovydsl version doc - [#1353](https://github.com/arturbosch/detekt/pull/1353)
- gradle plugin min gradle version - [#1352](https://github.com/arturbosch/detekt/issues/1352)
- Hide dev flags in CLI - [#1351](https://github.com/arturbosch/detekt/pull/1351)
- Filter wildcards for type references as they can be null - Closes #1345 - [#1349](https://github.com/arturbosch/detekt/pull/1349)
- I can't please detekt and/or ktlint with the following - what's kotlin idiomatic approach - [#1348](https://github.com/arturbosch/detekt/issues/1348)
- False positive UnusedPrivateClass in RC11 - [#1347](https://github.com/arturbosch/detekt/issues/1347)
- UnusedPrivateClass check led to an exception - [#1345](https://github.com/arturbosch/detekt/issues/1345)
- Added support for JSR test infrasture in performance ruleset - [#1343](https://github.com/arturbosch/detekt/pull/1343)
- Added SwallowedException ignore type config - [#1342](https://github.com/arturbosch/detekt/pull/1342)
- RC10 not published? - [#1339](https://github.com/arturbosch/detekt/issues/1339)
- Allow to exclude labeled return statements - #1317 - [#1336](https://github.com/arturbosch/detekt/pull/1336)
- Updated kotlin version to 1.3.10 - [#1332](https://github.com/arturbosch/detekt/pull/1332)
- Don't publish Gradle plugin to Bintray - [#1161](https://github.com/arturbosch/detekt/pull/1161)
- ComplexMethod false positive for returning anonymous inner class - [#1037](https://github.com/arturbosch/detekt/issues/1037)

See all issues at: [RC12](https://github.com/arturbosch/detekt/milestone/41)

#### RC11

##### Migration

- The --input parameter is no longer required. If it is missing, the current working directory is used instead.

##### Changelog

- Update docs dependencies due to security issues - [#1337](https://github.com/arturbosch/detekt/pull/1337)
- Fixes #1319 - false positive for UnusedImports - [#1335](https://github.com/arturbosch/detekt/pull/1335)
- Update appveyor.yml to support jdk11 - [#1334](https://github.com/arturbosch/detekt/pull/1334)
- Updated ObjectPropertyNaming privatePropertyPattern - fixes #1331 - [#1333](https://github.com/arturbosch/detekt/pull/1333)
- ObjectPropertyNaming defaults do not match Kotlin style guide - [#1331](https://github.com/arturbosch/detekt/issues/1331)
- Supports @this expr in extension functions - [#1328](https://github.com/arturbosch/detekt/pull/1328)
- Added first prototype for compiling test snippets - [#1327](https://github.com/arturbosch/detekt/pull/1327)
- Swallowed exception update - [#1326](https://github.com/arturbosch/detekt/pull/1326)
- Removed todo without description - [#1323](https://github.com/arturbosch/detekt/pull/1323)
- Added tests for excludeClassPattern in NamingRules - [#1322](https://github.com/arturbosch/detekt/pull/1322)
- Ignores interfaces in NestedClassesVisibility - fixes #1075 - [#1321](https://github.com/arturbosch/detekt/pull/1321)
- False positive for UnusedImports - [#1319](https://github.com/arturbosch/detekt/issues/1319)
- Option to only build reports on failure - [#1318](https://github.com/arturbosch/detekt/issues/1318)
- Update detekt-gradle-plugin version - [#1315](https://github.com/arturbosch/detekt/pull/1315)
- Ignore TooGenericExceptionCaught by default for tests. - [#1312](https://github.com/arturbosch/detekt/pull/1312)
- Update README with "detektPlugins" configuration. - [#1311](https://github.com/arturbosch/detekt/pull/1311)
- Add license scan report and status - [#1310](https://github.com/arturbosch/detekt/pull/1310)
- New Rule: UnusedPrivateClass - [#1309](https://github.com/arturbosch/detekt/pull/1309)
- Use current working directory if --input parameter is not specified. - [#1308](https://github.com/arturbosch/detekt/pull/1308)
- Set current directory as default input path - [#1301](https://github.com/arturbosch/detekt/issues/1301)

See all issues at: [RC11](https://github.com/arturbosch/detekt/milestone/40)

#### RC10

##### Migration

The configurations in the Detekt Gradle Plugin have changed to align the Plugin further with other
static analysis plugins. Similar to FindBugs the Detekt Gradle Plugin now defines two configurations:
`detekt` and `detektPlugins`.
- `detekt` is now used to define detekt dependencies such as the `detekt-cli`.
- `detektPlugins` is used to define custom detekt RuleSets and rules such as the `detekt-formatting`
rules

To define custom detekt extensions or to add the `detekt-formatting` rules you will now have to
define them as:

```kotlin
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:[version]")
    detektPlugins("your.custom.detekt.rules:rules:[version]")
}
```

The report id `plain` has been renamed to `txt`. If you were using `--report "plain:/tmp/plaintxt"` before it's now `--report "txt:/tmp/plaintxt"`.

The `--filters` argument no longer checks on the absolute path of files, but rather relative paths to the project root.


The published url changed slightly. So users using the old way of applying plugins need to change their gradle setup:
```
buildscript {
  repositories {
    jcenter()
    maven { url "https://plugins.gradle.org/m2/" }
  }
  dependencies {
    // https://mvnrepository.com/artifact/gradle.plugin.io.gitlab.arturbosch.detekt/detekt-gradle-plugin 
    // for version <= 1.0.0.RC9.2
    classpath "gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:[version]"
    // https://mvnrepository.com/artifact/io.gitlab.arturbosch.detekt/detekt-gradle-plugin?repo=gradle-plugins
    // for version >= 1.0.0-RC10
    classpath "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:[version]"
  }
}

apply plugin: "io.gitlab.arturbosch.detekt"
```


##### Changelog

- Update regex for private properties in ObjectPropertyNaming to be on pare with intellij - [#1303](https://github.com/arturbosch/detekt/pull/1303)
- Get rid of jdk classes - [#1302](https://github.com/arturbosch/detekt/pull/1302)
- Clarification on Use Cases/Limitations - [#1300](https://github.com/arturbosch/detekt/issues/1300)
- Drop kotlin-dsl plugin from detekt-gradle-plugin - [#1298](https://github.com/arturbosch/detekt/pull/1298)
- Ask for Gradle version with bug reports - [#1297](https://github.com/arturbosch/detekt/pull/1297)
- Upgrade to Kotlin 1.3.0 - [#1296](https://github.com/arturbosch/detekt/pull/1296)
- Ignore FunctionMaxLength by default for tests. - [#1293](https://github.com/arturbosch/detekt/pull/1293)
- Typealiases for RuleId and RuleSetId - [#1292](https://github.com/arturbosch/detekt/pull/1292)
- Generate test coverage report and upload to Codecov - [#1291](https://github.com/arturbosch/detekt/pull/1291)
- Drop FunctionMinLength & FunctionMaxLength - [#1290](https://github.com/arturbosch/detekt/issues/1290)
- Fix a simple typo in TooManyFunctions rule's description text - [#1289](https://github.com/arturbosch/detekt/pull/1289)
- [WIP] [POC] Config based aliases - [#1287](https://github.com/arturbosch/detekt/pull/1287)
- Question: drop kotlin-dsl dependency from Gradle plugin? - [#1286](https://github.com/arturbosch/detekt/issues/1286)
- Add test coverage report for detekt project - [#1285](https://github.com/arturbosch/detekt/issues/1285)
- Add test case for reported false positive - #1264 - [#1284](https://github.com/arturbosch/detekt/pull/1284)
- Depend on open jdk and include openjdk11 - [#1282](https://github.com/arturbosch/detekt/pull/1282)
- Drop rules that ktlint implements - [#1281](https://github.com/arturbosch/detekt/issues/1281)
- Readd missing `detekt-rules` dependency for cli to avoid NOP detekt run - [#1280](https://github.com/arturbosch/detekt/pull/1280)
- Change the way we locate and filter Rules - [#1278](https://github.com/arturbosch/detekt/issues/1278)
- Gradle plugin: print path reports in output - [#1277](https://github.com/arturbosch/detekt/issues/1277)
- MagicNumber: Allow 300 by default. - [#1275](https://github.com/arturbosch/detekt/pull/1275)
- Added tests for PropertyNaming rules - [#1274](https://github.com/arturbosch/detekt/pull/1274)
- Updated MethodOverloading message - fixed #1223 - [#1273](https://github.com/arturbosch/detekt/pull/1273)
- Show error messages for unused properties distinguish (#1243) - [#1272](https://github.com/arturbosch/detekt/pull/1272)
- No BuildFailure for create-baseline - [#1271](https://github.com/arturbosch/detekt/pull/1271)
- Add rule for detecting arrays of primitive types in function parameters - [#1270](https://github.com/arturbosch/detekt/pull/1270)
- Split UnnecessaryApply tests - [#1269](https://github.com/arturbosch/detekt/pull/1269)
- Consider variable to be this prefixed - Closes #1257 - [#1268](https://github.com/arturbosch/detekt/pull/1268)
- Remove duplicate configurations for reports. - [#1267](https://github.com/arturbosch/detekt/pull/1267)
- Update ktlint - [#1266](https://github.com/arturbosch/detekt/pull/1266)
- [Gradle] Don't fail the build if baseline file is missing - [#1265](https://github.com/arturbosch/detekt/issues/1265)
- false positive unused import - [#1264](https://github.com/arturbosch/detekt/issues/1264)
- Remove unused baseline - [#1263](https://github.com/arturbosch/detekt/pull/1263)
- detektBaseline task returns failure on success - [#1261](https://github.com/arturbosch/detekt/issues/1261)
- Fix formatting issues - [#1259](https://github.com/arturbosch/detekt/pull/1259)
- Fix formatting issues - [#1258](https://github.com/arturbosch/detekt/pull/1258)
- VarCouldBeVal false positive - [#1257](https://github.com/arturbosch/detekt/issues/1257)
- Use PathFilter in TestPattern to make patterns OS independent - [#1256](https://github.com/arturbosch/detekt/pull/1256)
- Run detekt-formatting plugin on detekt itself - [#1255](https://github.com/arturbosch/detekt/pull/1255)
- Travis: oraclejdk10 > openjdk10 - [#1254](https://github.com/arturbosch/detekt/pull/1254)
- Use detekt formatting plugin on detekt - [#1252](https://github.com/arturbosch/detekt/issues/1252)
- Issue with Travis build on JDK 10 - [#1251](https://github.com/arturbosch/detekt/issues/1251)
- Change --filters argument to only check relative paths - [#1250](https://github.com/arturbosch/detekt/pull/1250)
- Add missing reports {} closure in docs - [#1247](https://github.com/arturbosch/detekt/pull/1247)
- Removed `MaximumLineLength` - [#1246](https://github.com/arturbosch/detekt/pull/1246)
- Fixed #1238 - MethodOverloading false positive - [#1244](https://github.com/arturbosch/detekt/pull/1244)
- Improve err message for UnusedPrivateMember distinguish between property/param etc. - [#1243](https://github.com/arturbosch/detekt/issues/1243)
- run travis build on windows and linux - [#1241](https://github.com/arturbosch/detekt/pull/1241)
- AppVeyor: Disable Kotlin's incremental build support - [#1240](https://github.com/arturbosch/detekt/pull/1240)
- AppVeyor: Disable Kotlin incremental compilation - [#1239](https://github.com/arturbosch/detekt/pull/1239)
- MethodOverloading false positive if extension function for different receiver - [#1238](https://github.com/arturbosch/detekt/issues/1238)
- [RFC] activateAll config flag - [#1236](https://github.com/arturbosch/detekt/issues/1236)
- fix test-pattern to support windows path separator - [#1234](https://github.com/arturbosch/detekt/pull/1234)
- Exclude external functions from unused parameter check. - [#1232](https://github.com/arturbosch/detekt/pull/1232)
- AppVeyor: Timeout before deleting Gradle lock file - [#1231](https://github.com/arturbosch/detekt/pull/1231)
- AppVeyor: w: The '-d' option with a directory destination is ignored because '-Xbuild-file' is specified - [#1230](https://github.com/arturbosch/detekt/issues/1230)
- Add unnecessary apply rule - [#1229](https://github.com/arturbosch/detekt/pull/1229)
- Suppress UNUSED_PARAMETER should work as well - [#1228](https://github.com/arturbosch/detekt/issues/1228)
- false positive UnusedPrivateMember for external function - [#1227](https://github.com/arturbosch/detekt/issues/1227)
- CliArgs Doc Change - [#1225](https://github.com/arturbosch/detekt/pull/1225)
- MethodOverloading with wrong line - [#1223](https://github.com/arturbosch/detekt/issues/1223)
- create one detekt task per sourceset - [#1220](https://github.com/arturbosch/detekt/pull/1220)
- [WIP] AppVeyor: Save Gradle cache to the AppVeyor build cache - [#1218](https://github.com/arturbosch/detekt/pull/1218)
- AppVeyor fails downloading dependencies - [#1217](https://github.com/arturbosch/detekt/issues/1217)
- Unsafe cast is wrong - [#1216](https://github.com/arturbosch/detekt/issues/1216)
- Allow for additional aliases to @Suppress rules - [#1215](https://github.com/arturbosch/detekt/issues/1215)
- UnnecessaryApply rule - [#1214](https://github.com/arturbosch/detekt/issues/1214)
- Migrate JUnit tests to Spek - [#1213](https://github.com/arturbosch/detekt/pull/1213)
- Filters apply to absolute paths not relative paths - [#1212](https://github.com/arturbosch/detekt/issues/1212)
- fix gradle plugin link - [#1211](https://github.com/arturbosch/detekt/pull/1211)
- fix gradle plugin link - [#1210](https://github.com/arturbosch/detekt/pull/1210)
- Build: build detekt-cli before running detekt tasks - [#1207](https://github.com/arturbosch/detekt/pull/1207)
- [gradle plugin] test code refactoring - [#1205](https://github.com/arturbosch/detekt/pull/1205)
- Revert "Re-add additional empty check for report paths" - [#1204](https://github.com/arturbosch/detekt/pull/1204)
- Add new style multi-option issue templates - [#1203](https://github.com/arturbosch/detekt/pull/1203)
- clarify error message in verify documentation task - [#1202](https://github.com/arturbosch/detekt/pull/1202)
- Documentation verification requires committing changes but that's not clear - [#1199](https://github.com/arturbosch/detekt/issues/1199)
- Rule `Explicit it lambda parameter` - [#1197](https://github.com/arturbosch/detekt/pull/1197)
- Do not detect parameter name violations in overridden function - [#1196](https://github.com/arturbosch/detekt/pull/1196)
- Fatal error on the android project - Unable to find method 'org.gradle.api.tasks.TaskContainer.register - [#1195](https://github.com/arturbosch/detekt/issues/1195)
- [gradle-plugin] use lazy evaluation of properties - [#1194](https://github.com/arturbosch/detekt/pull/1194)
- Implement lazy configuration for Gradle plugin - [#1193](https://github.com/arturbosch/detekt/issues/1193)
- Revert "Revert "Update dependencies"" - [#1192](https://github.com/arturbosch/detekt/pull/1192)
- Revert "Update dependencies" - [#1191](https://github.com/arturbosch/detekt/pull/1191)
- Change PathSensitivity to RELATIVE - [#1190](https://github.com/arturbosch/detekt/pull/1190)
- Use new GitHub Issue Templates - [#1189](https://github.com/arturbosch/detekt/issues/1189)
- remove gitlab CI config - [#1188](https://github.com/arturbosch/detekt/pull/1188)
- explicitly give KtTestCompiler a filename ending in .kt - [#1187](https://github.com/arturbosch/detekt/pull/1187)
- make OptionalUnit ignore functions in interfaces - [#1186](https://github.com/arturbosch/detekt/pull/1186)
- update kotlin to v1.2.71 - [#1185](https://github.com/arturbosch/detekt/pull/1185)
- Rename Plain reporting to Txt. - [#1184](https://github.com/arturbosch/detekt/pull/1184)
- Allow importing just detekt-gradle-plugin into IDE - [#1183](https://github.com/arturbosch/detekt/pull/1183)
- Update dependencies - [#1182](https://github.com/arturbosch/detekt/pull/1182)
- Gradle Plugin: If tasks are configured eagerly configuration from DetektExtension isn't used - [#1181](https://github.com/arturbosch/detekt/issues/1181)
- Fix build failing to compile on master - [#1180](https://github.com/arturbosch/detekt/pull/1180)
- Master is broken - [#1179](https://github.com/arturbosch/detekt/issues/1179)
- Detekt should not use PathSensitivity.ABSOLUTE for Detekt task - [#1178](https://github.com/arturbosch/detekt/issues/1178)
- Re-remove kotlin-reflect dependency - [#1177](https://github.com/arturbosch/detekt/pull/1177)
- OptionalUnit triggers on a default method in an interface - [#1176](https://github.com/arturbosch/detekt/issues/1176)
- FunctionParameterNaming false positive - [#1175](https://github.com/arturbosch/detekt/issues/1175)
- Migrate all assertions to AssertJ - [#1174](https://github.com/arturbosch/detekt/pull/1174)
- rename configurations of Gradle Plugin to detekt and detektPlugins - [#1173](https://github.com/arturbosch/detekt/pull/1173)
- CI: Test on Java 10 - [#1172](https://github.com/arturbosch/detekt/pull/1172)
- Gradle configuration not respecting configuration in RC9.2 - [#1171](https://github.com/arturbosch/detekt/issues/1171)
- Choose single assertion library and test engine - [#1170](https://github.com/arturbosch/detekt/issues/1170)
- ObjectPropertyNaming should to flag const properties that are not simple types - [#1167](https://github.com/arturbosch/detekt/issues/1167)
- Architecture Compliance Rule - [#1164](https://github.com/arturbosch/detekt/issues/1164)
- Fix typo on getting started docs pages - [#1163](https://github.com/arturbosch/detekt/pull/1163)
- Detekt Gradle Plugin Configurations - [#1162](https://github.com/arturbosch/detekt/issues/1162)
- Gradle Plugin Portal "latest" version incorrect - [#1159](https://github.com/arturbosch/detekt/issues/1159)
- In Travis CI: "0 kotlin files were analyzed." - [#1158](https://github.com/arturbosch/detekt/issues/1158)

See all issues at: [RC10](https://github.com/arturbosch/detekt/milestone/39)

#### RC9.2

##### Migration

Please update to this bug fix version of RC9 as it contains many essential and important fixes for the new gradle plugin.
Also take a look at the migration section of RC9 if you are < RC9.

##### Changelog

- Revert change to ignoreNamedArguments - Closes #1115 - [#1157](https://github.com/arturbosch/detekt/pull/1157)
- Rename config and id of ConfigAware - [#1156](https://github.com/arturbosch/detekt/pull/1156)
- Add test case for parameter annotations - Closes #1115 - [#1155](https://github.com/arturbosch/detekt/pull/1155)
- use correct instance of valueOrDefault in FormattingRule - [#1154](https://github.com/arturbosch/detekt/pull/1154)
- Make `config` property in `Rule` effectively private - [#1153](https://github.com/arturbosch/detekt/issues/1153)
- Use correct accessor method for config in LazyRegex - [#1152](https://github.com/arturbosch/detekt/pull/1152)
- print test outcome for debugging during the build - [#1150](https://github.com/arturbosch/detekt/pull/1150)
- Fix Gradle Plugin Tests - [#1148](https://github.com/arturbosch/detekt/pull/1148)
- Prevent overwriting of defaultDependency detekt-cli by additional detekt dependencies - [#1147](https://github.com/arturbosch/detekt/pull/1147)
- Fix issue with wrong report name - [#1145](https://github.com/arturbosch/detekt/pull/1145)
- Set group to Detekt task - [#1144](https://github.com/arturbosch/detekt/pull/1144)
- Running RC9 gradlew detekt crashes with detekt-formatting included - [#1143](https://github.com/arturbosch/detekt/issues/1143)
- The IntelliJ IDEA plugin does not define the settings from the Gradle - [#1142](https://github.com/arturbosch/detekt/issues/1142)
- RC9 creates files called "C" with <checkstyle> tags inside - [#1141](https://github.com/arturbosch/detekt/issues/1141)
- Sample / demo code? - [#1140](https://github.com/arturbosch/detekt/issues/1140)
- Reimplement watch service - [#1139](https://github.com/arturbosch/detekt/pull/1139)
- RC9 not working with gradle 4.4 - [#1136](https://github.com/arturbosch/detekt/issues/1136)
- Fix issue when multiple input files are specified - [#1134](https://github.com/arturbosch/detekt/pull/1134)
- Nested functions not reported by FunctionNaming - [#1133](https://github.com/arturbosch/detekt/issues/1133)
- #1122/Add tests for ObjectPropertyNaming for private val overrides - [#1130](https://github.com/arturbosch/detekt/pull/1130)
- #1120/Ignore naming of overridden functions and properties - [#1129](https://github.com/arturbosch/detekt/pull/1129)
- Upload JUnit test results to AppVeyor - [#1128](https://github.com/arturbosch/detekt/pull/1128)
- 1125/Allow spaces in CLI filters param - [#1127](https://github.com/arturbosch/detekt/pull/1127)
- detektCheck starts failing - Was passed main parameter '--output' but no main parameter was defined in your arg class - [#1126](https://github.com/arturbosch/detekt/issues/1126)
- Spaces around separators in CLI input filters break filters - [#1125](https://github.com/arturbosch/detekt/issues/1125)
- HTML report created at the wrong place - [#1123](https://github.com/arturbosch/detekt/issues/1123)
- ObjectPropertyNaming/privatePropertyPattern not loaded from configuration - [#1122](https://github.com/arturbosch/detekt/issues/1122)
- Missing GitHub release & tag for RC9 - [#1121](https://github.com/arturbosch/detekt/issues/1121)
- FunctionNaming should exclude overridden functions - [#1120](https://github.com/arturbosch/detekt/issues/1120)
- Use plugin version as default Detekt version - [#1119](https://github.com/arturbosch/detekt/pull/1119)
- Remove hardcoded default version in Detekt Gradle Plugin - [#1118](https://github.com/arturbosch/detekt/issues/1118)
- Documented version not available in gradle plugins - [#1117](https://github.com/arturbosch/detekt/issues/1117)
- MagicNumber regression with RC9 in Annotation methods - [#1115](https://github.com/arturbosch/detekt/issues/1115)
- IndexOutOfBoundsException when changing --output to --report with RC9 - [#1114](https://github.com/arturbosch/detekt/issues/1114)
- Updated contributors list - [#1112](https://github.com/arturbosch/detekt/pull/1112)
- update Gradle to v4.10.1 - [#1109](https://github.com/arturbosch/detekt/pull/1109)
- Print stacktraces recursively - Closes #1107 - [#1108](https://github.com/arturbosch/detekt/pull/1108)
- Detektor doesn't print stack trace causes which makes debugging difficult - [#1107](https://github.com/arturbosch/detekt/issues/1107)
- Delete a bunch of files from the docs directory - [#1106](https://github.com/arturbosch/detekt/pull/1106)
- use KtImportDirective instead of KtImportList for ForbiddenImport - [#1105](https://github.com/arturbosch/detekt/pull/1105)
- Reference vcsreader repo via GrabResolver - Fixes #1101 - [#1104](https://github.com/arturbosch/detekt/pull/1104)
- org.vcsreader:vcsreader:1.1.0 does not exist - [#1101](https://github.com/arturbosch/detekt/issues/1101)

See all issues at: [RC9.2](https://github.com/arturbosch/detekt/milestone/38)

#### RC9

##### Migration

You need Gradle 4.9 or higher to migrate to RC9.

With RC9, which is the last major release candidate before 1.0.0, a new gradle plugin gets introduced.
The `detekt` extension configuration changes a bit and the concept of `profiles` was removed.
There is no `detektCheck` task anymore, just a `detekt` task which is incremental and is bound to the check task.
Now the `detekt` plugin must be applied to every project and just analyzes `Kotlin` files in the source set of this project.

An important change is that `detekt-gradle-plugin` does not pull the latest version which is found online but has a hardcoded version by default.
This can break your current setup if you have not specified a `version = "...""` property!
Furthermore this `version` property got now removed and the new `toolVersion` property is used to specify the underlying `detekt` tool version.
This was done to be inline with other static analysis tools.

Instead of writing
```gradle
detekt {
    defaultProfile { // or profile("main") {...}
        // properties
    }
}
```
we now write
```gradle
detekt {
    // properties
}
```

 If we want all our sub projects to get analyzed by detekt, something like
 ```gradle
 subprojects {
    detekt {
        // properties
    }
 }
 ```
can be applied to the root build file.

A full `detekt` configuration for multi-module gradle project could look like `detekt`'s own build file.
Attention(!) this must be translated to [groovy](https://arturbosch.github.io/detekt/groovydsl.html) if you do not use the [kotlin-dsl](https://arturbosch.github.io/detekt/kotlindsl.html).
```gradle
plugins {
	id("io.gitlab.arturbosch.detekt") version "[1.0.0.RC9]"
}
...
subprojects {
    ...
	apply {
		plugin("io.gitlab.arturbosch.detekt")
		...
	}

	val userHome = System.getProperty("user.home")

    detekt {
        debug = true
        toolVersion = usedDetektVersion
        config = files(
                project.rootDir.resolve("detekt-cli/src/main/resources/default-detekt-config.yml"),
                project.rootDir.resolve("reports/failfast.yml")
        )
        filters = ".*/resources/.*,.*/build/.*"
        baseline = project.rootDir.resolve("reports/baseline.xml")

        reports {
            xml.enabled = true
            html {
                enabled = true
                destination = project.rootDir.resolve("reports/detekt.html")
            }
        }

        idea {
            path = "$userHome/.idea"
            codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
            inspectionsProfile = "$userHome/.idea/inspect.xml"
            report = "project.projectDir/reports"
            mask = "*.kt"
        }
}
```

Make sure that all properties expecting a path expect a `FileCollection` or a `File` type now.
The `config` property now explicitly tells the user that `detekt` can consume multiple configuration yaml files (config = files(...)).

There is also a breaking change for the `detekt-cli` module which will attack custom gradle task and cli users.
The `output` and `reports` options are no longer. There is the new `--report` option which can be used multiple times to generate specific reports.
The report pattern now is `--report [report-id:path-to-store-report]`.
There are three provided output reports named: `plain`, `xml` and `html`. Custom reports can be added to leveraging the detekt extension mechanism as described on the [website](https://arturbosch.github.io/detekt/extensions.html).

##### Changes

- fix false positive in UnnecessaryParentheses - [#1098](https://github.com/arturbosch/detekt/pull/1098)
- remove duplicated `detekt` in readme - [#1097](https://github.com/arturbosch/detekt/pull/1097)
- use debug flag for printing outputs in gradle plugin - [#1096](https://github.com/arturbosch/detekt/pull/1096)
- update documentation to use new detekt gradle plugin - [#1095](https://github.com/arturbosch/detekt/pull/1095)
- remove img/ directory and link to docs images - [#1094](https://github.com/arturbosch/detekt/pull/1094)
- Update "Features" section of the README.md and documentation - [#1093](https://github.com/arturbosch/detekt/pull/1093)
- Allow to use kotlin.Any? - #1085 - [#1092](https://github.com/arturbosch/detekt/pull/1092)
- UnnecessaryAbstractClass: Support exclusion via Annotations. - [#1091](https://github.com/arturbosch/detekt/pull/1091)
- Auto deploy SNAPSHOT versions. - [#1088](https://github.com/arturbosch/detekt/pull/1088)
- MaxLineLength: Ignore comments that end with a long url. - [#1087](https://github.com/arturbosch/detekt/pull/1087)
- Move Issue template & Pull Request template into .github directory. - [#1086](https://github.com/arturbosch/detekt/pull/1086)
- EqualsWithHashCodeExist false positives - [#1085](https://github.com/arturbosch/detekt/issues/1085)
- Feature #1047 OptionalUnit - [#1084](https://github.com/arturbosch/detekt/pull/1084)
- Replace Regex with LazyRegex in Rules - [#1083](https://github.com/arturbosch/detekt/pull/1083)
- Invalid line value on TrailingWhitespace error with gradle - [#1082](https://github.com/arturbosch/detekt/issues/1082)
- Lazy regex evaluation on Rules - [#1080](https://github.com/arturbosch/detekt/pull/1080)
- Merge gradle plugin rework - [#1079](https://github.com/arturbosch/detekt/pull/1079)
- Fixed typo line:35 from diffently to differently - [#1076](https://github.com/arturbosch/detekt/pull/1076)
- Stuff RC8 - [#1074](https://github.com/arturbosch/detekt/pull/1074)
- Fixed #1065 - ThrowingExceptionsWithoutMessageOrCause assert - [#1073](https://github.com/arturbosch/detekt/pull/1073)
- re-add useJUnitPlatform - [#1071](https://github.com/arturbosch/detekt/pull/1071)
- MaxLineLength should ignore link only comments by default - [#1070](https://github.com/arturbosch/detekt/issues/1070)
- Detekt MagicNumber ignoreHashCodeFunction on by default - [#1069](https://github.com/arturbosch/detekt/issues/1069)
- MaxLineLength should ignore import statements by default - [#1068](https://github.com/arturbosch/detekt/issues/1068)
- NoTabs Ignore in strings - [#1067](https://github.com/arturbosch/detekt/issues/1067)
- ThrowingExceptionsWithoutMessageOrCause false positive - [#1065](https://github.com/arturbosch/detekt/issues/1065)
- Unknown exception thrown when running detekt - [#1064](https://github.com/arturbosch/detekt/issues/1064)
- Juggling between `CollapsibleIfStatements` and `ComplexCondition` - [#1063](https://github.com/arturbosch/detekt/issues/1063)
- Make "ComplexMethod" rule also ignore "return when" if configured - [#1062](https://github.com/arturbosch/detekt/pull/1062)
- Improvement #1055 MagicNumber - [#1061](https://github.com/arturbosch/detekt/pull/1061)
- Improvement #1056 TooGenericExceptionCaught - [#1060](https://github.com/arturbosch/detekt/pull/1060)
- Add ForbiddenVoid rule - [#1059](https://github.com/arturbosch/detekt/pull/1059)
- Forbid usage of Void - [#1058](https://github.com/arturbosch/detekt/issues/1058)
- DetektCheck only for git commiting files? - [#1057](https://github.com/arturbosch/detekt/issues/1057)
- TooGenericExceptionCaught should not be reported if the name is ignored - [#1056](https://github.com/arturbosch/detekt/issues/1056)
- False positive with MagicNumber on default value for parameters - [#1055](https://github.com/arturbosch/detekt/issues/1055)
- ComplexMethod should treat "return when" as "single when" expression when ignoring - [#1054](https://github.com/arturbosch/detekt/issues/1054)
- CLI description says only single config file supported - [#1053](https://github.com/arturbosch/detekt/issues/1053)
- Reworked NoTabs rule - [#1052](https://github.com/arturbosch/detekt/pull/1052)
- False positive: Unnecessary parentheses for functions with two function parameters - [#1051](https://github.com/arturbosch/detekt/issues/1051)
- Fixed #932 - Updated documentation for configuring detekt using kotlin-dsl - [#1050](https://github.com/arturbosch/detekt/pull/1050)
- Fix #1043 UtilityClassWithPublicConstructor - [#1049](https://github.com/arturbosch/detekt/pull/1049)
- Reworked UnconditionalJumpStatementInLoop - [#1048](https://github.com/arturbosch/detekt/pull/1048)
- OptionalUnit - lone Unit statement - [#1047](https://github.com/arturbosch/detekt/issues/1047)
- Add ParameterNaming rules for Constructors and Functions parameters - [#1046](https://github.com/arturbosch/detekt/pull/1046)
- Cannot generate baseline file - [#1044](https://github.com/arturbosch/detekt/issues/1044)
- Detekt reports classes with only constructors as UtilityClassWithPublicConstructor - [#1043](https://github.com/arturbosch/detekt/issues/1043)
- Fixed #1039 - Indentation and MaximumLineLength configuration paramet… - [#1042](https://github.com/arturbosch/detekt/pull/1042)
- RC8 patch - Updated rule config option and description - [#1041](https://github.com/arturbosch/detekt/pull/1041)
- Changing indentation settings is not effective - [#1039](https://github.com/arturbosch/detekt/issues/1039)
- Fixes Gradle plugin badge version - [#1038](https://github.com/arturbosch/detekt/pull/1038)
- Update LabeledExpression to support outer class refs - [#1036](https://github.com/arturbosch/detekt/pull/1036)
- Removed FeatureEnvy test case - [#1035](https://github.com/arturbosch/detekt/pull/1035)
- OptionalUnit update - [#1034](https://github.com/arturbosch/detekt/pull/1034)
- Fix incorrect line number in TrailingWhitespace - [#1033](https://github.com/arturbosch/detekt/pull/1033)
- Fix RuleProvider - [#1032](https://github.com/arturbosch/detekt/pull/1032)
- Update kotlin version to 1.2.60 - [#1031](https://github.com/arturbosch/detekt/pull/1031)
- TrailingWhitespace reports incorrect line number - [#1030](https://github.com/arturbosch/detekt/issues/1030)
- Tests are not run when using the CLI - [#1029](https://github.com/arturbosch/detekt/issues/1029)
- Fix #1027 - FunctionOnlyReturningConstant - [#1028](https://github.com/arturbosch/detekt/pull/1028)
- FunctionOnlyReturningConstant false positive for interfaces with default implementations - [#1027](https://github.com/arturbosch/detekt/issues/1027)
- Refactor ObjectPropertyNaming - [#1026](https://github.com/arturbosch/detekt/pull/1026)
- InstanceOfCheckForException being too general - [#1025](https://github.com/arturbosch/detekt/pull/1025)
- Add serialVersionUID to UnusedPrivateMember.allowedNames - [#1024](https://github.com/arturbosch/detekt/pull/1024)
- Ignore tabs in raw strings (""") for NoTabs? - [#1023](https://github.com/arturbosch/detekt/issues/1023)
- InstanceOfCheckForException being too general - [#1022](https://github.com/arturbosch/detekt/issues/1022)
- How to run "autoCorrect" - [#1021](https://github.com/arturbosch/detekt/issues/1021)
- Fix typo in KDoc of UnnecessaryAbstractClass - [#1020](https://github.com/arturbosch/detekt/pull/1020)
- Windows 10 : getting error while configuring detekt plugin - [#1019](https://github.com/arturbosch/detekt/issues/1019)
- Analyze issue #1014 - [#1018](https://github.com/arturbosch/detekt/pull/1018)
- Mr/gradle classes - [#1017](https://github.com/arturbosch/detekt/pull/1017)
- Change description of TopLevelPropertyNaming rule - [#1015](https://github.com/arturbosch/detekt/pull/1015)
- 'super.visitNamedDeclaration()' is not invoked in the short-circuit case - [#1014](https://github.com/arturbosch/detekt/issues/1014)
- Replace // with # in yaml code - [#1013](https://github.com/arturbosch/detekt/pull/1013)
- Activate rules in failfast.yml part3 - [#1004](https://github.com/arturbosch/detekt/pull/1004)
- UnnecessaryParentheses: Allow to use parenthesis when in math expression with mixed operators - [#969](https://github.com/arturbosch/detekt/issues/969)
- 1.0.0-RC7-2 is not published to gradlePluginPortal() - [#967](https://github.com/arturbosch/detekt/issues/967)

See all issues at: [RC9](https://github.com/arturbosch/detekt/milestone/36)

#### RC8

- Prepare rc8 - [#1011](https://github.com/arturbosch/detekt/pull/1011)
- Feature 'aliases documentation' - [#1008](https://github.com/arturbosch/detekt/pull/1008)
- Add another missing space - [#1006](https://github.com/arturbosch/detekt/pull/1006)
- Add a missing space - [#1005](https://github.com/arturbosch/detekt/pull/1005)
- Add prefix wildcard to SplitPattern - [#1002](https://github.com/arturbosch/detekt/pull/1002)
- Specify both prefix and suffix wildcard for ForbiddenImport pattern - [#1001](https://github.com/arturbosch/detekt/issues/1001)
- Flag empty nested functions - fixes #998 - [#999](https://github.com/arturbosch/detekt/pull/999)
- EmptyFunctionBlock should flag empty functions defined inside other functions - [#998](https://github.com/arturbosch/detekt/issues/998)
- UnusedImports: Add test for inner classes in same package - [#997](https://github.com/arturbosch/detekt/pull/997)
- Refactor ProfileStorage to be no singleton anymore - Fixes #980 - [#996](https://github.com/arturbosch/detekt/pull/996)
- Fix false positive lambda in constructor call - Fixes #990 - [#995](https://github.com/arturbosch/detekt/pull/995)
- Fix magic number report for named constructor calls - Fixes #992 - [#994](https://github.com/arturbosch/detekt/pull/994)
- magic number ignoreNamedArgument not working with inheriting abstract class - [#992](https://github.com/arturbosch/detekt/issues/992)
- Fix to work on maven multi-module project - [#991](https://github.com/arturbosch/detekt/pull/991)
- False positive UnnecessaryParentheses on constructors with lambdas - [#990](https://github.com/arturbosch/detekt/issues/990)
- UnusedImports: Add detection of imports in same package - [#989](https://github.com/arturbosch/detekt/pull/989)
- Document formatting rule set - Closes #925 - [#988](https://github.com/arturbosch/detekt/pull/988)
- Use ktlint for selfanalysis - [#987](https://github.com/arturbosch/detekt/pull/987)
- Refactor UnusedImports rule - [#986](https://github.com/arturbosch/detekt/pull/986)
- UnusedImports: False negative when importing class in same package - [#985](https://github.com/arturbosch/detekt/issues/985)
- Update kotlin version to 1.2.51 - [#984](https://github.com/arturbosch/detekt/pull/984)
- UnusedPrivateMember rule in abstract functions - [#983](https://github.com/arturbosch/detekt/issues/983)
- Mention --run-rule option and get_analysis_projects script - [#982](https://github.com/arturbosch/detekt/pull/982)
- Activate rules in failfast.yml part2 - [#981](https://github.com/arturbosch/detekt/pull/981)
- Detekt RC 7-3 checks the wrong Gradle module - [#980](https://github.com/arturbosch/detekt/issues/980)
- Add `aliases` to rules' documentations - [#979](https://github.com/arturbosch/detekt/issues/979)
- New rule: VarCouldBeVal - [#978](https://github.com/arturbosch/detekt/pull/978)
- Fix ExpressionBodySyntax when multiline expression - [#977](https://github.com/arturbosch/detekt/pull/977)
- ExpressionBodySyntax: false positive with includeLineWrapping in multiline expression - [#976](https://github.com/arturbosch/detekt/issues/976)
- Activate rules in failfast.yml - [#975](https://github.com/arturbosch/detekt/pull/975)
- UnusedPrivateMember: do not report unused parameters in abstract/open functions - [#973](https://github.com/arturbosch/detekt/pull/973)
- UnusedPrivateMember: Incorrectly reports unused params in open/abstract functions - [#972](https://github.com/arturbosch/detekt/issues/972)
- Use detekt 7-3 in self analysis - [#971](https://github.com/arturbosch/detekt/pull/971)
- UnnecessaryParentheses false positive when using extension function with default parameter - [#927](https://github.com/arturbosch/detekt/issues/927)
- Running ktlint formatting on every build - [#823](https://github.com/arturbosch/detekt/issues/823)

See all issues at: [RC8](https://github.com/arturbosch/detekt/milestone/37)

#### RC7-3

- UnusedPrivateMember: detect top level declarations - [#968](https://github.com/arturbosch/detekt/pull/968)
- Support suppression of single instances of MaxLineLength violations - [#966](https://github.com/arturbosch/detekt/pull/966)
- Fix SerialVersionUIDInSerializableClass when value's less then zero #964 - [#965](https://github.com/arturbosch/detekt/pull/965)
- SerialVersionUIDInSerializableClass reports when const value less then true - [#964](https://github.com/arturbosch/detekt/issues/964)
- Fixed #960 - private properties naming report - [#963](https://github.com/arturbosch/detekt/pull/963)
- Allow "All" in @Suppress statements - [#962](https://github.com/arturbosch/detekt/pull/962)
- TopLevelPropertyNaming description reports wrong regex with private properties - [#960](https://github.com/arturbosch/detekt/issues/960)
- Migrate detekt-formatting/build.gradle to Kotlin DSL - [#958](https://github.com/arturbosch/detekt/pull/958)
- Allow maxLines option for ExpressionBodySyntax - [#957](https://github.com/arturbosch/detekt/issues/957)
- Updated kotlin compiler to version 1.2.50 - [#956](https://github.com/arturbosch/detekt/pull/956)
- WildcardImport: Remove one force unwrap and reuse it from let function - [#955](https://github.com/arturbosch/detekt/pull/955)
- Use native junitPlatform from gradle 4.6 - [#953](https://github.com/arturbosch/detekt/pull/953)
- Fix #950 ExpressionBodySyntax - [#952](https://github.com/arturbosch/detekt/pull/952)
- Gradle native junitplatform in detekt gradle plugin project - [#951](https://github.com/arturbosch/detekt/pull/951)
- Prepare RC7-3 release - [#949](https://github.com/arturbosch/detekt/pull/949)
- Rule: find unused private top level properties, constants or functions - [#948](https://github.com/arturbosch/detekt/issues/948)
- Suppress undocumented classes in dokka - [#947](https://github.com/arturbosch/detekt/pull/947)
- Do not crash on rule exceptions - #793 #944 - [#946](https://github.com/arturbosch/detekt/pull/946)
- Provide signing config for bintray - #345 - [#945](https://github.com/arturbosch/detekt/pull/945)
- Exclude private functions for TooManyFunctions rule - [#943](https://github.com/arturbosch/detekt/issues/943)
- Add a space between two words - [#942](https://github.com/arturbosch/detekt/pull/942)
- MaybeConst tests - [#938](https://github.com/arturbosch/detekt/pull/938)
- Respect string interpolation - Closes #808 - [#937](https://github.com/arturbosch/detekt/pull/937)

See all issues at: [RC7-3](https://github.com/arturbosch/detekt/milestone/35)

#### RC7-2

- Generate javadocJar for maven central publishing - #345 - [#934](https://github.com/arturbosch/detekt/pull/934)
- TooManyFunctions : Add option to ignore private functions - [#933](https://github.com/arturbosch/detekt/pull/933)
- Fixed some IntelliJ inspection warnings in test cases - [#931](https://github.com/arturbosch/detekt/pull/931)
- Fixed MayBeConst false negative when concatenating strings #900 - [#930](https://github.com/arturbosch/detekt/pull/930)
- Remove #807 workaround - [#929](https://github.com/arturbosch/detekt/pull/929)
- Fix divergent issues with UnsafeCasts rule on windows - Closes #926 - [#928](https://github.com/arturbosch/detekt/pull/928)
- Divergent behaviour for UnsafeCast rule on *nix & Windows - [#926](https://github.com/arturbosch/detekt/issues/926)
- Build Gradle plugin using composite build - [#924](https://github.com/arturbosch/detekt/pull/924)
- Extended documentation - [#923](https://github.com/arturbosch/detekt/issues/923)
- Don't treat 'input' and 'output' parameters to Gradle as an InputDirectory/OutputDirectory - [#922](https://github.com/arturbosch/detekt/pull/922)
- Build Gradle plugin using a composite build - [#920](https://github.com/arturbosch/detekt/issues/920)
- CI: Test Windows build on Java 9 - [#919](https://github.com/arturbosch/detekt/pull/919)
- CI: Improve Windows testing - [#918](https://github.com/arturbosch/detekt/pull/918)
- Added missing message to CollapsibleIfStatements rule - [#917](https://github.com/arturbosch/detekt/pull/917)
- Support more range expressions in ForEachOnRange rule - [#916](https://github.com/arturbosch/detekt/pull/916)
- Support more range expressions in ForEachOnRange rule (downTo, step, until etc.) - [#915](https://github.com/arturbosch/detekt/issues/915)
- Add rule for preferring to syntax for creating pairs - [#914](https://github.com/arturbosch/detekt/pull/914)
- Upgrade to Gradle 4.7 - [#913](https://github.com/arturbosch/detekt/pull/913)
- Style rule: Prefer to over Pair - [#912](https://github.com/arturbosch/detekt/issues/912)
- Add style rule for mandatory braces for control flow statements - [#911](https://github.com/arturbosch/detekt/pull/911)
- Applied allowedNames in UnusedPrivateMember to declarations - [#910](https://github.com/arturbosch/detekt/pull/910)
- UnusedPrivateMember – allowedNames is not applied to properties - [#909](https://github.com/arturbosch/detekt/issues/909)
- Updated StringLiteralDuplication to treat multiline string parts as 1 - [#908](https://github.com/arturbosch/detekt/pull/908)
- Added support for special handling in RethrowCaughtException - [#907](https://github.com/arturbosch/detekt/pull/907)
- RethrowCaughtException Usage - [#906](https://github.com/arturbosch/detekt/issues/906)
- VariableNaming not working after 1.0.0.RC5-6 - [#905](https://github.com/arturbosch/detekt/issues/905)
- StringLiteralDuplication - 3/3 - [toString] unclear error - [#904](https://github.com/arturbosch/detekt/issues/904)
- Adjust terminal output in case there's only one file. - [#903](https://github.com/arturbosch/detekt/pull/903)
- Updated MatchingDeclarationName with typealias - [#901](https://github.com/arturbosch/detekt/pull/901)
- MatchingDeclarationName with typealias - [#898](https://github.com/arturbosch/detekt/issues/898)
- Exclude ForEachOnRange in default config for test-pattern. - [#897](https://github.com/arturbosch/detekt/pull/897)
- Make website/documentation link more prominent - [#896](https://github.com/arturbosch/detekt/issues/896)
- Print link to html report in console on check fail - [#894](https://github.com/arturbosch/detekt/issues/894)
- 0 kotlin files were analyzed while running on concourse - [#889](https://github.com/arturbosch/detekt/issues/889)
- Link to documentation for new 'maxIssues' config property - [#887](https://github.com/arturbosch/detekt/issues/887)
- `./gradlew detektBaseline` fails with "Creating a baseline.xml requires the --baseline parameter to specify a path." - [#886](https://github.com/arturbosch/detekt/issues/886)
- Add "FunctionName" alias for suppressing "FunctionNaming" rule - [#882](https://github.com/arturbosch/detekt/issues/882)
- CompositeConfig does not allow forcing the default value - [#881](https://github.com/arturbosch/detekt/issues/881)
- False positive with OptionalAbstractKeyword - [#879](https://github.com/arturbosch/detekt/issues/879)

See all issues at: [RC7-2](https://github.com/arturbosch/detekt/milestone/34)

#### RC7

- Add functionname alias - [#884](https://github.com/arturbosch/detekt/pull/884)
- Make executor in detekt configurable - #862 - [#883](https://github.com/arturbosch/detekt/pull/883)
- Add missing ` character to exception ruleset docs - [#878](https://github.com/arturbosch/detekt/pull/878)
- Updated kotlinVersion to 1.2.40 - [#874](https://github.com/arturbosch/detekt/pull/874)
- Gradle plugin - can't run detektCheck with JDK 9 - [#873](https://github.com/arturbosch/detekt/issues/873)
- Adjust top level property naming to be on pair with intellij - #866 - [#872](https://github.com/arturbosch/detekt/pull/872)
- Splitted positve and negative test cases - [#871](https://github.com/arturbosch/detekt/pull/871)
- Spaces around colon - [#870](https://github.com/arturbosch/detekt/issues/870)
- Corrected "Allowed Maximum" message - [#868](https://github.com/arturbosch/detekt/pull/868)
- Ignored deprecated functions for TooManyFunctions rule - [#867](https://github.com/arturbosch/detekt/pull/867)
- Allow top level properties to be named as constants - [#866](https://github.com/arturbosch/detekt/issues/866)
- Migrate detekt-gradle-plugin/settings.gradle to Gradle DSL - [#864](https://github.com/arturbosch/detekt/pull/864)
- Migrate detekt-gradle-plugin/settings.gradle to Kotlin DSL - [#863](https://github.com/arturbosch/detekt/issues/863)
- Incorrect "Allowed Maximum" message - [#861](https://github.com/arturbosch/detekt/issues/861)
- Detekt task always runs on the same module - [#860](https://github.com/arturbosch/detekt/issues/860)
- Ignore deprecated functions for TooManyFunctions rule - [#859](https://github.com/arturbosch/detekt/issues/859)
- Fixes #857 - Fixes UselessPostfixExpression false positives - [#858](https://github.com/arturbosch/detekt/pull/858)
- UselessPostfixExpression false positives - [#857](https://github.com/arturbosch/detekt/issues/857)
- Additional cli options for printing AST's and running single rules - [#856](https://github.com/arturbosch/detekt/pull/856)
- Implement detekt-formatting, a wrapper over ktlint - [#855](https://github.com/arturbosch/detekt/pull/855)
- detekt-cli tests - [#851](https://github.com/arturbosch/detekt/pull/851)
- Splitted test cases for NamingRulesSpec - [#850](https://github.com/arturbosch/detekt/pull/850)
- Added option to exclude comments from MaxLineLength - [#849](https://github.com/arturbosch/detekt/pull/849)
- Allow underscore as ignored exception name in EmptyCatchBlock - [#848](https://github.com/arturbosch/detekt/pull/848)
- Implement script to download given analysis projects - [#847](https://github.com/arturbosch/detekt/pull/847)
- Set up pipeline to test new detekt rules/features on selected kotlin projects - [#846](https://github.com/arturbosch/detekt/issues/846)
- Add dependencies for gradle plugin (for custom rules) - [#845](https://github.com/arturbosch/detekt/pull/845)
- Should ObjectPropertyNaming have a constantPattern? - [#844](https://github.com/arturbosch/detekt/issues/844)
- VariableMinLength is in the wrong category - [#843](https://github.com/arturbosch/detekt/issues/843)
- MatchingDeclarationName behavior results in false positives. - [#841](https://github.com/arturbosch/detekt/issues/841)
- Fix false positives in unused private members - [#840](https://github.com/arturbosch/detekt/pull/840)
- Use absolute paths in console output to make them clickable - [#839](https://github.com/arturbosch/detekt/pull/839)
- [Console Output] Change all file paths from relative to absolute - [#838](https://github.com/arturbosch/detekt/issues/838)
- False positive for UnnecessaryParentheses - [#836](https://github.com/arturbosch/detekt/issues/836)
- Fixed #783 - OptionalAbstractKeyword false positive - [#835](https://github.com/arturbosch/detekt/pull/835)
- Added rule debt to documentation - [#834](https://github.com/arturbosch/detekt/pull/834)
- Update mention of git commit hook in CONTRIBUTING.md - [#833](https://github.com/arturbosch/detekt/pull/833)
- Do not write deprecated build fail properties to default config file - [#831](https://github.com/arturbosch/detekt/pull/831)
- Fix debt reporting in console report - [#830](https://github.com/arturbosch/detekt/pull/830)
- Update kotlin to v1.2.31 - [#829](https://github.com/arturbosch/detekt/pull/829)
- Build depends on a full download of IntelliJ - [#827](https://github.com/arturbosch/detekt/issues/827)
- Extract intellij plugin to its own repository - [#826](https://github.com/arturbosch/detekt/pull/826)
- Updates Readme with corrected links to RuleSets documentation - [#825](https://github.com/arturbosch/detekt/pull/825)
- Added threshold change to changelog - [#824](https://github.com/arturbosch/detekt/pull/824)
- Threshold changed when updating from RC6-3 to RC6-4 - [#822](https://github.com/arturbosch/detekt/issues/822)
- Migrate detekt/build.gradle to kotlin dsl - [#821](https://github.com/arturbosch/detekt/pull/821)
- Migrate detekt-sample-extensions/build.gradle to kotlin dsl - [#820](https://github.com/arturbosch/detekt/pull/820)
- Fixed #816 - webpage documentation generator - [#819](https://github.com/arturbosch/detekt/pull/819)
- NestedClassesVisibility doesn't detect violation for object - [#817](https://github.com/arturbosch/detekt/pull/817)
- potential-bugs web page is broken - [#816](https://github.com/arturbosch/detekt/issues/816)
- Commit hook - [#815](https://github.com/arturbosch/detekt/pull/815)
- NestedClassesVisibility doesn't detect violation for "object" - [#814](https://github.com/arturbosch/detekt/issues/814)
- UnusedPrivateMember false positives - [#812](https://github.com/arturbosch/detekt/issues/812)
- deprecation message is printed even when warningThreshold and failThreshold are not used - [#811](https://github.com/arturbosch/detekt/issues/811)
- Added naming ruleset to RC6-3 migration guide - [#810](https://github.com/arturbosch/detekt/pull/810)
- 1.0.0.RC6-3 changelog migration block is incomplete - [#809](https://github.com/arturbosch/detekt/issues/809)
- Add Novoda static analysis plugin to readme - [#806](https://github.com/arturbosch/detekt/pull/806)
- Added filepath to console output when generating reports - [#805](https://github.com/arturbosch/detekt/pull/805)
- Updated EmptyFunctionBlock rule - [#804](https://github.com/arturbosch/detekt/pull/804)
- Updated debt report - [#803](https://github.com/arturbosch/detekt/pull/803)
- When generating reports it would be nice if the path would be printed out as well - [#801](https://github.com/arturbosch/detekt/issues/801)
- False negatives for EmptyFunctionBlock with overridden functions - [#800](https://github.com/arturbosch/detekt/issues/800)
- Updated rule debt values - [#776](https://github.com/arturbosch/detekt/pull/776)
- WIP: Detekt IntelliJ Plugin - [#773](https://github.com/arturbosch/detekt/pull/773)

See all issues at: [RC7](https://github.com/arturbosch/detekt/milestone/32)

#### RC6-4

- NoSuchElementException when running from build.gradle.kts - [#793](https://github.com/arturbosch/detekt/issues/793)
- No files analysed if path contains test - [#792](https://github.com/arturbosch/detekt/issues/792)
- Does detekt include copy/paste detector ? - [#789](https://github.com/arturbosch/detekt/issues/789)
- @Suppress("MaxLineLength") at file-level doesn't work - [#788](https://github.com/arturbosch/detekt/issues/788)
- Update test dependencies and publish versions - [#787](https://github.com/arturbosch/detekt/pull/787)
- Bitrise step to run detekt - [#785](https://github.com/arturbosch/detekt/issues/785)
- Add no tabs rule - [#782](https://github.com/arturbosch/detekt/pull/782)
- Added test case for EmptyCatchBlock rule - [#781](https://github.com/arturbosch/detekt/pull/781)
- Add trailing whitespace rule - [#780](https://github.com/arturbosch/detekt/pull/780)
- EmptyCatchBlock.allowedExceptionNameRegex doesn't seem to be useful - [#779](https://github.com/arturbosch/detekt/issues/779)
- Incorrect behavior for EqualsAlwaysReturnsTrueOrFalse - [#778](https://github.com/arturbosch/detekt/issues/778)
- Thresholds rework - [#774](https://github.com/arturbosch/detekt/pull/774)
- Implemented EndOfSentenceFormat to support urls as last argument - [#772](https://github.com/arturbosch/detekt/pull/772)
- Fixed report of unnecessary parentheses when assigning a lambda to a val - [#770](https://github.com/arturbosch/detekt/pull/770)
- TopLevelPropertyNaming ease UPPER_CASE notation on vals - [#769](https://github.com/arturbosch/detekt/issues/769)
- EndOfSentenceFormat does not work well with urls. - [#768](https://github.com/arturbosch/detekt/issues/768)
- UnnecessaryParentheses false positive when copying with a function - [#767](https://github.com/arturbosch/detekt/issues/767)
- Added excludeClassPattern for VariableNaming rule - [#765](https://github.com/arturbosch/detekt/pull/765)
- Add class name exclusion or pattern exclusion for VariableNaming rule - [#764](https://github.com/arturbosch/detekt/issues/764)
- update kotlin to v1.2.30 - [#763](https://github.com/arturbosch/detekt/pull/763)
- update gradle to v4.6 - [#762](https://github.com/arturbosch/detekt/pull/762)
- Remove useTabs from sample Maven config in README - [#761](https://github.com/arturbosch/detekt/pull/761)
- Change exceptions property to throwable - #109 - [#760](https://github.com/arturbosch/detekt/pull/760)
- Overriding defaults throws java.util.ConcurrentModificationException - [#758](https://github.com/arturbosch/detekt/issues/758)
- Rule: Unnecessary brackets - [#756](https://github.com/arturbosch/detekt/issues/756)
- What do you think about making configuration type safe? - [#755](https://github.com/arturbosch/detekt/issues/755)
- 0 kotlin files were analyzed Android Setup. - [#754](https://github.com/arturbosch/detekt/issues/754)
- Space before curly brackets - [#753](https://github.com/arturbosch/detekt/issues/753)
- Fix in yaml syntax - [#752](https://github.com/arturbosch/detekt/pull/752)
- Added ignoreSingleWhenExpression config for ComplexMethod rule - [#750](https://github.com/arturbosch/detekt/pull/750)
- Generate documentation inside website folder - [#746](https://github.com/arturbosch/detekt/pull/746)
- Removed obsolete OptionalReturnKeyword rule - [#745](https://github.com/arturbosch/detekt/pull/745)
- add task inputs/outputs to detektCheck task - [#743](https://github.com/arturbosch/detekt/pull/743)
- define inputs/outputs for detekt-generator task - [#742](https://github.com/arturbosch/detekt/pull/742)
- Fixes for .kts files - [#741](https://github.com/arturbosch/detekt/pull/741)
- EqualsAlwaysReturnsTrueOrFalse for multiple returns - [#740](https://github.com/arturbosch/detekt/pull/740)
- Getting EqualsAlwaysReturnsTrueOrFalse incorrectly (I think) - [#738](https://github.com/arturbosch/detekt/issues/738)
- add messages to all CodeSmells - [#737](https://github.com/arturbosch/detekt/pull/737)
- Added allowedExceptionNameRegex config for EmptyCatchBlock - [#736](https://github.com/arturbosch/detekt/pull/736)
- Extend EmptyCatchBlock - [#734](https://github.com/arturbosch/detekt/issues/734)
- Add MayBeConst rule - [#733](https://github.com/arturbosch/detekt/pull/733)
- Simplify `SingleAssign` by using `lateinit` and add tests for it - [#732](https://github.com/arturbosch/detekt/pull/732)
- Style rule: val constants can be declared as const - [#731](https://github.com/arturbosch/detekt/issues/731)
- Allow aliases for rules to allow for suppression - [#730](https://github.com/arturbosch/detekt/pull/730)
- More idiomatic build configuration - [#729](https://github.com/arturbosch/detekt/pull/729)
- Ignored classes with supertype entries from UnnecessaryAbstractClass - [#728](https://github.com/arturbosch/detekt/pull/728)
- Handle @Suppress("UNCHECKED_CAST") for UnsafeCast rule - [#726](https://github.com/arturbosch/detekt/issues/726)
- The "detektCheck" Gradle task is never UP-TO-DATE - [#725](https://github.com/arturbosch/detekt/issues/725)
- Fixed incorrect behavior of EmptyDefaultConstructor - #723 - [#724](https://github.com/arturbosch/detekt/pull/724)
- EmptyDefaultConstructor incorrect behavior - [#723](https://github.com/arturbosch/detekt/issues/723)
- OptionalReturnKeyword incorrect behavior - [#722](https://github.com/arturbosch/detekt/issues/722)
- Fix message for TooGenericExceptionCaught - [#720](https://github.com/arturbosch/detekt/pull/720)
- Gradle plugin rework - new dsl - [#719](https://github.com/arturbosch/detekt/pull/719)
- Corrected documentation in EnumNaming - [#717](https://github.com/arturbosch/detekt/pull/717)
- EnumNaming should also allow digits - [#716](https://github.com/arturbosch/detekt/issues/716)
- External dependency 'detekt-cli' not found when using Gradle plugin in submodule - [#713](https://github.com/arturbosch/detekt/issues/713)
- Quickfixes API - [#710](https://github.com/arturbosch/detekt/issues/710)

See all issues at: [RC6-4](https://github.com/arturbosch/detekt/milestone/33)


##### Migration

- changed threshold definition (`value >= threshold` instead of `value > threshold`)
- build failure threshold properties are now deprecated in favor of the new `maxIssues` property.
- `warningThreshold` and `failThreshold` will get removed in a later release, a deprecation warning is printed to the console.

```yaml
build:
  warningThreshold: 5 // deprecated
  failThreshold: 10 // deprecated
  maxIssues: 10
```

#### RC6-3

- Improve documentation of sample project - #438 - [#712](https://github.com/arturbosch/detekt/pull/712)
- Added tests for naming rules - [#711](https://github.com/arturbosch/detekt/pull/711)
- Gradle Plugin: Expose a copy of `profiles` - [#709](https://github.com/arturbosch/detekt/pull/709)
- update gradle to v4.5 - [#708](https://github.com/arturbosch/detekt/pull/708)
- EmptyClassBlock should not be reported for objects deriving from superclasses - [#707](https://github.com/arturbosch/detekt/issues/707)
- remove FeatureEnvy class - [#706](https://github.com/arturbosch/detekt/pull/706)
- Tooling: Accessing the profiles defined by the user - [#705](https://github.com/arturbosch/detekt/issues/705)
- make detekt-cli tests depend on updated documentation/config - [#704](https://github.com/arturbosch/detekt/pull/704)
- KDocStyle MultiRule with EndOfSentenceFormat Rule - [#703](https://github.com/arturbosch/detekt/pull/703)
- Add documentation for style rules - [#702](https://github.com/arturbosch/detekt/pull/702)
- Add documentation for complexity rules - [#701](https://github.com/arturbosch/detekt/pull/701)
- [Question] How to reference to a version of the plugin in the main project before it's even published? - [#700](https://github.com/arturbosch/detekt/issues/700)
- Rule: KDoc's first sentence should have a proper end - [#699](https://github.com/arturbosch/detekt/issues/699)
- Added documentation for naming rule set - [#697](https://github.com/arturbosch/detekt/pull/697)
- update kotlin to v1.2.20 - [#696](https://github.com/arturbosch/detekt/pull/696)
- Added `excludeAnnotatedClasses` to UseDataClass - #694 - [#695](https://github.com/arturbosch/detekt/pull/695)
- Consider adding `excludeAnnotatedClasses` to UseDataClass. - [#694](https://github.com/arturbosch/detekt/issues/694)
- Fix compliant case for UntilInsteadOfRangeTo rule - [#693](https://github.com/arturbosch/detekt/pull/693)
- Documentation generator truncates config descriptions - [#692](https://github.com/arturbosch/detekt/pull/692)
- Documentation generator truncates some configuration descriptions - [#691](https://github.com/arturbosch/detekt/issues/691)
- Introduce "naming" ruleset - [#690](https://github.com/arturbosch/detekt/pull/690)
- Sort Rules in Config & Documentation alphabetically - [#689](https://github.com/arturbosch/detekt/pull/689)
- Default configuration file should be sorted alphabetically - [#688](https://github.com/arturbosch/detekt/issues/688)
- Fix MatchingDeclarationName false positives - [#687](https://github.com/arturbosch/detekt/pull/687)
- MatchingDeclarationName has false positives - [#686](https://github.com/arturbosch/detekt/issues/686)
- UtilityClassWithPublicConstructor reports classes with delegates - [#682](https://github.com/arturbosch/detekt/issues/682)
- `ComplexMethod` rule question - [#680](https://github.com/arturbosch/detekt/issues/680)
- generator: anchor tags are lower case; TOC links to CamelCase - [#678](https://github.com/arturbosch/detekt/issues/678)

See all issues at: [RC6-3](https://github.com/arturbosch/detekt/milestone/31)

##### Migration

- ATTENTION!! The default configuration now uses an alphabetical order. We apologize for any inconveniences
this might cause.
- Introduced **naming** ruleset containing
  - Naming Rules (min/max length, naming regex rules, forbidden name)
  - `MatchingDeclarationName`
  - `MemberNameEqualsClassName`
- Fixed false positives in `UtilityClassWithPublicConstructor`, `MatchingDeclarationName` and `EmptyClassBlock`
- make sure to rerun your baseline (if you ignored some of these)!

#### RC6-2

- Updates two rules to detect violated range expressions outside of loops - [#684](https://github.com/arturbosch/detekt/pull/684)
- Add UntilInsteadOfRangeTo rule - [#676](https://github.com/arturbosch/detekt/pull/676)
- Implement MatchingDeclarationName rule - [#674](https://github.com/arturbosch/detekt/pull/674)
- Consider adding ignoreOptionalParameters to LongParameterList - [#673](https://github.com/arturbosch/detekt/issues/673)
- Fix false negative reporting of non-named argument - Fixes #659 - [#672](https://github.com/arturbosch/detekt/pull/672)
- Change LateInitUsage to LateinitUsage in failfast.yml - [#671](https://github.com/arturbosch/detekt/pull/671)
- Rule: 'rangeTo' or the '..' call can be replaced with 'until' - [#670](https://github.com/arturbosch/detekt/issues/670)
- Treat all compiler warnings as errors - [#669](https://github.com/arturbosch/detekt/pull/669)
- Do not run EmptyFunctionBlock on open functions - [#667](https://github.com/arturbosch/detekt/pull/667)
- EmptyFunctionBlock should not flag functions with open modifier - [#666](https://github.com/arturbosch/detekt/issues/666)
- Do not run EmptyClassBlock on objects of anonymous classes - [#665](https://github.com/arturbosch/detekt/pull/665)
- MatchingDeclarationName rule to match single declaration to file name - [#664](https://github.com/arturbosch/detekt/issues/664)
- Rename *.yaml to *.yml so fixtures use expected line endings - [#661](https://github.com/arturbosch/detekt/pull/661)
- Appveyor: Use default git config - [#660](https://github.com/arturbosch/detekt/pull/660)
- `ignoreNamedArguments` breaks marking non-named magic number params - [#659](https://github.com/arturbosch/detekt/issues/659)
- Run tests on Travis on Oracle JDK 8 & 9 - [#658](https://github.com/arturbosch/detekt/pull/658)

See all issues at: [RC6-2](https://github.com/arturbosch/detekt/milestone/30)

##### Migration

- The new rule `MatchingDeclarationName` is active on default. If a file has only one top-level declaration then the
file name must match the declaration name according to the jetbrains and android style guides.

#### RC6-1

- Added factory function check to MemberNameEqualsClassName - [#653](https://github.com/arturbosch/detekt/pull/653)
- detekt generator tests - rc6 - [#650](https://github.com/arturbosch/detekt/pull/650)
- Rule examples improvement - [#649](https://github.com/arturbosch/detekt/pull/649)
- Improved documentation for rule test cases - [#648](https://github.com/arturbosch/detekt/pull/648)
- Apply compiler and baseline changes to work properly on java 9 - [#647](https://github.com/arturbosch/detekt/pull/647)
- Test coverage rc6 - [#645](https://github.com/arturbosch/detekt/pull/645)
- Implements #643 - MagicNumber ignores default values in ctor properties - [#644](https://github.com/arturbosch/detekt/pull/644)
- update gradle to v4.4.1 - [#642](https://github.com/arturbosch/detekt/pull/642)
- Documentation for Exceptions RuleSet - [#640](https://github.com/arturbosch/detekt/pull/640)
- Documentation for "emtpy" rules - [#639](https://github.com/arturbosch/detekt/pull/639)
- Documentation for documentation rules - [#638](https://github.com/arturbosch/detekt/pull/638)
- Change variable min & max length to match IntelliJ. - [#635](https://github.com/arturbosch/detekt/pull/635)
- Remove "native filesystem" warning on Windows - [#634](https://github.com/arturbosch/detekt/pull/634)
- Failed to initialize native filesystem for Windows - [#630](https://github.com/arturbosch/detekt/issues/630)

See all issues at: [RC6-1](https://github.com/arturbosch/detekt/milestone/29)

##### Migration

- Running detekt under Java 9 should work again with newest Kotlin Version
- Ignores default values in constructor properties (MagicNumber) - [#644](https://github.com/arturbosch/detekt/pull/644)

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
        path = "$userHome/.idea"
        codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
        inspectionsProfile = "$userHome/.idea/inspect.xml"
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
        path = "$userHome/.idea"
        codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
        inspectionsProfile = "$userHome/.idea/inspect.xml"
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

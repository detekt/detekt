#!/usr/bin/env sh
# Publishes the final detekt 1.x release. It contains no new code: its only purpose is to tell
# Maven and Gradle consumers that detekt moved to the `dev.detekt` group in 2.x, via the
# <distributionManagement><relocation> block that `-PpublishRelocationInfo=true` adds to every POM.
#
# Relocation cannot be applied retroactively - a POM on Maven Central is immutable - so this only
# reaches users who bump to this version. Anyone pinned to an older 1.x release is unaffected.
#
# Before running:
#   - detekt 2.0.0 must already be on Maven Central (that is what the relocation points at,
#     see `relocationVersion` in build-logic/src/main/kotlin/packaging.gradle.kts)
#   - Versions.DETEKT must be bumped to the relocation release version
#
# Unlike scripts/release.sh this deliberately skips `:detekt-gradle-plugin:publishPlugins`: the
# Gradle Plugin Portal has no concept of relocation, and the 2.x plugin is published under the new
# `dev.detekt` plugin id.
#
# No jars are produced: the release consists of relocation POMs only, because the jar of a relocated
# version is never fetched by any resolver. Hence no `gradle build` step either. Modules with no 2.x
# counterpart and the Gradle plugin markers are not published at all.
set -e
gradle publishAllToMavenCentral --max-workers 1 -PpublishRelocationInfo=true
gradle githubRelease
gradle closeAndReleaseSonatypeStagingRepository

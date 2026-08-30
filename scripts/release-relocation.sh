#!/usr/bin/env sh
# Publishes the final detekt 1.x release as relocation POMs without artifacts.
#
# Before running:
#   - detekt 2.0.0 must already be on Maven Central
#   - Versions.DETEKT must be bumped to the relocation release version
#
# The Gradle Plugin Portal is skipped because it has no relocation mechanism.
set -e
gradle publishAllToMavenCentral --max-workers 1 -PpublishRelocationInfo=true
gradle githubRelease
gradle closeAndReleaseSonatypeStagingRepository

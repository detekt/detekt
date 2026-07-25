#!/usr/bin/env sh
set -e
gradle publishToMavenLocal
gradle build
# Uploads artifacts to Central Portal. After all steps complete, release manually at
# https://central.sonatype.com/publishing — or replace with publishAndReleaseToMavenCentral to release automatically.
# --no-configuration-cache is required due to a vanniktech bug with MavenCentralBuildService serialization:
# https://github.com/vanniktech/gradle-maven-publish-plugin/issues/1264
gradle publishToMavenCentral --no-configuration-cache
gradle :detekt-gradle-plugin:publishPlugins
gradle githubRelease --no-configuration-cache
gradle applyDocVersion

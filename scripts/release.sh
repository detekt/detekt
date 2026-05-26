#!/usr/bin/env sh
set -e
gradle publishToMavenLocal
gradle build
# Uploads artifacts to Central Portal. After all steps complete, release manually at
# https://central.sonatype.com/publishing — or replace with publishAndReleaseToMavenCentral to release automatically.
gradle publishToMavenCentral
gradle :detekt-gradle-plugin:publishPlugins
gradle githubRelease --no-configuration-cache
gradle applyDocVersion

#!/usr/bin/env sh
set -e
gradle publishToMavenLocal
gradle build
gradle publishAllToMavenCentral --max-workers 1
gradle :detekt-gradle-plugin:publishPlugins
gradle githubRelease
gradle applyDocVersion
gradle closeAndReleaseSonatypeStagingRepository

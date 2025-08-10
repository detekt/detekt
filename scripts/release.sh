#!/usr/bin/env sh
set -e
gradle publishToMavenLocal
gradle build
gradle publishToSonatype closeSonatypeStagingRepository --no-configuration-cache
gradle :detekt-gradle-plugin:publishPlugins
gradle githubRelease --no-configuration-cache
gradle applyDocVersion
gradle releaseSonatypeStagingRepository --no-configuration-cache

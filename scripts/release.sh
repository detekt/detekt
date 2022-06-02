#!/usr/bin/env sh
gradle publishToMavenLocal || exit
gradle build || exit
gradle publishAllToMavenCentral --max-workers 1 || exit
gradle :detekt-gradle-plugin:publishPlugins || exit
gradle githubRelease || exit
gradle applyDocVersion || exit
gradle closeAndReleaseRepository || exit

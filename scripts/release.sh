#!/usr/bin/env sh
gradle build || exit
gradle publishAllToMavenCentral --max-workers 1 || exit
gradle publishPlugins || exit
gradle githubRelease || exit
gradle applyDocVersion || exit
gradle closeAndReleaseRepository || exit

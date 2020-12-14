#!/usr/bin/env sh
gradle build publishToMavenLocal -x detekt -x test || exit
gradle build || exit
gradle publishAllPublicationsToMavenCentralRepository --max-workers 1 || exit
gradle publishPlugins -DautomatePublishing=true || exit
gradle githubRelease || exit
gradle applyDocVersion applySelfAnalysisVersion || exit
gradle closeAndReleaseRepository || exit

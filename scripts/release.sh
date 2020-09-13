#!/usr/bin/env sh
gradle build publishToMavenLocal -x detekt -x test
gradle build
gradle publishAllPublicationsToMavenCentralRepository --max-workers 1
gradle publishPlugins -DautomatePublishing=true
gradle githubRelease
gradle applyDocVersion applySelfAnalysisVersion

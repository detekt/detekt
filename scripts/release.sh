#!/usr/bin/env sh
gradle build publishToMavenLocal -x detekt -x test
gradle build
gradle publishDetektPublicationPublicationToBintrayRepository
gradle publishPlugins
gradle githubRelease
gradle applyDocVersion

package io.gitlab.arturbosch.detekt.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import java.net.URI

private val bintrayJCenterUri = URI.create(DefaultRepositoryHandler.BINTRAY_JCENTER_URL)

internal fun checkRequiredRepositoriesAreConfiguredOn(project: Project) {
    project.afterEvaluate {
        val missingJCenter = project.repositories.none {
            it is MavenArtifactRepository && it.url == bintrayJCenterUri
        }

        if (missingJCenter) {
            project.logger.error(
                "The project ${project.path} doesn't have the jcenter() repository in its repositories list; " +
                        "this is required for Detekt to work correctly. Please add jcenter() to the project's " +
                        "repositories { } closure and try again."
            )
        }
    }
}

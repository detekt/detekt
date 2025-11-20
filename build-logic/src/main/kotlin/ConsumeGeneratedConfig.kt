@file:Suppress("MissingPackageDeclaration")

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.dependencies

fun Project.consumeGeneratedConfig(
    fromProject: ProjectDependency,
    fromConfiguration: String,
    forTask: TaskProvider<*>,
) {
    val configurationName = "generatedConfigFor${forTask.name.replaceFirstChar { it.titlecase() }}"
    val generatedConfig = configurations.dependencyScope(configurationName)
    val generatedConfigFiles = configurations.resolvable("${configurationName}Files") {
        extendsFrom(generatedConfig.get())
    }

    dependencies {
        generatedConfig.get()(fromProject) {
            targetConfiguration = fromConfiguration
        }
    }

    forTask.configure {
        inputs.files(generatedConfigFiles)
            .withPropertyName(generatedConfig.name)
            .withPathSensitivity(PathSensitivity.RELATIVE)
    }
}

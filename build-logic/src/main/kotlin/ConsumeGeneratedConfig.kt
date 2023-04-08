import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.dependencies

fun Project.consumeGeneratedConfig(fromProject: ProjectDependency, fromConfiguration: String, forTask: String) {
    val configurationName = "generatedConfigFor${forTask.replaceFirstChar { it.titlecase() }}"
    val generatedConfig = configurations.create(configurationName).apply {
        isCanBeConsumed = false
        isCanBeResolved = true
    }

    dependencies {
        generatedConfig(fromProject) {
            targetConfiguration = fromConfiguration
        }
    }

    tasks.named(forTask).configure {
        inputs.files(generatedConfig)
            .withPropertyName(generatedConfig.name)
            .withPathSensitivity(PathSensitivity.RELATIVE)
    }
}

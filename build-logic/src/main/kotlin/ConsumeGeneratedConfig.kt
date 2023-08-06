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
    val generatedConfig = configurations.create(configurationName).apply {
        isCanBeConsumed = false
        isCanBeResolved = true
    }

    dependencies {
        generatedConfig(fromProject) {
            targetConfiguration = fromConfiguration
        }
    }

    forTask.configure {
        inputs.files(generatedConfig)
            .withPropertyName(generatedConfig.name)
            .withPathSensitivity(PathSensitivity.RELATIVE)
    }
}

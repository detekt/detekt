import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue

fun Project.consumeGeneratedConfig(fromProject: ProjectDependency, fromConfiguration: String, forTask: String) {
    val generatedConfig by configurations.creating {
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

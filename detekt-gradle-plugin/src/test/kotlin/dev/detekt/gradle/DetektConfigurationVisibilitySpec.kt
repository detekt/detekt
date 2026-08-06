package dev.detekt.gradle

import dev.detekt.gradle.plugin.DetektPlugin
import dev.detekt.gradle.plugin.internal.supportsConfigurationVisibility
import dev.detekt.gradle.testkit.DslGradleRunner
import dev.detekt.gradle.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.kotlin.dsl.apply
import org.junit.jupiter.api.Test

class DetektConfigurationVisibilitySpec {

    private val gradleRunner = DslGradleRunner(
        projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
        buildFileName = "build.gradle.kts",
        projectScript = {
            apply<DetektPlugin>()
        },
    ).also { it.setupProject() }

    /**
     * The deprecated `visible` flag must only be touched on the Gradle versions that still
     * honour it; everywhere else the configurations keep the default.
     */
    @Test
    fun `only sets the deprecated visible flag on Gradle versions that still honour it`() {
        val project = gradleRunner.buildProject()

        val expectedVisible = !supportsConfigurationVisibility

        @Suppress("DEPRECATION")
        assertThat(project.configurations.getByName("detekt").isVisible).isEqualTo(expectedVisible)

        @Suppress("DEPRECATION")
        assertThat(project.configurations.getByName("detektPlugins").isVisible)
            .isEqualTo(expectedVisible)
    }
}

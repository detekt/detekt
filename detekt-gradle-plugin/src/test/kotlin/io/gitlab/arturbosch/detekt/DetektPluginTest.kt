package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Task
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.testfixtures.ProjectBuilder
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DetektPluginTest : Spek({
    describe("detekt plugin") {

        fun Task.dependencies() = taskDependencies.getDependencies(this)

        it("lazily adds detekt as a dependency of the `check` task") {
            val project = ProjectBuilder.builder().build()

            /* Ordering here is important - to prove lazily adding the dependency works the LifecycleBasePlugin must be
             * added to the project after the detekt plugin. */
            project.pluginManager.apply(DetektPlugin::class.java)
            project.pluginManager.apply(LifecycleBasePlugin::class.java)

            assertThat(project.tasks.getAt("check").dependencies().map {it.name }).contains("detekt")
        }
    }
})

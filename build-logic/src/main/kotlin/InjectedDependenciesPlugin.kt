import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.domainObjectContainer

class InjectedDependenciesPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("generateClasspaths", GenerateClasspathsTask::class.java).configure {
            targetDir.convention(project.layout.buildDirectory.dir("detektClasspathMetadata"))
        }
    }
}

interface InjectedDependencySet {
    @get:Input
    val name: String

    @get:Classpath
    val injectedClasspath: ConfigurableFileCollection
}

abstract class GenerateClasspathsTask : DefaultTask() {
    @get:Nested
    val injectedDependencies = project.objects.domainObjectContainer(InjectedDependencySet::class)

    @get:OutputDirectory
    abstract val targetDir: DirectoryProperty

    @TaskAction
    fun doStuff() {
        val writeString = injectedDependencies.asMap.map {
            val classpathString =
                it.value.injectedClasspath.files.joinToString(java.io.File.pathSeparator) { it.invariantSeparatorsPath }
            "${it.key}-classpath=$classpathString"
        }
        targetDir.get().asFile.mkdirs()
        targetDir.file("detekt-classpath.properties").get().asFile.writeText(writeString.joinToString("\n"))
    }
}

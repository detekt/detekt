import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class InjectedDependenciesPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("injectedDependencies", InjectedDependenciesExtension::class.java)
        project.tasks.register("generateClasspaths", GenerateClasspathsTask::class.java).configure {
            injectedDependencySets.addAllLater(project.provider { extension.classpaths })
            targetDir.convention(project.layout.buildDirectory.dir("detektClasspathMetadata"))
        }
    }
}

interface InjectedDependenciesExtension {
    val classpaths: NamedDomainObjectContainer<InjectedDependencySet>
}

interface InjectedDependencySet {
    @get:Input
    val name: String

    @get:Classpath
    val injectedClasspath: ConfigurableFileCollection
}

abstract class GenerateClasspathsTask : DefaultTask() {
    @get:Nested
    val injectedDependencySets = project.objects.namedDomainObjectList(InjectedDependencySet::class.java)

    @get:OutputDirectory
    abstract val targetDir: DirectoryProperty

    @TaskAction
    fun doStuff() {
        val writeString = injectedDependencySets.asMap.map {
            val classpathString =
                it.value.injectedClasspath.files.joinToString(java.io.File.pathSeparator) { it.invariantSeparatorsPath }
            "${it.key}-classpath=$classpathString"
        }
        targetDir.get().asFile.mkdirs()
        targetDir.file("detekt-classpath.properties").get().asFile.writeText(writeString.joinToString("\n"))
    }
}

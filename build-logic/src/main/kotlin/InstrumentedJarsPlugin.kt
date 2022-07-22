import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectList
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponentFactory
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

class InstrumentedJarsPlugin @Inject constructor(
    private val softwareComponentFactory: SoftwareComponentFactory
) : Plugin<Project> {
    override fun apply(project: Project) {
        // create an adhoc component
        val adhocComponent = softwareComponentFactory.adhoc("myAdhocComponent")
        // add it to the list of components that this project declares
        project.components.add(adhocComponent)

        val ex = project.extensions.create("injected", InjectedExtension::class.java)
        val newTask = project.tasks.register("generateClasspaths", InjectedTask::class.java)

        newTask.configure {
            things.addAllLater(project.provider {ex.classpaths})
            fileOutput.convention(project.layout.buildDirectory.file("detektClasspath.properties"))
        }
    }
}

interface InjectedExtension {
    val classpaths: NamedDomainObjectContainer<Thing>
}

interface Thing {
    @get:Input
    val name: String

    @get:Classpath
    val classpath: ConfigurableFileCollection
}

abstract class InjectedTask: DefaultTask() {
    @get:Nested
    val things: NamedDomainObjectList<Thing> = project.objects.namedDomainObjectList(Thing::class.java)

    @OutputFile
    val fileOutput: RegularFileProperty = project.objects.fileProperty()

    @TaskAction
    fun doStuff() {
        val writeString = things.asMap.map {
            "${it.key}-classpath=${it.value.classpath.files.joinToString(java.io.File.pathSeparator) { it.invariantSeparatorsPath }}"
        }
        fileOutput.get().asFile.writeText(writeString.joinToString("\n"))
    }
}

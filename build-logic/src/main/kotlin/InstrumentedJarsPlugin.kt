import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponentFactory
import javax.inject.Inject

class InstrumentedJarsPlugin @Inject constructor(
    private val softwareComponentFactory: SoftwareComponentFactory
) : Plugin<Project> {
    override fun apply(project: Project) {
        // create an adhoc component
        val adhocComponent = softwareComponentFactory.adhoc("myAdhocComponent")
        // add it to the list of components that this project declares
        project.components.add(adhocComponent)
    }
}

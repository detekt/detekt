import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class CopyDirTask : DefaultTask() {

    @get:InputDirectory
    abstract val source: DirectoryProperty

    @get:OutputDirectory
    abstract val target: DirectoryProperty

    @get:Input
    abstract val placeholderName: Property<String>

    @get:Input
    abstract val placeholderValue: Property<String>

    @get:Inject
    abstract val fs: FileSystemOperations

    @TaskAction
    fun run() {
        fs.copy {
            from(source)
            into(target)
            filter { line ->
                line.replace(placeholderName.get().toRegex(), placeholderValue.get())
            }
        }
    }
}

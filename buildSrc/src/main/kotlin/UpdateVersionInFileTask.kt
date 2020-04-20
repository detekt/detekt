import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import java.io.File

open class UpdateVersionInFileTask : DefaultTask(), Runnable {

    private val fileProp: RegularFileProperty = project.objects.fileProperty()
    @get:InputFile
    var fileToUpdate: File
        get() = fileProp.get().asFile
        set(value) {
            fileProp.set(value)
        }

    private val containingProp: Property<String> = project.objects.property()

    @get:Input
    var linePartToFind: String
        get() = containingProp.get()
        set(value) {
            containingProp.set(value)
        }

    @Input
    var lineTransformation: ((String) -> String)? = null

    @TaskAction
    override fun run() {
        val transformation = lineTransformation
        checkNotNull(transformation) { "lineTransformation property is not set." }
        val newContent = fileToUpdate.readLines()
            .joinToString(LN) { if (it.contains(linePartToFind)) transformation(it) else it }
        fileToUpdate.writeText("$newContent$LN")
    }

    companion object {
        val LN: String = System.lineSeparator()
    }
}

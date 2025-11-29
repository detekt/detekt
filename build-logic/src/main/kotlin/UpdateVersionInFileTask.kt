@file:Suppress("MissingPackageDeclaration")

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

abstract class UpdateVersionInFileTask : DefaultTask() {

    @get:InputFile
    abstract val fileToUpdate: RegularFileProperty

    @get:Input
    abstract val linePartToFind: Property<String>

    @get:Input
    abstract val lineTransformation: Property<String>

    @TaskAction
    fun run() {
        val newContent = fileToUpdate.asFile.get().readLines()
            .joinToString(LN) { if (it.contains(linePartToFind.get())) lineTransformation.get() else it }
        fileToUpdate.asFile.get().writeText("$newContent$LN")
    }

    companion object {
        val LN: String = System.lineSeparator()
    }
}

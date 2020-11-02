package io.gitlab.arturbosch.detekt.gradle

import org.gradle.api.internal.file.AbstractFileCollection
import java.io.File

class TestFileCollection(private vararg val files: File) : AbstractFileCollection() {

    override fun getFiles(): MutableSet<File> = files.toMutableSet()
    override fun getDisplayName(): String = "FileCol"
}

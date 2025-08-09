package dev.detekt.gradle.plugin.testkit

import org.intellij.lang.annotations.Language

class ProjectLayout(
    val numberOfSourceFilesInRootPerSourceDir: Int,
    val numberOfFindingsInRootPerSourceDir: Int = 0,
    val srcDirs: List<String> = listOf("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin"),
) {

    private val mutableSubmodules: MutableList<Submodule> = mutableListOf()
    val submodules: List<Submodule>
        get() = mutableSubmodules

    @Suppress("LongParameterList")
    fun addSubmodule(
        name: String,
        numberOfSourceFilesPerSourceDir: Int,
        numberOfFindings: Int = 0,
        @Language("gradle.kts")
        buildFileContent: String? = null,
        srcDirs: List<String> = this.srcDirs,
        baselineFiles: List<String> = emptyList(),
    ) {
        val submodule = Submodule(
            name = name,
            numberOfSourceFilesPerSourceDir = numberOfSourceFilesPerSourceDir,
            numberOfFindings = numberOfFindings,
            buildFileContent = buildFileContent,
            srcDirs = srcDirs,
            baselineFiles = baselineFiles,
        )
        mutableSubmodules.add(submodule)
    }
}

data class Submodule(
    val name: String,
    val numberOfSourceFilesPerSourceDir: Int,
    val numberOfFindings: Int,
    @Language("gradle.kts")
    val buildFileContent: String?,
    val srcDirs: List<String>,
    val baselineFiles: List<String>,
)

package io.gitlab.arturbosch.detekt.testkit

class ProjectLayout(
    val numberOfSourceFilesInRootPerSourceDir: Int,
    val numberOfCodeSmellsInRootPerSourceDir: Int = 0,
    val srcDirs: List<String> = listOf("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")
) {

    private val mutableSubmodules: MutableList<Submodule> = mutableListOf()
    val submodules: List<Submodule>
        get() = mutableSubmodules

    @Suppress("LongParameterList")
    fun addSubmodule(
        name: String,
        numberOfSourceFilesPerSourceDir: Int,
        numberOfCodeSmells: Int = 0,
        buildFileContent: String? = null,
        srcDirs: List<String> = this.srcDirs,
        baselineFiles: List<String> = emptyList(),
    ) {
        val submodule = Submodule(
            name = name,
            numberOfSourceFilesPerSourceDir = numberOfSourceFilesPerSourceDir,
            numberOfCodeSmells = numberOfCodeSmells,
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
    val numberOfCodeSmells: Int,
    val buildFileContent: String?,
    val srcDirs: List<String>,
    val baselineFiles: List<String>,
)

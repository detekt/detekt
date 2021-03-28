package io.gitlab.arturbosch.detekt.testkit

class ProjectLayout(
    val numberOfSourceFilesInRootPerSourceDir: Int,
    val numberOfCodeSmellsInRootPerSourceDir: Int = 0,
    val srcDirs: List<String> = listOf("src/main/java", "src/test/java")
) {

    private val mutableSubmodules: MutableList<Submodule> = mutableListOf()
    val submodules: List<Submodule>
        get() = mutableSubmodules

    fun addSubmodule(
        name: String,
        numberOfSourceFilesPerSourceDir: Int,
        numberOfCodeSmells: Int = 0,
        buildFileContent: String? = null,
        srcDirs: List<String> = this.srcDirs
    ) {
        val submodule = Submodule(
            name = name,
            numberOfSourceFilesPerSourceDir = numberOfSourceFilesPerSourceDir,
            numberOfCodeSmells = numberOfCodeSmells,
            buildFileContent = buildFileContent,
            srcDirs = srcDirs
        )
        mutableSubmodules.add(submodule)
    }
}

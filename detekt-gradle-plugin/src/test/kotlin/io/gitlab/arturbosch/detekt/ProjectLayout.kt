package io.gitlab.arturbosch.detekt

data class ProjectLayout(
    val numberOfSourceFilesInRootPerSourceDir: Int,
    val numberOfCodeSmellsInRootPerSourceDir: Int = 0,
    val submodules: List<Submodule> = emptyList(),
    val srcDirs: List<String> = listOf("src/main/java")
) {

    fun withSubmodule(
        name: String,
        numberOfSourceFilesPerSourceDir: Int,
        numberOfCodeSmells: Int = 0,
        detektConfig: String? = null,
        srcDirs: List<String> = this.srcDirs
    ): ProjectLayout {

        val submodule = Submodule(
            name = name,
            numberOfSourceFilesPerSourceDir = numberOfSourceFilesPerSourceDir,
            numberOfCodeSmells = numberOfCodeSmells,
            detektConfig = detektConfig,
            srcDirs = srcDirs
        )
        return copy(
            submodules = submodules + submodule
        )
    }
}

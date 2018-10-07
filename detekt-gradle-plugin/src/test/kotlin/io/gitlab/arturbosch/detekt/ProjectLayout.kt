package io.gitlab.arturbosch.detekt

data class ProjectLayout(
		val numberOfSourceFilesInRootPerSourceDir: Int,
		val submodules: List<Submodule> = emptyList(),
		val srcDirs: List<String> = listOf("src/main/java")
) {

	fun withSubmodule(name: String,
					  numberOfSourceFilesInRootPerSourceDir: Int,
					  detektConfig: String? = null,
					  srcDirs: List<String> = this.srcDirs
	) = copy(submodules = (submodules + listOf(Submodule(name, numberOfSourceFilesInRootPerSourceDir, detektConfig, srcDirs))))
}
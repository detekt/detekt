package io.gitlab.arturbosch.detekt

data class Submodule(
    val name: String,
    val numberOfSourceFilesPerSourceDir: Int,
    val numberOfCodeSmells: Int,
    val detektConfig: String?,
    val srcDirs: List<String>
)

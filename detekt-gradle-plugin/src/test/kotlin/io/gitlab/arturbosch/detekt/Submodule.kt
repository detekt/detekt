package io.gitlab.arturbosch.detekt

data class Submodule(
    val name: String,
    val numberOfSourceFiles: Int,
    val detektConfig: String?,
    val srcDirs: List<String>
)

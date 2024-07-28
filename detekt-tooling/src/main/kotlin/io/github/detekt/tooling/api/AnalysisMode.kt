package io.github.detekt.tooling.api

@Suppress("EnumEntryName") // we use lower case enum names as the enum values are exposed in CLI --help
enum class AnalysisMode {
    full,
    light,
}

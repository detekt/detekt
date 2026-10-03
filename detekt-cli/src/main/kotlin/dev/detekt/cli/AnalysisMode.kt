package dev.detekt.cli

import dev.detekt.tooling.api.AnalysisMode

@Suppress("EnumEntryName") // we use lower case enum names as the enum values are exposed in CLI --help
enum class AnalysisMode {
    full,
    light,
    ;

    fun toTooling(): AnalysisMode =
        when (this) {
            full -> AnalysisMode.Full
            light -> AnalysisMode.Light
        }
}

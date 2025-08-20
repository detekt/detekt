package dev.detekt.tooling.api

@Suppress("EnumEntryName") // we use lower case enum names as the enum values are exposed in CLI --help
enum class AnalysisMode {
    /**
     * Allows rules to analyse the PSI & AST of files and also use additional information from the compiler like types,
     * symbols and smart casts. [light] mode is faster but rules that use additional compiler information will not run.
     */
    full,

    /**
     * Allows rules to analyse the PSI & AST of files only. Use [full] mode to allow rules to use additional information
     * from the compiler like types, symbols and smart casts.
     */
    light,
}

package dev.detekt.tooling.api

enum class AnalysisMode {
    /**
     * Allows rules to analyse the PSI & AST of files and also use additional information from the compiler like types,
     * symbols and smart casts. [Light] mode is faster but rules that use additional compiler information will not run.
     */
    Full,

    /**
     * Allows rules to analyse the PSI & AST of files only. Use [Full] mode to allow rules to use additional information
     * from the compiler like types, symbols and smart casts.
     */
    Light,
}

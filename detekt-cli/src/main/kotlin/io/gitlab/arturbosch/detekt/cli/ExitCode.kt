package io.gitlab.arturbosch.detekt.cli

enum class ExitCode(val number: Int) {
    NORMAL_RUN(0),
    UNEXPECTED_DETEKT_ERROR(1),
    MAX_ISSUES_REACHED(2),
    INVALID_CONFIG(3),
}

package io.github.detekt.tooling.api

enum class ExitStatus(val code: Int) {

    NORMAL_RUN(0),
    UNEXPECTED_DETEKT_ERROR(1),
    MAX_ISSUES_REACHED(2),
    INVALID_CONFIG(3),
}

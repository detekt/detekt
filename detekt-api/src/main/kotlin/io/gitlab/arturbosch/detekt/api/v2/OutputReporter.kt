package io.gitlab.arturbosch.detekt.api.v2

interface OutputReporter {

    fun render(detektion: Detektion): String?
}

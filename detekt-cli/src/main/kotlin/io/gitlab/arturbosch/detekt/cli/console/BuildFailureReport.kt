package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.cli.getOrComputeWeightedAmountOfIssues
import io.gitlab.arturbosch.detekt.cli.isValidAndSmallerOrEqual
import io.gitlab.arturbosch.detekt.cli.maxIssues

class BuildFailureReport : ConsoleReport() {

    override val priority: Int = Int.MIN_VALUE

    private var config: Config by SingleAssign()

    override fun init(config: Config) {
        this.config = config
    }

    override fun render(detektion: Detektion): String? {
        val amount = detektion.getOrComputeWeightedAmountOfIssues(config)
        val maxIssues = config.maxIssues()
        return when {
            maxIssues.isValidAndSmallerOrEqual(amount) -> {
                "Build failed with $amount weighted issues (threshold defined was $maxIssues).".red()
            }
            amount > 0 && maxIssues != -1 -> {
                "Build succeeded with $amount weighted issues (threshold defined was $maxIssues).".yellow()
            }
            else -> null
        }
    }
}

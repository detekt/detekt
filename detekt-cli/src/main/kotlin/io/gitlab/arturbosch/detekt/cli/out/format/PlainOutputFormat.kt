package io.gitlab.arturbosch.detekt.cli.out.format

import io.gitlab.arturbosch.detekt.api.Finding
import java.nio.file.Path

class PlainOutputFormat(report: Path) : OutputFormat(report) {
    override fun render(smells: List<Finding>): String = smells.map { it.compactWithSignature() }.joinToString("\n")
}

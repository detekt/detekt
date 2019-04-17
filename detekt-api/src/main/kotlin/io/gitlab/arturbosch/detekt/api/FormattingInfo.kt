package io.gitlab.arturbosch.detekt.api

import java.nio.file.Path

/**
 * Information that will help format the file.
 *
 * @author Nivaldo Bondança
 */
class FormattingInfo(
    val path: Path,
    val formattedContent: String
)

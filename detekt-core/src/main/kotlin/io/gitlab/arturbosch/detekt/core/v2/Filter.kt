package io.gitlab.arturbosch.detekt.core.v2

import io.gitlab.arturbosch.detekt.api.v2.NewIssue
import java.nio.file.Path

interface Filter {
    fun filter(path: Path): Boolean

    fun filter(issue: NewIssue): Boolean
}

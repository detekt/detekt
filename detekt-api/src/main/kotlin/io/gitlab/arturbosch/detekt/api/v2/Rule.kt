package io.gitlab.arturbosch.detekt.api.v2

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import org.jetbrains.kotlin.psi.KtFile

// This will be a sealed interface in 1.5
interface Rule {
    val id: String
    val description: String
}

interface PlainRule : Rule, (KtFile) -> List<Issue> {

    override fun invoke(file: KtFile): List<Issue>
}

interface TypeSolvingRule : Rule, (KtFile, ResolvedContext) -> List<Issue> {

    override fun invoke(file: KtFile, resolvedContext: ResolvedContext): List<Issue>
}

interface Issue {
    val entity: Entity
    val message: String
    val severity: SeverityLevel
    val debt: Debt
    val autoCorrectable: Boolean
}

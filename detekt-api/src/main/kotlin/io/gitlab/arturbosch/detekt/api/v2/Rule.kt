package io.gitlab.arturbosch.detekt.api.v2

import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

// This will be a sealed interface in 1.5
interface Rule {
    // This should not be here but the code is not ready yet to remove it
    val issue: Issue
}

interface PlainRule : Rule, (KtFile) -> List<NewIssue> {

    override fun invoke(file: KtFile): List<NewIssue>
}

interface TypeSolvingRule : Rule, (KtFile, BindingContext, CompilerResources) -> List<NewIssue> {

    override fun invoke(file: KtFile, binding: BindingContext, resources: CompilerResources): List<NewIssue>
}

interface NewIssue {
    val entity: Entity
    val message: String
    // This should be here but the code is not ready yet
    //val severity: SeverityLevel
    //val debt: Debt
}

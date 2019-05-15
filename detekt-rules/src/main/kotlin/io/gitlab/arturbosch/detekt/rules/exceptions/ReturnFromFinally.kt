package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * Reports all `return` statements in `finally` blocks.
 * Using `return` statements in `finally` blocks can discard and hide exceptions that are thrown in the `try` block.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         throw MyException()
 *     } finally {
 *         return // prevents MyException from being propagated
 *     }
 * }
 * </noncompliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class ReturnFromFinally(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ReturnFromFinally", Severity.Defect,
        "Do not return within a finally statement. This can discard exceptions.", Debt.TWENTY_MINS)

    override fun visitFinallySection(finallySection: KtFinallySection) {
        val innerFunctions = finallySection.finalExpression
            .collectByType<KtNamedFunction>()
            .toSet()
        finallySection.finalExpression
            .collectByType<KtReturnExpression>()
            .filter { isNotInInnerFunction(it, innerFunctions) }
            .forEach { report(CodeSmell(issue, Entity.from(it), issue.description)) }
    }

    private fun isNotInInnerFunction(
        returnStmts: KtReturnExpression,
        childFunctions: Set<KtNamedFunction>
    ): Boolean = !returnStmts.parents.any { childFunctions.contains(it) }
}

package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * This rule will report any public function which does not have the required documentation.
 * If the codebase should have documentation on all public functions enable this rule to enforce this.
 * Overridden functions are excluded by this rule.
 *
 * Optionally, this rule can also report missing documentation for undocumented parameters (e.g. missing @param tag) and
 * receivers (e.g. missing @receiver tag).
 */
class UndocumentedPublicFunction(config: Config = Config.empty) : Rule(config) {
    @Configuration("If set to true, this rule will also report type and value parameters that are not " +
        "properly documented (either using a '@param' tag on the function's KDoc or by directly documenting them).")
    private val reportUndocumentedParameter by config(false)

    @Configuration("If set to true, this rule will also report receivers that are not documented using a " +
        "'@receiver' tag on the function's KDoc.")
    private val reportUndocumentedReceiver by config(false)

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Public functions require documentation.",
        Debt.TWENTY_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.funKeyword == null && function.isLocal) return

        if (function.shouldBeDocumented()) {
            if (function.docComment == null) {
                report(createFinding(function, "function ${function.nameAsSafeName}"))
            }

            if (reportUndocumentedParameter) {
                function.valueParameters.forEach { parameter: KtParameter ->
                    if (parameter.isNotDocumented(function)) {
                        report(createFinding(parameter, "parameter ${parameter.nameAsSafeName}"))
                    }
                }
                function.typeParameters.forEach { parameter ->
                    if (parameter.isNotDocumented(function)) {
                        report(createFinding(parameter, "type parameter ${parameter.nameAsSafeName}"))
                    }
                }
            }

            if (reportUndocumentedReceiver && function.receiverTypeReference != null && function.isReceiverNotDocumented()) {
                report(createFinding(function, "receiver of the function ${function.nameAsSafeName}"))
            }
        }
    }

    private fun createFinding(location: KtNamedDeclaration, culprit: String): Finding =
        CodeSmell(issue, Entity.atName(location), "The $culprit is missing documentation.")

    private fun KtNamedFunction.shouldBeDocumented() =
        parents.filterIsInstance<KtClassOrObject>().all { it.isPublic } && isPublicNotOverridden()

    private fun KtParameter.isNotDocumented(function: KtNamedFunction): Boolean {
        if (docComment != null) return false
        val paramName = name ?: return true
        return function.getEntriesForTagAndSubject("param", paramName).isEmpty()
    }

    private fun KtTypeParameter.isNotDocumented(function: KtNamedFunction): Boolean {
        val paramName = name ?: return true
        return function.getEntriesForTagAndSubject("param", paramName).isEmpty()
    }

    private fun KtNamedFunction.isReceiverNotDocumented() = getEntriesForTagAndSubject("receiver").isEmpty()
}

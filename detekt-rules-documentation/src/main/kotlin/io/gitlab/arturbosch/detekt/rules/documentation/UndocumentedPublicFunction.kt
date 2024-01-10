package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isProtected
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * This rule will report any public function which does not have the required documentation.
 * If the codebase should have documentation on all public functions enable this rule to enforce this.
 * Overridden functions are excluded by this rule.
 */
class UndocumentedPublicFunction(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "Public functions require documentation.",
    )

    @Configuration("if protected functions should be searched")
    private val searchProtectedFunction: Boolean by config(false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.funKeyword == null && function.isLocal) return

        if (function.docComment == null && function.shouldBeDocumented()) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    "The function ${function.nameAsSafeName} is missing documentation."
                )
            )
        }
    }

    private fun KtNamedFunction.shouldBeDocumented() =
        if (searchProtectedFunction) {
            parents.filterIsInstance<KtClassOrObject>().all { it.isPublic || it.isProtected() }
        } else {
            parents.filterIsInstance<KtClassOrObject>().all { it.isPublic }
        } &&
            isPublicNotOverridden(searchProtectedFunction)
}

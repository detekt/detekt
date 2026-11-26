package dev.detekt.rules.comments

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isProtected
import dev.detekt.psi.isPublicNotOverridden
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * This rule will report any public function which does not have the required documentation.
 * If the codebase should have documentation on all public functions enable this rule to enforce this.
 * Overridden functions are excluded by this rule.
 *
 * Optionally, this rule can also report the receivers of extension functions that are not documented using a
 * `@receiver` tag. This is disabled by default.
 */
class UndocumentedPublicFunction(config: Config) : Rule(config, "Public functions require documentation.") {

    @Configuration("if protected functions should be searched")
    private val searchProtectedFunction: Boolean by config(false)

    @Configuration("if undocumented receivers of extension functions should be reported")
    private val reportUndocumentedReceiver: Boolean by config(false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.funKeyword == null && function.isLocal) return
        if (!function.shouldBeDocumented()) return

        val doc = function.docComment
        if (doc == null) {
            report(function, "The function ${function.nameAsSafeName} is missing documentation.")
            return
        }

        if (reportUndocumentedReceiver && function.hasUndocumentedReceiver(doc)) {
            report(function, "The receiver of the function ${function.nameAsSafeName} is missing documentation.")
        }
    }

    private fun KtNamedFunction.hasUndocumentedReceiver(doc: KDoc): Boolean =
        receiverTypeReference != null &&
            doc.getAllSections().none { it.findTagsByName("receiver").isNotEmpty() }

    private fun report(function: KtNamedFunction, message: String) {
        report(Finding(Entity.atName(function), message))
    }

    private fun KtNamedFunction.shouldBeDocumented() =
        if (searchProtectedFunction) {
            parents.filterIsInstance<KtClassOrObject>().all { it.isPublic || it.isProtected() }
        } else {
            parents.filterIsInstance<KtClassOrObject>().all { it.isPublic }
        } &&
            isPublicNotOverridden(searchProtectedFunction)
}

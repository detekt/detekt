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
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType

/**
 * This rule reports functions that are documented yet miss documentation for their return type.
 *
 * There are two kinds of problems this rule reports:
 *
 * - Missing `@return` in documentation for functions that do not return Unit.
 * - Missing `@return`, `@throws` or `@exception` documentation for functions that return Nothing.
 *
 * You can configure:
 *
 * - which types are considered to be Unit via the `nonDocumentedReturnTypes` configuration.
 * - which types are considered to be Nothing via the `throwsOrExceptionReturnTypes` configuration.
 *
 * <noncompliant>
 * /**
 *  * My return type is not documented
 *  */
 *  fun foo(): String = /* ... */
 *
 *  /**
 *   * My return type is documented
 *   */
 *  fun bar(): Nothing = /* ... */
 * </noncompliant>
 *
 * <compliant>
 * /**
 *  * My return type is documented
 *  *
 *  * @return Something
 *  */
 * fun baz(): String = /* ... */
 *
 * fun okBecauseNotDocumented(): String = /* ... */
 *
 * /**
 *  * I am nothing, so I can also document my return type with a throws/exception
 *  *
 *  * @throws ExceptionOne if something goes wrong
 *  * @exception ExceptionTwo if something else goes wrong
 *  */
 * fun nothingish(): Nothing = /* ... */
 * </compliant>
 */
@RequiresTypeResolution
class UndocumentedPublicFunctionReturn(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Non-trivial return types should be documented.",
        Debt.TEN_MINS
    )

    @Configuration("Return types that do not need to be documented (e.g. kotlin.Unit)")
    private val nonDocumentedReturnTypes: List<String> by config(listOf("kotlin.Unit"))

    @Configuration(
        "Return types that can also be documented with @throws or @exception tags (e.g. kotlin.Nothing)"
    )
    private val throwsOrExceptionReturnTypes: List<String> by config(listOf("kotlin.Nothing"))

    override fun visitNamedFunction(function: KtNamedFunction) {
        val doc = function.docComment
        if (bindingContext == BindingContext.EMPTY || doc == null) return
        val returnType = function.createTypeBindingForReturnType(bindingContext)?.type?.fqNameOrNull()?.toString()
            ?: return
        if (returnType in throwsOrExceptionReturnTypes) {
            handleNothingLikeDocs(doc, function)
        } else if (returnType !in nonDocumentedReturnTypes) {
            handleShouldHaveReturnDocs(doc, function)
        }
    }

    private fun handleNothingLikeDocs(doc: KDoc, function: KtNamedFunction) {
        val hasThrows = doc.getAllSections().any { it.findTagsByName("throws").isNotEmpty() }
        val hasException = doc.getAllSections().any { it.findTagsByName("exception").isNotEmpty() }
        val hasReturn = doc.getAllSections().any { it.findTagsByName("return").isNotEmpty() }
        if (!hasThrows && !hasException && !hasReturn) {
            report(
                function.createReport("Missing @throws, @exception or @return tag to document special return type.")
            )
        }
    }

    private fun handleShouldHaveReturnDocs(doc: KDoc, function: KtNamedFunction) {
        val hasReturn = doc.getAllSections().any { it.findTagsByName("return").isNotEmpty() }
        if (!hasReturn) {
            report(
                function.createReport("Missing @return tag to document returned value.")
            )
        }
    }

    private fun KtNamedFunction.createReport(message: String): Finding =
        CodeSmell(issue, Entity.atName(this), message)
}

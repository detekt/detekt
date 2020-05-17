package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * Library functions/properties should have an explicit return type.
 * Inferred return type can easily be changed by mistake which may lead to breaking changes.
 *
 * <noncompliant>
 * // code from a library
 * val strs = listOf("foo, bar")
 * fun bar() = 5
 * class Parser {
 *      fun parse() = ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * // code from a library
 * val strs: List<String> = listOf("foo, bar")
 * fun bar(): Int = 5
 *
 * class Parser {
 *      fun parse(): ParsingResult = ...
 * }
 * </compliant>
 *
 * @active since v1.2.0
 */
class LibraryCodeMustSpecifyReturnType(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        this.javaClass.simpleName,
        Severity.Style,
        "Library functions/properties should have an explicit return type. " +
            "Inferred return type can easily be changed by mistake which may lead to breaking changes.",
        Debt.FIVE_MINS
    )

    override fun visitCondition(root: KtFile): Boolean =
        super.visitCondition(root) && filters != null

    override fun visitProperty(property: KtProperty) {
        if (!property.isLocal && property.isPublic && property.typeReference == null) {
            report(CodeSmell(
                issue,
                Entity.atName(property),
                "Library property '${property.nameAsSafeName}' without explicit return type."
            ))
        }
        super.visitProperty(property)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!function.isLocal &&
            function.isPublic &&
            function.hasExpressionBodyWithoutExplicitReturnType()) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    "Library function '${function.nameAsSafeName}' without explicit return type."
                )
            )
        }
        super.visitNamedFunction(function)
    }

    private fun KtNamedFunction.hasExpressionBodyWithoutExplicitReturnType(): Boolean =
        equalsToken != null && !hasDeclaredReturnType()
}

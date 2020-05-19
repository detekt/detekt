package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isUnit

/**
 * This rule warns on instances where a function, annotated with either `@CheckReturnValue` or `@CheckResult`,
 * returns a value but that value is not used in any way. The Kotlin compiler gives no warning for this scenario
 * normally so that's the rationale behind this rule.
 *
 * fun returnsValue() = 42
 * fun returnsNoValue() {}
 *
 * <noncompliant>
 *     returnsValue()
 * </noncompliant>
 *
 * <compliant>
 *     if (42 == returnsValue()) {}
 *     val x = returnsValue()
 * </compliant>
 */
class IgnoredReturnValue(config: Config = Config.empty) : Rule(config) {

    private val requiredAnnotations = listOf(
            "CheckReturnValue",
            "CheckResult"
    )

    override val issue: Issue = Issue(
            "IgnoredReturnValue",
            Severity.Defect,
            "This call returns a value which is ignored",
            Debt.TWENTY_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return
        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        val returnType = resolvedCall.resultingDescriptor.returnType ?: return
        val annotations = resolvedCall.resultingDescriptor.annotations

        if (returnType.isUnit()) {
            return
        }
        if (annotations.none { (it.fqName?.pathSegments()?.last()?.asString() in requiredAnnotations) }) {
            return
        }

        val elementsToInspect = mutableListOf<PsiElement>(expression)
        val parent = expression.parent

        if (parent is KtDotQualifiedExpression) {
            elementsToInspect += parent.getParentOfType<KtDotQualifiedExpression>(true) ?: parent
        }
        if (parent is KtBlockExpression && parent.lastBlockStatementOrThis() == expression) {
            elementsToInspect -= expression
        }

        if (elementsToInspect.any(PsiElement::isIsolated)) {
            report(
                    CodeSmell(
                            issue,
                            Entity.from(expression),
                            message = "The call ${expression.text} is returning a value that is ignored."
                    )
            )
        }
    }
}

private val PsiElement.isIsolated: Boolean
    get() =
        true == this.prevSibling?.isAnIsolationElement && true == this.nextSibling?.isAnIsolationElement

private val PsiElement?.isAnIsolationElement: Boolean
    get() {
        if (this is PsiWhiteSpace || this is PsiComment) {
            return true
        }
        if (this is LeafPsiElement && this.elementType is KtSingleValueToken) {
            val token = (this.elementType as KtSingleValueToken)
            if (token.value == ";") {
                return true
            }
        }
        return false
    }

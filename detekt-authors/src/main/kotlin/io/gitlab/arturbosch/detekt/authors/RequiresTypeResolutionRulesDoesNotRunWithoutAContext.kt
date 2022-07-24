@file:Suppress("ForbiddenComment")

package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName

/**
 * A rule annotated with RequiresTypeResolution should override `visitCondition` and return false if the provided
 * `bindingContext` is empty.
 *
 * <noncompliant>
 * @@RequiresTypeResolution
 * class MyRule(config: Config = Config.empty) : Rule(config) {
 *     override fun visitKtFile(file: KtFile) =
 *         if (bindingContext == BindingContext.EMPTY) return
 *         ...
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * @@RequiresTypeResolution
 * class MyRule(config: Config = Config.empty) : Rule(config) {
 *     override fun visitCondition(root: KtFile) =
 *         bindingContext != BindingContext.EMPTY && super.visitCondition(root)
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault("1.22.0")
class RequiresTypeResolutionRulesDoesNotRunWithoutAContext(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Rules marked as RequiresTypeResolution shouldn't run without `bindingContext`.",
        Debt.FIVE_MINS
    )

    override fun visitClass(klass: KtClass) {
        if (klass.annotationEntries.map { it.text }.contains("@RequiresTypeResolution")) {
            val function: KtNamedFunction? = klass.findFunctionByName("visitCondition") as? KtNamedFunction

            if (function == null) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(klass),
                        message
                    )
                )
                return
            }

            if (function.bodyExpression?.text != requiredImplementation) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(function),
                        message
                    )
                )
            }
        }
    }
}

private const val requiredImplementation = "bindingContext != BindingContext.EMPTY && super.visitCondition(root)"
private const val message = "This rule is annotated with `RequiresTypeResolution` but `visitCondition` " +
    "implementation is not `$requiredImplementation`"

package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

/**
 * Down-casting immutable types from kotlin.collections should be discouraged.
 * The result of the downcast is platform specific and can lead to unexpected crashes.
 * Prefer to use instead the `toMutable<Type>()` functions.
 *
 * <noncompliant>
 * val list : List<Int> = getAList()
 * if (list is MutableList) {
 *     list.add(42)
 * }
 *
 * (list as MutableList).add(42)
 * </noncompliant>
 *
 * <compliant>
 * val list : List<Int> = getAList()
 * list.toMutableList().add(42)
 * </compliant>
 *
 */
@RequiresTypeResolution
class DontDowncastCollectionTypes(config: Config) : Rule(config) {

    override val issue = Issue(
        "DontDowncastCollectionTypes",
        Severity.Warning,
        "Down-casting immutable collection types is breaking the collection contract",
        Debt.TEN_MINS
    )

    override fun visitIsExpression(expression: KtIsExpression) {
        super.visitIsExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        checkForDowncast(expression, expression.leftHandSide, expression.typeReference)
    }

    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return

        checkForDowncast(expression, expression.left, expression.right)
    }

    private fun checkForDowncast(parent: KtExpression, left: KtExpression, right: KtTypeReference?) {
        val lhsType = left
            .getType(bindingContext)
            ?.fqNameOrNull()
            ?.shortName()
            ?.asString()

        val rhsType = right
            ?.typeElement
            ?.safeAs<KtUserType>()
            ?.referencedName

        if (lhsType in immutableTypes && rhsType in mutableTypes) {
            var message = "Down-casting from type $lhsType to $rhsType is risky."
            if (rhsType != null && rhsType.startsWith("Mutable")) {
                message += " Use `to$rhsType()` instead."
            }
            report(CodeSmell(issue, Entity.from(parent), message))
        }
    }

    companion object {
        val immutableTypes = listOf("List", "Map", "Set")

        // Kotlin Stdlib Mutable types plus Type-aliases from:
        // https://github.com/JetBrains/kotlin/blob/46b7a774b558001c136be225cf4367fa09ba1aee/libraries/stdlib/jvm/src/kotlin/collections/TypeAliases.kt#L13-L17
        val mutableTypes = listOf(
            "MutableList",
            "MutableMap",
            "MutableSet",
            "ArrayList",
            "LinkedHashSet",
            "HashSet",
            "LinkedHashMap",
            "HashMap",
        )
    }
}

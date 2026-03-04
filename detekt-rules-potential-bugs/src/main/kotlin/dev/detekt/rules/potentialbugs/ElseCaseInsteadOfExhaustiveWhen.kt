package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * This rule reports `when` expressions that contain an `else` case even though they have an exhaustive set of cases.
 *
 * This occurs when the subject of the `when` expression is either an enum class, sealed class or of type boolean.
 * Using `else` cases for these expressions can lead to unintended behavior when adding new enum types, sealed subtypes
 * or changing the nullability of a boolean, since this will be implicitly handled by the `else` case.
 *
 * <noncompliant>
 * enum class Color {
 *     RED,
 *     GREEN,
 *     BLUE
 * }
 *
 * when(c) {
 *     Color.RED -> {}
 *     Color.GREEN -> {}
 *     else -> {}
 * }
 * </noncompliant>
 *
 * <compliant>
 * enum class Color {
 *     RED,
 *     GREEN,
 *     BLUE
 * }
 *
 * when(c) {
 *     Color.RED -> {}
 *     Color.GREEN -> {}
 *     Color.BLUE -> {}
 * }
 * </compliant>
 */
class ElseCaseInsteadOfExhaustiveWhen(config: Config) :
    Rule(
        config,
        "A `when` expression that has an exhaustive set of cases should not contain an `else` case."
    ),
    RequiresAnalysisApi {

    @Configuration(
        "List of fully qualified types which should be ignored for when expressions with a subject. " +
            "Example `kotlinx.serialization.json.JsonObject`"
    )
    private val ignoredSubjectTypes: List<String> by config(emptyList())

    override fun visitWhenExpression(whenExpression: KtWhenExpression) {
        super.visitWhenExpression(whenExpression)

        checkForElseCaseInsteadOfExhaustiveWhenExpression(whenExpression)
    }

    private fun checkForElseCaseInsteadOfExhaustiveWhenExpression(whenExpression: KtWhenExpression) {
        val subjectExpression = whenExpression.subjectExpression ?: return
        if (whenExpression.elseExpression == null) return

        analyze(whenExpression) {
            val subjectType = subjectExpression.expressionType
            val subjectSymbol = subjectType?.symbol as? KaClassSymbol
            if (ignoredSubjectTypes.contains(subjectSymbol?.classId?.asFqNameString())) {
                return
            }

            val isEnumSubject = subjectSymbol?.classKind == KaClassKind.ENUM_CLASS
            val isSealedSubject = isNonExpectedSealedClass(subjectSymbol)
            val isBooleanSubject = subjectType?.isBooleanType == true

            if (isEnumSubject || isSealedSubject || isBooleanSubject) {
                val subjectTypeName = when {
                    isEnumSubject -> "enum class"
                    isSealedSubject -> "sealed class"
                    else -> "boolean"
                }
                val message = "When expression with $subjectTypeName subject should not contain an `else` case."
                report(Finding(Entity.from(whenExpression), message))
            }
        }
    }

    /**
     * `when` expressions on `expect` sealed classes in the common code of multiplatform projects still require an
     * `else` branch. This happens because subclasses of `actual` platform implementations aren't known in the common
     *  code.
     */
    private fun isNonExpectedSealedClass(symbol: KaClassSymbol?): Boolean =
        symbol?.modality == KaSymbolModality.SEALED && !symbol.isExpect
}

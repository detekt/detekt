package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isBooleanOrNullableBoolean

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
@RequiresTypeResolution
class ElseCaseInsteadOfExhaustiveWhen(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "ElseCaseInsteadOfExhaustiveWhen",
        "A `when` expression that has an exhaustive set of cases should not contain an `else` case.",
        Debt.FIVE_MINS
    )

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

        val subjectType = subjectExpression.getType(bindingContext)
        if (ignoredSubjectTypes.contains(subjectType?.fqNameOrNull()?.toString())) {
            return
        }

        val isEnumSubject = WhenChecker.getClassDescriptorOfTypeIfEnum(subjectType) != null
        val isSealedSubject = isNonExpectedSealedClass(subjectType)
        val isBooleanSubject = subjectType?.isBooleanOrNullableBoolean() == true

        if (isEnumSubject || isSealedSubject || isBooleanSubject) {
            val subjectTypeName = when {
                isEnumSubject -> "enum class"
                isSealedSubject -> "sealed class"
                else -> "boolean"
            }
            val message = "When expression with $subjectTypeName subject should not contain an `else` case."
            report(CodeSmell(issue, Entity.from(whenExpression), message))
        }
    }

    /**
     * `when` expressions on `expect` sealed classes in the common code of multiplatform projects still require an
     * `else` branch. This happens because subclasses of `actual` platform implementations aren't known in the common
     *  code.
     */
    private fun isNonExpectedSealedClass(type: KotlinType?): Boolean {
        val sealedClassDescriptor = WhenChecker.getClassDescriptorOfTypeIfSealed(type)
        return sealedClassDescriptor != null && !sealedClassDescriptor.isExpect
    }
}

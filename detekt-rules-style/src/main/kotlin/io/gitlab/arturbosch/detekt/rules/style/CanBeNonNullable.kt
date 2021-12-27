package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.isOpen
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.calls.smartcasts.getKotlinTypeForComparison
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.isNullable

/**
 * This rule inspects variables marked as nullable and reports which could be
 * declared as non-nullable instead.
 *
 * <noncompliant>
 * class A {
 *     var a: Int? = 5
 *
 *     fun foo() {
 *         a = 6
 *     }
 * }
 *
 * class A {
 *     val a: Int?
 *         get() = 5
 * }
 * </noncompliant>
 *
 * <compliant>
 * class A {
 *     var a: Int = 5
 *
 *     fun foo() {
 *         a = 6
 *     }
 * }
 *
 * class A {
 *     val a: Int
 *         get() = 5
 * }
 * </compliant>
 */
@RequiresTypeResolution
class CanBeNonNullable(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Variable can be changed to non-nullable, as it is never set to null.",
        Debt.TEN_MINS
    )

    override fun visitKtFile(file: KtFile) {
        if (bindingContext == BindingContext.EMPTY) return
        NonNullableCheckVisitor().visitKtFile(file)
        super.visitKtFile(file)
    }

    private inner class NonNullableCheckVisitor : DetektVisitor() {
        // A list of properties that are marked as nullable during their
        // declaration but do not explicitly receive a nullable value in
        // the declaration, so they could potentially be marked as non-nullable
        // if the file does not encounter these properties being assigned
        // a nullable value.
        private val candidateProps = mutableMapOf<FqName, KtProperty>()

        override fun visitKtFile(file: KtFile) {
            super.visitKtFile(file)
            // Any candidate properties that were not removed during the inspection
            // of the Kotlin file were never assigned nullable values in the code,
            // thus they can be converted to non-nullable.
            candidateProps.forEach { (_, property) ->
                report(
                    CodeSmell(
                        issue,
                        Entity.from(property),
                        "The nullable variable '${property.name}' can be made non-nullable."
                    )
                )
            }
        }

        override fun visitClass(klass: KtClass) {
            if (!klass.isInterface()) {
                super.visitClass(klass)
            }
        }

        override fun visitProperty(property: KtProperty) {
            if (property.getKotlinTypeForComparison(bindingContext)?.isNullable() != true) return
            val fqName = property.fqName ?: return
            if (property.isCandidate()) {
                candidateProps[fqName] = property
            }
        }

        override fun visitBinaryExpression(expression: KtBinaryExpression) {
            if (expression.operationToken == KtTokens.EQ) {
                val fqName = expression.left
                    ?.getResolvedCall(bindingContext)
                    ?.resultingDescriptor
                    ?.fqNameOrNull()
                if (
                    fqName != null &&
                    candidateProps.containsKey(fqName) &&
                    expression.right?.isNullableType() == true
                ) {
                    // A candidate property has been assigned a nullable value
                    // in the file's code, so it can be removed from the map of
                    // candidates for flagging.
                    candidateProps.remove(fqName)
                }
            }
            super.visitBinaryExpression(expression)
        }

        private fun KtProperty.isCandidate(): Boolean {
            if (isOpen()) return false
            val isSetToNonNullable = initializer?.isNullableType() != true &&
                getter?.isNullableType() != true &&
                delegate?.returnsNullable() != true
            val cannotSetViaNonPrivateMeans = !isVar || (isPrivate() || (setter?.isPrivate() == true))
            return isSetToNonNullable && cannotSetViaNonPrivateMeans
        }

        private fun KtPropertyDelegate?.returnsNullable(): Boolean {
            val property = this?.parent as? KtProperty ?: return false
            val propertyDescriptor =
                bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, property] as? PropertyDescriptor
            return propertyDescriptor?.getter?.let {
                bindingContext[BindingContext.DELEGATED_PROPERTY_RESOLVED_CALL, it]
                    ?.resultingDescriptor
                    ?.returnType
                    ?.isNullable() == true
            } ?: false
        }

        private fun KtExpression?.isNullableType(): Boolean {
            return when (this) {
                is KtConstantExpression -> {
                    this.text == "null"
                }
                is KtIfExpression -> {
                    this.then.isNullableType() || this.`else`.isNullableType()
                }
                is KtPropertyAccessor -> {
                    (initializer?.getType(bindingContext)?.isNullable() == true) ||
                        (
                            bodyExpression
                                ?.collectDescendantsOfType<KtReturnExpression>()
                                ?.any { it.returnedExpression.isNullableType() } == true
                            )
                }
                else -> {
                    this?.getType(bindingContext)?.isNullable() == true
                }
            }
        }
    }
}

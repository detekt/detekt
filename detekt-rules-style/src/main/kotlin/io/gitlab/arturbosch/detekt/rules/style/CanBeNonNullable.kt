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
import io.gitlab.arturbosch.detekt.rules.isNonNullCheck
import io.gitlab.arturbosch.detekt.rules.isNullCheck
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOperator
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtWhenConditionIsPattern
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
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
 *
 * fun foo(a: Int?) {
 *     val b = a!! + 2
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
 *
 * fun foo(a: Int) {
 *     val b = a + 2
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
        super.visitKtFile(file)
        PropertyCheckVisitor().visitKtFile(file)
        ParameterCheckVisitor().visitKtFile(file)
    }

    private inner class ParameterCheckVisitor : DetektVisitor() {
        private val candidateParams = mutableMapOf<DeclarationDescriptor, KtParameter>()

        override fun visitKtFile(file: KtFile) {
            super.visitKtFile(file)
            // Any candidate params that were not removed during the inspection
            // of the Kotlin file and were not added to referenceParams were never
            // treated as nullable params in the code, thus they can be converted
            // to non-nullable.
            candidateParams.forEach { (_, param) ->
                report(
                    CodeSmell(
                        issue,
                        Entity.from(param),
                        "The nullable parameter '${param.name}' can be made non-nullable."
                    )
                )
            }
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            // Operator functions - like setValue() - may require a nullable param to
            // correspond with function type parameters, so those functions would not
            // have the choice to mark the param as non-nullable.
            if (!function.isOperator()) {
                function.valueParameters.asSequence()
                    .filter {
                        it.typeReference?.typeElement is KtNullableType
                    }.mapNotNull { parameter ->
                        bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, parameter]?.let {
                            it to parameter
                        }
                    }.forEach { (k, v) -> candidateParams[k] = v }
            }
            super.visitNamedFunction(function)
        }

        override fun visitWhenExpression(expression: KtWhenExpression) {
            val subjectDescriptor = expression.subjectExpression
                ?.let { it as? KtNameReferenceExpression }
                ?.getResolvedCall(bindingContext)
                ?.resultingDescriptor
            val whenConditions = expression.entries.flatMap { it.conditions.asList() }
            if (subjectDescriptor != null) {
                val isNullCheck = whenConditions.any { whenCondition ->
                    when (whenCondition) {
                        is KtWhenConditionWithExpression -> whenCondition.expression?.text == "null"
                        is KtWhenConditionIsPattern -> {
                            (whenCondition.typeReference.isNullable(bindingContext) || whenCondition.isNegated)
                        }
                        else -> false
                    }
                }
                if (isNullCheck || expression.elseExpression.isValidElseExpression()) {
                    candidateParams.remove(subjectDescriptor)
                }
            } else {
                whenConditions.forEach { whenCondition ->
                    if (whenCondition is KtWhenConditionWithExpression) {
                        whenCondition.expression.evaluateCheckStatement(expression.elseExpression)
                    }
                }
            }
            super.visitWhenExpression(expression)
        }

        override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
            expression.receiverExpression
                .getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.let(candidateParams::remove)
            super.visitSafeQualifiedExpression(expression)
        }

        override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
            val isExtensionForNullable = expression.getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.extensionReceiverParameter
                ?.type
                ?.isMarkedNullable
            if (isExtensionForNullable == true) {
                expression.receiverExpression
                    .getResolvedCall(bindingContext)
                    ?.resultingDescriptor
                    ?.let(candidateParams::remove)
            }
            super.visitDotQualifiedExpression(expression)
        }

        override fun visitBinaryExpression(expression: KtBinaryExpression) {
            if (expression.operationToken == KtTokens.ELVIS) {
                expression.left
                    ?.getResolvedCall(bindingContext)
                    ?.resultingDescriptor
                    ?.let(candidateParams::remove)
            }
            super.visitBinaryExpression(expression)
        }

        override fun visitIfExpression(expression: KtIfExpression) {
            expression.condition.evaluateCheckStatement(expression.`else`)
            super.visitIfExpression(expression)
        }

        private fun KtExpression?.getNonNullChecks(): List<CallableDescriptor>? {
            return when (this) {
                is KtBinaryExpression -> evaluateBinaryExpression()
                is KtIsExpression -> evaluateIsExpression()
                else -> null
            }
        }

        private fun KtExpression?.evaluateCheckStatement(elseExpression: KtExpression?) {
            this.getNonNullChecks()?.let { nonNullChecks ->
                if (elseExpression.isValidElseExpression()) {
                    nonNullChecks.forEach(candidateParams::remove)
                }
            }
        }

        private fun KtExpression?.isValidElseExpression(): Boolean {
            return this != null && this !is KtIfExpression && this !is KtWhenExpression
        }

        private fun KtBinaryExpression.evaluateBinaryExpression(): List<CallableDescriptor> {
            val leftExpression = left
            val rightExpression = right
            val nonNullChecks = mutableListOf<CallableDescriptor>()

            fun getDescriptor(leftExpression: KtExpression?, rightExpression: KtExpression?): CallableDescriptor? {
                return when {
                    leftExpression is KtNameReferenceExpression -> leftExpression
                    rightExpression is KtNameReferenceExpression -> rightExpression
                    else -> null
                }?.getResolvedCall(bindingContext)
                    ?.resultingDescriptor
            }

            if (isNullCheck()) {
                getDescriptor(leftExpression, rightExpression)?.let(candidateParams::remove)
            } else if (isNonNullCheck()) {
                getDescriptor(leftExpression, rightExpression)?.let(nonNullChecks::add)
            }

            // Recursively iterate into the if-check if possible
            leftExpression.getNonNullChecks()?.let(nonNullChecks::addAll)
            rightExpression.getNonNullChecks()?.let(nonNullChecks::addAll)
            return nonNullChecks
        }

        private fun KtIsExpression.evaluateIsExpression(): List<CallableDescriptor> {
            val descriptor = this.leftHandSide.getResolvedCall(bindingContext)?.resultingDescriptor
                ?: return emptyList()
            return if (typeReference.isNullable(bindingContext) || isNegated) {
                candidateParams.remove(descriptor)
                emptyList()
            } else {
                listOf(descriptor)
            }
        }

        private fun KtTypeReference?.isNullable(bindingContext: BindingContext): Boolean {
            return this?.let { bindingContext[BindingContext.TYPE, it] }?.isMarkedNullable == true
        }
    }

    private inner class PropertyCheckVisitor : DetektVisitor() {
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
            if (property.getKotlinTypeForComparison(bindingContext)?.isNullable() == true) {
                val fqName = property.fqName
                if (property.isCandidate() && fqName != null) {
                    candidateProps[fqName] = property
                }
            }
            super.visitProperty(property)
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

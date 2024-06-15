package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isAbstract
import io.gitlab.arturbosch.detekt.rules.isNonNullCheck
import io.gitlab.arturbosch.detekt.rules.isNullCheck
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenConditionIsPattern
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.isFirstStatement
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi2ir.deparenthesize
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.getKotlinTypeForComparison
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

/**
 * This rule inspects variables marked as nullable and reports which could be
 * declared as non-nullable instead.
 *
 * It's preferred to not have functions that do "nothing".
 * A function that does nothing when the value is null hides the logic,
 * so it should not allow null values in the first place.
 * It is better to move the null checks up around the calls,
 * instead of having it inside the function.
 *
 * This could lead to less nullability overall in the codebase.
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
 *
 * fun foo(a: Int?) {
 *     if (a != null) {
 *         println(a)
 *     }
 * }
 *
 * fun foo(a: Int?) {
 *     if (a == null) return
 *     println(a)
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
 *
 * fun foo(a: Int) {
 *     println(a)
 * }
 * </compliant>
 */
@RequiresTypeResolution
class CanBeNonNullable(config: Config) : Rule(
    config,
    "Variable can be changed to non-nullable, as it is never set to null."
) {

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        PropertyCheckVisitor().visitKtFile(file)
        ParameterCheckVisitor().visitKtFile(file)
    }

    @Suppress("TooManyFunctions")
    private inner class ParameterCheckVisitor : DetektVisitor() {
        private val nullableParams = mutableMapOf<DeclarationDescriptor, NullableParam>()

        override fun visitNamedFunction(function: KtNamedFunction) {
            if (function.isOverride()) {
                return
            }

            val candidateDescriptors = mutableSetOf<DeclarationDescriptor>()
            function.valueParameters.asSequence()
                .filter {
                    it.typeReference?.typeElement is KtNullableType
                }
                .mapNotNull { parameter ->
                    bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, parameter]?.let {
                        it to parameter
                    }
                }
                .forEach { (descriptor, param) ->
                    candidateDescriptors.add(descriptor)
                    nullableParams[descriptor] = NullableParam(param)
                }

            val validSingleChildExpression = if (function.initializer == null) {
                val children = function.bodyBlockExpression
                    ?.allChildren
                    ?.filterIsInstance<KtExpression>()
                    ?.toList()
                    .orEmpty()
                if (children.size == 1) {
                    children.first().determineSingleExpression(candidateDescriptors)
                } else {
                    INELIGIBLE_SINGLE_EXPRESSION
                }
            } else {
                INELIGIBLE_SINGLE_EXPRESSION
            }

            // Evaluate the function, then analyze afterwards whether the candidate properties
            // could be made non-nullable.
            super.visitNamedFunction(function)

            candidateDescriptors.asSequence()
                .mapNotNull(nullableParams::remove)
                // The heuristic for whether a nullable param can be made non-nullable is:
                // * It has been forced into a non-null type, either by `!!` or by
                //   `checkNonNull()`/`requireNonNull()`, or
                // * The containing function only consists of a single non-null check on
                //   the param, either via an if/when check or with a safe-qualified expression.
                .filter {
                    val onlyNonNullCheck = validSingleChildExpression && it.isNonNullChecked && !it.isNullChecked
                    it.isNonNullForced || it.isNullCheckReturnsUnit || onlyNonNullCheck
                }
                .forEach { nullableParam ->
                    report(
                        CodeSmell(
                            Entity.from(nullableParam.param),
                            "The nullable parameter '${nullableParam.param.name}' can be made non-nullable."
                        )
                    )
                }
        }

        override fun visitCallExpression(expression: KtCallExpression) {
            val calleeName = expression.calleeExpression
                .getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.name
                ?.toString()
            // Check for whether a call to `checkNonNull()` or `requireNonNull()` has
            // been made.
            if (calleeName == REQUIRE_NOT_NULL_NAME || calleeName == CHECK_NOT_NULL_NAME) {
                expression.valueArguments.forEach { valueArgument ->
                    valueArgument.getArgumentExpression()?.let { argumentExpression ->
                        updateNullableParam(argumentExpression) { it.isNonNullForced = true }
                    }
                }
            }
            super.visitCallExpression(expression)
        }

        override fun visitPostfixExpression(expression: KtPostfixExpression) {
            if (expression.operationToken == KtTokens.EXCLEXCL) {
                expression.baseExpression?.let { baseExpression ->
                    updateNullableParam(baseExpression) { it.isNonNullForced = true }
                }
            }
            super.visitPostfixExpression(expression)
        }

        override fun visitWhenExpression(expression: KtWhenExpression) {
            val nullCheckedDescriptor = expression.subjectExpression
                ?.collectDescendantsOfType<KtNameReferenceExpression>()
                .orEmpty()
                .mapNotNull { it.getResolvedCall(bindingContext) }
                .filter { callDescriptor -> callDescriptor.resultingDescriptor.returnType?.isNullable() == true }
                .mapNotNull { callDescriptor -> callDescriptor.resultingDescriptor as? ValueParameterDescriptor }
            val whenConditions = expression.entries.flatMap { it.conditions.asList() }
            if (nullCheckedDescriptor.isNotEmpty()) {
                whenConditions.evaluateSubjectWhenExpression(expression, nullCheckedDescriptor)
            } else {
                whenConditions.forEach { whenCondition ->
                    if (whenCondition is KtWhenConditionWithExpression) {
                        whenCondition.expression.evaluateCheckStatement(expression.elseExpression)
                    }
                }
            }
            super.visitWhenExpression(expression)
        }

        override fun visitIfExpression(expression: KtIfExpression) {
            expression.condition.evaluateCheckStatement(expression.`else`)
            if (expression.isFirstStatement()) {
                evaluateNullCheckReturnsUnit(expression.condition, expression.then)
            }
            super.visitIfExpression(expression)
        }

        private fun evaluateNullCheckReturnsUnit(condition: KtExpression?, then: KtExpression?) {
            val thenExpression = if (then is KtBlockExpression) then.firstStatement else then
            if (thenExpression !is KtReturnExpression) return
            if (thenExpression.returnedExpression != null) return

            if (condition is KtBinaryExpression && condition.isNullCheck()) {
                getDescriptor(condition.left, condition.right)
                    ?.let { nullableParams[it] }
                    ?.let { it.isNullCheckReturnsUnit = true }
            }
        }

        override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
            updateNullableParam(expression.receiverExpression) { it.isNonNullChecked = true }
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
                    .getRootExpression()
                    ?.let { rootExpression ->
                        updateNullableParam(rootExpression) { it.isNullChecked = true }
                    }
            }
            super.visitDotQualifiedExpression(expression)
        }

        override fun visitBinaryExpression(expression: KtBinaryExpression) {
            if (expression.operationToken == KtTokens.ELVIS) {
                expression.left
                    .getRootExpression()
                    ?.let { rootExpression ->
                        updateNullableParam(rootExpression) { it.isNullChecked = true }
                    }
            }
            super.visitBinaryExpression(expression)
        }

        private fun KtExpression?.getRootExpression(): KtExpression? {
            // Look for the expression that was the root of a potential call chain.
            var receiverExpression = this
            while (receiverExpression is KtQualifiedExpression) {
                receiverExpression = receiverExpression.receiverExpression
            }
            return receiverExpression
        }

        private fun KtExpression?.determineSingleExpression(candidateDescriptors: Set<DeclarationDescriptor>): Boolean {
            return when (this) {
                is KtReturnExpression -> INELIGIBLE_SINGLE_EXPRESSION
                is KtIfExpression -> ELIGIBLE_SINGLE_EXPRESSION
                is KtDotQualifiedExpression -> {
                    this.getRootExpression()
                        .getResolvedCall(bindingContext)
                        ?.resultingDescriptor
                        ?.let(candidateDescriptors::contains) == true
                }
                is KtCallExpression -> INELIGIBLE_SINGLE_EXPRESSION
                else -> ELIGIBLE_SINGLE_EXPRESSION
            }
        }

        private fun KtElement?.getNonNullChecks(parentOperatorToken: IElementType?): List<CallableDescriptor>? {
            return when (this) {
                is KtBinaryExpression -> evaluateBinaryExpression(parentOperatorToken)
                is KtIsExpression -> evaluateIsExpression()
                else -> null
            }
        }

        private fun KtExpression?.evaluateCheckStatement(elseExpression: KtExpression?) {
            this.getNonNullChecks(null)?.let { nonNullChecks ->
                val nullableParamCallback = if (elseExpression.isValidElseExpression()) {
                    { nullableParam: NullableParam ->
                        nullableParam.isNonNullChecked = true
                        nullableParam.isNullChecked = true
                    }
                } else {
                    { nullableParam -> nullableParam.isNonNullChecked = true }
                }
                nonNullChecks.forEach {
                    nullableParams[it]?.let(nullableParamCallback)
                }
            }
        }

        // Helper function for if- and when-statements that will recursively check for whether
        // any function params have been checked for being a non-nullable type.
        private fun KtBinaryExpression.evaluateBinaryExpression(
            parentOperatorToken: IElementType?,
        ): List<CallableDescriptor> {
            val leftExpression = left?.deparenthesize()
            val rightExpression = right?.deparenthesize()
            val nonNullChecks = mutableListOf<CallableDescriptor>()

            if (isNullCheck()) {
                getDescriptor(leftExpression, rightExpression)
                    ?.let { nullableParams[it] }
                    ?.let { it.isNullChecked = true }
            } else if (isNonNullCheck() && parentOperatorToken != KtTokens.OROR) {
                getDescriptor(leftExpression, rightExpression)?.let(nonNullChecks::add)
            }

            leftExpression.getNonNullChecks(operationToken)?.let(nonNullChecks::addAll)
            rightExpression.getNonNullChecks(operationToken)?.let(nonNullChecks::addAll)
            return nonNullChecks
        }

        private fun getDescriptor(leftExpression: KtElement?, rightExpression: KtElement?): CallableDescriptor? {
            return when {
                leftExpression is KtNameReferenceExpression -> leftExpression
                rightExpression is KtNameReferenceExpression -> rightExpression
                else -> null
            }?.getResolvedCall(bindingContext)
                ?.resultingDescriptor
        }

        private fun KtIsExpression.evaluateIsExpression(): List<CallableDescriptor> {
            val descriptor = this.leftHandSide.getResolvedCall(bindingContext)?.resultingDescriptor
                ?: return emptyList()
            return if (isNullableCheck(typeReference, isNegated)) {
                nullableParams[descriptor]?.let { it.isNullChecked = true }
                emptyList()
            } else {
                listOf(descriptor)
            }
        }

        private fun List<KtWhenCondition>.evaluateSubjectWhenExpression(
            expression: KtWhenExpression,
            subjectDescriptors: List<CallableDescriptor>,
        ) {
            var isNonNullChecked = false
            var isNullChecked = false
            forEach { whenCondition ->
                when (whenCondition) {
                    is KtWhenConditionWithExpression -> {
                        if (whenCondition.expression?.text == "null") {
                            isNullChecked = true
                        }
                    }
                    is KtWhenConditionIsPattern -> {
                        if (isNullableCheck(whenCondition.typeReference, whenCondition.isNegated)) {
                            isNullChecked = true
                        } else {
                            isNonNullChecked = true
                        }
                    }
                }
            }
            if (expression.elseExpression.isValidElseExpression()) {
                if (isNullChecked) {
                    isNonNullChecked = true
                } else if (isNonNullChecked) {
                    isNullChecked = true
                }
            }
            subjectDescriptors.forEach { callableDescriptor ->
                nullableParams[callableDescriptor]?.let {
                    if (isNullChecked) it.isNullChecked = true
                    if (isNonNullChecked) it.isNonNullChecked = true
                }
            }
        }

        private fun isNullableCheck(typeReference: KtTypeReference?, isNegated: Boolean): Boolean {
            val isNullable = typeReference.isNullable(bindingContext)
            return (isNullable && !isNegated) || (!isNullable && isNegated)
        }

        private fun KtExpression?.isValidElseExpression(): Boolean {
            return this != null && this !is KtIfExpression && this !is KtWhenExpression
        }

        private fun KtTypeReference?.isNullable(bindingContext: BindingContext): Boolean {
            return this?.let { bindingContext[BindingContext.TYPE, it] }?.isMarkedNullable == true
        }

        private fun updateNullableParam(expression: KtExpression, updateCallback: (NullableParam) -> Unit) {
            expression.getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.let { nullableParams[it] }
                ?.let(updateCallback)
        }
    }

    private class NullableParam(val param: KtParameter) {
        var isNullChecked = false
        var isNonNullChecked = false
        var isNonNullForced = false
        var isNullCheckReturnsUnit = false
    }

    private inner class PropertyCheckVisitor : DetektVisitor() {
        // A list of properties that are marked as nullable during their
        // declaration but do not explicitly receive a nullable value in
        // the declaration, so they could potentially be marked as non-nullable
        // if the file does not encounter these properties being assigned
        // a nullable value.
        private val candidateProps = mutableMapOf<FqName, KtProperty>()

        override fun visitKtFile(file: KtFile) {
            file.collectCandidateProps()
            super.visitKtFile(file)
            // Any candidate properties that were not removed during the inspection
            // of the Kotlin file were never assigned nullable values in the code,
            // thus they can be converted to non-nullable.
            candidateProps.forEach { (_, property) ->
                report(
                    CodeSmell(
                        Entity.from(property),
                        "The nullable variable '${property.name}' can be made non-nullable."
                    )
                )
            }
        }

        private fun KtFile.collectCandidateProps() {
            forEachDescendantOfType<KtProperty> { property ->
                val fqName = property.fqName
                if (fqName != null && property.isCandidate()) {
                    candidateProps[fqName] = property
                }
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

        /**
         * Determines whether a type is nullable and can be made non-nullable; for most properties
         * this is simply whether they are nullable, but for type parameters they can only be made
         * non-nullable when explicitly marked nullable.
         *
         * Note that [KotlinType.isNullable] for type parameter types is true unless it inherits
         * from a non-nullable type, e.g.:
         * - nullable: <T> or <T : Any?>
         * - non-nullable: <T : Any>
         * But even if T is nullable, a property `val t: T` cannot be made into a non-nullable type.
         */
        private fun KotlinType.isNullableAndCanBeNonNullable(): Boolean {
            return if (isTypeParameter()) isMarkedNullable else isNullable()
        }

        private fun KtProperty.isCandidate(): Boolean {
            if (isOpen() || isAbstract() || containingClass()?.isInterface() == true) return false

            val type = getKotlinTypeForComparison(bindingContext)
            if (type?.isNullableAndCanBeNonNullable() != true) return false

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
                    // only consider types which can be made non-nullable as nullable to warn on
                    // cases where a field has declared type `T?` but is only assigned as `T`; here
                    // `T` should not be considered nullable to enforce that the field could be
                    // declared as just `T`
                    this?.getType(bindingContext)?.isNullableAndCanBeNonNullable() == true
                }
            }
        }
    }

    private companion object {
        private const val REQUIRE_NOT_NULL_NAME = "requireNotNull"
        private const val CHECK_NOT_NULL_NAME = "checkNotNull"

        private const val INELIGIBLE_SINGLE_EXPRESSION = false
        private const val ELIGIBLE_SINGLE_EXPRESSION = true
    }
}

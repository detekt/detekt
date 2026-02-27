package dev.detekt.rules.style

import com.intellij.psi.tree.IElementType
import dev.detekt.api.Config
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isAbstract
import dev.detekt.psi.isNonNullCheck
import dev.detekt.psi.isNullCheck
import dev.detekt.psi.isNullable
import dev.detekt.psi.isOpen
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.singleVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
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
import org.jetbrains.kotlin.psi.KtPsiUtil
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
import org.jetbrains.kotlin.util.OperatorNameConventions

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
class CanBeNonNullable(config: Config) :
    Rule(
        config,
        "Variable can be changed to non-nullable, as it is never set to null."
    ),
    RequiresAnalysisApi {

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        PropertyCheckVisitor().visitKtFile(file)
        ParameterCheckVisitor().visitKtFile(file)
    }

    @Suppress("TooManyFunctions")
    private inner class ParameterCheckVisitor : DetektVisitor() {
        private val nullableParams = mutableMapOf<KaVariableSymbol, NullableParam>()
        private var currentFunction: KtNamedFunction? = null

        override fun visitNamedFunction(function: KtNamedFunction) {
            val previousFunction = currentFunction
            currentFunction = function
            if (function.isOverride()) {
                return
            }

            val candidateDescriptors = mutableSetOf<KaVariableSymbol>()
            function.valueParameters.asSequence()
                .filter {
                    it.typeReference?.typeElement is KtNullableType
                }
                .mapNotNull { parameter ->
                    analyze(parameter) {
                        parameter.symbol
                    } to parameter
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
                    children.first().determineSingleExpression()
                } else {
                    INELIGIBLE_SINGLE_EXPRESSION
                }
            } else {
                INELIGIBLE_SINGLE_EXPRESSION
            }

            // Evaluate the function, then analyze afterwards whether the candidate properties
            // could be made non-nullable.
            super.visitNamedFunction(function)
            currentFunction = previousFunction

            candidateDescriptors.asSequence()
                .mapNotNull(nullableParams::remove)
                // The heuristic for whether a nullable param can be made non-nullable is:
                // * It has been forced into a non-null type, either by `!!` or by
                //   `checkNonNull()`/`requireNonNull()`, or
                // * The containing function only consists of a single non-null check on
                //   the param at the TOP LEVEL (not nested), either via an if/when check
                //   or with a safe-qualified expression.
                .filter {
                    val onlyNonNullCheck =
                        validSingleChildExpression && it.isTopLevelNonNullCheck && !it.isNullChecked
                    val onlySafeCallsWithoutExplicitCheck =
                        validSingleChildExpression &&
                            it.isNonNullChecked &&
                            !it.isNullChecked &&
                            !it.hasExplicitNullCheck
                    it.isNonNullForced ||
                        it.isNullCheckReturnsUnit ||
                        onlyNonNullCheck ||
                        onlySafeCallsWithoutExplicitCheck
                }
                .forEach { nullableParam ->
                    report(
                        Finding(
                            Entity.from(nullableParam.param),
                            "The nullable parameter '${nullableParam.param.name}' can be made non-nullable."
                        )
                    )
                }
        }

        override fun visitCallExpression(expression: KtCallExpression) {
            val calleeName = expression.calleeExpression?.let {
                analyze(it) {
                    it.resolveToCall()
                        ?.singleFunctionCallOrNull()
                        ?.symbol
                        ?.callableId
                        ?.callableName
                        ?.asString()
                }
            }
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
            val isTopLevel = expression.parent?.parent is KtNamedFunction
            val nullCheckedDescriptor = expression.subjectExpression
                ?.collectDescendantsOfType<KtNameReferenceExpression>()
                .orEmpty()
                .mapNotNull {
                    analyze(it) {
                        it.resolveToCall()?.singleVariableAccessCall()?.symbol?.createPointer()
                    }
                }
                .filter { kaVariableSymbolPointer ->
                    analyze(expression) {
                        kaVariableSymbolPointer.restoreSymbol()?.returnType?.isMarkedNullable != false
                    }
                }
                .mapNotNull { callDescriptor -> analyze(expression) { callDescriptor.restoreSymbol() } }
            val whenConditions = expression.entries.flatMap { it.conditions.asList() }
            if (nullCheckedDescriptor.isNotEmpty()) {
                whenConditions.evaluateSubjectWhenExpression(expression, nullCheckedDescriptor, isTopLevel)
            } else {
                whenConditions.forEach { whenCondition ->
                    if (whenCondition is KtWhenConditionWithExpression) {
                        whenCondition.expression.evaluateCheckStatement(expression.elseExpression, isTopLevel)
                    }
                }
            }
            super.visitWhenExpression(expression)
        }

        override fun visitIfExpression(expression: KtIfExpression) {
            val isTopLevel = expression.parent?.parent is KtNamedFunction
            expression.condition.evaluateCheckStatement(expression.`else`, isTopLevel)
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
            val isTopLevel = expression.parent?.parent is KtNamedFunction
            updateNullableParam(expression.receiverExpression) {
                it.isNonNullChecked = true
                if (isTopLevel) {
                    it.isTopLevelNonNullCheck = true
                }
            }
            super.visitSafeQualifiedExpression(expression)
        }

        override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
            val isExtensionForNullable = analyze(expression) {
                expression.resolveToCall()
                    ?.singleFunctionCallOrNull()
                    ?.symbol
                    ?.receiverParameter
                    ?.returnType
                    ?.isMarkedNullable != false
            }
            if (isExtensionForNullable) {
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

        private fun KtExpression?.determineSingleExpression(): Boolean =
            when (this) {
                is KtReturnExpression -> INELIGIBLE_SINGLE_EXPRESSION
                is KtIfExpression -> ELIGIBLE_SINGLE_EXPRESSION
                is KtDotQualifiedExpression -> INELIGIBLE_SINGLE_EXPRESSION
                is KtCallExpression -> INELIGIBLE_SINGLE_EXPRESSION
                else -> ELIGIBLE_SINGLE_EXPRESSION
            }

        private fun KtElement?.getNonNullChecks(parentOperatorToken: IElementType?): List<KaVariableSymbol>? =
            when (this) {
                is KtBinaryExpression -> evaluateBinaryExpression(parentOperatorToken)
                is KtIsExpression -> evaluateIsExpression()
                else -> null
            }

        @Suppress("FunctionSignature")
        private fun KtExpression?.evaluateCheckStatement(
            elseExpression: KtExpression?,
            isTopLevel: Boolean = false,
        ) {
            this.getNonNullChecks(null)?.let { nonNullChecks ->
                val nullableParamCallback = if (elseExpression.isValidElseExpression()) {
                    { nullableParam: NullableParam ->
                        nullableParam.isNonNullChecked = true
                        nullableParam.isNullChecked = true
                        nullableParam.hasExplicitNullCheck = true
                    }
                } else {
                    { nullableParam: NullableParam ->
                        nullableParam.isNonNullChecked = true
                        nullableParam.hasExplicitNullCheck = true
                        if (isTopLevel) {
                            nullableParam.isTopLevelNonNullCheck = true
                        }
                    }
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
        ): List<KaVariableSymbol> {
            val leftExpression = left?.let { KtPsiUtil.safeDeparenthesize(it) }
            val rightExpression = right?.let { KtPsiUtil.safeDeparenthesize(it) }
            val nonNullChecks = mutableListOf<KaVariableSymbol>()

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

        private fun getDescriptor(leftExpression: KtElement?, rightExpression: KtElement?): KaVariableSymbol? =
            when {
                leftExpression is KtNameReferenceExpression -> leftExpression
                rightExpression is KtNameReferenceExpression -> rightExpression
                else -> null
            }?.let {
                analyze(it) {
                    it.resolveToCall()?.singleVariableAccessCall()?.symbol
                }
            }

        private fun KtIsExpression.evaluateIsExpression(): List<KaVariableSymbol> {
            val descriptor = analyze(this.leftHandSide) {
                this@evaluateIsExpression.leftHandSide.resolveToCall()
                    ?.singleVariableAccessCall()
                    ?.symbol
            } ?: return emptyList()
            return if (isNullableCheck(typeReference, isNegated)) {
                nullableParams[descriptor]?.let { it.isNullChecked = true }
                emptyList()
            } else {
                listOf(descriptor)
            }
        }

        @Suppress("CyclomaticComplexMethod")
        private fun List<KtWhenCondition>.evaluateSubjectWhenExpression(
            expression: KtWhenExpression,
            subjectDescriptors: List<KaVariableSymbol>,
            isTopLevel: Boolean = false,
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
                    if (isNonNullChecked) {
                        it.isNonNullChecked = true
                        it.hasExplicitNullCheck = true
                        if (isTopLevel && !isNullChecked) {
                            it.isTopLevelNonNullCheck = true
                        }
                    }
                }
            }
        }

        private fun isNullableCheck(typeReference: KtTypeReference?, isNegated: Boolean): Boolean {
            typeReference ?: return false
            val isNullable = analyze(typeReference) {
                typeReference.type.isMarkedNullable
            }
            return (isNullable && !isNegated) || (!isNullable && isNegated)
        }

        private fun KtExpression?.isValidElseExpression(): Boolean =
            this != null && this !is KtIfExpression && this !is KtWhenExpression

        private fun updateNullableParam(expression: KtExpression, updateCallback: (NullableParam) -> Unit) {
            analyze(expression) {
                expression.resolveToCall()?.singleVariableAccessCall()?.let {
                    nullableParams[it.symbol]
                }?.let(updateCallback)
            }
        }
    }

    private class NullableParam(val param: KtParameter) {
        var isNullChecked = false
        var isNonNullChecked = false
        var isNonNullForced = false
        var isNullCheckReturnsUnit = false
        var isTopLevelNonNullCheck = false
        var hasExplicitNullCheck = false
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
                    Finding(
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
                    ?.let {
                        analyze(it) {
                            it.resolveToCall()
                                ?.singleVariableAccessCall()
                                ?.symbol
                                ?.callableId
                                ?.asSingleFqName()
                        }
                    }
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
            if (isOpen() || isAbstract() || containingClass()?.isInterface() == true) return false
            val propertyType = this.typeReference ?: return false

            analyze(propertyType) {
                if (!propertyType.type.isMarkedNullable) return false
            }

            val isSetToNonNullable = initializer?.isNullableType() != true &&
                getter?.isNullableType() != true &&
                delegate?.returnsNullable() != true
            val cannotSetViaNonPrivateMeans =
                !isVar || (isPrivate() || (setter?.isPrivate() == true))
            return isSetToNonNullable && cannotSetViaNonPrivateMeans
        }

        private fun KtPropertyDelegate?.returnsNullable(): Boolean {
            val delegate = this ?: return true
            return analyze(delegate) {
                val functionSymbol = delegate
                    .mainReference
                    ?.resolveToSymbols()
                    ?.filterIsInstance<KaFunctionSymbol>()
                    ?.firstOrNull {
                        it.callableId?.callableName == OperatorNameConventions.GET_VALUE
                    }
                functionSymbol?.run {
                    if (returnType is KaTypeParameterType) {
                        // todo<k2> ignoring some case which in pre k2 was passing as earlier
                        //  using BindingContext.DELEGATED_PROPERTY_RESOLVED_CALL we were able to
                        //  get the actual type of the implementation
                        returnType.isNullable
                    } else {
                        returnType.isMarkedNullable
                    }
                } ?: true
            }
        }

        private fun KtExpression?.isNullableType(): Boolean =
            when (this) {
                is KtPropertyAccessor -> {
                    if (initializer != null) {
                        initializer?.let { initializer ->
                            analyze(initializer) { initializer.isNullable(true) }
                        } ?: true
                    } else {
                        bodyExpression
                            ?.collectDescendantsOfType<KtReturnExpression>()
                            ?.any { it.returnedExpression.isNullableType() } == true
                    }
                }

                else -> {
                    // only consider types which can be made non-nullable as nullable to warn on
                    // cases where a field has declared type `T?` but is only assigned as `T`; here
                    // `T` should not be considered nullable to enforce that the field could be
                    // declared as just `T`
                    this?.let {
                        analyze(it) {
                            val expressionType = it.expressionType
                            if (expressionType is KaTypeParameterType) {
                                expressionType.isMarkedNullable
                            } else {
                                this@isNullableType.isNullable(true)
                            }
                        }
                    } ?: true
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

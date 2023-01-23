package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isOperator
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.expressions.OperatorConventions
import org.jetbrains.kotlin.util.OperatorNameConventions

private const val ARRAY_GET_METHOD_NAME = "get"

/**
 * Reports unused private functions.
 * If these private functions are unused they should be removed. Otherwise, this dead code
 * can lead to confusion and potential bugs.
 */
@Suppress("ViolatesTypeResolutionRequirements")
@ActiveByDefault(since = "1.16.0")
class UnusedPrivateMember(config: Config = Config.empty) : Rule(config) {

    override val defaultRuleIdAliases: Set<String> = setOf("UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

    override val issue: Issue = Issue(
        "UnusedPrivateMember",
        Severity.Maintainability,
        "Private function is unused and should be removed.",
        Debt.FIVE_MINS,
    )

    @Configuration("unused private function names matching this regex are ignored")
    private val allowedNames: Regex by config("(_|ignored|expected|serialVersionUID)", String::toRegex)

    override fun visit(root: KtFile) {
        super.visit(root)
        root.acceptUnusedMemberVisitor(UnusedFunctionVisitor(allowedNames, bindingContext))
    }

    private fun KtFile.acceptUnusedMemberVisitor(visitor: UnusedFunctionVisitor) {
        accept(visitor)
        visitor.getUnusedReports(issue).forEach { report(it) }
    }
}

private class UnusedFunctionVisitor(
    private val allowedNames: Regex,
    private val bindingContext: BindingContext,
) : DetektVisitor() {

    private val functionDeclarations = mutableMapOf<String, MutableList<KtFunction>>()
    private val functionReferences = mutableMapOf<String, MutableList<KtReferenceExpression>>()
    private val propertyDelegates = mutableListOf<KtPropertyDelegate>()

    @Suppress("ComplexMethod", "LongMethod")
    fun getUnusedReports(issue: Issue): List<CodeSmell> {
        val propertyDelegateResultingDescriptors by lazy(LazyThreadSafetyMode.NONE) {
            propertyDelegates.flatMap { it.resultingDescriptors() }
        }
        return functionDeclarations
            .filterNot { (_, functions) ->
                // Without a binding context we can't know if an operator is called. So we ignore it to avoid
                // false positives. More context at #4242
                bindingContext == BindingContext.EMPTY && functions.any { it.isOperator() }
            }
            .flatMap { (functionName, functions) ->
                val isOperator = functions.any { it.isOperator() }
                val references = functionReferences[functionName].orEmpty()
                val unusedFunctions = when {
                    (functions.size > 1 || isOperator) && bindingContext != BindingContext.EMPTY -> {
                        val functionNameAsName = Name.identifier(functionName)
                        val referencesViaOperator = if (isOperator) {
                            val operatorToken = OperatorConventions.getOperationSymbolForName(functionNameAsName)
                            val operatorValue = (operatorToken as? KtSingleValueToken)?.value
                            val directReferences = operatorValue?.let { functionReferences[it] }.orEmpty()
                            val assignmentReferences = when (operatorToken) {
                                KtTokens.PLUS,
                                KtTokens.MINUS,
                                KtTokens.MUL,
                                KtTokens.DIV,
                                KtTokens.PERC,
                                -> operatorValue?.let { functionReferences["$it="] }.orEmpty()
                                else -> emptyList()
                            }
                            val containingReferences = if (functionNameAsName == OperatorNameConventions.CONTAINS) {
                                listOf(KtTokens.IN_KEYWORD, KtTokens.NOT_IN).flatMap {
                                    functionReferences[it.value].orEmpty()
                                }
                            } else {
                                emptyList()
                            }
                            directReferences + assignmentReferences + containingReferences
                        } else {
                            emptyList()
                        }
                        val referenceDescriptors = (references + referencesViaOperator)
                            .mapNotNull { it.getResolvedCall(bindingContext)?.resultingDescriptor }
                            .map { it.original }
                            .let {
                                if (functionNameAsName in OperatorNameConventions.DELEGATED_PROPERTY_OPERATORS) {
                                    it + propertyDelegateResultingDescriptors
                                } else {
                                    it
                                }
                            }
                        functions.filterNot {
                            bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, it] in referenceDescriptors
                        }
                    }
                    references.isEmpty() -> functions
                    else -> emptyList()
                }
                unusedFunctions.map {
                    CodeSmell(issue, Entity.atName(it), "Private function `$functionName` is unused.")
                }
            }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!isDeclaredInsideAnInterface(function) && function.isPrivate()) {
            collectFunction(function)
        }
        super.visitNamedFunction(function)
    }

    private fun isDeclaredInsideAnInterface(function: KtNamedFunction) =
        function.getStrictParentOfType<KtClass>()?.isInterface() == true

    private fun collectFunction(function: KtNamedFunction) {
        val name = function.nameAsSafeName.identifier
        if (!allowedNames.matches(name)) {
            functionDeclarations.getOrPut(name) { mutableListOf() }.add(function)
        }
    }

    private fun KtPropertyDelegate.resultingDescriptors(): List<FunctionDescriptor> {
        val property = this.parent as? KtProperty ?: return emptyList()
        val propertyDescriptor =
            bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, property] as? PropertyDescriptor
        return listOfNotNull(propertyDescriptor?.getter, propertyDescriptor?.setter).mapNotNull {
            bindingContext[BindingContext.DELEGATED_PROPERTY_RESOLVED_CALL, it]?.resultingDescriptor
        }
    }

    override fun visitPropertyDelegate(delegate: KtPropertyDelegate) {
        super.visitPropertyDelegate(delegate)
        propertyDelegates.add(delegate)
    }

    /*
     * We need to collect all private function declarations and references to these functions
     * for the whole file as Kotlin allows access to private and internal object declarations
     * from everywhere in the file.
     */
    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        val name = when (expression) {
            is KtOperationReferenceExpression -> expression.getReferencedName()
            is KtNameReferenceExpression -> expression.getReferencedName()
            is KtArrayAccessExpression -> ARRAY_GET_METHOD_NAME
            else -> null
        } ?: return
        functionReferences.getOrPut(name) { mutableListOf() }.add(expression)
    }
}

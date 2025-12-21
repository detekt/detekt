package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isOperator
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtCallExpression
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
import org.jetbrains.kotlin.types.expressions.OperatorConventions
import org.jetbrains.kotlin.util.OperatorNameConventions

private const val ARRAY_GET_METHOD_NAME = "get"

/**
 * Reports unused private functions.
 *
 * If these private functions are unused they should be removed. Otherwise, this dead code
 * can lead to confusion and potential bugs.
 */
@ActiveByDefault(since = "1.16.0")
@Alias("unused")
class UnusedPrivateFunction(config: Config) :
    Rule(
        config,
        "Private function is unused and should be removed."
    ),
    RequiresAnalysisApi {

    @Configuration("unused private function names matching this regex are ignored")
    private val allowedNames: Regex by config("", String::toRegex)

    override fun visit(root: KtFile) {
        super.visit(root)
        val visitor = UnusedFunctionVisitor(allowedNames)
        root.accept(visitor)
        visitor.getUnusedReports().forEach { report(it) }
    }
}

private class UnusedFunctionVisitor(private val allowedNames: Regex) : DetektVisitor() {

    private val functionDeclarations = mutableMapOf<String, MutableList<KtFunction>>()
    private val functionReferences = mutableMapOf<String, MutableList<KtReferenceExpression>>()
    private val invokeOperatorReferences = mutableMapOf<KaCallableSymbol, MutableList<KtReferenceExpression>>()
    private val propertyDelegates = mutableListOf<KtPropertyDelegate>()

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    fun getUnusedReports(): List<Finding> {
        val propertyDelegateSymbols by lazy(LazyThreadSafetyMode.NONE) {
            propertyDelegates.flatMap { it.symbols() }
        }
        return functionDeclarations
            .flatMap { (functionName, functions) ->
                val isOperator = functions.any { it.isOperator() }
                val references = functionReferences[functionName].orEmpty()
                val unusedFunctions = when {
                    functions.size > 1 || isOperator -> {
                        val functionNameAsName = Name.identifier(functionName)
                        val operatorToken = OperatorConventions.getOperationSymbolForName(functionNameAsName)
                        val referencesViaOperator = if (
                            isOperator &&
                            functionNameAsName != OperatorNameConventions.INVOKE
                        ) {
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
                        } else if (functionNameAsName == OperatorNameConventions.INVOKE) {
                            getInvokeReferences(functions)
                        } else {
                            emptyList()
                        }
                        val referenceSymbols = (references + referencesViaOperator)
                            .mapNotNull {
                                analyze(it) {
                                    it.resolveToCall()?.singleFunctionCallOrNull()?.symbol
                                        ?: it.mainReference.resolveToSymbol() as? KaFunctionSymbol
                                }
                            }
                            .let {
                                if (functionNameAsName in OperatorNameConventions.DELEGATED_PROPERTY_OPERATORS) {
                                    it + propertyDelegateSymbols
                                } else {
                                    it
                                }
                            }
                        functions.filterNot {
                            analyze(it) {
                                it.symbol in referenceSymbols
                            }
                        }
                    }

                    references.isEmpty() -> functions

                    else -> emptyList()
                }
                unusedFunctions.map {
                    Finding(Entity.atName(it), "Private function `$functionName` is unused.")
                }
            }
    }

    private fun getInvokeReferences(functions: MutableList<KtFunction>) =
        functions.flatMap { function ->
            analyze(function) { invokeOperatorReferences[function.symbol] }.orEmpty()
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

    private fun KtPropertyDelegate.symbols(): List<KaFunctionSymbol> {
        val delegate = (this.parent as? KtProperty)?.delegate ?: return emptyList()
        return analyze(delegate) {
            delegate.mainReference?.resolveToSymbols()?.filterIsInstance<KaFunctionSymbol>().orEmpty()
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

            is KtCallExpression -> {
                analyze(expression) {
                    val symbol = expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol
                    val psi = symbol?.psi
                    if ((psi as? KtNamedFunction)?.isOperator() == true) {
                        invokeOperatorReferences.getOrPut(symbol) { mutableListOf() }.add(expression)
                    }
                    null
                }
            }

            else -> null
        } ?: return
        functionReferences.getOrPut(name) { mutableListOf() }.add(expression)
    }
}

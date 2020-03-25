package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isAbstract
import io.gitlab.arturbosch.detekt.rules.isExternal
import io.gitlab.arturbosch.detekt.rules.isMainFunction
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOperator
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.expressions.OperatorConventions

/**
 * Reports unused private properties, function parameters and functions.
 * If these private elements are unused they should be removed. Otherwise this dead code
 * can lead to confusion and potential bugs.
 *
 * @configuration allowedNames - unused private member names matching this regex are ignored
 * (default: `'(_|ignored|expected|serialVersionUID)'`)
 */
class UnusedPrivateMember(config: Config = Config.empty) : Rule(config) {

    companion object {
        const val ALLOWED_NAMES_PATTERN = "allowedNames"
    }

    override val defaultRuleIdAliases: Set<String> = setOf("UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

    override val issue: Issue = Issue("UnusedPrivateMember",
            Severity.Maintainability,
            "Private member is unused.",
            Debt.FIVE_MINS)

    private val allowedNames by LazyRegex(ALLOWED_NAMES_PATTERN, "(_|ignored|expected|serialVersionUID)")

    override fun visit(root: KtFile) {
        super.visit(root)
        root.acceptUnusedMemberVisitor(UnusedFunctionVisitor(allowedNames, bindingContext))
        root.acceptUnusedMemberVisitor(UnusedParameterVisitor(allowedNames))
        root.acceptUnusedMemberVisitor(UnusedPropertyVisitor(allowedNames))
    }

    private fun KtFile.acceptUnusedMemberVisitor(visitor: UnusedMemberVisitor) {
        accept(visitor)
        visitor.getUnusedReports(issue).forEach { report(it) }
    }
}

private abstract class UnusedMemberVisitor(protected val allowedNames: Regex) : DetektVisitor() {

    abstract fun getUnusedReports(issue: Issue): List<CodeSmell>
}

private class UnusedFunctionVisitor(
    allowedNames: Regex,
    private val bindingContext: BindingContext
) : UnusedMemberVisitor(allowedNames) {

    private val functionDeclarations = mutableMapOf<String, MutableList<KtFunction>>()
    private val functionReferences = mutableMapOf<String, MutableList<KtReferenceExpression>>()

    override fun getUnusedReports(issue: Issue): List<CodeSmell> {
        return functionDeclarations.flatMap { (name, functions) ->
            val isOperator = functions.any { it.isOperator() }
            val references = functionReferences[name].orEmpty()
            val unusedFunctions = when {
                (functions.size > 1 || isOperator) && bindingContext != BindingContext.EMPTY -> {
                    val referencesViaOperator = if (isOperator) {
                        val operatorToken = OperatorConventions.getOperationSymbolForName(Name.identifier(name))
                        val operatorValue = (operatorToken as? KtSingleValueToken)?.value
                        operatorValue?.let { functionReferences[it] }.orEmpty()
                    } else {
                        emptyList()
                    }
                    val referenceDescriptors = (references + referencesViaOperator).mapNotNull {
                        it.getResolvedCall(bindingContext)?.resultingDescriptor
                    }
                    functions.filter {
                        bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, it] !in referenceDescriptors
                    }
                }
                references.isEmpty() -> functions
                else -> emptyList()
            }
            unusedFunctions.map {
                CodeSmell(issue, Entity.from(it), "Private function $name is unused.")
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

    /*
     * We need to collect all private function declarations and references to these functions
     * for the whole file as Kotlin allows to access private and internal object declarations
     * from everywhere in the file.
     */
    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        val name = when (expression) {
            is KtOperationReferenceExpression -> expression.getReferencedName()
            is KtNameReferenceExpression -> expression.getReferencedName()
            else -> null
        } ?: return
        functionReferences.getOrPut(name) { mutableListOf() }.add(expression)
    }
}

private class UnusedParameterVisitor(allowedNames: Regex) : UnusedMemberVisitor(allowedNames) {

    private var unusedParameters: MutableSet<KtParameter> = mutableSetOf()

    override fun getUnusedReports(issue: Issue): List<CodeSmell> {
        return unusedParameters.map {
            CodeSmell(issue, Entity.from(it), "Function parameter ${it.nameAsSafeName.identifier} is unused.")
        }
    }

    override fun visitClass(klass: KtClass) {
        if (klass.isInterface()) return

        super.visitClassOrObject(klass)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!function.isRelevant()) {
            return
        }

        collectParameters(function)

        super.visitNamedFunction(function)
    }

    private fun collectParameters(function: KtNamedFunction) {
        val parameters = mutableMapOf<String, KtParameter>()
        function.valueParameterList?.parameters?.forEach { parameter ->
            val name = parameter.nameAsSafeName.identifier
            if (!allowedNames.matches(name)) {
                parameters[name] = parameter
            }
        }

        function.accept(object : DetektVisitor() {
            override fun visitProperty(property: KtProperty) {
                if (property.isLocal) {
                    val name = property.nameAsSafeName.identifier
                    parameters.remove(name)
                }
                super.visitProperty(property)
            }

            override fun visitReferenceExpression(expression: KtReferenceExpression) {
                parameters.remove(expression.text)
                super.visitReferenceExpression(expression)
            }
        })

        unusedParameters.addAll(parameters.values)
    }

    private fun KtNamedFunction.isRelevant() = !isAllowedToHaveUnusedParameters()

    private fun KtNamedFunction.isAllowedToHaveUnusedParameters() =
            isAbstract() || isOpen() || isOverride() || isOperator() || isMainFunction() || isExternal()
}

private class UnusedPropertyVisitor(allowedNames: Regex) : UnusedMemberVisitor(allowedNames) {

    private val properties = mutableSetOf<KtNamedDeclaration>()
    private val nameAccesses = mutableSetOf<String>()

    override fun getUnusedReports(issue: Issue): List<CodeSmell> {
        return properties
                .filter { it.nameAsSafeName.identifier !in nameAccesses }
                .map { CodeSmell(issue, Entity.from(it),
                    "Private property ${it.nameAsSafeName.identifier} is unused.") }
    }

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        if (parameter.isLoopParameter) {
            val destructuringDeclaration = parameter.destructuringDeclaration
            if (destructuringDeclaration != null) {
                for (variable in destructuringDeclaration.entries) {
                    maybeAddUnusedProperty(variable)
                }
            } else {
                maybeAddUnusedProperty(parameter)
            }
        }
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor)
        constructor.valueParameters
                .filter { it.isPrivate() || !it.hasValOrVar() }
                .forEach { maybeAddUnusedProperty(it) }
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        super.visitSecondaryConstructor(constructor)
        constructor.valueParameters.forEach { maybeAddUnusedProperty(it) }
    }

    private fun maybeAddUnusedProperty(it: KtNamedDeclaration) {
        if (!allowedNames.matches(it.nameAsSafeName.identifier)) {
            properties.add(it)
        }
    }

    override fun visitProperty(property: KtProperty) {
        if (property.isPrivate() && property.isMemberOrTopLevel() || property.isLocal) {
            maybeAddUnusedProperty(property)
        }
        super.visitProperty(property)
    }

    private fun KtProperty.isMemberOrTopLevel() = isMember || isTopLevel

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        nameAccesses.add(expression.text)
        super.visitReferenceExpression(expression)
    }
}

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isAbstract
import io.gitlab.arturbosch.detekt.rules.isActual
import io.gitlab.arturbosch.detekt.rules.isExpect
import io.gitlab.arturbosch.detekt.rules.isExternal
import io.gitlab.arturbosch.detekt.rules.isMainFunction
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOperator
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentName
import org.jetbrains.kotlin.psi.psiUtil.isProtected

/**
 * An unused parameter can be removed to simplify the signature of the function.
 *
 * <noncompliant>
 * fun foo(unused: String) {
 *     println()
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(used: String) {
 *     println(used)
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.23.0")
@Alias("UNUSED_PARAMETER", "unused")
class UnusedParameter(config: Config) : Rule(
    config,
    "Function parameter is unused and should be removed."
) {

    @Configuration("unused parameter names matching this regex are ignored")
    private val allowedNames: Regex by config("ignored|expected", String::toRegex)

    @Configuration("ignore functions annotated with these annotations")
    private val ignoreAnnotatedFunctions: List<String> by config(emptyList())

    @Configuration("ignore parameters annotated with these annotations")
    private val ignoreAnnotatedParameters: List<String> by config(emptyList())

    override fun visit(root: KtFile) {
        super.visit(root)
        val visitor = UnusedParameterVisitor(allowedNames, ignoreAnnotatedFunctions, ignoreAnnotatedParameters)
        root.accept(visitor)
        visitor.getUnusedReports().forEach { report(it) }
    }
}

private class UnusedParameterVisitor(
    private val allowedNames: Regex,
    private val ignoreAnnotatedFunctions: List<String>,
    private val ignoreAnnotatedParameters: List<String>,
) : DetektVisitor() {

    private val unusedParameters: MutableSet<KtParameter> = mutableSetOf()

    fun getUnusedReports(): List<Finding> =
        unusedParameters.map {
            Finding(Entity.atName(it), "Function parameter `${it.nameAsSafeName.identifier}` is unused.")
        }

    override fun visitClassOrObject(klassOrObject: KtClassOrObject) {
        if (klassOrObject.isExpect()) return

        super.visitClassOrObject(klassOrObject)
    }

    override fun visitClass(klass: KtClass) {
        if (klass.isInterface()) return
        if (klass.isExternal()) return

        super.visitClass(klass)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!function.isRelevant() || ignoreAnnotatedFunctions.any { function.hasAnnotation(it) }) {
            return
        }

        collectParameters(function)

        super.visitNamedFunction(function)
    }

    private fun collectParameters(function: KtNamedFunction) {
        val parameters = mutableMapOf<String, KtParameter>()
        function.valueParameterList?.parameters?.forEach { parameter ->
            val name = parameter.nameAsSafeName.identifier
            if (!allowedNames.matches(name) && ignoreAnnotatedParameters.none { parameter.hasAnnotation(it) }) {
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
                if (expression.parent !is KtValueArgumentName) {
                    parameters.remove(expression.text.removeSurrounding("`"))
                }
                super.visitReferenceExpression(expression)
            }
        })

        unusedParameters.addAll(parameters.values)
    }

    private fun KtNamedFunction.isRelevant() = !isAllowedToHaveUnusedParameters()

    private fun KtNamedFunction.isAllowedToHaveUnusedParameters() =
        isAbstract() ||
            isOpen() ||
            isOverride() ||
            isOperator() ||
            isMainFunction() ||
            isExternal() ||
            isExpect() ||
            isActual() ||
            isProtected()
}

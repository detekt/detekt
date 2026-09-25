package dev.detekt.rules.style

import com.intellij.psi.PsiElement
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
import dev.detekt.psi.isAbstract
import dev.detekt.psi.isActual
import dev.detekt.psi.isExpect
import dev.detekt.psi.isExternal
import dev.detekt.psi.isMainFunction
import dev.detekt.psi.isOpen
import dev.detekt.psi.isOperator
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtValueArgumentName
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
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
class UnusedParameter(config: Config) :
    Rule(config, "Function parameter is unused and should be removed."),
    RequiresAnalysisApi {

    @Configuration("unused parameter names matching this regex are ignored")
    private val allowedNames: Regex by config("ignored|expected", String::toRegex)

    override fun visit(root: KtFile) {
        super.visit(root)

        // Declaration and usage need different traversals. Collection stops at declarations that are
        // allowed to have unused parameters, while a reference to a parameter can sit anywhere in the
        // file, including inside those same declarations.
        val declarations = ParameterDeclarationVisitor(allowedNames)
        root.accept(declarations)
        val usages = ParameterUsageVisitor()
        root.accept(usages)

        declarations.candidates
            .filterNot { it in usages.usedParameters }
            .forEach {
                report(Finding(Entity.atName(it), "Function parameter `${it.nameAsSafeName.identifier}` is unused."))
            }
    }
}

private class ParameterDeclarationVisitor(private val allowedNames: Regex) : DetektVisitor() {

    val candidates: MutableSet<KtParameter> = mutableSetOf()

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
        if (!function.isRelevant()) {
            return
        }

        collectParameters(function.valueParameters)

        super.visitNamedFunction(function)
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        if (constructor.isRelevant()) {
            // A parameter declared with `val` or `var` is a property, so UnusedPrivateProperty owns it.
            collectParameters(constructor.valueParameters.filterNot { it.isPropertyParameter() })
        }

        super.visitPrimaryConstructor(constructor)
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        if (constructor.isRelevant()) {
            collectParameters(constructor.valueParameters)
        }

        super.visitSecondaryConstructor(constructor)
    }

    private fun collectParameters(parameters: List<KtParameter>) {
        parameters
            .filterNot { allowedNames.matches(it.nameAsSafeName.identifier) }
            .forEach { candidates.add(it) }
    }

    private fun KtNamedFunction.isRelevant() = !isAllowedToHaveUnusedParameters()

    private fun KtConstructor<*>.isRelevant() = !isAllowedToHaveUnusedParameters()

    private fun KtConstructor<*>.isAllowedToHaveUnusedParameters(): Boolean {
        if (isActual()) return true
        val klass = containingClassOrObject as? KtClass ?: return false
        return klass.isData() || klass.isValue() || klass.isInline()
    }

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

private class ParameterUsageVisitor : DetektVisitor() {

    val usedParameters: MutableSet<PsiElement> = mutableSetOf()

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)

        if (expression !is KtNameReferenceExpression) return
        // `foo(bar = 1)` names the callee's parameter, it does not read it.
        if (expression.parent is KtValueArgumentName) return

        analyze(expression) {
            val symbol = expression.mainReference.resolveToSymbol() as? KaValueParameterSymbol ?: return
            symbol.psi?.let { usedParameters.add(it) }
        }
    }
}

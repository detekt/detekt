package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
import org.jetbrains.kotlin.descriptors.isTopLevelInPackage
import org.jetbrains.kotlin.load.kotlin.toSourceElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.resolve.source.toSourceElement

/**
 * An unused variable can be removed to simplify the source file.
 *
 * This rule also detects top level private variables
 *
 * <noncompliant>
 * fun foo() {
 *     val unused = "unused"
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(){
 *     val used = "used"
 *     println(used)
 * }
 * </compliant>
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.23.0")
class UnusedVariable(config: Config) : Rule(
    config,
    "Variable is unused and should be removed."
) {

    override val defaultRuleIdAliases: Set<String> =
        setOf("UNUSED_VARIABLE", "unused")

    @Configuration("unused variables names matching this regex are ignored")
    private val allowedNames: Regex by config(
        "ignored|_",
        String::toRegex
    )

    override fun visit(root: KtFile) {
        super.visit(root)
        val visitor = UnusedVariableVisitor(allowedNames, bindingContext)
        root.accept(visitor)
        visitor.getUnusedReports().forEach { report(it) }
    }
}

@Suppress("unused")
private class UnusedVariableVisitor(
    private val allowedNames: Regex,
    private val bindingContext: BindingContext,
) : DetektVisitor() {

    private val variables = mutableMapOf<PsiElement, KtNamedDeclaration>()
    private val usedVariables = mutableSetOf<PsiElement>()

    fun getUnusedReports(): List<CodeSmell> {
        val unusedVariableNames = variables
            .filterKeys { it !in usedVariables }

        return unusedVariableNames
            .values
            .map {
                CodeSmell(
                    entity = Entity.atName(it),
                    message = "Variable `${it.nameAsSafeName.identifier}` is unused."
                )
            }
    }

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)

        when (dcl) {
            is KtProperty -> if (dcl.isLocal || (dcl.isTopLevel && dcl.isPrivate())) {
                registerNewDeclaration(dcl)
            }

            is KtParameter -> when {
                dcl.destructuringDeclaration != null -> dcl.destructuringDeclaration?.entries?.forEach {
                    registerNewDeclaration(it)
                }

                dcl.isLoopParameter -> registerNewDeclaration(dcl)
            }
        }
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)

        val references = when (expression) {
            is KtNameReferenceExpression -> expression.getReferenceTargets(bindingContext)
            is KtCallExpression -> {
                expression.getChildrenOfType<KtValueArgumentList>()
                    .flatMap { it.arguments }
                    .flatMap {
                        it.getArgumentExpression()?.getReferenceTargets(bindingContext).orEmpty()
                    }
            }

            else -> return
        }

        references
            .filter { it.isTopLevelPrivateVariable() || it is LocalVariableDescriptor }
            .forEach(::registerVariableUse)
    }

    private fun registerVariableUse(descriptor: DeclarationDescriptor) {
        descriptor.toSourceElement.getPsi()?.also {
            usedVariables.add(it)
        }
    }

    private fun registerNewDeclaration(declaration: KtNamedDeclaration) {
        if (allowedNames.matches(declaration.name.orEmpty())) {
            return
        }

        declaration.toSourceElement().getPsi()?.also {
            variables[it] = declaration
        }
    }

    fun DeclarationDescriptor.isTopLevelPrivateVariable() =
        this is PropertyDescriptor && isTopLevelInPackage() && visibility.name == Visibilities.Private.name
}

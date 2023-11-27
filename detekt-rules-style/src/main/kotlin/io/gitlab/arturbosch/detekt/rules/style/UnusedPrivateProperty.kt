package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isActual
import io.gitlab.arturbosch.detekt.rules.isExpect
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * An unused private property can be removed to simplify the source file.
 *
 * This rule also detects unused constructor parameters since these can become
 * properties of the class when they are declared with `val` or `var`.
 *
 * <noncompliant>
 * class Foo {
 *     private val unused = "unused"
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Foo {
 *     private val used = "used"
 *
 *     fun greet() {
 *         println(used)
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.23.0")
class UnusedPrivateProperty(config: Config = Config.empty) : Rule(config) {
    override val defaultRuleIdAliases: Set<String> =
        setOf("UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused", "UnusedPrivateMember")

    override val issue: Issue = Issue(
        "UnusedPrivateProperty",
        "Property is unused and should be removed.",
    )

    @Configuration("unused property names matching this regex are ignored")
    private val allowedNames: Regex by config("_|ignored|expected|serialVersionUID", String::toRegex)

    override fun visit(root: KtFile) {
        super.visit(root)
        val visitor = UnusedPrivatePropertyVisitor(allowedNames)
        root.accept(visitor)
        visitor.getUnusedReports(issue).forEach { report(it) }
    }
}

@Suppress("unused")
private class UnusedPrivatePropertyVisitor(private val allowedNames: Regex) : DetektVisitor() {

    private val properties = mutableSetOf<KtNamedDeclaration>()
    private val nameAccesses = mutableSetOf<String>()

    fun getUnusedReports(issue: Issue): List<CodeSmell> {
        return properties
            .filter { it.nameAsSafeName.identifier !in nameAccesses }
            .map {
                CodeSmell(
                    issue,
                    Entity.atName(it),
                    "Private property `${it.nameAsSafeName.identifier}` is unused.",
                )
            }
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
            .filter {
                (it.isPrivate() || (!it.hasValOrVar() && !constructor.isActual())) &&
                    it.containingClassOrObject?.isExpect() == false &&
                    isConstructorForDataOrValueClass(constructor).not()
            }
            .forEach { maybeAddUnusedProperty(it) }
    }

    private fun isConstructorForDataOrValueClass(constructor: PsiElement): Boolean {
        val parent = constructor.parent as? KtClass ?: return false
        return parent.isData() || parent.isValue() || parent.isInline()
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
        if (!skipNode(expression)) {
            nameAccesses.add(expression.text.removeSurrounding("`"))
        }
        super.visitReferenceExpression(expression)
    }

    private fun skipNode(expression: KtReferenceExpression): Boolean {
        return when {
            expression.isPackageDirective() -> true
            expression.isImportDirective() -> true
            else -> false
        }
    }
}

private fun PsiElement.isPackageDirective(): Boolean {
    return this is KtPackageDirective || parent?.isPackageDirective() == true
}

private fun PsiElement.isImportDirective(): Boolean {
    return this is KtImportDirective || parent?.isImportDirective() == true
}

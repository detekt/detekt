package io.gitlab.arturbosch.detekt.rules.style

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
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaLocalVariableSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.resolve.source.toSourceElement

/**
 * An unused variable can be removed to simplify the source file.
 *
 * <noncompliant>
 * fun foo() {
 *     val unused = "unused"
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     val used = "used"
 *     println(used)
 * }
 * </compliant>
 */
@ActiveByDefault(since = "2.0.0")
@Alias("UNUSED_VARIABLE", "unused")
class UnusedVariable(config: Config) :
    Rule(
        config,
        "Variable is unused and should be removed."
    ),
    RequiresAnalysisApi {

    @Configuration("unused variables names matching this regex are ignored")
    private val allowedNames: Regex by config(
        "ignored|_",
        String::toRegex
    )

    override fun visit(root: KtFile) {
        super.visit(root)
        val visitor = UnusedVariableVisitor(allowedNames)
        root.accept(visitor)
        visitor.getUnusedReports().forEach { report(it) }
    }
}

@Suppress("unused")
private class UnusedVariableVisitor(private val allowedNames: Regex) : DetektVisitor() {

    private val variables = mutableMapOf<PsiElement, KtNamedDeclaration>()
    private val usedVariables = mutableSetOf<PsiElement>()

    fun getUnusedReports(): List<Finding> {
        val unusedVariableNames = variables
            .filterKeys { it !in usedVariables }

        return unusedVariableNames
            .values
            .filter { !allowedNames.matches(it.nameAsSafeName.identifier) }
            .map {
                Finding(
                    entity = Entity.atName(it),
                    message = "Variable `${it.nameAsSafeName.identifier}` is unused."
                )
            }
    }

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)

        when (dcl) {
            is KtProperty -> if (dcl.isLocal) {
                registerNewDeclaration(dcl)
            }

            is KtParameter -> when {
                dcl.destructuringDeclaration != null ->
                    dcl.destructuringDeclaration
                        ?.entries
                        ?.forEach(::registerNewDeclaration)
                dcl.isLoopParameter -> registerNewDeclaration(dcl)
            }

            is KtVariableDeclaration -> registerNewDeclaration(dcl)
        }
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)

        val references = when (expression) {
            is KtNameReferenceExpression -> {
                analyze(expression) {
                    listOfNotNull(expression.resolveToLocalVariableSymbol())
                }
            }
            is KtCallExpression -> {
                val arguments = expression.getChildrenOfType<KtValueArgumentList>().flatMap { it.arguments }
                if (arguments.isNotEmpty()) {
                    analyze(expression) {
                        arguments.mapNotNull {
                            it.getArgumentExpression()?.resolveToLocalVariableSymbol()
                        }
                    }
                } else {
                    emptyList()
                }
            }

            else -> return
        }

        references.forEach(::registerVariableUse)
    }

    @Suppress("ModifierListSpacing")
    context(session: KaSession)
    private fun KtExpression.resolveToLocalVariableSymbol(): KaLocalVariableSymbol? = with(session) {
        mainReference?.resolveToSymbol() as? KaLocalVariableSymbol
    }

    private fun registerVariableUse(symbol: KaLocalVariableSymbol) {
        symbol.psi?.also {
            usedVariables.add(it)
        }
    }

    private fun registerNewDeclaration(declaration: KtNamedDeclaration) {
        declaration.toSourceElement().getPsi()?.also {
            variables[it] = declaration
        }
    }
}

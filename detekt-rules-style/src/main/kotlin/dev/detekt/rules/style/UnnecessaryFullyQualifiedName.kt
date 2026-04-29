package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaScopeKind
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getPossiblyQualifiedCallExpression
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * This rule reports unnecessary fully qualified class names and function calls.
 * The fully qualified names can be replaced with imports to make the code more readable.
 *
 * The rule does not report:
 * - Import statements
 * - Package declarations
 * - String literals
 * - Nested class references without packages (e.g., Outer.Inner)
 *
 * See [PMD UnnecessaryFullyQualifiedName](https://pmd.github.io/latest/pmd_rules_java_codestyle.html#unnecessaryfullyqualifiedname)
 * for a similar rule in the Java ecosystem.
 *
 * <noncompliant>
 * class Foo {
 *     fun bar(): java.util.List<String> {
 *         val date = java.time.LocalDate.now()
 *         return java.util.ArrayList()
 *     }
 *     fun baz() {
 *         kotlin.io.println("Hello")
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * import java.time.LocalDate
 * import java.util.ArrayList
 * import java.util.List
 *
 * class Foo {
 *     fun bar(): List<String> {
 *         val date = LocalDate.now()
 *         return ArrayList()
 *     }
 *     fun baz() {
 *         println("Hello")
 *     }
 * }
 * </compliant>
 */
class UnnecessaryFullyQualifiedName(config: Config) :
    Rule(config, "Unnecessary fully qualified names make code harder to read. Use imports instead."),
    RequiresAnalysisApi {

    @Suppress("ReturnCount")
    override fun visitUserType(type: KtUserType) {
        super.visitUserType(type)

        if (isInImportOrPackage(type)) return
        if (isInStringLiteral(type)) return

        // Skip if this type is part of a larger qualified type to avoid duplicate reporting.
        // For example, in java.util.Map.Entry, we have two KtUserType nodes:
        // 1. java.util.Map (as the qualifier)
        // 2. java.util.Map.Entry (the full type)
        // We only want to report the full type, not the partial qualifier
        val parent = type.parent
        if (parent is KtUserType && parent.qualifier == type) {
            return
        }

        val typeText = type.text
        if (!typeText.contains(".")) return

        analyze(type) {
            val resolvedSymbol = type.referenceExpression?.mainReference?.resolveToSymbol() ?: return
            val packageFqName = resolvedSymbol.packageFqName() ?: return
            if (!typeText.startsWith(packageFqName)) return
            if (hasNameCollision(type, resolvedSymbol)) return
        }

        val qualifiedName = typeText.substringBefore('<')
        report(
            Finding(
                Entity.from(type),
                "Fully qualified class name '$qualifiedName' can be replaced with an import.",
            ),
        )
    }

    @Suppress("ReturnCount")
    override fun visitClassLiteralExpression(expression: KtClassLiteralExpression) {
        super.visitClassLiteralExpression(expression)

        if (isInImportOrPackage(expression)) return
        if (isInStringLiteral(expression)) return

        val receiverExpression = expression.receiverExpression ?: return
        val receiverText = receiverExpression.text
        if (!receiverText.contains(".")) return

        analyze(receiverExpression) {
            val resolvedSymbol = receiverExpression.expressionType?.symbol ?: return
            val packageFqName = resolvedSymbol.packageFqName() ?: return
            if (!receiverText.startsWith("$packageFqName.")) return
            if (hasNameCollision(expression, resolvedSymbol)) return
        }

        report(
            Finding(
                Entity.from(receiverExpression),
                "Fully qualified class reference '$receiverText' can be replaced with an import.",
            ),
        )
    }

    @Suppress("ReturnCount", "CyclomaticComplexMethod")
    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        if (isInImportOrPackage(expression)) return
        if (isInStringLiteral(expression)) return

        // Skip if this expression is part of a larger dot-qualified expression to avoid duplicates
        val parent = expression.parent
        if (parent is KtDotQualifiedExpression && parent.receiverExpression == expression) {
            return
        }

        if (!expression.text.contains(".")) return
        val receiverText = expression.receiverExpression.text
        val selectorText = expression.getPossiblyQualifiedCallExpression()?.calleeExpression?.text
            ?: expression.selectorExpression?.text
            ?: return

        analyze(expression) {
            val resolvedCall = expression.resolveToCall()?.successfulCallOrNull<KaCallableMemberCall<*, *>>()
            val symbol = resolvedCall?.partiallyAppliedSymbol?.symbol ?: return
            val packageFqName = symbol.packageFqName() ?: return
            if (!receiverText.startsWith(packageFqName)) return

            // If the leftmost part of the receiver resolves to a variable/property, this is a method call on an
            // instance (e.g. `val kotlin = Foo(); kotlin.method()`), not a package-qualified reference
            if (isReceiverLocalVariableOrProperty(expression.receiverExpression)) return

            val classId = symbol.callableId?.classId
            val symbolToCheck = classId?.let { findClass(it) } ?: symbol
            if (hasNameCollision(expression, symbolToCheck)) return

            val finding = if (classId != null) {
                Finding(
                    Entity.from(expression.receiverExpression),
                    "Fully qualified class reference '$receiverText' can be replaced with an import.",
                )
            } else {
                val type = if (symbol is KaConstructorSymbol) "class reference" else "function call"
                Finding(
                    Entity.from(expression),
                    "Fully qualified $type '$receiverText.$selectorText' can be replaced with an import.",
                )
            }
            report(finding)
        }
    }

    private fun KaSession.isReceiverLocalVariableOrProperty(receiver: KtExpression): Boolean {
        val leftmost = leftmostReference(receiver) ?: return false
        return leftmost.mainReference.resolveToSymbol() is KaVariableSymbol
    }

    private fun leftmostReference(expression: KtExpression): KtNameReferenceExpression? =
        when (expression) {
            is KtNameReferenceExpression -> expression
            is KtDotQualifiedExpression -> leftmostReference(expression.receiverExpression)
            else -> null
        }

    private fun isInImportOrPackage(element: KtElement): Boolean =
        element.getParentOfType<KtImportDirective>(strict = false) != null ||
            element.getParentOfType<KtPackageDirective>(strict = false) != null

    private fun isInStringLiteral(element: KtElement): Boolean =
        element.getParentOfType<KtStringTemplateExpression>(strict = false) != null

    context(session: KaSession)
    private fun hasNameCollision(element: KtElement, resolvedSymbol: KaSymbol): Boolean {
        val simpleName = when (resolvedSymbol) {
            is KaClassSymbol -> resolvedSymbol.classId?.shortClassName
            is KaCallableSymbol -> resolvedSymbol.callableId?.callableName
            else -> null
        } ?: return false

        if (resolvedSymbol !is KaCallableSymbol && isShadowedByTypeParameter(element, simpleName)) return true

        return findLocalSymbols(element, resolvedSymbol, simpleName).any { it != resolvedSymbol }
    }

    context(session: KaSession)
    private fun findLocalSymbols(element: KtElement, resolvedSymbol: KaSymbol, name: Name): Sequence<KaSymbol> {
        val scope = with(session) {
            element.containingKtFile.scopeContext(element).compositeScope { it !is KaScopeKind.ImportingScope }
        }
        return if (resolvedSymbol is KaCallableSymbol) scope.callables(name) else scope.classifiers(name)
    }

    private fun isShadowedByTypeParameter(element: KtElement, name: Name): Boolean =
        element.parents
            .filterIsInstance<KtTypeParameterListOwner>()
            .flatMap { it.typeParameters }
            .any { it.nameAsName == name }

    private fun KaSymbol.packageFqName(): String? =
        when (this) {
            is KaClassSymbol -> classId?.packageFqName
            is KaConstructorSymbol -> containingClassId?.packageFqName
            is KaCallableSymbol -> callableId?.packageName
            else -> null
        }?.asString()?.takeIf { it.isNotBlank() }
}

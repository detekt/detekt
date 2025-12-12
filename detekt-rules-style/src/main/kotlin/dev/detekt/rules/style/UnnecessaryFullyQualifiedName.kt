package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * This rule reports unnecessary fully qualified class names and function calls.
 * The fully qualified names can be replaced with imports to make the code more readable.
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
class UnnecessaryFullyQualifiedName(config: Config) : Rule(
    config,
    "Unnecessary fully qualified names make code harder to read. Use imports instead."
) {

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

        if (shouldReportAsFullyQualified(typeText)) {
            val qualifiedName = typeText.substringBefore('<')
            report(
                Finding(
                    Entity.from(type),
                    "Fully qualified class name '$qualifiedName' can be replaced with an import."
                )
            )
        }
    }

    override fun visitClassLiteralExpression(expression: KtClassLiteralExpression) {
        super.visitClassLiteralExpression(expression)

        if (isInImportOrPackage(expression)) return
        if (isInStringLiteral(expression)) return

        val receiverExpression = expression.receiverExpression ?: return
        val receiverText = receiverExpression.text

        if (shouldReportAsFullyQualified(receiverText)) {
            report(
                Finding(
                    Entity.from(receiverExpression),
                    "Fully qualified class reference '$receiverText' can be replaced with an import."
                )
            )
        }
    }

    @Suppress("ReturnCount", "CyclomaticComplexMethod", "LongMethod")
    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        if (isInImportOrPackage(expression)) return
        if (isInStringLiteral(expression)) return

        // Skip if this expression is part of a larger dot-qualified expression to avoid duplicates
        val parent = expression.parent
        if (parent is KtDotQualifiedExpression && parent.receiverExpression == expression) {
            return
        }

        // Skip if this is handled by visitUserType (i.e., this is part of a type)
        if (expression.getParentOfType<KtUserType>(strict = false) != null) {
            return
        }

        checkConstructorReference(expression)?.let { finding ->
            report(finding)
            return
        }

        val receiverText = expression.receiverExpression.text
        val selectorText = expression.selectorExpression?.text ?: return

        checkQualifiedClassReference(expression, receiverText, selectorText)?.let { finding ->
            report(finding)
            return
        }

        checkConstructorOrFunctionCall(expression, receiverText, selectorText)?.let { finding ->
            report(finding)
        }
    }

    @Suppress("ReturnCount")
    private fun checkConstructorReference(expression: KtDotQualifiedExpression): Finding? {
        val fullText = expression.text
        if (!fullText.startsWith("::")) return null

        val classNamePart = fullText.substring(2)
        if (!shouldReportAsFullyQualified(classNamePart)) return null

        return Finding(
            Entity.from(expression),
            "Fully qualified constructor reference '$classNamePart' can be replaced with an import."
        )
    }

    @Suppress("ReturnCount")
    private fun checkQualifiedClassReference(
        expression: KtDotQualifiedExpression,
        receiverText: String,
        selectorText: String,
    ): Finding? {
        if (!shouldReportAsFullyQualified(receiverText)) return null

        val receiverParts = receiverText.split('.')
        val lastPart = receiverParts.lastOrNull() ?: return null
        if (lastPart.isEmpty() || !lastPart[0].isUpperCase()) return null

        return if (selectorText.contains('(')) {
            val methodName = selectorText.substringBefore('(')
            val fullName = "$receiverText.$methodName"
            Finding(
                Entity.from(expression),
                "Fully qualified static method call '$fullName' can be replaced with an import."
            )
        } else {
            Finding(
                Entity.from(expression.receiverExpression),
                "Fully qualified class reference '$receiverText' can be replaced with an import."
            )
        }
    }

    @Suppress("ReturnCount")
    private fun checkConstructorOrFunctionCall(
        expression: KtDotQualifiedExpression,
        receiverText: String,
        selectorText: String,
    ): Finding? {
        if (!selectorText.contains('(')) return null

        val functionOrClassName = selectorText.substringBefore('(')
        if (functionOrClassName.isEmpty()) return null

        if (functionOrClassName[0].isUpperCase()) {
            val fullClassName = "$receiverText.$functionOrClassName"
            if (shouldReportAsFullyQualified(fullClassName)) {
                return Finding(
                    Entity.from(expression),
                    "Fully qualified class reference '$fullClassName' can be replaced with an import."
                )
            }
        } else if (functionOrClassName[0].isLowerCase() &&
            isLikelyPackageQualifiedFunction(expression, receiverText)
        ) {
            val fullName = "$receiverText.$functionOrClassName"
            return Finding(
                Entity.from(expression),
                "Fully qualified function call '$fullName' can be replaced with an import."
            )
        }
        return null
    }

    @Suppress("ReturnCount")
    private fun shouldReportAsFullyQualified(typeText: String): Boolean {
        if (!typeText.contains('.')) return false

        val baseType = typeText.substringBefore('<')
        val parts = baseType.split('.')
        if (parts.size < 2) return false

        val validParts = parts.all { part ->
            part.isNotEmpty() &&
                part.all { char -> char.isLetterOrDigit() || char == '_' } &&
                part[0].isLetter()
        }
        if (!validParts) return false

        val hasPackagePart = parts.any { it.isNotEmpty() && it[0].isLowerCase() }
        val hasClassPart = parts.any { it.isNotEmpty() && it[0].isUpperCase() }

        return hasPackagePart && hasClassPart && !parts.all { it.isNotEmpty() && it[0].isUpperCase() }
    }

    private fun isInImportOrPackage(element: KtElement): Boolean =
        element.getParentOfType<KtImportDirective>(strict = false) != null ||
            element.getParentOfType<KtPackageDirective>(strict = false) != null

    private fun isInStringLiteral(element: KtElement): Boolean =
        element.getParentOfType<KtStringTemplateExpression>(strict = false) != null

    @Suppress("ReturnCount")
    private fun isLikelyPackageQualifiedFunction(
        expression: KtDotQualifiedExpression,
        receiverText: String,
    ): Boolean {
        if (!receiverText.contains('.')) return false

        val parts = receiverText.split('.')
        if (parts.size < 2) return false

        var currentReceiver: KtExpression = expression.receiverExpression

        while (currentReceiver is KtDotQualifiedExpression) {
            currentReceiver = currentReceiver.receiverExpression
        }

        if (currentReceiver is KtNameReferenceExpression) {
            val firstIdentifier = currentReceiver.getReferencedName()
            return firstIdentifier in KNOWN_PACKAGES
        }

        return false
    }

    companion object {
        private val KNOWN_PACKAGES = setOf("kotlin", "java", "javax", "org", "com", "io", "net")
    }
}

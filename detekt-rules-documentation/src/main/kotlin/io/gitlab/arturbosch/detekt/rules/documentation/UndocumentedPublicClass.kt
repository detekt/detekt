package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.config
import io.gitlab.arturbosch.detekt.rules.isPublicInherited
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * This rule reports public classes, objects and interfaces which do not have the required documentation.
 * Enable this rule if the codebase should have documentation on every public class, interface and object.
 *
 * By default this rule also searches for nested and inner classes and objects. This default behavior can be changed
 * with the configuration options of this rule.
 */
class UndocumentedPublicClass(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Public classes, interfaces and objects require documentation.",
        Debt.TWENTY_MINS
    )

    @Configuration("if nested classes should be searched")
    private val searchInNestedClass: Boolean by config(true)

    @Configuration("if inner classes should be searched")
    private val searchInInnerClass: Boolean by config(true)

    @Configuration("if inner objects should be searched")
    private val searchInInnerObject: Boolean by config(true)

    @Configuration("if inner interfaces should be searched")
    private val searchInInnerInterface: Boolean by config(true)

    override fun visitClass(klass: KtClass) {
        if (requiresDocumentation(klass)) {
            reportIfUndocumented(klass)
        }

        super.visitClass(klass)
    }

    private fun requiresDocumentation(
        klass: KtClass
    ) = klass.isTopLevel() || klass.isInnerClass() || klass.isNestedClass() || klass.isInnerInterface()

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        if (declaration.isCompanionWithoutName() || declaration.isLocal || !searchInInnerObject) {
            return
        }

        reportIfUndocumented(declaration)
        super.visitObjectDeclaration(declaration)
    }

    private fun reportIfUndocumented(element: KtClassOrObject) {
        if (isPublicAndPublicInherited(element) &&
            element.notEnumEntry() &&
            element.docComment == null
        ) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(element),
                    "${element.nameAsSafeName} is missing required documentation."
                )
            )
        }
    }

    private fun isPublicAndPublicInherited(element: KtClassOrObject) =
        element.isPublicInherited() && element.isPublicNotOverridden()

    private fun KtObjectDeclaration.isCompanionWithoutName() =
        isCompanion() && nameAsSafeName.asString() == "Companion"

    private fun KtClass.isNestedClass() = !isInterface() && !isTopLevel() && !isInner() && searchInNestedClass

    private fun KtClass.isInnerClass() = !isInterface() && isInner() && searchInInnerClass

    private fun KtClass.isInnerInterface() = !isTopLevel() && isInterface() && searchInInnerInterface

    private fun KtClassOrObject.notEnumEntry() = this !is KtEnumEntry
}

package dev.detekt.rules.comments

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isPublicNotOverridden
import dev.detekt.rules.comments.internal.isPublicInherited
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * This rule reports public classes, objects and interfaces which do not have the required documentation.
 * Enable this rule if the codebase should have documentation on every public class, interface and object.
 *
 * By default, this rule also searches for nested and inner classes and objects. This default behavior can be changed
 * with the configuration options of this rule.
 */
class UndocumentedPublicClass(config: Config) :
    Rule(config, "Public classes, interfaces and objects require documentation.") {

    @Configuration("if nested classes should be searched")
    private val searchInNestedClass: Boolean by config(true)

    @Configuration("if inner classes should be searched")
    private val searchInInnerClass: Boolean by config(true)

    @Configuration("if inner objects should be searched")
    private val searchInInnerObject: Boolean by config(true)

    @Configuration("if inner interfaces should be searched")
    private val searchInInnerInterface: Boolean by config(true)

    @Configuration("if protected classes should be searched")
    private val searchInProtectedClass: Boolean by config(false)

    @Configuration("whether default companion objects should be exempted")
    private val ignoreDefaultCompanionObject: Boolean by config(false)

    override fun visitClass(klass: KtClass) {
        if (requiresDocumentation(klass)) {
            reportIfUndocumented(klass)
        }

        super.visitClass(klass)
    }

    private fun requiresDocumentation(klass: KtClass) =
        klass.isTopLevel() || klass.isInnerClass() || klass.isNestedClass() || klass.isInnerInterface()

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        val isNonPublicCompanionWithoutNameOrDisabled = declaration.isDefaultCompanionObject() &&
            (!declaration.isPublic || ignoreDefaultCompanionObject)

        if (
            isNonPublicCompanionWithoutNameOrDisabled ||
            declaration.isLocal ||
            !searchInInnerObject
        ) {
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
                Finding(
                    Entity.atName(element),
                    "${element.nameAsSafeName} is missing required documentation."
                )
            )
        }
    }

    private fun isPublicAndPublicInherited(element: KtClassOrObject) =
        element.isPublicInherited(searchInProtectedClass) &&
            element.isPublicNotOverridden(
                searchInProtectedClass
            )

    private fun KtObjectDeclaration.isDefaultCompanionObject() =
        isCompanion() &&
            nameAsSafeName.asString() == "Companion" &&
            // For companions _named_ `Companion`, we treat this as a "non-default" companion object. It simplifies
            // the expected logic and narrows an edge-case.
            nameIdentifier?.text != "Companion"

    private fun KtClass.isNestedClass() = !isInterface() && !isTopLevel() && !isInner() && searchInNestedClass

    private fun KtClass.isInnerClass() = !isInterface() && isInner() && searchInInnerClass

    private fun KtClass.isInnerInterface() = !isTopLevel() && isInterface() && searchInInnerInterface

    private fun KtClassOrObject.notEnumEntry() = this !is KtEnumEntry
}

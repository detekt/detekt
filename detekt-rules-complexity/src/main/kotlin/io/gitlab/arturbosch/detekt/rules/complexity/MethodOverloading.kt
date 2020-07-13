package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.isExtensionDeclaration

/**
 * This rule reports methods which are overloaded often.
 * Method overloading tightly couples these methods together which might make the code harder to understand.
 *
 * Refactor these methods and try to use optional parameters instead to prevent some of the overloading.
 *
 * @configuration threshold - number of overloads which will trigger the rule (default: `6`)
 */
class MethodOverloading(
    config: Config = Config.empty,
    threshold: Int = DEFAULT_THRESHOLD_OVERLOAD_COUNT
) : ThresholdRule(config, threshold) {

    override val issue = Issue("MethodOverloading", Severity.Maintainability,
            "Methods which are overloaded often might be harder to maintain. " +
                    "Furthermore, these methods are tightly coupled. " +
                    "Refactor these methods and try to use optional parameters.",
            Debt.TWENTY_MINS)

    override fun visitKtFile(file: KtFile) {
        val visitor = OverloadedMethodVisitor()
        file.getChildrenOfType<KtNamedFunction>().forEach { visitor.visitMethod(it) }
        visitor.reportIfThresholdExceeded(Entity.atPackageOrFirstDecl(file))
        super.visitKtFile(file)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        val visitor = OverloadedMethodVisitor()
        classOrObject.accept(visitor)
        visitor.reportIfThresholdExceeded(Entity.atName(classOrObject))
        super.visitClassOrObject(classOrObject)
    }

    internal inner class OverloadedMethodVisitor : DetektVisitor() {

        private var methods = HashMap<String, Int>()

        fun reportIfThresholdExceeded(entity: Entity) {
            for ((name, value) in methods.filterValues { it >= threshold }) {
                report(
                    ThresholdedCodeSmell(
                        issue,
                        entity,
                        Metric("OVERLOAD SIZE: ", value, threshold),
                        message = "The method '$name' is overloaded $value times."
                    )
                )
            }
        }

        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            val body = classOrObject.body ?: return
            body.functions.forEach { visitMethod(it) }
            body.enumEntries.forEach { enumEntry ->
                enumEntry.body?.functions?.forEach { visitMethod(it) }
            }
        }

        fun visitMethod(function: KtNamedFunction) {
            var name = function.name
            if (name == null || function.isOverriddenInsideEnumEntry()) {
                return
            }
            val receiver = function.receiverTypeReference
            if (function.isExtensionDeclaration() && receiver != null) {
                name = receiver.text + '.' + name
            }
            methods[name] = methods.getOrDefault(name, 0) + 1
        }

        private fun KtNamedFunction.isOverriddenInsideEnumEntry() = containingClass() is KtEnumEntry && isOverride()
    }

    companion object {
        const val DEFAULT_THRESHOLD_OVERLOAD_COUNT = 6
    }
}

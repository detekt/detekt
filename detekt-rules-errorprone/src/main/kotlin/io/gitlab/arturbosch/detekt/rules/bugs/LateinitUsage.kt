package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isLateinit
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * Reports usages of the lateinit modifier.
 *
 * Using lateinit for property initialization can be error-prone and the actual initialization is not
 * guaranteed. Try using constructor injection or delegation to initialize properties.
 *
 * <noncompliant>
 * class Foo {
 *     private lateinit var i1: Int
 *     lateinit var i2: Int
 * }
 * </noncompliant>
 */
class LateinitUsage(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "Usage of `lateinit` detected. Using `lateinit` for property initialization " +
            "is error prone, try using constructor injection or delegation.",
        Debt.TWENTY_MINS
    )

    @Configuration("Allows you to disable the rule for a list of classes")
    private val ignoreOnClassesPattern: Regex by config("", String::toRegex)

    private val properties = mutableListOf<KtProperty>()

    override fun visitProperty(property: KtProperty) {
        if (property.isLateinit()) {
            properties.add(property)
        }
    }

    override fun visit(root: KtFile) {
        properties.clear()

        super.visit(root)

        properties.filterNot { it.containingClass()?.name?.matches(ignoreOnClassesPattern) == true }
            .forEach {
                report(CodeSmell(issue, Entity.from(it), "Usages of lateinit should be avoided."))
            }
    }
}

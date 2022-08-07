@file:Suppress("ForbiddenComment")

package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperclassesWithoutAny
import kotlin.reflect.KClass

/**
 * If a rule uses `bindingContext` should be annotated with `@RequiresTypeResolution`. And if it doesn't it shouldn't
 * be annotated with it.
 */
@ActiveByDefault("1.22.0")
@RequiresTypeResolution
class RequiresTypeResolution(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "`@RequiresTypeResolution` should be used if and only if `bindingContext` is used.",
        Debt.FIVE_MINS
    )

    private val klasses: MutableList<KtClass> = mutableListOf()
    private var usesBindingContext: Boolean = false

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        klasses.forEach { klass ->
            val isAnnotatedWithRequiresTypeResolution = klass.isAnnotatedWith(RequiresTypeResolution::class)
            if (usesBindingContext && !isAnnotatedWithRequiresTypeResolution) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(klass),
                        "`${klass.name}` uses `bindingContext` but is not annotated with `@RequiresTypeResolution`"
                    )
                )
            } else if (!usesBindingContext && isAnnotatedWithRequiresTypeResolution) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(klass),
                        "`${klass.name}` is annotated with `@RequiresTypeResolution` but doesn't use `bindingContext`"
                    )
                )
            }
        }
    }

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        if (klass.extendsFrom(BaseRule::class)) {
            klasses.add(klass)
        }
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        usesBindingContext = usesBindingContext ||
            (expression is KtNameReferenceExpression && expression.text == "bindingContext")
    }
}

context(BaseRule) private inline fun <reified T : Any> KtClass.extendsFrom(kClass: KClass<T>): Boolean {
    return bindingContext[BindingContext.CLASS, this]
        ?.getAllSuperclassesWithoutAny()
        .orEmpty()
        .any { it.fqNameOrNull()?.toString() == checkNotNull(kClass.qualifiedName) }
}

context(BaseRule) private inline fun <reified T : Any> KtClass.isAnnotatedWith(kClass: KClass<T>): Boolean {
    return annotationEntries
        .asSequence()
        .mapNotNull { it.typeReference }
        .mapNotNull { bindingContext[BindingContext.TYPE, it] }
        .any { it.fqNameOrNull()?.toString() == checkNotNull(kClass.qualifiedName) }
}

package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
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
 * If a rule uses the property [Rule.bindingContext] should be annotated with `@RequiresFullAnalysis`.
 * And if the rule doesn't use that property it shouldn't be annotated with it.
 */
@ActiveByDefault("1.22.0")
@RequiresFullAnalysis
class ViolatesTypeResolutionRequirements(config: Config) : Rule(
    config,
    "`@RequiresFullAnalysis` should be used if and only if the property `bindingContext` is used."
) {

    private val klasses: MutableList<KtClass> = mutableListOf()
    private var usesBindingContext: Boolean = false

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        klasses.forEach { klass ->
            val isAnnotatedWithRequiresFullAnalysis = klass.isAnnotatedWith(RequiresFullAnalysis::class)
            if (usesBindingContext && !isAnnotatedWithRequiresFullAnalysis) {
                report(
                    CodeSmell(
                        Entity.atName(klass),
                        "`${klass.name}` uses `bindingContext` but is not annotated with `@RequiresFullAnalysis`"
                    )
                )
            } else if (!usesBindingContext && isAnnotatedWithRequiresFullAnalysis) {
                report(
                    CodeSmell(
                        Entity.atName(klass),
                        "`${klass.name}` is annotated with `@RequiresFullAnalysis` but doesn't use `bindingContext`"
                    )
                )
            }
        }
        klasses.clear()
        usesBindingContext = false
    }

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        if (klass.extendsFrom(Rule::class)) {
            klasses.add(klass)
        }
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        usesBindingContext = usesBindingContext ||
            (expression is KtNameReferenceExpression && expression.text == "bindingContext")
    }

    private inline fun <reified T : Any> KtClass.extendsFrom(kClass: KClass<T>): Boolean =
        bindingContext[BindingContext.CLASS, this]
            ?.getAllSuperclassesWithoutAny()
            .orEmpty()
            .any { it.fqNameOrNull()?.toString() == checkNotNull(kClass.qualifiedName) }

    private inline fun <reified T : Any> KtClass.isAnnotatedWith(kClass: KClass<T>): Boolean =
        annotationEntries
            .asSequence()
            .mapNotNull { it.typeReference }
            .mapNotNull { bindingContext[BindingContext.TYPE, it] }
            .any { it.fqNameOrNull()?.toString() == checkNotNull(kClass.qualifiedName) }
}

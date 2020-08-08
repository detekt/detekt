package io.github.detekt.custom

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

/**
 * Expensive setup code can slow down test discovery.
 * Make sure to use memoization when declaring non trivial types.
 *
 * @configuration allowedTypes - full qualified type
 * (default: `kotlin.String, kotlin.Nothing, kotlin.Int, kotlin.Double, java.io.File, java.nio.file.Path`)
 * @configuration scopingFunctions - names of functions used to declare a test group (default: `describe, context`)
 *
 * <noncompliant>
 * class MyTest : Spek({
 *     describe("...") {
 *         val ast = expensiveParse("code")
 *
 *         test("...") {
 *             assertThat(ast)...
 *         }
 *     }
 * })
 * </noncompliant>
 *
 * <compliant>
 * class MyTest : Spek({
 *     val ast by memoized { expensiveParse("code") }
 *
 *     test("...") {
 *         assertThat(ast)...
 *     }
 * })
 * </compliant>
 */
class SpekTestDiscovery(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Performance,
        """Spek tests can be quite expensive during test discovery.
            |Compared to Junit5, Spek does not only use reflection to discover the test classes but also needs
            |to instantiate and run the constructor closure to find tests.
            |Try using only simple setup code to not slow down the startup of single tests or test suites.
        """.trimMargin(),
        Debt.TEN_MINS
    )

    private val allowedTypes = valueOrDefaultCommaSeparated(
        ALLOWED_TYPES,
        listOf(
            "kotlin.Nothing",
            "kotlin.String",
            "kotlin.Int",
            "kotlin.Double",
            "java.nio.file.Path",
            "java.io.File"
        )
    ).toSet()

    private val scopingFunctions = valueOrDefaultCommaSeparated(
        SCOPING_FUNCTIONS,
        listOf("describe", "context")
    ).toSet()

    override fun visitClass(klass: KtClass) {
        bindingContext != BindingContext.EMPTY ?: return
        if (extendsSpek(klass)) {
            val lambda = getInitLambda(klass) ?: return
            inspectSpekGroup(lambda)
        }
    }

    private fun inspectSpekGroup(lambda: KtLambdaExpression) {
        lambda.bodyExpression?.statements?.forEach {
            when (it) {
                is KtProperty -> handleProperties(it)
                is KtCallExpression -> handleScopingFunctions(it)
            }
        }
    }

    private fun handleProperties(property: KtProperty) {
        if (!property.hasDelegate()) {
            val initExpr = property.initializer
            val fqType = initExpr?.getType(bindingContext)
                ?.getJetTypeFqName(false)
            if (fqType != null && fqType !in allowedTypes) {
                report(CodeSmell(
                    issue,
                    Entity.atName(property),
                    "Variable declarations which do not met the allowed types should be memoized."
                ))
            }
        }
    }

    private fun handleScopingFunctions(call: KtCallExpression) {
        val calledName = call.calleeExpression?.text
        if (calledName in scopingFunctions && call.valueArguments.isNotEmpty()) {
            val scopingLambda = call.valueArguments.last()
                .getArgumentExpression()
                as? KtLambdaExpression
                ?: return
            inspectSpekGroup(scopingLambda)
        }
    }

    private fun extendsSpek(klass: KtClass): Boolean {
        val entries = klass.superTypeListEntries
        if (entries.size == 1) {
            val entry = entries.first()
            val superType = entry.typeReference?.text
            return superType == "Spek"
        }
        return false
    }

    private fun getInitLambda(klass: KtClass): KtLambdaExpression? {
        val superType = klass.superTypeListEntries.first() as? KtSuperTypeCallEntry
        if (superType?.valueArguments?.size == 1) {
            val expr = superType.valueArguments.first().getArgumentExpression()
            return expr as? KtLambdaExpression
        }
        return null
    }

    companion object {
        const val ALLOWED_TYPES = "allowedTypes"
        const val SCOPING_FUNCTIONS = "scopingFunctions"
    }
}

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.intellij.lang.annotations.Language
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseEmptyCounterpartSpec : Spek({
    setupKotlinEnvironment()

    val rule by memoized { UseEmptyCounterpart(Config.empty) }

    describe("UseEmptyCounterpart rule") {

        val exprsWithEmptyCounterparts: Set<ExprWithEmptyCounterpart> = setOf(
            ExprWithEmptyCounterpart(
                ArgExpr(
                    "arrayOf()",
                    """val a = arrayOf<Any>()""",
                    """val a = arrayOf(0)"""
                ),
                EmptyExpr(
                    "emptyArray()",
                    """val a = emptyArray<Any>()"""
                )
            ),
            ExprWithEmptyCounterpart(
                ArgExpr(
                    "listOf()",
                    """val a = listOf<Any>()""",
                    """val a = listOf(0)"""
                ),
                EmptyExpr(
                    "emptyList()",
                    """val a = emptyList<Any>()"""
                )
            ),
            ExprWithEmptyCounterpart(
                ArgExpr(
                    "mapOf()",
                    """val a = mapOf<Any, Any>()""",
                    """val a = mapOf(0 to 0)"""
                ),
                EmptyExpr(
                    "emptyMap()",
                    """val a = emptyMap<Any, Any>()"""
                )
            ),
            ExprWithEmptyCounterpart(
                ArgExpr(
                    "sequenceOf()",
                    """val a = sequenceOf<Any>()""",
                    """val a = sequenceOf(0)"""
                ),
                EmptyExpr(
                    "emptySequence()",
                    """val a = emptySequence<Any>()"""
                )
            ),
            ExprWithEmptyCounterpart(
                ArgExpr(
                    "setOf()",
                    """val a = setOf<Any>()""",
                    """val a = setOf(0)"""
                ),
                EmptyExpr(
                    "emptySet()",
                    """val a = emptySet<Any>()"""
                )
            )
        )

        exprsWithEmptyCounterparts.forEach { (argInstantiation, emptyInstantiation) ->

            it("reports no-arg $argInstantiation") {
                val findings = rule.compileAndLint(argInstantiation.noArgExample)
                assertThat(findings).hasSize(1)
            }

            it("does not report $emptyInstantiation") {
                val findings = rule.compileAndLint(emptyInstantiation.example)
                assertThat(findings).isEmpty()
            }

            it("does not report $argInstantiation with arguments") {
                val findings = rule.compileAndLint(argInstantiation.argExample)
                assertThat(findings).isEmpty()
            }
        }
    }
})

private data class ExprWithEmptyCounterpart(
    @Language("kotlin") val argInstantiation: ArgExpr,
    @Language("kotlin") val emptyInstantiation: EmptyExpr
)

private data class EmptyExpr(private val name: String, @Language("kotlin") val example: String) {

    override fun toString() = name
}

private data class ArgExpr(
    private val name: String,
    @Language("kotlin") val noArgExample: String,
    @Language("kotlin") val argExample: String
) {

    override fun toString(): String = name
}

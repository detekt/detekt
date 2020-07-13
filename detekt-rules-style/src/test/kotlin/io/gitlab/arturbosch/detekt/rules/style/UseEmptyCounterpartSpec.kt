package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseEmptyCounterpartSpec : Spek({
    setupKotlinEnvironment()

    val rule by memoized { UseEmptyCounterpart(Config.empty) }

    describe("UseEmptyCounterpart rule") {

        it("reports no-arg instantiation") {
            val code = """
                val array = arrayOf<Any>()
                val list = listOf<Any>()
                val map = mapOf<Any, Any>()
                val sequence = sequenceOf<Any>()
                val set = setOf<Any>()
            """
            assertThat(rule.compileAndLint(code)).hasSize(5)
        }

        it("does not report empty instantiation") {
            val code = """
                val array = emptyArray<Any>()
                val list = emptyList<Any>()
                val map = emptyMap<Any, Any>()
                val sequence = emptySequence<Any>()
                val set = emptySet<Any>()
            """
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        it("does not report instantiation with arguments") {
            val code = """
                val array = arrayOf(0)
                val list = listOf(0)
                val map = mapOf(0 to 0)
                val sequence = sequenceOf(0)
                val set = setOf(0)
            """
            assertThat(rule.compileAndLint(code)).isEmpty()
        }
    }
})

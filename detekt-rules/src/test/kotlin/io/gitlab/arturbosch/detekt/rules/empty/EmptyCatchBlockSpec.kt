package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EmptyCatchBlockSpec : Spek({

    val subject by memoized { EmptyCatchBlock(Config.empty) }

    describe("EmptyCatchBlock rule") {

        it("reports the empty catch body") {
            val code = """
                fun f() {
                    try { val answer = 42 }
                    catch (ex: Exception) { }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report empty catch blocks which are ignored based on the exception name") {
            val code = """
                fun f() {
                    try { val answer = 42 }
                    catch (ignore: Exception) { }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})

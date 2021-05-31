package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private const val EXCEPTION_NAMES = "exceptionNames"

private val tooGenericExceptions = listOf(
    "Error",
    "Exception",
    "Throwable",
    "RuntimeException"
)

class TooGenericExceptionThrownSpec : Spek({

    describe("a file with many thrown exceptions") {

        tooGenericExceptions.forEach { exceptionName ->
            it("should report $exceptionName") {
                val config = TestConfig(mapOf(EXCEPTION_NAMES to "[$exceptionName]"))
                val rule = TooGenericExceptionCaught(config)

                val findings = rule.compileAndLint(tooGenericExceptionCode)

                assertThat(findings).hasSize(1)
            }
        }

        it("should not report thrown exceptions") {
            val config = TestConfig(mapOf(EXCEPTION_NAMES to "['MyException', Bar]"))
            val rule = TooGenericExceptionCaught(config)

            val findings = rule.compileAndLint(tooGenericExceptionCode)

            assertThat(findings).isEmpty()
        }
    }
})

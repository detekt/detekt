package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.regex.PatternSyntaxException

class TooGenericExceptionCaughtSpec : Spek({

    describe("a file with many caught exceptions") {

        it("should find one of each kind") {
            val rule = TooGenericExceptionCaught(Config.empty)

            val findings = rule.compileAndLint(tooGenericExceptionCode)

            assertThat(findings).hasSize(caughtExceptionDefaults.size)
        }
    }

    describe("a file with a caught exception which is ignored") {

        val code = """
            class MyTooGenericException : RuntimeException()

            fun f() {
                try {
                    throw Throwable()
                } catch (myIgnore: MyTooGenericException) {
                    throw Error()
                }
            }
        """

        it("should not report an ignored catch blocks because of its exception name") {
            val config = TestConfig(mapOf(TooGenericExceptionCaught.ALLOWED_EXCEPTION_NAME_REGEX to "myIgnore"))
            val rule = TooGenericExceptionCaught(config)

            val findings = rule.compileAndLint(code)

            assertThat(findings).isEmpty()
        }

        it("should not report an ignored catch blocks because of its exception type") {
            val config = TestConfig(mapOf(TooGenericExceptionCaught.CAUGHT_EXCEPTIONS_PROPERTY to "[MyException]"))
            val rule = TooGenericExceptionCaught(config)

            val findings = rule.compileAndLint(code)

            assertThat(findings).isEmpty()
        }

        it("should not fail when disabled with invalid regex on allowed exception names") {
            val configRules = mapOf(
                    "active" to "false",
                    TooGenericExceptionCaught.ALLOWED_EXCEPTION_NAME_REGEX to "*MyException"
            )
            val config = TestConfig(configRules)
            val rule = TooGenericExceptionCaught(config)
            val findings = rule.compileAndLint(tooGenericExceptionCode)

            assertThat(findings).isEmpty()
        }

        it("should fail with invalid regex on allowed exception names") {
            val config = TestConfig(mapOf(TooGenericExceptionCaught.ALLOWED_EXCEPTION_NAME_REGEX to "*Foo"))
            val rule = TooGenericExceptionCaught(config)
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                rule.compileAndLint(tooGenericExceptionCode)
            }
        }
    }
})

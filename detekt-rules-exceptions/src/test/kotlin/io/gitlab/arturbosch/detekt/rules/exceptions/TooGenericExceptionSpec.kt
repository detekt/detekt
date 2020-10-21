package io.gitlab.arturbosch.detekt.rules.exceptions

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TooGenericExceptionSpec : Spek({

    describe("TooGenericException rule") {

        it("should not report any as all catch exception rules are deactivated") {
            val config = yamlConfig("deactivated-exceptions.yml")
            val ruleSet = ExceptionsProvider().instance(config)
            val file = compileContentForTest(tooGenericExceptionCode)

            @Suppress("DEPRECATION")
            val findings = ruleSet.accept(file)

            assertThat(findings).isEmpty()
        }
    }
})

const val tooGenericExceptionCode = """
        fun main() {
        try {
            throw Throwable()
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw Error()
        } catch (e: Error) {
            throw Exception()
        } catch (e: Exception) {
        } catch (e: IllegalMonitorStateException) {
        } catch (e: IndexOutOfBoundsException) {
            throw RuntimeException()
        } catch (e: Throwable) {
        } catch (e: RuntimeException) {
            throw NullPointerException()
        } catch (e: NullPointerException) {

        }
    }"""

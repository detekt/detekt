package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.internal.YamlConfig
import io.gitlab.arturbosch.detekt.core.rules.createRuleSet
import io.gitlab.arturbosch.detekt.core.rules.visitFile
import io.gitlab.arturbosch.detekt.rules.providers.ExceptionsProvider
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TooGenericExceptionSpec : Spek({

    describe("TooGenericException rule") {

        it("should not report any as all catch exception rules are deactivated") {
            val config = YamlConfig.loadResource(resource("deactivated-exceptions.yml").toURL())
            val ruleSet = ExceptionsProvider().createRuleSet(config)
            val file = compileContentForTest(tooGenericExceptionCode)

            val findings = ruleSet.visitFile(file)

            assertThat(findings).isEmpty()
        }
    }
})

const val tooGenericExceptionCode =
    """
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

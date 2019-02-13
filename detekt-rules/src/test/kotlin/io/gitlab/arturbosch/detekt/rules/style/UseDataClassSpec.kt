package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Ivan Balaksha
 */
class UseDataClassSpec : Spek({

    val subject by memoized { UseDataClass(Config.empty) }

    describe("UseDataClass rule") {

        it("reports potential data classes") {
            assertThat(subject.lint(Case.UseDataClassPositive.path())).hasSize(5)
        }

        it("does not report invalid data class candidates") {
            assertThat(subject.lint(Case.UseDataClassNegative.path())).isEmpty()
        }

        it("does not report inline classes") {
            assertThat(subject.lint("inline class A(val x: Int)")).isEmpty()
        }

        it("does not report a potential data class when class has an annotation which is ignored") {
            val code = """
				import kotlin.SinceKotlin

				@SinceKotlin("1.0.0")
				class AnnotatedClass(val i: Int) {}
				"""
            val config = TestConfig(mapOf(UseDataClass.EXCLUDE_ANNOTATED_CLASSES to "kotlin.*"))
            assertThat(UseDataClass(config).lint(code)).isEmpty()
        }
    }
})

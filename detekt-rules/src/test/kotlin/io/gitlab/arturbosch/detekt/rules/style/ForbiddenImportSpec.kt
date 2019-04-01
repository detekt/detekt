package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForbiddenImportSpec : Spek({
    describe("ForbiddenImport rule") {
        val code = """
			package foo

			import kotlin.jvm.JvmField
			import kotlin.SinceKotlin

			import com.example.R.string
			import net.example.R.dimen
		"""

        it("should report nothing by default") {
            val findings = ForbiddenImport().lint(code)
            assertThat(findings).hasSize(0)
        }

        it("should report nothing when imports are blank") {
            val findings = ForbiddenImport(TestConfig(mapOf(ForbiddenImport.IMPORTS to "  "))).lint(code)
            assertThat(findings).hasSize(0)
        }

        it("should report nothing when imports do not match") {
            val findings = ForbiddenImport(TestConfig(mapOf(ForbiddenImport.IMPORTS to "org.*"))).lint(code)
            assertThat(findings).hasSize(0)
        }

        it("should report kotlin.* when imports are kotlin.*") {
            val findings = ForbiddenImport(TestConfig(mapOf(ForbiddenImport.IMPORTS to "kotlin.*"))).lint(code)
            assertThat(findings).hasSize(2)
        }

        it("should report kotlin.SinceKotlin when specified via fully qualified name") {
            val findings = ForbiddenImport(TestConfig(mapOf(ForbiddenImport.IMPORTS to "kotlin.SinceKotlin"))).lint(code)
            assertThat(findings).hasSize(1)
        }

        it("should report kotlin.SinceKotlin and kotlin.jvm.JvmField when specified via fully qualified names") {
            val findings = ForbiddenImport(TestConfig(mapOf(ForbiddenImport.IMPORTS to "kotlin.SinceKotlin,kotlin.jvm.JvmField"))).lint(code)
            assertThat(findings).hasSize(2)
        }

        it("should report kotlin.SinceKotlin when specified via kotlin.Since*") {
            val findings = ForbiddenImport(TestConfig(mapOf(ForbiddenImport.IMPORTS to "kotlin.Since*"))).lint(code)
            assertThat(findings).hasSize(1)
        }

        it("should report both com.example.R.string and net.example.R.dimen") {
            val findings = ForbiddenImport(TestConfig(mapOf(ForbiddenImport.IMPORTS to "*.R.*"))).lint(code)
            assertThat(findings).hasSize(2)
        }
    }
})

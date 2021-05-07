package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RedundantExplicitImportSpec : Spek({
    describe("RedundantExplicitImport rule") {
        it("should report nothing if no default imports are used") {
            val code = """
               package test
                
               import com.test.test
               import java.util.list
            """
            val findings = RedundantExplicitImport().lint(code)
            assertThat(findings).isEmpty()
        }

        it("should report default imports") {
            val code = """
                import kotlin.UInt
                import kotlin.annotation.Retention
                import kotlin.collections.List
                import kotlin.io.DEFAULT_BUFFER_SIDE
                import kotlin.ranges.CharRange
                import kotlin.sequences.SequenceScope
                import kotlin.text.Regex
            """.trimIndent()
            val findings = RedundantExplicitImport().lint(code)
            assertThat(findings).hasSize(code.lines().size)
        }

        it("should report default jvm imports") {
            val code = """
                import java.lang.Class
                import kotlin.jvm.JvmDefault
            """.trimIndent()
            val findings = RedundantExplicitImport().lint(code)
            assertThat(findings).hasSize(code.lines().size)
        }

        it("should report wildcard default imports") {
            val code = """
                import kotlin.*
                import java.lang.*
            """.trimIndent()
            val findings = RedundantExplicitImport().lint(code)
            assertThat(findings).hasSize(code.lines().size)
        }

        it("will report shadowed java classes") {
            val code = """
               import java.lang.Exception
            """.trimIndent()
            val findings = RedundantExplicitImport().lint(code)
            assertThat(findings).hasSize(code.lines().size)
        }

        it("should report nothing if imports aliased") {
            val code = """
                import java.lang.Exception as JavaException
                import kotlin.Exception as KotlinException
            """.trimIndent()
            val findings = RedundantExplicitImport().lint(code)
            assertThat(findings).isEmpty()
        }
    }
})

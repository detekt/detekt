package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

class NoTabsSpec {
    private val subject = NoTabs(Config.empty)

    @Test
    fun `should flag a line that contains a tab`() {
        val code = """
            class NoTabsPositive {
            ${TAB}fun methodOk() { // reports 3
            ${TAB}${TAB}println("A message")
            
            $TAB}
            
              val str = "${'$'}{${TAB}${TAB}methodOk()}" // reports 1
              val multiStr = $TQ${'$'}{${TAB}methodOk()}$TQ // reports 1
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).hasSize(5)
    }

    @Test
    fun `should not flag a line that does not contain a tab`() {
        val code = """
            class NoTabsNegative {
            
                fun methodOk() {
                    println("A message")
                }
            
                val str = "A \t tab	"
                val multiStr = ""${'"'}A \t tab	""${'"'}
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }
}

private const val TQ = "\"\"\""
private const val TAB = "\t"

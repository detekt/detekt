package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Test

class NoTabsSpec {
    private val subject = NoTabs()

    @Test
    fun `should flag a line that contains a tab`() {
        val file = compileContentForTest(
            """
                class NoTabsPositive {
                ${TAB}fun methodOk() { // reports 3
                ${TAB}${TAB}println("A message")
                
                $TAB}
                
                  val str = "${'$'}{${TAB}${TAB}methodOk()}" // reports 1
                  val multiStr = $TQ${'$'}{${TAB}methodOk()}$TQ // reports 1
                }
            """.trimIndent()
        )
        subject.visitFile(file)
        assertThat(subject.findings).hasSize(5)
    }

    @Test
    fun `should not flag a line that does not contain a tab`() {
        val file = compileContentForTest(
            """
                class NoTabsNegative {
                
                    fun methodOk() {
                        println("A message")
                    }
                
                    val str = "A \t tab	"
                    val multiStr = ""${'"'}A \t tab	""${'"'}
                }
            """.trimIndent()
        )
        subject.visitFile(file)
        assertThat(subject.findings).isEmpty()
    }
}

private const val TQ = "\"\"\""
private const val TAB = "\t"

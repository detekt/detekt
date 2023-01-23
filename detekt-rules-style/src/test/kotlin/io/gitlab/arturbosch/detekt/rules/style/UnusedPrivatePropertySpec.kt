package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val ALLOWED_NAMES_PATTERN = "allowedNames"

@KotlinCoreEnvironmentTest
class UnusedPrivatePropertySpec(val env: KotlinCoreEnvironment) {
    val subject = UnusedPrivateProperty()

    val regexTestingCode = """
                class Test {
                    private val used = "This is used"
                    private val unused = "This is not used"

                    fun use() {
                        println(used)
                    }
                }
    """.trimIndent()

    @Nested
    inner class `classes with properties` {

        @Test
        fun `reports an unused member`() {
            val code = """
                class Test {
                    private val unused = "This is not used"

                    fun use() {
                        println("This is not using a property")
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report unused public members`() {
            val code = """
                class Test {
                    val unused = "This is not used"

                    fun use() {
                        println("This is not using a property")
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used members`() {
            val code = """
                class Test {
                    private val used = "This is used"

                    fun use() {
                        println(used)
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used members but reports unused members`() {
            val code = """
                class Test {
                    private val used = "This is used"
                    private val unused = "This is not used"

                    fun use() {
                        println(used)
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not fail when disabled with invalid regex`() {
            val configRules = mapOf(
                "active" to "false",
                ALLOWED_NAMES_PATTERN to "*foo",
            )
            val config = TestConfig(configRules)
            assertThat(UnusedPrivateMember(config).lint(regexTestingCode)).isEmpty()
        }

        @Test
        fun `does fail when enabled with invalid regex`() {
            val configRules = mapOf(ALLOWED_NAMES_PATTERN to "*foo")
            val config = TestConfig(configRules)
            assertThatExceptionOfType(PatternSyntaxException::class.java)
                .isThrownBy { UnusedPrivateMember(config).lint(regexTestingCode) }
        }
    }
}

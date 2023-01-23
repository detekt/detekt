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

    @Nested
    inner class `classes with properties and local properties` {

        @Test
        fun `reports multiple unused properties`() {
            val code = """
                class UnusedPrivatePropertyPositive {
                    private val unusedField = 5
                    val publicField = 2
                    private val clashingName = 4
                    private fun unusedFunction() {
                        val unusedLocal = 5
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(3)
        }

        @Test
        fun `reports an unused member`() {
            val code = """
                class Test {
                    private val unused = "This is not used"

                    fun use() {
                        val used = "This is used"
                        println(used)
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report used members`() {
            val code = """
                class Test {
                    private val used = "This is used"

                    fun use() {
                        val text = used
                        println(text)
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used members and properties`() {
            val code = """
                class C {
                    val myNumber = 5
                
                    fun publicFunction(usedParam: String) {
                        println(usedParam)
                        println(PC.THE_CONST)
                        println("Hello " ext "World" ext "!")
                        println(::doubleColonObjectReferenced)
                        println(this::doubleColonThisReferenced)
                    }
                
                    fun usesAllowedNames() {
                        for ((index, _) in mapOf(0 to 0, 1 to 1, 2 to 2)) {  // unused but allowed name
                            println(index)
                        }
                        try {
                        } catch (_: OutOfMemoryError) { // unused but allowed name
                        }
                    }
                
                    private fun doubleColonThisReferenced() {}
                
                    companion object {
                        private infix fun String.ext(other: String): String {
                            return this + other
                        }
                
                        private fun doubleColonObjectReferenced() {}
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used private classes`() {
            val code = """
                private class PC { // used private class
                    companion object {
                        internal const val THE_CONST = "" // used private const

                        object OO {
                            const val BLA = 4
                        }
                    }
                }

                internal fun libraryFunction() = run {
                    val o: Function1<Any, Any> = object : Function1<Any, Any> {
                        override fun invoke(p1: Any): Any { // unused but overridden param
                            throw UnsupportedOperationException("not implemented")
                        }
                    }
                    println(o("$\{PC.Companion.OO.BLA.toString() + ""}"))
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(0)
        }

        @Test
        fun `reports unused local properties`() {
            val code = """
                class Test {
                    private val used = "This is used"

                    fun use() {
                        val unused = used
                        println(used)
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `objects with properties` {
        @Test
        fun `reports multiple unused properties`() {
            val code = """
                object UnusedPrivateMemberPositiveObject {
                    private const val unusedObjectConst = 2
                    private val unusedField = 5
                    private val clashingName = 5
                    val useForClashingName = clashingName
                    private val unusedObjectField = 4
                
                    object Foo {
                        private val unusedNestedVal = 1
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(4)
        }

        @Test
        fun `does not report public properties`() {
            val code = """
                object O { // public
                    const val NUMBER = 5 // public
                }

                private object PO { // private, but constants may be used
                    const val TEXT = "text"
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `loop iterators` {

        @Test
        fun `should not depend on evaluation order of functions or properties`() {
            val code = """
                fun RuleSetProvider.provided() = ruleSetId in defaultRuleSetIds

                val defaultRuleSetIds = listOf("comments", "complexity", "empty-blocks",
                        "exceptions", "potential-bugs", "performance", "style")
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `doesn't report loop properties`() {
            val code = """
                class Test {
                    fun use() {
                        for (i in 0 until 10) {
                            println(i)
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports unused loop property`() {
            val code = """
                class Test {
                    fun use() {
                        for (i in 0 until 10) {
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports unused loop property in indexed array`() {
            val code = """
                class Test {
                    fun use() {
                        val array = intArrayOf(1, 2, 3)
                        for ((index, value) in array.withIndex()) {
                            println(index)
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports all unused loop properties in indexed array`() {
            val code = """
                class Test {
                    fun use() {
                        val array = intArrayOf(1, 2, 3)
                        for ((index, value) in array.withIndex()) {
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `does not report used loop properties in indexed array`() {
            val code = """
                class Test {
                    fun use() {
                        val array = intArrayOf(1, 2, 3)
                        for ((index, value) in array.withIndex()) {
                            println(index)
                            println(value)
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `properties used to initialize other properties` {

        @Test
        fun `does not report properties used by other properties`() {
            val code = """
                class Test {
                    private val used = "This is used"
                    private val text = used

                    fun use() {
                        println(text)
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report properties used by inner classes`() {
            val code = """
                class Test {
                    private val unused = "This is not used"

                    inner class Something {
                        val test = unused
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}

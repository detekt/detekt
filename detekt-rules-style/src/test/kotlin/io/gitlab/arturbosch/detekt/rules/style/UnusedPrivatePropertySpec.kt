package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val ALLOWED_NAMES_PATTERN = "allowedNames"

@KotlinCoreEnvironmentTest
class UnusedPrivatePropertySpec(val env: KotlinCoreEnvironment) {
    val subject = UnusedPrivateProperty(Config.empty)

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
        fun `does fail when enabled with invalid regex`() {
            val config = TestConfig(ALLOWED_NAMES_PATTERN to "*foo")
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

    @Nested
    inner class `top level properties` {
        @Test
        fun `reports single parameters if they are unused`() {
            val code = """
                private val usedTopLevelVal = 1
                private const val unusedTopLevelConst = 1
                private val unusedTopLevelVal = usedTopLevelVal
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `does not report used top level properties`() {
            val code = """
                val stuff = object : Iterator<String?> {
                
                    var mutatable: String? = null
                
                    private fun preCall() {
                        mutatable = "done"
                    }
                
                    override fun next(): String? {
                        preCall()
                        return mutatable
                    }
                
                    override fun hasNext(): Boolean = true
                }
                
                fun main(args: Array<String>) {
                    println(stuff.next())
                    calledFromMain()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `unused class declarations which are allowed` {

        @Test
        fun `does not report the unused private property`() {
            val code = """
                class Test {
                    private val ignored = ""
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `nested class declarations` {

        @Test
        fun `reports unused nested private property`() {
            val code = """
                class Test {
                    class Inner {
                        private val unused = 1
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report used nested private property`() {
            val code = """
                class Test {
                    class Inner {
                        private val used = 1
                        fun someFunction() = used
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `error messages` {
        @Test
        fun `are specific for local variables`() {
            val code = """
                fun foo(){ val unused = 1 }
            """.trimIndent()

            val lint = subject.lint(code)

            assertThat(lint.first().message).startsWith("Private property")
        }
    }

    @Nested
    inner class `suppress unused property warning annotations` {
        @Test
        fun `does not report annotated private constructor properties`() {
            val code = """
                class Test(@Suppress("unused") private val foo: String) {}
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports private constructor properties without annotation`() {
            val code = """
                class Test(
                    @Suppress("unused") private val foo: String,
                    private val bar: String
                ) {}
            """.trimIndent()

            val lint = subject.lint(code)

            assertThat(lint).hasSize(1)
            assertThat(lint[0].entity.signature).isEqualTo("Test.kt\$Test\$private val bar: String")
        }

        @Test
        fun `does not report private constructor properties in annotated class`() {
            val code = """
                @Suppress("unused")
                class Test(
                    private val foo: String,
                    private val bar: String
                ) {}
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private constructor properties in class with annotated outer class`() {
            val code = """
                @Suppress("unused")
                class Test(
                    private val foo: String,
                    private val bar: String
                ) {
                    class InnerTest(
                        private val baz: String
                    ) {}
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private constructor properties in annotated file`() {
            val code = """
                @file:Suppress("unused")
                
                class Test(
                    private val foo: String,
                    private val bar: String
                ) {
                    class InnerTest(
                        private val baz: String
                    ) {}
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report annotated private properties`() {
            val code = """
                class Test {
                    @Suppress("unused") private val foo: String
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports private properties without annotation`() {
            val code = """
                class Test {
                    @Suppress("unused") private val foo: String
                    private val bar: String
                }
            """.trimIndent()

            val lint = subject.lint(code)

            assertThat(lint).hasSize(1)
            assertThat(lint[0].entity.signature).isEqualTo("Test.kt\$Test\$private val bar: String")
        }

        @Test
        fun `does not report private properties in annotated class`() {
            val code = """
                @Suppress("unused")
                class Test {
                    private val foo: String
                    private val bar: String
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private properties in class with annotated outer class`() {
            val code = """
                @Suppress("unused")
                class Test {
                    private val foo: String
                    private val bar: String
                
                    class InnerTest {
                        private val baz: String
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private properties in annotated file`() {
            val code = """
                @file:Suppress("unused")
                
                class Test {
                    private val foo: String
                    private val bar: String
                
                    class InnerTest {
                        private val baz: String
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `backtick identifiers - #3825` {

        @Test
        fun `does report unused variables with keyword name`() {
            val code = """
                fun main() {
                    val `in` = "foo"
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report used variables with keyword name`() {
            val code = """
                fun main() {
                    val `in` = "fee"
                    val expected = "foo"
                    println(expected == `in`)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used variables when referenced with backticks`() {
            val code = """
                fun main() {
                    val actual = "fee"
                    val expected = "foo"
                    println(expected == `actual`)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used variables when declared with backticks`() {
            val code = """
                fun main() {
                    val `actual` = "fee"
                    val expected = "foo"
                    println(expected == actual)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `highlights declaration name - #4916` {
        @Test
        fun property() {
            val code = """
                class Test {
                    /**
                     * kdoc
                     */
                    private val foo = 1
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1).hasStartSourceLocation(5, 17)
        }
    }

    @Nested
    inner class `actual functions and classes` {

        @Test
        fun `should not report unused actual fields defined as parameters of primary constructors`() {
            val code = """
                actual class Foo actual constructor(actual val bar: String) {}
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports unused private fields defined as parameters of primary constructors`() {
            val code = """
                actual class Foo actual constructor(private val bar: String) {}
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `properties in primary constructors` {
        @Test
        fun `reports unused private property`() {
            val code = """
                class Test(private val unused: Any)
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report public property`() {
            val code = """
                class Test(val unused: Any)
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private property used in init block`() {
            val code = """
                class Test(private val used: Any) {
                    init { used.toString() }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private property used in function`() {
            val code = """
                class Test(private val used: Any) {
                    fun something() {
                        used.toString()
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report unused private property in data class - #6142`() {
            val code = """
                data class Foo(
                    private val foo: Int,
                    private val bar: String,
                )
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report unused private property in data class with named constructor`() {
            val code = """
                data class Foo constructor(
                    private val foo: Int,
                    private val bar: String,
                )
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report unused private property in value or inline class`() {
            val code = """
                @JvmInline value class Foo(private val value: String)
                inline class Bar(private val value: String)
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `parameters in primary constructors` {
        @Test
        fun `reports unused parameter`() {
            val code = """
                class Test(unused: Any)
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report used parameter for calling super`() {
            val code = """
                class Parent(val ignored: Any)
                class Test(used: Any) : Parent(used)
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used parameter in init block`() {
            val code = """
                class Test(used: Any) {
                    init {
                        used.toString()
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used parameter to initialize property`() {
            val code = """
                class Test(used: Any) {
                    val usedString = used.toString()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `secondary parameters` {
        @Test
        fun `report unused parameters in secondary constructors`() {
            val code = """
                private class ClassWithSecondaryConstructor {
                    constructor(used: Any, unused: Any) {
                        used.toString()
                    }
                
                    // this is actually unused, but clashes with the other constructor
                    constructor(used: Any)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `irrelevant references are ignored` {
        @Test
        fun `package declarations are ignored`() {
            val code = """
                package org.detekt
                fun main() {
                    val org = 1
                    val detekt = 1
                    println("foo")
                }
            """.trimIndent()

            val results = subject.lint(code)
            assertThat(results).hasSize(2)
            assertThat(results).anyMatch { it.message == "Private property `org` is unused." }
            assertThat(results).anyMatch { it.message == "Private property `detekt` is unused." }
        }

        @Test
        fun `import declarations are ignored`() {
            val code = """
                import org.detekt.Foo
                fun main() {
                    val org = 1
                    val detekt = 1
                    println("foo")
                }
            """.trimIndent()

            val results = subject.lint(code)
            assertThat(results).hasSize(2)
            assertThat(results).anyMatch { it.message == "Private property `org` is unused." }
            assertThat(results).anyMatch { it.message == "Private property `detekt` is unused." }
        }
    }
}

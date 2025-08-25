package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.SourceLocation
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val ALLOWED_NAMES_PATTERN = "allowedNames"

@KotlinCoreEnvironmentTest
class UnusedPrivatePropertySpec(val env: KotlinEnvironmentContainer) {
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
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not reports when used in guard clause`() {
            val code = """
                class Test {
                    private val used = true
                
                    fun use() {
                        val a = '1'.digitToInt() + 1
                        val c = false
                        when (a) {
                            1 if used -> Unit
                            2      -> if (c) Unit else Unit
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report unused public properties`() {
            val code = """
                class Test {
                    val unused = "This is not used"
                
                    fun use() {
                        println("This is not using a property")
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used properties`() {
            val code = """
                class Test {
                    private val used = "This is used"
                
                    fun use() {
                        println(used)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used properties but reports unused properties`() {
            val code = """
                class Test {
                    private val used = "This is used"
                    private val unused = "This is not used"
                
                    fun use() {
                        println(used)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does fail when enabled with invalid regex`() {
            val config = TestConfig(ALLOWED_NAMES_PATTERN to "*foo")
            assertThatExceptionOfType(PatternSyntaxException::class.java)
                .isThrownBy { UnusedPrivateProperty(config).lintWithContext(env, regexTestingCode) }
        }
    }

    @Nested
    inner class `classes with properties and local properties` {

        @Test
        fun `not reports class property when unused with local variable with same name`() {
            val code = """
                class Test {
                    private val commonVal = "This is used"
        
                    fun use() {
                        val commonVal = 1
                        println(this.commonVal)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code))
                .hasSize(0)
        }

        @Test
        fun `does report local fun variable is used instead of property in local class`() {
            val code = """
                fun test() {
                    val commonVal = 1
                    class LocalClass {
                        private val commonVal: Int = 42
                        fun foo() = println(commonVal)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).singleElement()
                .hasStartSourceLocation(4, 21)
        }

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
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
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
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report used properties`() {
            val code = """
                class Test {
                    private val used = "This is used"
                
                    fun use() {
                        val text = used
                        println(text)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used properties and properties`() {
            val code = """
                class C {
                    val myNumber = 5
                
                    fun publicFunction(usedParam: String) {
                        println(usedParam)
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
                    println(o("$\\{PC.Companion.OO.BLA.toString()}"))
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(0)
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
            assertThat(subject.lintWithContext(env, code)).hasSize(4)
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `top level properties` {

        @Test
        fun `not report top level public properties`() {
            val code = """
                val notUsedTopLevelVal = 1
                fun using(){
                  println("foo")
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .isEmpty()
        }

        @Test
        fun `reports top level properties if they are unused`() {
            val code = """
                private val usedTopLevelVal = 1
                private const val unusedTopLevelConst = 1
                private val unusedTopLevelVal = usedTopLevelVal
            """.trimIndent()
            val findings = subject.lintWithContext(env, code, allowCompilationErrors = true)
            assertThat(findings).hasSize(2)
            assertThat(findings).element(0)
                .hasStartSourceLocation(2, 19)
            assertThat(findings).element(1)
                .hasStartSourceLocation(3, 13)
        }

        @Test
        fun `not report when top level properties are used in function`() {
            val code = """
                private val usedTopLevelVal = 1
                fun using(){
                  println(usedTopLevelVal)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .isEmpty()
        }

        @Test
        fun `report when top level properties have same name as function parameter`() {
            val code = """
                private val foo = 1
                fun using (foo:Int) {
                  println(foo)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).singleElement()
                .hasStartSourceLocation(SourceLocation(1, 13))
        }

        @Test
        fun `not report ignored private properties in top level`() {
            val code = """
               private val foo = 2 // not ignored
               private val ignored = 3 // ignored   
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `nested class declarations` {
        @Test
        fun `should report for local classes with same name and property`() {
            val code = """
                fun containerFunction() {
                    class Foo(private val value: Int)
                }
        
                fun containerFunction1() {
                    class Foo(private val value: Int)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).element(0)
                .hasStartSourceLocation(2, 27)
            assertThat(findings).element(1)
                .hasStartSourceLocation(6, 27)
        }

        @Test
        fun `does proper class when variable is common`() {
            val code = """
                class Class {
                    private val commonVal: Int = 0
                    inner class LocalClass1 {
                        private val commonVal: Int = 0
                        inner class LocalClass2 {
                            private val commonVal: Int = 0
                            fun test() = println(this@LocalClass1.commonVal)
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).element(0)
                .hasStartSourceLocation(2, 17)
            assertThat(findings).element(1)
                .hasStartSourceLocation(6, 25)
        }

        @Test
        fun `reports unused nested private property`() {
            val code = """
                class Test {
                    class Inner {
                        private val unused = 1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `suppress unused property warning annotations` {
        @Test
        fun `does not report annotated private constructor properties`() {
            val code = """
                class Test(@Suppress("UnusedPrivateProperty") private val foo: String) {}
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports private constructor properties without annotation`() {
            val code = """
                class Test(
                    @Suppress("UnusedPrivateProperty") private val foo: String,
                    private val bar: String
                ) {}
            """.trimIndent()

            val lint = subject.lintWithContext(env, code)

            assertThat(lint).singleElement()
                .hasMessage("Private property `bar` is unused.")
        }

        @Test
        fun `does not report private constructor properties in annotated class`() {
            val code = """
                @Suppress("UnusedPrivateProperty")
                class Test(
                    private val foo: String,
                    private val bar: String
                ) {}
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report private constructor properties in class with annotated outer class`() {
            val code = """
                @Suppress("UnusedPrivateProperty")
                class Test(
                    private val foo: String,
                    private val bar: String
                ) {
                    class InnerTest(
                        private val baz: String
                    ) {}
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report annotated private properties`() {
            val code = """
                class Test {
                    @Suppress("UnusedPrivateProperty") private val foo: String = "foo"
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports private properties without annotation`() {
            val code = """
                class Test {
                    @Suppress("UnusedPrivateProperty") private val foo: String = "foo"
                    private val bar: String = "bar"
                }
            """.trimIndent()

            val lint = subject.lintWithContext(env, code)

            assertThat(lint).singleElement()
                .hasMessage("Private property `bar` is unused.")
        }

        @Test
        fun `does not report private properties in annotated class`() {
            val code = """
                @Suppress("UnusedPrivateProperty")
                class Test {
                    private val foo: String = "foo"
                    private val bar: String = "bar"
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report private properties in class with annotated outer class`() {
            val code = """
                @Suppress("UnusedPrivateProperty")
                class Test {
                    private val foo: String = "foo"
                    private val bar: String = "bar"
                    
                    @Suppress("UnusedPrivateProperty")
                    class InnerTest {
                        private val baz: String = "baz"
                    }
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).singleElement()
                .hasStartSourceLocation(5, 17)
        }
    }

    @Nested
    inner class `actual functions and classes` {

        @Test
        fun `should not report unused actual fields defined as parameters of primary constructors`() {
            val code = """
                actual class Foo actual constructor(actual val bar: String) {}
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }

        @Test
        fun `reports unused private fields defined as parameters of primary constructors`() {
            val code = """
                actual class Foo actual constructor(private val bar: String) {}
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).hasSize(1)
        }
    }

    @Nested
    inner class `properties in primary constructors` {

        @Test
        fun `reports unused vararg parameter`() {
            val code = """
               class Test(vararg unused: Any)
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
        }

        @Test
        fun `not reports used vararg parameter`() {
            val code = """
                open class Parent(private vararg val unused: Any)
                class Child(private vararg val used: Any) : Parent(used)
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
        }

        @Test
        fun `not reports ignored vararg parameter`() {
            val code = """
                open class Super(private vararg val ignored: Any)
                class Foo(private vararg val used: Any) : Super(used)
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .isEmpty()
        }

        @Test
        fun `reports destructuring vararg parameters`() {
            val code = """
                class TestConfig(val foo: String, vararg pairs: Pair<String, Any>) : Config {
                    val values: Map<String, Any> = mapOf(*pairs)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true))
                .hasSize(0)
        }

        @Test
        fun `reports unused private property`() {
            val code = """
                class Test(private val unused: Any)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports for multiple classes with same properties name in the same file`() {
            val code = """
                class C(initial:Int = 0){
                
                constructor(c:Float, d:Float):this(c.toInt()){
                   println(c)
                }
                  
               }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isNotEmpty()
        }

        @Test
        fun `does not report public property`() {
            val code = """
                class Test(val unused: Any)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report private property used in init block`() {
            val code = """
                class Test(private val used: Any) {
                    init { used.toString() }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report unused private property in data class - #6142`() {
            val code = """
                data class Foo(
                    private val foo: Int,
                    private val bar: String,
                )
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report unused private property in data class with named constructor`() {
            val code = """
                data class Foo constructor(
                    private val foo: Int,
                    private val bar: String,
                )
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report unused private property in value or inline class`() {
            val code = """
                @JvmInline value class Foo(private val value: String)
                inline class Bar(private val value: String)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `parameters in primary constructors` {
        @Test
        fun `reports unused parameter`() {
            val code = """
                class Test(unused: Any)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report used parameter for calling super`() {
            val code = """
                open class Parent(val ignored: Any)
                class Test(used: Any) : Parent(used)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used parameter to initialize property`() {
            val code = """
                class Test(used: Any) {
                    val usedString = used.toString()
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports parameter used for delegation`() {
            val code = """
                interface Detektion
                class DelegatingResult(
                    result: Detektion,
                    val findings: Map<Any,Any>
                ) : Detektion by result
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
                
                    constructor(used: Any)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }
    }
}

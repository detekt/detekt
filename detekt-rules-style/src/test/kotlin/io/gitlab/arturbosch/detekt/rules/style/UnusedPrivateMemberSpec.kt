package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val ALLOWED_NAMES_PATTERN = "allowedNames"

@KotlinCoreEnvironmentTest
class UnusedPrivateMemberSpec(val env: KotlinCoreEnvironment) {
    val subject = UnusedPrivateMember()

    val regexTestingCode = """
                class Test {
                    private val used = "This is used"
                    private val unused = "This is not used"

                    fun use() {
                        println(used)
                    }
                }
    """

    @Nested
    inner class `interface functions` {

        @Test
        fun `should not report parameters in interface functions`() {
            val code = """
                interface UserPlugin {
                    fun plug(application: Application)
                    fun unplug()
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `expect functions and classes` {

        @Test
        fun `should not report parameters in expect class functions`() {
            val code = """
                expect class Foo {
                    fun bar(i: Int)
                    fun baz(i: Int, s: String)
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report parameters in expect object functions`() {
            val code = """
                expect object Foo {
                    fun bar(i: Int)
                    fun baz(i: Int, s: String)
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report parameters in expect functions`() {
            val code = """
                expect fun bar(i: Int)
                expect fun baz(i: Int, s: String)
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report parameters in expect class with constructor`() {
            val code = """
                expect class Foo1(private val bar: String) {}
                expect class Foo2(bar: String) {}
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `actual functions and classes` {

        @Test
        fun `should not report unused parameters in actual functions`() {
            val code = """
                actual class Foo {
                    actual fun bar(i: Int) {}
                    actual fun baz(i: Int, s: String) {}
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report unused parameters in actual constructors`() {
            val code = """
                actual class Foo actual constructor(bar: String) {}
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report unused actual fields defined as parameters of primary constructors`() {
            val code = """
                actual class Foo actual constructor(actual val bar: String) {}
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports unused private fields defined as parameters of primary constructors`() {
            val code = """
                actual class Foo actual constructor(private val bar: String) {}
            """
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `external functions` {

        @Test
        fun `should not report parameters in external functions`() {
            val code = "external fun foo(bar: String)"
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `external classes` {

        @Test
        fun `should not report functions in external classes`() {
            val code = """
                external class Bugsnag {
                    companion object {
                        fun start(value: Int)
                        fun notify(error: String)
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `protected functions` {

        @Test
        fun `should not report parameters in protected functions`() {
            val code = """
                open class Foo {
                    protected fun fee(bar: String) {}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `overridden functions` {

        @Test
        fun `should not report parameters in not private functions`() {
            val code = """
                override fun funA() {
                    objectA.resolve(valA, object : MyCallback {
                        override fun onResolveFailed(throwable: Throwable) {
                            errorMessage.visibility = View.VISIBLE
                        }
                    })
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report in overriden classes`() {
            val code = """
                abstract class Parent {
                    abstract fun abstractFun(arg: Any)
                    open fun openFun(arg: Any): Int = 0
                }

                class Child : Parent() {
                    override fun abstractFun(arg: Any) {
                        println(arg)
                    }

                    override fun openFun(arg: Any): Int {
                        println(arg)
                        return 1
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `non-private classes` {
        @Test
        fun `should not report internal classes`() {
            val code = """
                internal class IC // unused but internal
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `classes accessing constants from companion objects` {

        @Test
        fun `should not report used constants`() {
            val code = """
                class A {
                    companion object {
                        private const val MY_CONST = 42
                    }

                    fun a() {
                        Completable.timer(MY_CONST.toLong(), TimeUnit.MILLISECONDS)
                                .subscribe()
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

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
            """
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
            """
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
            """
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
            """
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not fail when disabled with invalid regex`() {
            val configRules = mapOf(
                "active" to "false",
                ALLOWED_NAMES_PATTERN to "*foo"
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
                class UnusedPrivateMemberPositive {
                    private val unusedField = 5
                    val publicField = 2
                    private val clashingName = 4
                    private fun unusedFunction(unusedParam: Int) {
                        val unusedLocal = 5
                    }
                }
            """
            assertThat(subject.lint(code)).hasSize(5)
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
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
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `top level functions` {

        @Test
        fun `reports top-level unused functions`() {
            val code = """
                private fun unusedTopLevelFunction() = 5
            """
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report used top level functions`() {
            val code = """
                private fun calledFromMain() {}
                
                fun main(args: Array<String>) {
                    calledFromMain()
                }
            """
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
            """
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
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `unused private functions` {
        @Test
        fun `does not report used private functions`() {
            val code = """
            class Test {
                val value = usedMethod()

                private fun usedMethod(): Int {
                    return 5
                }
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports unused private functions`() {
            val code = """
            class Test {
                private fun unusedFunction(): Int {
                    return 5
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report function used in interface - #1613`() {
            val code = """
                interface Bar {
                    fun doSomething() {
                        doSomethingElse()
                    }
                }
                private fun doSomethingElse() {}
            """

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `private functions only used by unused private functions` {

        @Test
        fun `reports the non called private function`() {
            val code = """
            class Test {
                private fun unusedFunction(): Int {
                    return someOtherUnusedFunction()
                }

                private fun someOtherUnusedFunction() {
                    println("Never used")
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
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
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report the unused private function and parameter`() {
            val code = """
                class Test {
                    private fun ignored(ignored: Int) {}
                }
            """
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
            """
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
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `properties in primary constructors` {
        @Test
        fun `reports unused private property`() {
            val code = """
                class Test(private val unused: Any)
            """
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report public property`() {
            val code = """
                class Test(val unused: Any)
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private property used in init block`() {
            val code = """
                class Test(private val used: Any) {
                    init { used.toString() }
                }
            """
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
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `error messages` {
        @Test
        fun `are specific for function parameters`() {
            val code = """
                fun foo(unused: Int){}
            """

            val lint = subject.lint(code)

            assertThat(lint.first().message).startsWith("Function parameter")
        }

        @Test
        fun `are specific for local variables`() {
            val code = """
                fun foo(){ val unused = 1 }
            """

            val lint = subject.lint(code)

            assertThat(lint.first().message).startsWith("Private property")
        }

        @Test
        fun `are specific for private functions`() {
            val code = """
            class Test {
                private fun unusedFunction(): Int {
                    return 5
                }
            }
            """

            val lint = subject.lint(code)

            assertThat(lint.first().message).startsWith("Private function")
        }
    }

    @Nested
    inner class `suppress unused property warning annotations` {
        @Test
        fun `does not report annotated private constructor properties`() {
            val code = """
                class Test(@Suppress("unused") private val foo: String) {}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports private constructor properties without annotation`() {
            val code = """
                class Test(
                    @Suppress("unused") private val foo: String,
                    private val bar: String
                ) {}
            """

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
            """

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
            """

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
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report annotated private properties`() {
            val code = """
                class Test {
                    @Suppress("unused") private val foo: String
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports private properties without annotation`() {
            val code = """
                class Test {
                    @Suppress("unused") private val foo: String
                    private val bar: String
                }
            """

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
            """

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
            """

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
            """

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `suppress unused function warning annotations` {
        @Test
        fun `does not report annotated private functions`() {
            val code = """
                @Suppress("unused")
                private fun foo(): String = ""
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports private functions without annotation`() {
            val code = """
                private fun foo(): String = ""
            """

            val findings = subject.lint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].entity.signature).isEqualTo("Test.kt\$private fun foo(): String")
        }

        @Test
        fun `does not report private functions in annotated class`() {
            val code = """
                @Suppress("unused")
                class Test {
                    private fun foo(): String = ""
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private functions in class with annotated outer class`() {
            val code = """
                @Suppress("unused")
                class Test {
                    private fun foo(): String = ""
                    private fun bar(): String = ""

                    class InnerTest {
                        private fun baz(): String = ""
                    }
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private functions in annotated file`() {
            val code = """
                @file:Suppress("unused")
                class Test {
                    private fun foo(): String = ""
                    private fun bar(): String = ""

                    class InnerTest {
                        private fun baz(): String = ""
                    }
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `main methods` {

        @Test
        fun `does not report the args parameter of the main function inside an object`() {
            val code = """
                object O {

                    @JvmStatic
                    fun main(args: Array<String>) {
                        println("b")
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report the args parameter of the main function as top level function`() {
            val code = """
                fun main(args: Array<String>) {
                    println("b")
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `operators` {

        @Test
        fun `does not report used plus operator - #1354`() {
            val code = """
                import java.util.Date
                class Foo {
                    val bla: Date = Date(System.currentTimeMillis()) + 300L
                    companion object {
                        private operator fun Date.plus(diff: Long): Date = Date(this.time + diff)
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used plus operator without type solving - #4242`() {
            val code = """
                import java.util.Date
                class Foo {
                    val bla: Date = Date(System.currentTimeMillis()) + 300L
                    companion object {
                        private operator fun Date.plus(diff: Long): Date = Date(this.time + diff)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report used invoke operator without type solving - #4435`() {
            val code = """
                object Test {
                    private operator fun invoke(i: Int): Int = i

                    fun answer() = Test(1)
                }

                val answer = Test.answer()
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report used operator methods when used with the equal sign`() {
            val code = """
                class Test {
                    fun f() {
                        var number: Int? = 0
                        number += 1
                        number -= 1
                        number *= 1
                        number /= 1
                        number %= 1
                    }
                    private operator fun Int?.plus(other: Int) = 1
                    private operator fun Int?.minus(other: Int) = 2
                    private operator fun Int?.times(other: Int) = 3
                    private operator fun Int?.div(other: Int) = 4
                    private operator fun Int?.rem(other: Int) = 5
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 'contains' operator function that is used as 'in'`() {
            val code = """
                class C {
                    val isInside = "bar" in listOf("foo".toRegex())
                    
                    private operator fun Iterable<Regex>.contains(a: String): Boolean {
                        return any { it.matches(a) }
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 'contains' operator function that is used as '!in'`() {
            val code = """
                class C {
                    val isInside = "bar" !in listOf("foo".toRegex())
                    
                    private operator fun Iterable<Regex>.contains(a: String): Boolean {
                        return any { it.matches(a) }
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `report unused minus operator`() {
            val code = """
                import java.util.Date
                class Foo {
                    companion object {
                        private operator fun Date.minus(diff: Long): Date = Date(this.time - diff)
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `same named functions` {

        @Test
        fun `report it when the file has same named functions`() {
            val code = """
                class Test {
                    private fun f(): Int {
                        return 5
                    }
                }

                class Test2 {
                    private fun f(): Int {
                        return 5
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `report it when the class has same named functions`() {
            val code = """
                class Test {
                    val value = f(1)

                    private fun f(): Int {
                        return 5
                    }

                    private fun f(num: Int): Int {
                        return num
                    }

                    private fun f(num: String): Int {
                        return num.toInt()
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `report it when the class has same named extension functions`() {
            val code = """
                class Test {
                    val value = 1.f()

                    private fun f(): Int {
                        return 5
                    }

                    private fun Int.f(): Int {
                        return this
                    }

                    private fun String.f(): Int {
                        return toInt()
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }
    }

    @Nested
    inner class `operator functions - #2579` {

        @Test
        fun `Does not report unused operators`() {
            val code = """
                class Test {
                    private operator fun Foo.plus(other: Foo): Foo = Foo(value + other.value)

                    inner class Foo(val value: Int) {
                        fun double(): Foo = this + this
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }

        @Test
        fun `Report unused operators`() {
            val code = """
                class Test {
                    private operator fun Foo.plus(other: Foo): Foo = Foo(value + other.value)
                    private operator fun Foo.minus(other: Foo): Foo = Foo(value - other.value)

                    inner class Foo(val value: Int) {
                        fun double(): Foo = this + this
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1).hasSourceLocations(
                SourceLocation(3, 30)
            )
        }
    }

    @Nested
    inner class `overloaded extension functions - #2579` {

        @Test
        fun `Does not report used private extension functions`() {
            val code = """
                class A
                class B
                class C(val elements: Set<B>, val flag: Boolean)

                class Test {
                    private fun A.someMethod(
                          param1: B,
                          param2: Boolean = true
                      ) = someMethod(setOf(param1), param2)

                    private fun A.someMethod(
                          param1: Set<B>,
                          param2: Boolean = true
                      ) = C(param1, param2)

                    fun main() {
                        val aInstance = A()
                        aInstance.someMethod(B(), true)
                        aInstance.someMethod(setOf(B(), B()), false)
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }
    }

    @Nested
    inner class `getValue and setValue operator functions - #3128` {

        @Test
        fun `does not report used private getValue and setValue operator functions`() {
            val code = """
                import kotlin.reflect.KProperty

                class Test {
                    var delegated by "Hello"

                    private operator fun String.getValue(test: Test, prop: KProperty<*>): String {
                        return "working"
                    }

                    private operator fun String.setValue(test: Test, prop: KProperty<*>, value: String) {
                        error("setValue")
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }

        @Test
        fun `does not report getValue and setValue operator function parameters`() {
            val code = """
                import kotlin.reflect.KProperty
        
                class SingleAssign<String> {
        
                    operator fun getValue(thisRef: Any?, property: KProperty<*>): kotlin.String {
                        return ""
                    }

                    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }

        @Test
        fun `reports unused private getValue and setValue operator functions`() {
            val code = """
                import kotlin.reflect.KProperty

                class Test {
                    private operator fun String.getValue(test: Test, prop: KProperty<*>): String {
                        return "working"
                    }

                    private operator fun String.setValue(test: Test, prop: KProperty<*>, value: String) {
                        error("setValue")
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
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
            """
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
            """
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
            """
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `list get overloaded operator function - #3640` {
        @Test
        fun `report used private list get operator function - declared in a class - called by operator`() {
            val code = """
                class StringWrapper(
                    val s: String
                )

                class TestWrapper {
                    private operator fun List<StringWrapper>.get(s: String) =
                        this.firstOrNull { it.s == s }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a class - called by operator`() {
            val code = """
                class StringWrapper(
                    val s: String
                )

                class TestWrapper(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(s: String) = strings[s]

                    private operator fun List<StringWrapper>.get(s: String) =
                        this.firstOrNull { it.s == s }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a class - called by operator - multiple parameters`() {
            val code = """
                class StringWrapper(
                    val s: String
                )

                class TestWrapper(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(a: String, b: String) = strings[a, b]

                    private operator fun List<StringWrapper>.get(a: String, b: String) =
                        this.firstOrNull { it.s == b }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a class - called directly`() {
            val code = """
                class StringWrapper(
                    val s: String
                )

                class TestWrapper(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(s: String) = strings.get(s)

                    private operator fun List<StringWrapper>.get(s: String) =
                        this.firstOrNull { it.s == s }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }

        @Test
        fun `report used private list get operator function - declared in a file - called by operator`() {
            val code = """
                class StringWrapper(
                    val s: String
                )

                private operator fun List<StringWrapper>.get(s: String) =
                    this.firstOrNull { it.s == s }
            """
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a file - called by operator`() {
            val code = """
                class StringWrapper(
                    val s: String
                )

                class Test(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(s: String) = strings[s]
                }

                private operator fun List<StringWrapper>.get(s: String) =
                    this.firstOrNull { it.s == s }
            """
            assertThat(subject.lintWithContext(env, code)).hasSize(0)
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a file - called directly`() {
            val code = """
                class StringWrapper(
                    val s: String
                )

                class Test(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(s: String) = strings.get(s)
                }

                private operator fun List<StringWrapper>.get(s: String) =
                    this.firstOrNull { it.s == s }
            """
            assertThat(subject.lintWithContext(env, code)).hasSize(0)
        }
    }

    @Nested
    inner class `highlights declaration name` {
        @Test
        fun function() {
            val code = """
                class Test {
                    /**
                     * kdoc
                     */
                    private fun foo() = 1
                }
            """
            assertThat(subject.lint(code)).hasSize(1).hasSourceLocation(5, 17)
        }

        @Test
        fun property() {
            val code = """
                class Test {
                    /**
                     * kdoc
                     */
                    private val foo = 1
                }
            """
            assertThat(subject.lint(code)).hasSize(1).hasSourceLocation(5, 17)
        }

        @Test
        fun parameter() {
            val code = """
                class Test {
                    fun test(
                        /**
                         * kdoc
                         */
                        x: Int
                    ) = 1
                }
            """
            assertThat(subject.lint(code)).hasSize(1).hasSourceLocation(6, 9)
        }
    }
}

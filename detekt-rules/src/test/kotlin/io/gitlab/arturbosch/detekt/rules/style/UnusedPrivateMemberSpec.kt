package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.regex.PatternSyntaxException

class UnusedPrivateMemberSpec : Spek({

    val subject by memoized { UnusedPrivateMember() }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    val regexTestingCode = """
                class Test {
                    private val used = "This is used"
                    private val unused = "This is not used"

                    fun use() {
                        println(used)
                    }
                }
                """

    describe("cases file with different findings") {

        it("positive cases file") {
            assertThat(subject.lint(Case.UnusedPrivateMemberPositive.path())).hasSize(13)
        }

        it("negative cases file") {
            assertThat(subject.lint(Case.UnusedPrivateMemberNegative.path())).isEmpty()
        }
    }

    describe("interface functions") {

        it("should not report parameters in interface functions") {
            val code = """
                interface UserPlugin {
                    fun plug(application: Application)
                    fun unplug()
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    describe("external functions") {

        it("should not report parameters in external functions") {
            val code = "external fun foo(bar: String)"
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    describe("overridden functions") {

        it("should not report parameters in not private functions") {
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
    }

    describe("classes accessing constants from companion objects") {

        it("should not report used constants") {
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

    describe("several classes with properties") {

        it("reports an unused member") {
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

        it("does not report unused public members") {
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

        it("does not report used members") {
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

        it("does not report used members but reports unused members") {
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

        it("does not fail when disabled with invalid regex") {
            val configRules = mapOf(
                "active" to "false",
                UnusedPrivateMember.ALLOWED_NAMES_PATTERN to "*foo"
            )
            val config = TestConfig(configRules)
            assertThat(UnusedPrivateMember(config).lint(regexTestingCode)).isEmpty()
        }

        it("does fail when enabled with invalid regex") {
            val configRules = mapOf(UnusedPrivateMember.ALLOWED_NAMES_PATTERN to "*foo")
            val config = TestConfig(configRules)
            assertThatExceptionOfType(PatternSyntaxException::class.java)
                .isThrownBy { UnusedPrivateMember(config).lint(regexTestingCode) }
        }
    }

    describe("several classes with properties and local properties") {

        it("reports an unused member") {
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

        it("does not report used members") {
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

        it("reports unused local properties") {
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

    describe("loop iterators") {

        it("should not depend on evaluation order of functions or properties") {
            val code = """
                fun RuleSetProvider.provided() = ruleSetId in defaultRuleSetIds

                val defaultRuleSetIds = listOf("comments", "complexity", "empty-blocks",
                        "exceptions", "potential-bugs", "performance", "style")
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("doesn't report loop properties") {
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

        it("reports unused loop property") {
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

        it("reports unused loop property in indexed array") {
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

        it("reports all unused loop properties in indexed array") {
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

        it("does not report used loop properties in indexed array") {
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

    describe("properties used to initialize other properties") {

        it("does not report properties used by other properties") {
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

        it("does not report properties used by inner classes") {
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

    describe("function parameters") {
        it("reports single parameters if they are unused") {
            val code = """
            class Test {
                val value = usedMethod(1)

                private fun usedMethod(unusedParameter: Int): Int {
                    return 5
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports two parameters if they are unused and called the same in different methods") {
            val code = """
            class Test {
                val value = usedMethod(1)
                val value2 = usedMethod2(1)

                private fun usedMethod(unusedParameter: Int): Int {
                    return 5
                }

                private fun usedMethod2(unusedParameter: Int) {
                    return 5
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(2)
        }

        it("does not report single parameters if they used in return statement") {
            val code = """
            class Test {
                val value = usedMethod(1)

                private fun usedMethod(used: Int): Int {
                    return used
                }
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report single parameters if they used in function") {
            val code = """
            class Test {
                val value = usedMethod(1)

                private fun usedMethod(used: Int) {
                    println(used)
                }
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports parameters that are unused in return statement") {
            val code = """
            class Test {
                val value = usedMethod(1, 2)

                private fun usedMethod(unusedParameter: Int, usedParameter: Int): Int {
                    return usedParameter
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports parameters that are unused in function") {
            val code = """
            class Test {
                val value = usedMethod(1, 2)

                private fun usedMethod(unusedParameter: Int, usedParameter: Int) {
                    println(usedParameter)
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    describe("top level function parameters") {
        it("reports single parameters if they are unused") {
            val code = """
            fun function(unusedParameter: Int): Int {
                return 5
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report single parameters if they used in return statement") {
            val code = """
            fun function(used: Int): Int {
                return used
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report single parameters if they used in function") {
            val code = """
            fun function(used: Int) {
                println(used)
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports parameters that are unused in return statement") {
            val code = """
            fun function(unusedParameter: Int, usedParameter: Int): Int {
                return usedParameter
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports parameters that are unused in function") {
            val code = """
            fun function(unusedParameter: Int, usedParameter: Int) {
                println(usedParameter)
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    describe("unused private functions") {
        it("does not report used private functions") {
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

        it("reports unused private functions") {
            val code = """
            class Test {
                private fun unusedFunction(): Int {
                    return 5
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report function used in interface - #1613") {
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

    describe("private functions only used by unused private functions") {

        it("reports the non called private function") {
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

    describe("unused class declarations which are allowed") {

        it("does not report the unused private property") {
            val code = """
                class Test {
                    private val ignored = ""
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report the unused private function and parameter") {
            val code = """
                class Test {
                    private fun ignored(ignored: Int) {}
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    describe("nested class declarations") {

        it("reports unused nested private property") {
            val code = """
                class Test {
                    class Inner {
                        private val unused = 1
                    }
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report used nested private property") {
            val code = """
                class Test {
                    class Inner {
                        private val used = 1
                        fun someFunction() = used
                    }
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    describe("parameters in primary constructors") {
        it("reports unused private property") {
            val code = """
                class Test(private val unused: Any)
                """
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports unused parameter") {
            val code = """
                class Test(unused: Any)
                """
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report used parameter for calling super") {
            val code = """
                class Parent(val ignored: Any)
                class Test(used: Any) : Parent(used)
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report used parameter in init block") {
            val code = """
                class Test(used: Any) {
                    init {
                        used.toString()
                    }
                }
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report used parameter to initialize property") {
            val code = """
                class Test(used: Any) {
                    val usedString = used.toString()
                }
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report public property") {
            val code = """
                class Test(val unused: Any)
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report private property used in init block") {
            val code = """
                class Test(private val used: Any) {
                    init { used.toString() }
                }
                """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report private property used in function") {
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

    describe("error messages") {
        it("are specific for function parameters") {
            val code = """
                fun foo(unused: Int){}
            """

            val lint = subject.lint(code)

            assertThat(lint.first().message).startsWith("Function parameter")
        }

        it("are specific for local variables") {
            val code = """
                fun foo(){ val unused = 1 }
            """

            val lint = subject.lint(code)

            assertThat(lint.first().message).startsWith("Private property")
        }

        it("are specific for private functions") {
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

    describe("suppress unused parameter warning annotations") {
        it("does not report annotated parameters") {
            val code = """
                fun foo(@Suppress("UNUSED_PARAMETER") unused: String){}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports parameters without annotation") {
            val code = """
                fun foo(@Suppress("UNUSED_PARAMETER") unused: String, unusedWithoutAnnotation: String){}
            """

            val lint = subject.lint(code)

            assertThat(lint).hasSize(1)
            assertThat(lint[0].entity.signature).isEqualTo("Test.kt\$unusedWithoutAnnotation: String")
        }

        it("does not report parameters in annotated function") {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                fun foo(unused: String, otherUnused: String){}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report parameters in annotated class") {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                class Test {
                    fun foo(unused: String, otherUnused: String){}
                    fun bar(unused: String){}
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report parameters in annotated object") {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                object Test {
                    fun foo(unused: String){}
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report parameters in class with annotated outer class") {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                class Test {
                    fun foo(unused: String){}

                    class InnerTest {
                        fun bar(unused: String){}
                    }
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report parameters in annotated file") {
            val code = """
                @file:Suppress("UNUSED_PARAMETER")

                class Test {
                    fun foo(unused: String){}

                    class InnerTest {
                        fun bar(unused: String){}
                    }
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    describe("suppress unused property warning annotations") {
        it("does not report annotated private constructor properties") {
            val code = """
                class Test(@Suppress("unused") private val foo: String) {}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports private constructor properties without annotation") {
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

        it("does not report private constructor properties in annotated class") {
            val code = """
                @Suppress("unused")
                class Test(
                    private val foo: String,
                    private val bar: String
                ) {}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report private constructor properties in class with annotated outer class") {
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

        it("does not report private constructor properties in annotated file") {
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

        it("does not report annotated private properties") {
            val code = """
                class Test {
                    @Suppress("unused") private val foo: String
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports private properties without annotation") {
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

        it("does not report private properties in annotated class") {
            val code = """
                @Suppress("unused")
                class Test {
                    private val foo: String
                    private val bar: String
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report private properties in class with annotated outer class") {
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

        it("does not report private properties in annotated file") {
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

    describe("suppress unused function warning annotations") {
        it("does not report annotated private functions") {
            val code = """
                @Suppress("unused")
                private fun foo(): String = ""
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports private functions without annotation") {
            val code = """
                private fun foo(): String = ""
            """

            val findings = subject.lint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].entity.signature).isEqualTo("Test.kt\$private fun foo(): String")
        }

        it("does not report private functions in annotated class") {
            val code = """
                @Suppress("unused")
                class Test {
                    private fun foo(): String = ""
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report private functions in class with annotated outer class") {
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

        it("does not report private functions in annotated file") {
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

    describe("main methods") {

        it("does not report the args parameter of the main function inside an object") {
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

        it("does not report the args parameter of the main function as top level function") {
            val code = """
                fun main(args: Array<String>) {
                    println("b")
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    describe("operators") {

        it("does not report used plus operator - #1354") {
            val code = """
                import java.util.Date
                class Foo {
                    val bla: Date = Date(System.currentTimeMillis()) + 300L
                    companion object {
                        private operator fun Date.plus(diff: Long): Date = Date(this.time + diff)
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    describe("same named functions") {

        it("report it when the file has same named functions") {
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
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(2)
        }

        it("report it when the class has same named functions") {
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
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(2)
        }

        it("report it when the class has same named extension functions") {
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
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(2)
        }
    }
})

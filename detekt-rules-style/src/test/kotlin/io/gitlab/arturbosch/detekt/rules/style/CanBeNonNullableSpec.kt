package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CanBeNonNullableSpec(val env: KotlinCoreEnvironment) {
    val subject = CanBeNonNullable(Config.empty)

    @Nested
    inner class `evaluating private properties` {
        @Test
        fun `reports when class-level vars are never assigned nullable values`() {
            val code = """
                class A(bVal: Int) {
                    private var a: Int? = 5
                    private var b: Int?
                    
                    init {
                        b = bVal
                    }
                    
                    fun foo(): Int {
                        val b = a!!
                        a = b + 1
                        val a = null
                        return b
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `reports when class-level vars are never assigned nullable values via chained call`() {
            val code = """
                class A {
                    $COMMON_CHAIN_CODE
                    private var bar: RandomBar = RandomBar()
                    private var a: String? = ""
                    private var b: String? = ""
                    private var c: String? = ""

                    fun foo() {
                        a = bar.nonNullId
                        b = bar.nonNullId.nullable().nonNullable()
                        c = bar.nullId.nonNullable()
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        @Test
        fun `reports when vars utilize non-nullable delegate values`() {
            val code = """
                import kotlin.reflect.KProperty
                
                class A {
                    private var a: Int? by PropDelegate()
                
                    fun foo(): Int {
                        val b = a!!
                        a = b + 1
                        val a = null
                        return b
                    }
                }
                
                class PropDelegate(private var propVal: Int = 0) {
                    operator fun getValue(thisRef: A, property: KProperty<*>): Int {
                        return propVal
                    }
                
                    operator fun setValue(thisRef: A, property: KProperty<*>, value: Any?) {
                        if (value is Int) {
                            propVal = value
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `reports when file-level vars are never assigned nullable values`() {
            val code = """
                private var fileA: Int? = 5
                private var fileB: Int? = 5
                
                fun fileFoo() {
                    fileB = 6
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `reports when class-level name shadowed vars are never assigned nullable values`() {
            val code = """
                class A {
                    fun baz() {
                        val r = object: Runnable {
                            var g: Int? = null
                            override fun run() { g = null }
                        }
                    }
                    private var g: Int? = 0 // never assigned
                }
                
                class B {
                    fun baz() {
                        var g: Int? = null
                        g = null
                    }
                    private var g: Int? = 0 // never assigned
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code))
                .hasSize(2)
                .hasStartSourceLocations(
                    SourceLocation(8, 5),
                    SourceLocation(16, 5)
                )
        }

        @Test
        fun `does not report when class-level vars are assigned nullable values`() {
            val code = """
                import kotlin.random.Random
                
                class A(fVal: Int?) {
                    private var a: Int? = 0
                    private var b: Int? = 0
                    private var c: Int? = 0
                    private var d: Int? = 0
                    private var e: Int? = null
                    private var f: Int?
                
                    init {
                        f = fVal
                    }
                
                    fun foo(fizz: Int): Int {
                        a = null
                        b = if (fizz % 2 == 0) fizz else null
                        c = buzz(fizz)
                        d = a
                        return fizz
                    }
                
                    private fun buzz(bizz: Int): Int? {
                        return if (bizz % 2 == 0) null else bizz
                    }
                }
                class B {
                    fun baz() {
                        g = null
                    }
                    private var g: Int? = 0
                }
                class C {
                    fun baz(a: Int?) {
                        g = a
                    }
                    private var g: Int? = 0
                }
                class D {
                    fun baz(a: Int?) {
                        g = a?.also { println("assigning baz a to g") }
                        if (a != null) println(1) else println(2)
                    }
                    private var g: Int? = 0
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when class-level vars are assigned nullable values via chained call`() {
            val code = """
                $COMMON_CHAIN_CODE

                class A {
                    private var a: String? = ""
                    private var b: String? = ""
                    private var c: String? = ""
                    private val bar: RandomBar = RandomBar()

                    fun foo() {
                        a = bar.nullId
                        b = bar.nullId.nonNullable().nullable()
                        c = bar.nonNullId.nullable()
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report vars that utilize nullable delegate values`() {
            val code = """
                class A(private var aDelegate: Int?) {
                    private var a: Int? by this::aDelegate
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when file-level vars are assigned nullable values`() {
            val code = """
                import kotlin.random.Random
                
                private var fileA: Int? = 5
                private var fileB: Int? = null
                private var fileC: Int? = 5
                
                fun fileFoo() {
                    fileC = null
                }
                
                class A {
                    fun foo() {
                        fileA = null
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports when vars with private setters are never assigned nullable values`() {
            val code = """
                class A {
                    var a: Int? = 5
                        private set
                    fun foo() {
                        a = 6
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report when vars with private setters are assigned nullable values`() {
            val code = """
                class A {
                    var a: Int? = 5
                        private set
                    fun foo() {
                        a = null
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when vars use public setters`() {
            val code = """
                class A {
                    var a: Int? = 5
                    fun foo() {
                        a = 6
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when vars use non-private setters`() {
            val code = """
                class A {
                    var a: Int? = 5
                        internal set
                    fun foo() {
                        a = 6
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when private vars are declared in the constructor`() {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        a = 6
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `evaluating public properties` {
        @Test
        fun `reports when class-level vals are set to non-nullable values`() {
            val code = """
                class A(cVal: Int) {
                    val a: Int? = 5
                    val b: Int?
                    val c: Int?
                
                    init {
                        b = 5
                        c = cVal
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        @Test
        fun `reports when vals utilize non-nullable delegate values`() {
            val code = """
                class A {
                    val a: Int? by lazy {
                        5
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports when file-level vals are set to non-nullable values`() {
            val code = """
                val fileA: Int? = 5
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report when class-level vals are assigned a nullable value`() {
            val code = """
                class A(cVal: Int?) {
                    val a: Int? = null
                    val b: Int?
                    val c: Int?
                
                    init {
                        b = null
                        c = cVal
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when class-level vals are assigned a nullable value by chained call`() {
            val code = """
                $COMMON_CHAIN_CODE
                class A(cVal: Int?) {
                    val bar = RandomBar()
                    val a: String?
                    val b: String?
                    val c: String?

                    init {
                        a = bar.nullId
                        b = bar.nullId.nonNullable().nullable()
                        c = bar.nonNullId.nullable()
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when vals utilize nullable delegate values`() {
            val code = """
                import kotlin.random.Random
                
                class A {
                    val d: Int? by lazy {
                        val randVal = Random.nextInt()
                        if (randVal % 2 == 0) randVal else null
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when file-level vals are assigned a nullable value`() {
            val code = """
                val fileA: Int? = null
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when vals are declared non-nullable`() {
            val code = """
                class A {
                    val a: Int = 5
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when vals are declared in the constructor`() {
            val code = """
                class A(private val a: Int?)
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports when vals with getters never return nullable values`() {
            val code = """
                class A {
                    val a: Int?
                        get() = 5
                    val b: Int?
                        get() {
                            return 5
                        }
                    val c: Int?
                        get() = foo()
                
                    private fun foo(): Int {
                        return 5
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        @Test
        fun `does not report when vals with getters return potentially-nullable values`() {
            val code = """
                import kotlin.random.Random
                
                class A {
                    val a: Int?
                        get() = Random.nextInt()?.let { if (it % 2 == 0) it else null }
                    val b: Int?
                        get() {
                            return Random.nextInt()?.let { if (it % 2 == 0) it else null }
                        }
                    val c: Int?
                        get() = foo()
                    
                    private fun foo(): Int? {
                        val randInt = Random.nextInt()
                        return if (randInt % 2 == 0) randInt else null
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report on non-null properties of a parameterized type`() {
            val code = """
                class P<T>(foo: T) {
                    val a: T = foo
                    val b: T? = null
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports on properties of an unnecessarily nullable parameterized type`() {
            val code = """
                class P<T>(foo: T) {
                    val bar: T? = foo
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports on properties of an unnecessarily nullable parameterized type extending Any`() {
            val code = """
                class P<T : Any>(foo: T) {
                    val bar: T? = foo
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report on properties of a parameterized type which must be nullable`() {
            val code = """
                import kotlin.random.Random
                
                class P<T : Any>(private val foo: T) {
                    val a: T
                        get() = foo
                    val b: T? = foo.takeIf { Random.nextBoolean() }
                    val c: T?
                        get() = foo.takeIf { Random.nextBoolean() }
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report on properties of a parameterized type which resolve to Nothing`() {
            val code = """
                class P<T> {
                    val foo: T
                        get() = error("")
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports on properties of an unnecessarily nullable parameterized type which resolve to Nothing`() {
            val code = """
                class P<T> {
                    val foo: T?
                        get() = error("")
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    @Test
    fun `does not report open properties`() {
        val code = """
            open class A {
                open val a: Int? = 5
                open var b: Int? = 5
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report abstract properties`() {
        val code = """
            abstract class A {
                abstract val a: Int?
                abstract var b: Int?
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report properties whose initial assignment derives from unsafe non-Java code`() {
        val code = """
            class A(msg: String?) {
                private val e = Exception(msg)
                // e.localizedMessage is marked as String! by Kotlin, meaning Kotlin
                // cannot guarantee that it will be non-null, even though it is treated
                // as non-null in Kotlin code.
                private var a: String? = e.localizedMessage
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report interface properties`() {
        val code = """
            interface A {
                val a: Int?
                var b: Int?
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Nested
    inner class `nullable function parameters` {
        @Nested
        inner class `using a de-nullifier` {
            @Test
            fun `does report when a param is de-nullified with a postfix expression`() {
                val code = """
                    fun foo(a: Int?) {
                        val b = a!! + 2
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does report when a param is de-nullified with a dot-qualified expression`() {
                val code = """
                    fun foo(a: Int?) {
                        val b = a!!.plus(2)
                    }
                    
                    fun fizz(b: Int?) = b!!.plus(2)
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
            }

            @Test
            fun `does report when a de-nullifier precondition is called on the param`() {
                val code = """
                    fun foo(a: Int?, b: Int?) {
                        val aNonNull = requireNotNull(a)
                        val c = aNonNull + checkNotNull(b)
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
            }

            @Test
            fun `does not report a double-bang call the field of a non-null param`() {
                val code = """
                    class A(val a: Int?)
                    
                    fun foo(a: A) {
                        val b = a.a!! + 2
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report on overridden function parameter`() {
                val code = """
                    interface A {
                        fun foo(a: Int?)
                    }
                    
                    class B : A {
                        override fun foo(a: Int?) {
                            val b = a!! + 2
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class `using a null-safe expression` {
            @Nested
            inner class `in initializer` {
                @Test
                fun `does not report when the safe-qualified expression is the only expression of the function`() {
                    val code = """
                        class A {
                            val foo = "BAR"
                        }
                        
                        fun foo(a: A?) = a?.foo
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when chained nullable call is the only expression of the function`() {
                    val code = """
                        $COMMON_CHAIN_CODE

                        fun foo1(bar: RandomBar?) = bar?.nullId?.nonNullable()?.nullable()
                        fun foo2(bar: RandomBar?) = bar?.nonNullId?.nullable()
                        fun foo3(bar: RandomBar?) = bar?.nonNullId.nonNullable()
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }
            }

            @Nested
            inner class `in a non-return statement` {
                @Test
                fun `does report when the safe-qualified expression is the only expression of the function`() {
                    val code = """
                        class A(val foo: String)
                        
                        fun foo(a: A?) {
                            a?.let { println(it.foo) }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does not report when the safe-qualified expression is within a lambda`() {
                    val code = """
                        class A {
                            fun doFoo(callback: () -> Unit) {
                                callback.invoke()
                            }
                        }
                        
                        fun foo(a: String?, aObj: A) {
                            aObj.doFoo {
                                a?.let { println("a not null") }
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when the safe-qualified expression is not the only expression of the function`() {
                    val code = """
                        class A {
                            fun doFoo() { println("FOO") }
                        }
                        
                        fun foo(a: A?) {
                            a?.doFoo()
                            val b = 5 + 2
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }
            }

            @Nested
            inner class `in a return statement` {
                @Test
                fun `does not report when the safe-qualified expression is the only expression of the function`() {
                    val code = """
                        class A {
                            val foo = "BAR"
                        }
                        
                        fun fizz(aObj: A?): String? {
                            return aObj?.foo
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }
            }
        }

        @Nested
        inner class `when statements` {
            @Nested
            inner class `without a subject` {
                @Test
                fun `does not report when the parameter is checked on nullity`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                a == null -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when the parameter is checked on nullity in a reversed manner`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                null == a -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when the parameter is checked on nullity with multiple clauses`() {
                    val code = """
                        fun foo(a: Int?, other: Int) {
                            when {
                                a == null && other % 2 == 0 -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does report when the parameter is only checked on non-nullity`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                a != null -> println(2 + a)
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does report when the parameter is only checked on non-nullity with and condition`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                a != null && a % 2 == 0 -> println(2 + a)
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does not report when the parameter is checked on non-nullity with or condition`() {
                    val code = """
                        fun foo(a: Int?, other: Int) {
                            when {
                                a != null || other % 2 == 0 -> println(2 + other)
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when the parameter is checked on non-nullity inside braces with or condition`() {
                    val code = """
                        fun foo(a: Int?, other: Int) {
                            when {
                                (a != null) || other % 2 == 0 -> println(2 + other)
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `doesn't report when the parameter is checked on non null with or condition nested 1 lvl deep`() {
                    val code = """
                        fun foo(a: Int?, other: Int, other1: Int) {
                            when {
                                other1 % 3 == 0 && (a != null || other % 2 == 0) -> println(2 + other)
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when the parameter is checked on non-nullity with an else statement`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                a != null -> println(2 + a)
                                else -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does report when the parameter chained call does not handle null`() {
                    val code = """
                        $COMMON_CHAIN_CODE
                        fun foo(a: RandomBar?) {
                            when {
                                a?.nullId?.length == 0 -> println("doesn't handle when a is null")
                                a?.nonNullId?.nullable() != null -> println("does not handle a is null case")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does report when parameter is not checked for nullability in boolean conditions`() {
                    val code = """
                        class A {
                            operator fun not() = this
                        }
                        
                        var c = 100
                        fun test(a: A, b: A?) {
                            when {
                                b != null && !b != a -> {
                                    println("doesn't handle null")
                                }
                                (b != null) && !b != a -> {
                                    println("doesn't handle null")
                                }
                                (c > 10 || (b != null && !b != a)) -> {
                                    println("doesn't handle null")
                                }
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does not report when parameter handles for nullability with not operator`() {
                    val code = """
                        operator fun A?.not() = this
                        class A
                        
                        fun test(a: A, b: A?) {
                            when {
                                !b != a -> {
                                    println("handles b is null/non-null case")
                                }
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when parameter handles for null with get operator`() {
                    val code = """
                        operator fun A?.get(key: String) = this
                        class A
                        
                        fun test(a: A, b: A?) {
                            when {
                                b[""] != a -> {
                                    println("handles b is null/non-null case")
                                }
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report on nullable type matching`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                a !is Int -> println("a is null")
                            }
                        }
                        
                        fun fizz(b: Int?) {
                            when {
                                b is Int? -> println("b is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does report on non-null type matching`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                a is Int -> println(2 + a)
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does report on non-null type matching with multiple clauses`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                a is Int && a % 2 == 0 -> println(2 + a)
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does not report on non-null type matching with an else statement`() {
                    val code = """
                        fun foo(a: Int?) {
                            when {
                                a is Int -> println(2 + a)
                                else -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }
            }

            @Nested
            inner class `with a subject` {

                @Suppress("LongMethod")
                @Test
                fun `does not report for #5629`() {
                    val code = """
                        import kotlinx.coroutines.CoroutineScope
                        import kotlinx.coroutines.Dispatchers
                        import kotlinx.coroutines.channels.BufferOverflow
                        import kotlinx.coroutines.delay
                        import kotlinx.coroutines.flow.Flow
                        import kotlinx.coroutines.flow.MutableSharedFlow
                        import kotlinx.coroutines.launch
                        import kotlinx.coroutines.withContext
                        import kotlin.random.Random
                        
                        suspend inline fun log(msg: () -> String) {
                          delay(1_000)
                          println(msg())
                        }

                        @Suppress("MayBeConstant") 
                        val SHARED_FLOW_DEFAULT_BUFFER = 64
                        
                        fun <T> bufferedSharedFlow(
                          capacity: Int = SHARED_FLOW_DEFAULT_BUFFER,
                          onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
                        ) =
                          MutableSharedFlow<T>(extraBufferCapacity = capacity, onBufferOverflow = onBufferOverflow)
                        
                        sealed class BarType {
                          object One : BarType()
                          object Two : BarType()
                        }
                        
                        class Bar {
                          val type
                            get() = when(System.currentTimeMillis()) {
                              0L -> BarType.One
                              else -> BarType.Two
                            }
                        }
                        
                        internal interface BarHelper {
                          suspend fun findNextBar(targetBar: Bar? = null): Bar?
                        }
                        
                        internal class BarHelperImpl : BarHelper {
                          override suspend fun findNextBar(targetBar: Bar?): Bar? {
                            val bars = listOf(Bar(), Bar())
                            val nextBar =
                              bars
                                .find { bar ->
                                  bar.type is BarType.One && when(val type = bar.type) {
                                    is BarType.One -> withContext(Dispatchers.IO) {
                                      type == BarType.One
                                    }
                        
                                    else -> true
                                  }
                                }
                        
                            val isRandomTrue = Random.nextBoolean()
                        
                            return when {
                              isRandomTrue -> nextBar
                        
                              else -> targetBar ?: nextBar
                            }
                          }
                        }
                        
                        internal interface Foo {
                          suspend fun foo(bar: Bar? = null): Bar?
                        }
                        
                        internal interface Baz {
                          fun baz(): Flow<Bar?>
                        }
                        
                        internal class BazImpl : Baz {
                          private val b = bufferedSharedFlow<Bar?>(capacity = 1)
                        
                          override fun baz() = b
                        }
                        
                        internal interface Bang
                        
                        internal class BangImpl(
                          private val baz: Baz,
                          private val barHelper: BarHelper,
                          private val scope: CoroutineScope
                        ) {
                          fun start() {
                            with(scope) {
                              launch {
                                bang()
                              }
                            }
                          }
                        
                          private fun CoroutineScope.bang() {
                            launch {
                              baz
                                .baz()
                                .collect { bar ->
                                  log { "Bang (bar=${'$'}bar)" }
                        
                                  handleBar(barHelper.findNextBar(bar))
                                }
                            }
                          }
                        
                          private suspend fun handleBar(bar: Bar?) { // error is on this line
                            when(bar?.type) {
                              BarType.One -> log { "One" }
                              BarType.Two -> log { "Two" }
                              null -> log { "Null" }
                            }
                          }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when the parameter is checked on nullity`() {
                    val code = """
                        fun foo(a: Int?) {
                            when (a) {
                                null -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when else handles null case with non null property`() {
                    val code = """
                        $COMMON_CHAIN_CODE
                        fun foo(a: RandomBar?) {
                            when(a?.nonNullId) {
                                is String -> println("")
                                else -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report when else handles null case with nullable property`() {
                    val code = """
                        $COMMON_CHAIN_CODE
                        fun foo(a: RandomBar?) {
                            when(a?.nullId) {
                                is String -> println("")
                                else -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does report when null case is not handled in chained call`() {
                    val code = """
                        $COMMON_CHAIN_CODE
                        fun foo(a: RandomBar?) {
                            when(a?.nullId?.nullable()) {
                                is String -> println("")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does not report when null case is handled in chained call`() {
                    val code = """
                        $COMMON_CHAIN_CODE
                        fun foo(a: RandomBar?) {
                            when(a?.nullId?.nullable()) {
                                null -> println("")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does report when null case is handled in unsafe non-nullable chained call`() {
                    val code = """
                        $COMMON_CHAIN_CODE
                        fun foo(a: RandomBar?) {
                            when(a!!.nonNullId.nullable()) {
                                null -> println("")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does report when null case is handled in unsafe nullable chained call`() {
                    val code = """
                        $COMMON_CHAIN_CODE
                        fun foo(a: RandomBar?) {
                            when(a!!.nullId?.nullable()) {
                                null -> println("")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does not report when prefix operator takes null`() {
                    val code = """
                        operator fun A?.not() = false
                        class A
                        fun foo(a: A?) {
                            when(!a) {
                                true -> println("handles a is null/non-null case")
                                false -> println("handles a is null/non-null case")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does report when suffix operator returns null and null is not handled`() {
                    val code = """
                        operator fun A?.get(key: String) = this
                        class A
                        fun foo(a: A?) {
                            when(a[""]) {
                                is A -> println("handles a is non-null case")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does not report when suffix operator returns null and null is handled`() {
                    val code = """
                        operator fun A?.get(key: String) = this
                        class A
                        fun foo(a: A?) {
                            when(a[""]) {
                                is A -> println("handles only a is non-null case")
                                null -> println("handles only a is non-null case")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report on nullable type matching`() {
                    val code = """
                        fun foo(a: Int?) {
                            when (a) {
                                !is Int -> println("a is null")
                            }
                        }
                        
                        fun fizz(b: Int?) {
                            when (b) {
                                is Int? -> println("b is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does report on non-null type matching`() {
                    val code = """
                        fun foo(a: Int?) {
                            when(a) {
                                is Int -> println(2 + a)
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does not report on non-null type matching with an else statement`() {
                    val code = """
                        fun foo(a: Int?) {
                            when(a) {
                                is Int -> println(2 + a)
                                else -> println("a is null")
                            }
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }
            }
        }

        @Nested
        inner class `if-statements` {
            @Test
            fun `does not report when the parameter is checked on nullity`() {
                val code = """
                    fun foo(a: Int?) {
                        if (a == null) {
                            println("'a' is null")
                        }
                    }
                    
                    fun fizz(a: Int?) {
                        if (null == a) {
                            println("'a' is null")
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report when the if-check is in the else statement`() {
                val code = """
                    fun foo(num: Int, a: Int?) {
                        if (num % 2 == 0) {
                            println("'num' is even")
                        } else if (a == null) {
                            println("'a' is null")
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does report when the parameter is only checked on non-nullity in a function`() {
                val code = """
                    fun foo(a: Int?) {
                        if (a != null) {
                            println(a + 5)
                        }
                    }
                    
                    fun fizz(a: Int?) {
                        if (null != a) {
                            println(a + 5)
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
            }

            @Test
            fun `does report when the parameter is only checked on non-nullity with multiple clauses`() {
                val code = """
                    fun foo(a: Int?, other: Int) {
                        if (a != null && other % 2 == 0) {
                            println(a + 5)
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does report null-check returning unit type`() {
                val code = """
                    fun foo(a: Int?) {
                        if (a == null) return
                        println(a)
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does report null-check returning unit type in block`() {
                val code = """
                    fun foo(a: Int?) {
                        if (a == null) { return }
                        println(a)
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does not report guard statement with side effect ahead`() {
                val code = """
                    fun foo(a: Int?) {
                        println("side effect")
                        if (a == null) return
                        println(a)
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report null-check returning non-unit type`() {
                val code = """
                    fun foo(a: Int?): Int {
                        if (a == null) return 0
                        println(a)
                        return a
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report when the parameter is checked on non-nullity with an else statement`() {
                val code = """
                    fun foo(a: Int?) {
                        if (a != null) {
                            println(a + 5)
                        } else {
                            println(5)
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report when there are other expressions after the non-null check`() {
                val code = """
                    fun foo(a: Int?) {
                        if (a != null) {
                            println(a + 5)
                        }
                        val b = 5 + 2
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }
    }

    companion object {
        @Language("kotlin")
        private const val COMMON_CHAIN_CODE = """
            /**
            * Helper function which take non null receiver of type [M] and returns nullable type [M]
            */
            fun <M : Any> M.nullable(): M? = if (System.currentTimeMillis() % 2 == 0L) this else null

            /**
            * Helper function which takes nullable receiver of type [M] and return non null [M]
            */
            inline fun <reified M : Any> M?.nonNullable(): M 
                = this ?: M::class.java.getConstructor().newInstance()
            class RandomBar {
                val nonNullId
                    get() = System.currentTimeMillis().toString()
                val nullId
                    get() = if (System.currentTimeMillis() % 2 == 0L) System.currentTimeMillis().toString() else null
            }
        """
    }
}

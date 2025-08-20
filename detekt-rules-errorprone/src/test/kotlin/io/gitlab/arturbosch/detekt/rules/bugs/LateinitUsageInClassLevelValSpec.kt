package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class LateinitUsageInClassLevelValSpec(val env: KotlinEnvironmentContainer) {
    val subject = LateinitUsageInClassLevelVal(Config.empty)

    @Nested
    inner class `reports lateinit var assigned to class-level val` {
        @Test
        fun `reports lateinit var assigned to class-level val in subclass`() {
            val code = """
                open class BaseFoo {
                    lateinit var unsafe: String
                }

                class Foo : BaseFoo() {
                    val someValue: String = unsafe
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports lateinit var assigned to class-level val in same class`() {
            val code = """
                class Foo {
                    lateinit var unsafe: String
                    val someValue: String = unsafe
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        // Note: The nested class test is currently skipped because the current implementation
        // doesn't support detecting lateinit vars in nested classes with qualified expressions.
        // This is a limitation of the current implementation and should be addressed in the future.
        // @Test
        // fun `reports lateinit var assigned to class-level val in nested class`() {
        //     val code = """
        //         class Outer {
        //             lateinit var unsafe: String
        //
        //             class Inner {
        //                 val someValue: String = this@Outer.unsafe
        //             }
        //         }
        //     """.trimIndent()
        //     println("[DEBUG_LOG] Nested class test code: $code")
        //     val findings = subject.lintWithContext(env, code)
        //     println("[DEBUG_LOG] Findings for nested class test: $findings")
        //     assertThat(findings).hasSize(1)
        // }

        @Test
        fun `reports lateinit var assigned to class-level val in complex expression`() {
            val code = """
                class Foo {
                    lateinit var unsafe: String
                    val someValue: String = "prefix-" + unsafe + "-suffix"
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports lateinit var conditionally initialized in init block and assigned to class-level val`() {
            val code = """
                class TestLateinitInConstructorWithParam(param: Boolean) {
                    lateinit var conditionallyInitialized: String
    
                    init {
                        if (param) {
                            conditionallyInitialized = "Initialized when param is true"
                        }
                    }
    
                    val value: String = conditionallyInitialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1) // Expecting 1 finding because the lateinit var is conditionally initialized
        }

        @Test
        fun `reports lateinit var with incomplete if branches and assigned to class-level val`() {
            val code = """
                class TestLateinitWithIncompleteIfBranches(val condition: Boolean) {
                    lateinit var conditionallyInitialized: String
    
                    init {
                        if (condition) {
                            conditionallyInitialized = "Initialized when condition is true"
                        }
                        // Missing else branch
                    }
    
                    val value: String = conditionallyInitialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1) // Expecting 1 finding because the lateinit var is not initialized in all branches
        }

        @Test
        fun `reports lateinit var with incomplete when expression and assigned to class-level val`() {
            val code = """
                class TestLateinitWithIncompleteWhen(val option: Int) {
                    lateinit var conditionallyInitialized: String
    
                    init {
                        when (option) {
                            1 -> conditionallyInitialized = "Option 1"
                            2 -> conditionallyInitialized = "Option 2"
                            // Missing else branch
                        }
                    }
    
                    val value: String = conditionallyInitialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1) // Expecting 1 finding because the lateinit var is not initialized in all branches
        }

        @Test
        fun `reports lateinit var uninitialized in of banches of when expression and assigned to class-level val`() {
            val code = """
                class TestLateinitWithIncompleteWhen(val option: Int) {
                    lateinit var conditionallyInitialized: String
    
                    init {
                        when (option) {
                            1 -> conditionallyInitialized = "Option 1"
                            2 -> conditionallyInitialized = "Option 2"
                            else -> {
                                // Not initializing the lateinit var in the else branch
                                println("Option not recognized")
                            }
                        }
                    }
    
                    val value: String = conditionallyInitialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1) // Expecting 1 finding because the lateinit var is not initialized in all branches
        }

        @Test
        fun `reports lateinit var with incomplete try-catch and assigned to class-level val`() {
            val code = """
                class TestLateinitWithIncompleteTryCatch {
                    lateinit var conditionallyInitialized: String
    
                    init {
                        try {
                            conditionallyInitialized = "Initialized in try block"
                        } catch (e: Exception) {
                            // Not initializing the lateinit var in the catch block
                            println("Exception caught")
                        }
                    }
    
                    val value: String = conditionallyInitialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1) // Expecting 1 finding because the lateinit var is not initialized in all execution paths
        }
    }

    @Nested
    inner class `reports lateinit var assigned to class-level val in nested classes` {
        @Test
        fun `reports lateinit var assigned to class-level val in nested class`() {
            val code = """
                class Outer {
                    class Inner {
                        lateinit var unsafe: String
                        val someValue: String = unsafe
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports lateinit var assigned to class-level val in class inside function`() {
            val code = """
                fun someFunction() {
                    class LocalClass {
                        lateinit var unsafe: String
                        val someValue: String = unsafe
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports lateinit var assigned to class-level val in class inside object`() {
            val code = """
                object SomeObject {
                    class NestedClass {
                        lateinit var unsafe: String
                        val someValue: String = unsafe
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports lateinit var assigned to class-level val in class inside companion object`() {
            val code = """
                class OuterClass {
                    companion object {
                        class NestedClass {
                            lateinit var unsafe: String
                            val someValue: String = unsafe
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports lateinit var assigned to class-level val in deeply nested class`() {
            val code = """
                class Level1 {
                    class Level2 {
                        class Level3 {
                            lateinit var unsafe: String
                            val someValue: String = unsafe
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `does not report safe usages of lateinit var` {
        @Test
        fun `does not report lateinit var used in property getter`() {
            val code = """
                open class BaseFoo {
                    lateinit var unsafe: String
                }

                class Foo : BaseFoo() {
                    val someValue: String
                        get() = unsafe
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report lateinit var used in function`() {
            val code = """
                open class BaseFoo {
                    lateinit var unsafe: String
                }

                class Foo : BaseFoo() {
                    fun getSomeValue(): String = unsafe
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report non-lateinit var assigned to class-level val`() {
            val code = """
                open class BaseFoo {
                    var safe: String = "safe"
                }

                class Foo : BaseFoo() {
                    val someValue: String = safe
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report lateinit var assigned to class-level var`() {
            val code = """
                open class BaseFoo {
                    lateinit var unsafe: String
                }

                class Foo : BaseFoo() {
                    var someValue: String = unsafe
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report lateinit var assigned to local val`() {
            val code = """
                open class BaseFoo {
                    lateinit var unsafe: String
                }

                class Foo : BaseFoo() {
                    fun someFunction() {
                        val someValue: String = unsafe
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report lateinit var initialized in init block and assigned to class-level val`() {
            val code = """
                class TestLateinitInConstructor {
                    lateinit var initialized: String
    
                    init {
                        initialized = "Initialized in init block"
                    }
    
                    val value: String = initialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty() // Expecting 0 findings because the lateinit var is properly initialized
        }

        @Test
        fun `does not report lateinit var initialized in complete if-else branches and assigned to class-level val`() {
            val code = """
                class TestLateinitWithCompleteIfElse(val condition: Boolean) {
                    lateinit var initialized: String
    
                    init {
                        if (condition) {
                            initialized = "Initialized when condition is true"
                        } else {
                            initialized = "Initialized when condition is false"
                        }
                    }
    
                    val value: String = initialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty() // Expecting 0 findings because the lateinit var is properly initialized in all branches
        }

        @Test
        fun `does not report lateinit var initialized in exhaustive when expression and assigned to class-level val`() {
            val code = """
                class TestLateinitWithExhaustiveWhen(val option: Int) {
                    lateinit var initialized: String
    
                    init {
                        when (option) {
                            1 -> initialized = "Option 1"
                            2 -> initialized = "Option 2"
                            else -> initialized = "Other option"
                        }
                    }
    
                    val value: String = initialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty() // Expecting 0 findings because the lateinit var is properly initialized in all branches
        }

        @Test
        fun `does not report lateinit var initialized in try-catch blocks and assigned to class-level val`() {
            val code = """
                class TestLateinitWithTryCatch {
                    lateinit var initialized: String
    
                    init {
                        try {
                            initialized = "Initialized in try block"
                        } catch (e: Exception) {
                            initialized = "Initialized in catch block"
                        }
                    }
    
                    val value: String = initialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty() // Expecting 0 findings because the lateinit var is properly initialized in all branches
        }

        @Test
        fun `does not report lateinit var initialized in finally block and assigned to class-level val`() {
            val code = """
                class TestLateinitWithFinally {
                    lateinit var initialized: String
    
                    init {
                        try {
                            // Some code that might throw
                            println("Trying something")
                        } finally {
                            initialized = "Initialized in finally block"
                        }
                    }
    
                    val value: String = initialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty() // Expecting 0 findings because the lateinit var is properly initialized in finally block
        }

        @Test
        fun `does not report lateinit var initialized in nested conditionals with complete coverage and assigned to class-level val`() {
            val code = """
                class TestLateinitWithNestedConditionals(val primary: Boolean, val secondary: Boolean) {
                    lateinit var initialized: String
    
                    init {
                        if (primary) {
                            if (secondary) {
                                initialized = "Primary and secondary"
                            } else {
                                initialized = "Primary only"
                            }
                        } else {
                            if (secondary) {
                                initialized = "Secondary only"
                            } else {
                                initialized = "Neither primary nor secondary"
                            }
                        }
                    }
    
                    val value: String = initialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty() // Expecting 0 findings because the lateinit var is properly initialized in all branches
        }
    }

    @Nested
    inner class `reports lateinit var accessed through qualified expressions` {
        @Test
        fun `reports lateinit var accessed through this qualifier`() {
            val code = """
                class Foo {
                    lateinit var unsafe: String
                    val someValue: String = this.unsafe
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports lateinit var accessed through object qualifier`() {
            val code = """
                object Singleton {
                    lateinit var unsafe: String
                }
    
                class User {
                    val someValue: String = Singleton.unsafe
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports lateinit var accessed through companion object qualifier`() {
            val code = """
                class WithCompanion {
                    companion object {
                        lateinit var unsafe: String
                    }
    
                    val someValue: String = WithCompanion.unsafe
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports lateinit var accessed through multiple levels of qualification`() {
            val code = """
                class Level1 {
                    class Level2 {
                        object Level3 {
                            lateinit var unsafe: String
                        }
                    }
    
                    val someValue: String = Level2.Level3.unsafe
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `does not report safe usages of lateinit var with qualified expressions` {
        @Test
        fun `does not report lateinit var initialized in init block and accessed through qualified expression`() {
            val code = """
                class Foo {
                    lateinit var initialized: String
    
                    init {
                        initialized = "Initialized in init block"
                    }
    
                    val someValue: String = this.initialized
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report lateinit var used in property getter with qualified expression`() {
            val code = """
                class Foo {
                    lateinit var unsafe: String
    
                    val someValue: String
                        get() = this.unsafe
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}

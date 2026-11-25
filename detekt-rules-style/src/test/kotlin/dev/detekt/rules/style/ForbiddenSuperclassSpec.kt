package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.ValueWithReason
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.toConfig
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val PARENT_CLASSES = "parentClasses"

@KotlinCoreEnvironmentTest
class ForbiddenSuperclassSpec(private val env: KotlinEnvironmentContainer) {

    @Nested
    inner class `parent classes configured via config` {

        private val subject = ForbiddenSuperclass(
            TestConfig(PARENT_CLASSES to listOf("com.example.ViewModel"))
        )

        @Test
        fun `reports a class extending the forbidden class via import`() {
            val code = """
                import com.example.ViewModel

                class CustomViewModel : ViewModel()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)
            assertThat(findings).hasSize(1)
            assertThat(findings.first()).hasStartSourceLocation(line = 3, column = 7)
        }

        @Test
        fun `reports a class extending the forbidden class via fully qualified name`() {
            val code = "class CustomViewModel : com.example.ViewModel()"
            val findings = subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a subclass extending a forbidden class transitively`() {
            val code = """
                import com.example.ViewModel

                open class BaseViewModel : ViewModel()

                class CustomViewModel : BaseViewModel()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)
            // BaseViewModel extends the forbidden parent directly, CustomViewModel inherits it transitively
            assertThat(findings).hasSize(2)
            assertThat(findings.last()).hasTextLocation("CustomViewModel")
        }

        @Test
        fun `reports a subclass extending a forbidden class transitively over two levels`() {
            val code = """
                import com.example.ViewModel

                open class FirstLevel : ViewModel()

                open class SecondLevel : FirstLevel()

                class ThirdLevel : SecondLevel()
            """.trimIndent()
            // every class in the chain extends the forbidden parent transitively
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).hasSize(3)
        }

        @Test
        fun `does not report a class extending an allowed class`() {
            val code = """
                open class AllowedBase

                class CustomViewModel : AllowedBase()
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).isEmpty()
        }

        @Test
        fun `does not report a class with no parent`() {
            val code = "class Plain"
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).isEmpty()
        }

        @Test
        fun `does not report the forbidden class itself`() {
            val code = """
                package com.example

                open class ViewModel
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports a subclass extending the forbidden class through a typealias`() {
            val code = """
                import com.example.ViewModel

                typealias VM = ViewModel

                open class Base : VM()

                class Custom : Base()
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).hasSize(2)
        }

        @Test
        fun `reports multiple offenders in a file`() {
            val code = """
                import com.example.ViewModel

                class First : ViewModel()

                class Second : ViewModel()
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).hasSize(2)
        }
    }

    @Nested
    inner class `interfaces configured as parents` {

        private val subject = ForbiddenSuperclass(
            TestConfig(PARENT_CLASSES to listOf("com.example.ForbiddenInterface"))
        )

        @Test
        fun `reports a class implementing the forbidden interface`() {
            val code = """
                import com.example.ForbiddenInterface

                class Custom : ForbiddenInterface
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).hasSize(1)
        }

        @Test
        fun `reports a class implementing the forbidden interface together with a superclass`() {
            val code = """
                import com.example.ForbiddenInterface

                open class Base

                class Custom : Base(), ForbiddenInterface
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).hasSize(1)
        }

        @Test
        fun `interface declarations themselves are not reported`() {
            val code = """
                package com.example

                interface ForbiddenInterface
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `glob patterns` {

        private val subject = ForbiddenSuperclass(
            TestConfig(PARENT_CLASSES to listOf("com.example.internal.*", "com.example.?bservable"))
        )

        @Test
        fun `reports a subclass matching a wildcard pattern`() {
            val code = "class Custom : com.example.internal.BaseTool()"
            assertThat(
                subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY, INTERNAL_PARENT_DEPENDENCY)
            ).hasSize(1)
        }

        @Test
        fun `does not report a subclass outside the wildcard pattern`() {
            val code = "class Custom : com.example.api.BaseTool()"
            assertThat(
                subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY, API_PARENT_DEPENDENCY)
            ).isEmpty()
        }

        @Test
        fun `reports a subclass matching a single character wildcard`() {
            val code = "class Custom : com.example.Observable()"
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).hasSize(1)
        }
    }

    @Nested
    inner class reasons {

        private val subject = ForbiddenSuperclass(
            TestConfig(
                PARENT_CLASSES to listOf(
                    ValueWithReason("com.example.ViewModel", "Use the custom BaseViewModel of this project instead.")
                        .toConfig(),
                    "com.example.PlainParent",
                )
            )
        )

        @Test
        fun `includes the configured reason in the finding`() {
            val code = "class Custom : com.example.ViewModel()"
            val findings = subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)
            assertThat(findings.first()).hasMessage(
                "The parent class `com.example.ViewModel` has been forbidden: " +
                    "Use the custom BaseViewModel of this project instead."
            )
        }

        @Test
        fun `includes the default reason when no reason is configured`() {
            val code = "class Custom : com.example.PlainParent()"
            val findings = subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)
            assertThat(findings.first()).hasMessage(
                "The parent class `com.example.PlainParent` has been forbidden in the detekt config."
            )
        }
    }

    @Nested
    inner class `non matching declarations` {

        @Test
        fun `does not report object declarations`() {
            val subject = ForbiddenSuperclass(
                TestConfig(PARENT_CLASSES to listOf("com.example.ViewModel"))
            )
            val code = "object Singleton"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports nothing when the rule has an empty configuration`() {
            val subject = ForbiddenSuperclass(Config.empty)
            val code = "class Custom : java.lang.Thread()"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class suppression {

        @Test
        fun `suppressed class is not reported`() {
            val subject = ForbiddenSuperclass(
                TestConfig(PARENT_CLASSES to listOf("com.example.ViewModel"))
            )
            val code = """
                @Suppress("ForbiddenSuperclass")
                class Custom : com.example.ViewModel()
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, FORBIDDEN_PARENT_DEPENDENCY)).isEmpty()
        }
    }

    private companion object {
        val FORBIDDEN_PARENT_DEPENDENCY = """
            package com.example

            open class ViewModel

            interface ForbiddenInterface

            open class Observable

            open class PlainParent
        """.trimIndent()

        val INTERNAL_PARENT_DEPENDENCY = """
            package com.example.internal

            open class BaseTool
        """.trimIndent()

        val API_PARENT_DEPENDENCY = """
            package com.example.api

            open class BaseTool
        """.trimIndent()
    }
}

package dev.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class DeprecationSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = Deprecation(Config.empty)

    @Test
    fun `reports when supertype is deprecated`() {
        val code = """
            @Deprecated("deprecation message")
            abstract class Foo {
                abstract fun bar() : Int
            
                fun baz() {
                }
            }
            
            abstract class Oof : Foo() {
                fun spam() {
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("""Foo is deprecated with message "deprecation message"""")
    }

    @Test
    fun `does not report when supertype is not deprecated`() {
        val code = """
            abstract class Oof : Foo() {
                fun spam() {
                }
            }
            abstract class Foo {
                abstract fun bar() : Int
            
                fun baz() {
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `report when property delegate is deprecated`() {
        val stateFile = """
            package state

            import kotlin.reflect.KProperty

            interface State {
                val value: Double
            }

            @Deprecated("Some reason")
            operator fun State.getValue(thisObj: Any?, property: KProperty<*>): Double = value
        """.trimIndent()
        val code = """
            import state.State
            import state.getValue
            fun foo(state: State) {
                val d by state
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, stateFile)).singleElement()
            .hasMessage("""state is deprecated with message "Some reason"""")
    }

    @Test
    fun `does report import location when excludeImportStatements has default value`() {
        val deprecatedFile = """
            package com.example.detekt.featureflag

            @Deprecated("Moved to other service")
            enum class LegacyFeatureFlags {
                FEATURE_A, FEATURE_B,
            }
        """.trimIndent()
        val code = """
            import com.example.detekt.featureflag.LegacyFeatureFlags
            class FeatureFlagManager {
                @Suppress("DEPRECATION")
                fun getFeatureFlagValue(featureFlag: LegacyFeatureFlags): Boolean {
                    return true
                }
            }

            class Manager(private val featureFlagManager: FeatureFlagManager) {
                fun doSomething() {
                    @Suppress("DEPRECATION")
                    val isFeatureAEnabled =
                        featureFlagManager.getFeatureFlagValue(LegacyFeatureFlags.FEATURE_A)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, deprecatedFile)).singleElement()
            .hasStartSourceLocation(1, 1)
    }

    @Nested
    inner class `With ignore import true` {
        private val ignoredImportSubject =
            Deprecation(TestConfig(IGNORED_IMPORT to true))

        @Test
        fun `does not report import location - #7402`() {
            val deprecatedFile = """
                package com.example.detekt.featureflag

                @Deprecated("Moved to other service")
                enum class LegacyFeatureFlags {
                    FEATURE_A, FEATURE_B,
                }
            """.trimIndent()
            val code = """
                import com.example.detekt.featureflag.LegacyFeatureFlags
                class FeatureFlagManager {
                    @Suppress("DEPRECATION")
                    fun getFeatureFlagValue(featureFlag: LegacyFeatureFlags): Boolean {
                        return true
                    }
                }

                class Manager(private val featureFlagManager: FeatureFlagManager) {
                    fun doSomething() {
                        @Suppress("DEPRECATION")
                        val isFeatureAEnabled =
                            featureFlagManager.getFeatureFlagValue(LegacyFeatureFlags.FEATURE_A)
                    }
                }
            """.trimIndent()
            assertThat(ignoredImportSubject.lintWithContext(env, code, deprecatedFile))
                .isEmpty()
        }

        @Test
        fun `report when property delegate is deprecated`() {
            val stateFile = """
                package state
                import kotlin.reflect.KProperty
                interface State {
                    val value: Double
                }
                @Deprecated("Some reason")
                operator fun State.getValue(thisObj: Any?, property: KProperty<*>): Double = value
            """.trimIndent()
            val code = """
                import state.State
                import state.getValue
                fun foo(state: State) {
                    val d by state
                }
            """.trimIndent()
            assertThat(ignoredImportSubject.lintWithContext(env, code, stateFile)).singleElement()
                .hasMessage("""state is deprecated with message "Some reason"""")
        }
    }

    companion object {
        private const val IGNORED_IMPORT = "excludeImportStatements"
    }
}

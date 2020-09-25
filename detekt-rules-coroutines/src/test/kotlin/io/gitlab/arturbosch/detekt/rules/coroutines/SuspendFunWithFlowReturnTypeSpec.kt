package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SuspendFunWithFlowReturnTypeSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { SuspendFunWithFlowReturnType(Config.empty) }

    describe("SuspendFunWithFlowReturn") {

        it("reports when top-level suspend function has explicit Flow return type") {
            val code = """
                import kotlinx.coroutines.flow.Flow
                import kotlinx.coroutines.flow.flowOf
                import kotlinx.coroutines.flow.MutableStateFlow
                import kotlinx.coroutines.flow.StateFlow
                import kotlinx.coroutines.yield

                suspend fun flowValues(): Flow<Long> {
                    yield()
                    return flowOf(1L, 2L, 3L)
                }

                suspend fun stateFlowValues(): StateFlow<Long> {
                    yield()
                    return MutableStateFlow(value = 1L)
                }

                suspend fun mutableStateFlowValues(): MutableStateFlow<Long> {
                    yield()
                    return MutableStateFlow(value = 1L)
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        it("reports when top-level suspend function has explicit Flow return type and star import used") {
            val code = """
                import kotlinx.coroutines.flow.*
                import kotlinx.coroutines.yield

                suspend fun flowValues(): Flow<Long> {
                    yield()
                    return flowOf(1L, 2L, 3L)
                }

                suspend fun stateFlowValues(): StateFlow<Long> {
                    yield()
                    return MutableStateFlow(value = 1L)
                }

                suspend fun mutableStateFlowValues(): MutableStateFlow<Long> {
                    yield()
                    return MutableStateFlow(value = 1L)
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        it("reports when top-level suspend function has explicit FQN Flow return type") {
            val code = """
                import kotlinx.coroutines.yield

                suspend fun flowValues(): kotlinx.coroutines.flow.Flow<Long> {
                    yield()
                    return kotlinx.coroutines.flow.flowOf(1L, 2L, 3L)
                }

                suspend fun stateFlowValues(): kotlinx.coroutines.flow.StateFlow<Long> {
                    yield()
                    return kotlinx.coroutines.flow.MutableStateFlow(value = 1L)
                }

                suspend fun mutableStateFlowValues(): kotlinx.coroutines.flow.MutableStateFlow<Long> {
                    yield()
                    return kotlinx.coroutines.flow.MutableStateFlow(value = 1L)
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        it("reports when top-level suspend function has implicit Flow return type") {
            val code = """
                import kotlinx.coroutines.flow.flowOf
                import kotlinx.coroutines.flow.MutableStateFlow

                suspend fun flowValues() = flowOf(1L, 2L, 3L)
                suspend fun mutableStateFlowValues() = MutableStateFlow(value = 1L)
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        it("reports when interface suspend function has explicit Flow return type") {
            val code = """
                import kotlinx.coroutines.flow.Flow
                import kotlinx.coroutines.flow.MutableStateFlow
                import kotlinx.coroutines.flow.StateFlow

                interface ValuesRepository {
                    suspend fun flowValues(): Flow<Long>
                    suspend fun stateFlowValues(): StateFlow<Long>
                    suspend fun mutableStateFlowValues(): MutableStateFlow<Long>
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        it("reports when class suspend function has explicit Flow return type") {
            val code = """
                import kotlinx.coroutines.flow.Flow
                import kotlinx.coroutines.flow.flowOf
                import kotlinx.coroutines.flow.MutableStateFlow
                import kotlinx.coroutines.flow.StateFlow
                import kotlinx.coroutines.yield

                class ValuesRepository {
                    suspend fun flowValues(): Flow<Long> {
                        yield()
                        return flowOf(1L, 2L, 3L)
                    }

                    suspend fun stateFlowValues(): StateFlow<Long> {
                        yield()
                        return MutableStateFlow(value = 1L)
                    }

                    suspend fun mutableStateFlowValues(): MutableStateFlow<Long> {
                        yield()
                        return MutableStateFlow(value = 1L)
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        it("reports when class suspend function has implicit Flow return type") {
            val code = """
                import kotlinx.coroutines.flow.flowOf
                import kotlinx.coroutines.flow.MutableStateFlow

                class ValuesRepository {
                    suspend fun flowValues() = flowOf(1L, 2L, 3L)
                    suspend fun mutableStateFlowValues() = MutableStateFlow(value = 1L)
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        it("reports when suspend extension function has explicit Flow return type") {
            val code = """
                import kotlinx.coroutines.flow.asFlow
                import kotlinx.coroutines.flow.Flow
                import kotlinx.coroutines.flow.MutableStateFlow
                import kotlinx.coroutines.flow.StateFlow
                import kotlinx.coroutines.yield

                suspend fun Long.flowValues(): Flow<Long> {
                    yield()
                    return (0..this).asFlow()
                }

                suspend fun Long.stateFlowValues(): StateFlow<Long> {
                    yield()
                    return MutableStateFlow(value = this)
                }

                suspend fun Long.mutableStateFlowValues(): MutableStateFlow<Long> {
                    yield()
                    return MutableStateFlow(value = this)
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        it("reports when suspend extension function has implicit Flow return type") {
            val code = """
                import kotlinx.coroutines.flow.asFlow
                import kotlinx.coroutines.flow.MutableStateFlow

                suspend fun Long.flowValues() = (0..this).asFlow()
                suspend fun Long.mutableStateFlowValues() = MutableStateFlow(value = this)
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        it("does not report when suspend lambda has Flow return type") {
            val code = """
                import kotlinx.coroutines.flow.Flow
                import kotlinx.coroutines.flow.MutableStateFlow
                import kotlinx.coroutines.flow.StateFlow

                fun doSomething1(block: suspend () -> Flow<Long>) {
                    TODO()
                }
                fun doSomething2(block: suspend () -> StateFlow<Long>) {
                    TODO()
                }

                fun doSomething3(block: suspend () -> MutableStateFlow<Long>) {
                    TODO()
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report when suspend functions have non-Flow return types") {
            val code = """
                import kotlinx.coroutines.delay

                suspend fun delayValue(value: Long): Long {
                    delay(1_000L)
                    return value
                }

                suspend fun delayValue2(value: Long) = value.apply { delay(1_000L) }

                suspend fun Long.delayValue(): Long {
                    delay(1_000L)
                    return this
                }

                suspend fun Long.delayValue2() = this.apply { delay(1_000L) }

                interface ValueRepository {
                    suspend fun getValue(): Long
                }

                class ValueRepository2 {
                    suspend fun getValue(): Long {
                        delay(1_000L)
                        return 5L
                    }
                }

                class ValueRepository3 {
                    suspend fun getValue() = 5L.apply { delay(1_000L) }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report when non-suspend functions have Flow return types") {
            val code = """
                import kotlinx.coroutines.flow.flowOf
                import kotlinx.coroutines.flow.Flow
                import kotlinx.coroutines.flow.MutableStateFlow
                import kotlinx.coroutines.flow.StateFlow

                fun flowValues(): Flow<Long> {
                    return flowOf(1L, 2L, 3L)
                }

                fun stateFlowValues(): StateFlow<Long> {
                    return MutableStateFlow(value = 1L)
                }

                fun mutableStateFlowValues(): MutableStateFlow<Long> {
                    return MutableStateFlow(value = 1L)
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})

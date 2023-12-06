package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CoroutineLaunchedInTestWithoutRunTestSpec(val env: KotlinCoreEnvironment) {

    private val subject = CoroutineLaunchedInTestWithoutRunTest(Config.empty)

    @Test
    fun `reports when coroutine is launched in test without a runTest block`() {
        val code = """
            import org.junit.Test
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch

            class A {
                @Test
                fun `test that launches a coroutine`() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when coroutine is launched in test with a runBlocking block`() {
        val code = """
            import org.junit.Test
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking

            class A {
                @Test
                fun `test that launches a coroutine`() = runBlocking {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.launch {
                        throw Exception()
                    }
                    
                    Unit
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    /**
    @Test
    fun `reports when coroutine is launched in test with a runBlocking block in another function`() {
        val code = """
            import org.junit.Test
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking

            class A {
                @Test
                fun `test that launches a coroutine`() = runBlocking {
                    launchCoroutine()
                }
                
                fun launchCoroutine() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }
    **/

    @Test
    fun `no reports when coroutine is launched not in a test with a runBlocking block`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking

            class A {
                fun `test that launches a coroutine`() = runBlocking {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.launch {
                        throw Exception()
                    }
                    
                    Unit
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `no reports when coroutine is launched in test with a runTest block`() {
        val code = """
            import org.junit.Test
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.test.runTest

            class A {
                @Test
                fun `test that launches a coroutine`() = runTest {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `no reports when no coroutine is launched in test without a runTest block`() {
        val code = """
            import org.junit.Test

            class A {
                @Test
                fun `test that launches a coroutine`() {
                    assert(true)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}

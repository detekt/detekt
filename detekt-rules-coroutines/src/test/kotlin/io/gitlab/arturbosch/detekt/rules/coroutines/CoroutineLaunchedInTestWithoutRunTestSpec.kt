package io.gitlab.arturbosch.detekt.rules.coroutines

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.getContextForPaths
import io.gitlab.arturbosch.detekt.test.location
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CoroutineLaunchedInTestWithoutRunTestSpec(private val env: KotlinCoreEnvironment) {

    private val subject = CoroutineLaunchedInTestWithoutRunTest(Config.empty)

    @Test
    fun `reports when coroutine is launched in test without a runTest block`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch

            class A {
                annotation class Test

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
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking

            class A {
                annotation class Test
                
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

    @Test
    fun `reports when coroutine is launched in test with a runBlocking block in another function`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking
            
            class A {
                annotation class Test

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

    @Test
    fun `reports when coroutine is launched in test with a runBlocking block in a recursive function`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking
            
            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine`() = runBlocking {
                    launchCoroutine()
                }

                fun launchCoroutine() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    launchCoroutine()

                    scope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `no reports when coroutine is launched in test with a runTest block in a recursive function`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.test.runTest

            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine`() = runTest {
                    launchCoroutine()
                }
                
                fun launchCoroutine() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    launchCoroutine()

                    scope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

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
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.test.runTest

            class A {
                annotation class Test

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
            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine`() {
                    assert(true)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `FunTraverseHelper correctly caches explored functions states`() {
        val subject = FunCoroutineLaunchesTraverseHelper()

        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking
            
            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine`() = runBlocking {
                    launchCoroutineOne()
                }

                @Test
                fun `test that does not launch a coroutine`() = runBlocking {
                    doNotLaunchCoroutineOne()()
                }
                
                fun launchCoroutineOne() { launchCoroutineTwo() }
                fun launchCoroutineTwo() { launchCoroutineThree() }
                fun launchCoroutineThree() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.launch {
                        throw Exception()
                    }
                }

                fun doNotLaunchCoroutineOne() { doNotLaunchCoroutineTwo() }
                fun doNotLaunchCoroutineTwo() { doNotLaunchCoroutineThree() }
                fun doNotLaunchCoroutineThree() { return }
            }
        """.trimIndent()

        val ktFile = compileContentForTest(code)
        val bindingContext = env.getContextForPaths(listOf(ktFile))

        val namedFunctions = ktFile
            .collectDescendantsOfType<KtNamedFunction>()

        val testLaunch = namedFunctions.first { it.name == "test that launches a coroutine" }
        val testNotLaunch = namedFunctions.first { it.name == "test that does not launch a coroutine" }

        subject.isFunctionLaunchingCoroutines(testLaunch, bindingContext)

        assertThat(subject.exploredFunctionsCache).hasSize(4)
        assertThat(subject.exploredFunctionsCache.values.filter { it }).hasSize(4)

        subject.isFunctionLaunchingCoroutines(testNotLaunch, bindingContext)

        assertThat(subject.exploredFunctionsCache).hasSize(8)
        assertThat(subject.exploredFunctionsCache.values.filterNot { it }).hasSize(4)
    }

    @Test
    fun `reports two times when coroutine is launched from two tests without runTest block`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking

            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine one`() = runBlocking {
                    launchCoroutine()
                }
                
                @Test
                fun `test that launches a coroutine two`() = runBlocking {
                    launchCoroutine()
                }
                
                fun launchCoroutine() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    launchCoroutine()

                    scope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
        assert(findings.any { it.location.source.line == 9 })
        assert(findings.any { it.location.source.line == 14 })
    }

    @Test
    fun `reports correctly coroutine launches in deep functions`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking
            
            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine`() = runBlocking {
                    launchCoroutineOne()
                }

                @Test
                fun `test that launches a coroutine2`() = runBlocking {
                    launchCoroutineTwo()
                }

                @Test
                fun `test that does not launch a coroutine`() = runBlocking {
                    doNotLaunchCoroutineOne()
                }
                
                fun launchCoroutineOne() {
                    launchCoroutineTwo()
                }
                
                fun launchCoroutineTwo() {
                    launchCoroutineThree()
                }

                fun launchCoroutineThree() {
                    launchCoroutineFour()
                }

                fun launchCoroutineFour() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.launch {
                        throw Exception()
                    }
                }
                
                fun doNotLaunchCoroutineOne() {
                    doNotLaunchCoroutineTwo()
                }
                
                fun doNotLaunchCoroutineTwo() {
                    doNotLaunchCoroutineThree()
                }

                fun doNotLaunchCoroutineThree() {
                    return
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }
}

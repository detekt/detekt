package io.gitlab.arturbosch.detekt.rules.coroutines

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.getContextForPaths
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
    fun `FunTraverseHelper does not return children of already explored functions`() {
        val subject = FunTraverseHelper()

        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking

            class A {
                annotation class Test

                @Test
                fun `test one`() = runBlocking {
                    functionOne()
                }

                @Test
                fun `test two`() = runBlocking {
                    functionOne()
                }


                fun functionOne() {
                    functionTwo()
                }

                fun functionTwo() {
                    return
                }
            }
        """.trimIndent()

        val ktFile = compileContentForTest(code)
        val bindingContext = env.getContextForPaths(listOf(ktFile))

        val namedFunctions = ktFile
            .collectDescendantsOfType<KtNamedFunction>()

        val testFunctionOne = namedFunctions.first { it.name == "test one" }
        val testFunctionTwo = namedFunctions.first { it.name == "test two" }

        val calledFunctionTestOne =
            subject.getFunctionWithCalledFunctionsAndExploreIfNew(testFunctionOne, bindingContext)

        assertThat(calledFunctionTestOne).hasSize(3)
        assert(calledFunctionTestOne[0].name == "test one")
        assert(calledFunctionTestOne[1].name == "functionOne")
        assert(calledFunctionTestOne[2].name == "functionTwo")

        val calledFunctionTestTwo =
            subject.getFunctionWithCalledFunctionsAndExploreIfNew(testFunctionTwo, bindingContext)

        assertThat(calledFunctionTestTwo).hasSize(2)
        assert(calledFunctionTestTwo[0].name == "test two")
        assert(calledFunctionTestTwo[1].name == "functionOne")
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
        assert(findings.any { it.startPosition.line == 9 })
        assert(findings.any { it.startPosition.line == 14 })
    }
}

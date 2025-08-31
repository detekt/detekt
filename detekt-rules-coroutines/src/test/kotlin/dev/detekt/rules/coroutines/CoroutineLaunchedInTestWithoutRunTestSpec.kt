package dev.detekt.rules.coroutines

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.location
import dev.detekt.test.utils.KotlinAnalysisApiEngine
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CoroutineLaunchedInTestWithoutRunTestSpec(private val env: KotlinEnvironmentContainer) {

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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when two coroutine is launched in a test as one violation`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.async

            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine`() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.launch {
                        throw Exception()
                    }

                    scope.async {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when coroutine is deferred in test without a runTest block`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.async

            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine in async`() {
                    val scope = CoroutineScope(Dispatchers.Unconfined)
                    scope.async {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when coroutine is launched using GlobalScope in test without a runTest block`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch

            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine`() {
                    GlobalScope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when coroutine is launched using GlobalScope in test in another fun without a runTest block`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch

            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine`() {
                    launchCoroutineInGlobalScope()
                }

                fun launchCoroutineInGlobalScope() {
                    GlobalScope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
                    doNotLaunchCoroutineOne()
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

        val ktFile = KotlinAnalysisApiEngine.compile(code)

        val namedFunctions = ktFile
            .collectDescendantsOfType<KtNamedFunction>()

        val testLaunch = namedFunctions.first { it.name == "test that launches a coroutine" }
        val testNotLaunch = namedFunctions.first { it.name == "test that does not launch a coroutine" }

        analyze(testLaunch) {
            subject.isFunctionLaunchingCoroutines(testLaunch)
            assertThat(subject.exploredFunctionsCache).hasSize(4)
            assertThat(subject.exploredFunctionsCache.values.filter { it }).hasSize(4)
        }

        analyze(testNotLaunch) {
            subject.isFunctionLaunchingCoroutines(testNotLaunch)
            assertThat(subject.exploredFunctionsCache).hasSize(8)
            assertThat(subject.exploredFunctionsCache.values.filterNot { it }).hasSize(4)
        }
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

        val findings = subject.lintWithContext(env, code)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `reports correctly scope is present in a class`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.runBlocking

            class ScopeHolder {
                val scope = CoroutineScope(Dispatchers.Unconfined)
            }
            
            class A {
                annotation class Test

                @Test
                fun `test that launches a coroutine using scope holder`() = runBlocking {
                    val scopeHolder = ScopeHolder()
                    scopeHolder.scope.launch {
                        throw Exception()
                    }
                }

                @Test
                fun `test that launches a coroutine using scope holder in another function`() = runBlocking {
                    launchCoroutineWithScopeHolder()
                }
                
                fun launchCoroutineWithScopeHolder() {
                    val scopeHolder = ScopeHolder()
                    scopeHolder.scope.launch {
                        throw Exception()
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `reports TC where flow is launched using launchIn- #7200`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.flow.flowOf
            import kotlinx.coroutines.flow.launchIn

            annotation class Test

            class TestCoroutineRunTest {
                @Test
                fun testFlowNoRunTest() {
                    flowOf("foo", "bar").launchIn(GlobalScope)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports TC where flow is launched using launchIn in other function`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.flow.flowOf
            import kotlinx.coroutines.flow.launchIn

            annotation class Test

            class TestCoroutineRunTest {
                @Test
                fun testFlowNoRunTest() {
                    launchFlow()
                }

                fun launchFlow() {
                    flowOf("foo", "bar").launchIn(GlobalScope)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports TC where flow is collected inside GlobalScope`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.flow.flowOf
            import kotlinx.coroutines.launch

            annotation class Test

            class TestCoroutineRunTest {
                @Test
                fun testFlowNoRunTest() {
                    GlobalScope.launch {
                        flowOf("foo", "bar").collect { println(it) }
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports TC where flow is collected inside GlobalScope in other function`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.flow.flowOf
            import kotlinx.coroutines.launch

            annotation class Test

            class TestCoroutineRunTest {
                @Test
                fun testFlowNoRunTest() {
                    launchFlow()
                }

                fun launchFlow() {
                    GlobalScope.launch {
                        flowOf("foo", "bar").collect { println(it) }
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports TC where flow is collected in GlobalScope in other file - #7192`() {
        val file = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch

            annotation class Test

            class TestCoroutineRunTestDetekt {
                fun testCoroutine() {
                    GlobalScope.launch {
                        val foo = "foo"
                        println(foo)
                    }
                }
            }
        """.trimIndent()

        val code = """
            class TestCoroutineRunTest {
                @Test
                fun testNoRunTest() {
                    TestCoroutineRunTestDetekt().testCoroutine()
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, file)).hasSize(1)
    }
}

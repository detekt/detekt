package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class SuspendFunInFinallySectionSpec(private val env: KotlinCoreEnvironment) {
    private val subject = SuspendFunInFinallySection(Config.empty)

    @Test
    fun `report unwrapped suspend function in finally section`() {
        val code = """
            import kotlinx.coroutines.*
            
            fun main(): Unit = runBlocking {
                launch {
                    try {
                        yield()
                    } finally {
                        test()
                        val p = test()
                    }
                }
            }
            
            suspend fun test() { yield() }
        """.trimIndent()

        assertFindingsForSuspendCall(
            findings = subject.compileAndLintWithContext(env, code),
            "test".find(1)(code),
            "test".find(2)(code)
        )
    }

    @Test
    fun `does not report wrapped suspend function in finally section`() {
        val code = """
            import kotlinx.coroutines.*
            
            typealias NC = NonCancellable
            
            fun main(): Unit = runBlocking {
                launch {
                    try {
                        yield()
                    } finally {
                        withContext(NonCancellable) { test() }
                        // Alias argument
                        withContext(NC) { test() }
                    }
                }
            }
            
            suspend fun test() { yield() }
        """.trimIndent()

        assertFindingsForSuspendCall(
            findings = subject.compileAndLintWithContext(env, code),
            *NOTHING
        )
    }

    @Test
    fun `report wrapped suspend function in finally section with different context`() {
        val code = """
            import kotlinx.coroutines.*
            
            fun main(): Unit = runBlocking {
                launch {
                    try {
                        yield()
                    } finally {
                        withContext(Dispatchers.Default) { test() }
                    }
                }
            }
            
            suspend fun test() { yield() }
        """.trimIndent()

        assertFindingsForSuspendCall(
            findings = subject.compileAndLintWithContext(env, code),
            "test".find(1)(code),
            "withContext".find(1)(code),
        )
    }

    @Test
    fun `does not report ordinary function in finally section`() {
        val code = """
            import kotlinx.coroutines.*
            
            fun main(): Unit = runBlocking {
                launch {
                    try {
                        yield()
                    } finally {
                        test()
                    }
                }
            }
            
            fun test() { println(".") }
        """.trimIndent()

        assertFindingsForSuspendCall(
            findings = subject.compileAndLintWithContext(env, code),
            *NOTHING
        )
    }

    @Test
    fun `my test`() {
        val code = """
            import kotlinx.coroutines.*
            import kotlin.coroutines.*
                        
            fun main(): Unit = runBlocking {
                launch {
                    try {
                        yield()
                    } finally {
                        // Nested calls. I don't see a way to implement it 
                        // (e.g. call made to external library that provides non-cancellable context)
                        suspend fun wrapper(b: suspend () -> Unit) = withContext(NonCancellable) { b() }
                        wrapper { test() }
            
                        // Lambda
                        // New coroutine still will be cancelled if launched in cancelled context
                        val lambda: suspend (suspend () -> Unit) -> Unit = { block -> launch { block() } } 
                        withContext(NonCancellable) { lambda { test() } }
                    }
                }
            }
            
            suspend fun test() { yield() }
        """.trimIndent()

        assertFindingsForSuspendCall(
            findings = subject.compileAndLintWithContext(env, code),
            "wrapper".find(2)(code),
            "test".find(1)(code),
            "block".find(2)(code),
        )
    }

    companion object {
        private val NOTHING: Array<Pair<Int, Int>> = emptyArray()

        private fun String.find(ordinal: Int): (String) -> Pair<Int, Int> =
            { code ->
                fun String.next(string: String, start: Int): Int? = indexOf(string, start).takeIf { it != -1 }

                val indices = generateSequence(code.next(this, 0)) { startIndex ->
                    code.next(this, startIndex + 1)
                }
                val index = requireNotNull(indices.elementAtOrNull(ordinal - 1)) {
                    "There's no $ordinal. occurrence of '$this' in '$code'"
                }
                index to index + this.length
            }

        private fun assertFindingsForSuspendCall(findings: List<Finding>, vararg locations: Pair<Int, Int>) {
            assertThat(findings)
                .hasTextLocations(
                    *(locations.map { it.first to it.second }).toTypedArray()
                )
        }
    }
}

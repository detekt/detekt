package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IteratorNotThrowingNoSuchElementExceptionSpec {
    private val subject = IteratorNotThrowingNoSuchElementException(Config.empty)

    @Test
    fun `reports invalid next() implementations`() {
        val code = """
            // reports IteratorNotThrowingNoSuchElementException, IteratorHasNextCallsNextMethod
            class IteratorImpl2 : Iterator<String> {
                override fun hasNext(): Boolean {
                    next()
                    return true
                }
            
                override fun next(): String {
                    return ""
                }
            }
            
            class IteratorImplContainer {
                // reports IteratorNotThrowingNoSuchElementException, IteratorHasNextCallsNextMethod
                object IteratorImplNegative3 : Iterator<String> {
                    override fun hasNext(): Boolean {
                        next()
                        return true
                    }
            
                    override fun next(): String {
                        throw IllegalStateException()
                    }
                }
            }
            
            // reports IteratorNotThrowingNoSuchElementException, IteratorHasNextCallsNextMethod
            interface InterfaceIterator : Iterator<String> {
                override fun hasNext(): Boolean {
                    next()
                    return true
                }
            
                override fun next(): String {
                    return ""
                }
            }
            
            // reports IteratorNotThrowingNoSuchElementException, IteratorHasNextCallsNextMethod
            abstract class AbstractIterator : Iterator<String> {
                override fun hasNext(): Boolean {
                    if (true) {
                        next()
                    }
                    return true
                }
            
                override fun next(): String {
                    return ""
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(4)
    }

    @Test
    fun `does not report correct next() implementations`() {
        val code = """
            import java.util.NoSuchElementException
            
            class IteratorImplOk : Iterator<String> {
            
                override fun hasNext(): Boolean {
                    return true
                }
            
                override fun next(): String {
                    if (!hasNext()) throw NoSuchElementException()
                    return ""
                }
            
                // next method overload should not be reported
                private fun next(i: Int) {
                }
            }
            
            class NoIteratorImpl
            
            abstract class AbstractIteratorNotOverridden : Iterator<String>
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}

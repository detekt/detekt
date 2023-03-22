package io.gitlab.arturbosch.detekt.rules.documentation

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

/**
 * Unit tests for [TodoWithoutLink] rule.
 */
class TodoWithoutLinkSpec {

    private val sut = TodoWithoutLink()

    private fun List<Finding>.assert(vararg failingTodos: FailingTodo) {
        assert(size == failingTodos.size) { "issues size: $size" }
        assert(all { it.id == "TodoWithoutLink" })
        assert(map { it.location.source.line }.sorted() == failingTodos.map { it.atLine }.sorted())
    }

    private fun String.lint(): List<Finding> {
        val file = compileContentForTest(this.trimIndent())
        return sut.lint(file)
    }

    @Test
    fun contains_todo_in_comments_and_docs() {
        """
        package io.gitlab.arturbosch
        import io.gitlab.arturbosch.detekt.api.RuleSetProvider
        /**
         * todo 1 
         * https://github.com/detekt/detekt
         TODO 2
         */
        class DetektRuleSetProvider : RuleSetProvider {
            override val ruleSetId: String = "detekt-rules"
            // ToDo 3 
            // https://github.com/detekt/detekt 
            override fun instance(config: Config) = RuleSet(
                // TODO() 4
                rules = listOf(
                    TodoWithoutLink(config), Todo
                )
            )
        }
        
        // TODO: 5  https://github.com/detekt/detekt        
        implementation(project(":detekt"))
        /* TODO: 6 */   
        
        /* TODO: 7 */
        implementation(project(":detekt"))
        /** TODO: 8 https://github.com/detekt/detekt */
        fun foo(s:String) = TODO()
        """
            .lint().assert(
                FailingTodo(id = 2, atLine = 6),
                FailingTodo(id = 4, atLine = 13),
                FailingTodo(id = 6, atLine = 22),
                FailingTodo(id = 7, atLine = 24),
            )
    }

    @Test
    fun contains_links_in_comment() {
        """
        // lorem
        // TODO 1 https://github.com/detekt/detekt 
        // lorem
        // TODO 2
        // lorem 
        // https://github.com/detekt/detekt 
        // lorem
        // TODO 3
        
        // https://github.com/detekt/detekt 
        // lorem
        // TODO 4
        // lorem
        """
            .lint().assert(FailingTodo(id = 3, atLine = 8), FailingTodo(id = 4, atLine = 12))
    }

    @Test
    fun contains_links_in_doc() {
        """
        /** 
         * lorem
         * TODO 1 https://github.com/detekt/detekt 
         * lorem
         * TODO 2
         * lorem 
         * https://github.com/detekt/detekt 
         * lorem
         * TODO 3
         
         * https://github.com/detekt/detekt 
         * lorem
         * TODO 4
         * lorem
         
        */
        @Module()
        interface A
        """
            .lint().assert(FailingTodo(id = 4, atLine = 13))
    }

    @Test
    fun contains_todo_in_doc_without_element() {
        """
        /**
         * TODO
         */
        """
            .lint().assert()
    }

    private data class FailingTodo(val id: Int, val atLine: Int)
}


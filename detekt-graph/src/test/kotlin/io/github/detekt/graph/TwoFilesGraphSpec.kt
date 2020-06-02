package io.github.detekt.graph

import io.github.detekt.graph.api.Graph
import io.github.detekt.graph.api.isEntry
import io.github.detekt.graph.api.isReachable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.isNotNull
import strikt.assertions.isTrue

class TwoFilesGraphSpec : Spek({

    setupGraphEnvironment(
        "foo.foo",
        "file1" to """
                package foo

                import bar.bar

                fun foo() {
                    bar()
                }
            """.trimIndent(),

        "file2" to """
                package bar

                fun bar() = TODO()
            """.trimIndent()
    )

    val graph: Graph by memoized()

    describe("graph for two kotlin files") {

        it("marks foo.foo as entry") {
            expectThat(graph.nodeByFqName("foo.foo"))
                .isNotNull()
                .get { isEntry() }.isTrue()
        }

        it("bar.bar is reachable from foo.foo") {
            expectThat(graph.nodeByFqName("bar.bar"))
                .isNotNull()
                .get { isReachable() }.isTrue()
        }

        it("reaches all nodes") {
            expectThat(graph.nodes { true }.toList())
                .all { get { graph.isReachable(this) }.isTrue() }
        }
    }
})

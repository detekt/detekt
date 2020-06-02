package io.github.detekt.graph

import io.github.detekt.graph.api.Edge
import io.github.detekt.graph.api.Graph
import io.github.detekt.graph.api.Node
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.filter
import strikt.assertions.first
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo

internal class SimpleOneFileGraphSpec : Spek({

    setupGraphEnvironment(
        "test.main",
        "file1.kt" to """
            package test

            class Class1 {

                fun funWithUnit() = TODO()
                fun funWithReturn(): Int = 5
            }

            class Class2 {

                fun notCalled() = TODO()
            }

            fun main(args: Array<String>) {
                Class1().funWithUnit()
                val r = Class1().funWithReturn()
            }
        """.trimIndent()
    )

    val graph: Graph by memoized()

    describe("graph for a kotlin file") {

        context("File node") {

            val fileNode by memoized { graph.nodesOfType(Node.Type.FILE).first() }

            it("generates a node for the file") {
                expectThat(fileNode)
                    .isA<FileNode>()
                    .get { type }.isEqualTo(Node.Type.FILE)
            }

            it("has an incoming edge to the package node 'test'") {
                expectThat(graph.incomingEdges(fileNode))
                    .hasSize(1)
                    .first()
                    .get { type }.isEqualTo(Edge.Type.ENCLOSES)
            }

            it("has three outgoing edges to declarations") {
                expectThat(graph.outgoingEdges(fileNode)) {
                    hasSize(3)
                    filter { it.target is ClassNode }.hasSize(2)
                    filter { it.target is FunctionNode }.hasSize(1)
                }
            }
        }

        context("Class node") {

            it("has two outgoing edges to functions") {
                val class1Node = requireNotNull(graph.nodeBySimpleName("Class1"))

                expectThat(graph.outgoingEdges(class1Node))
                    .filter { it.target is FunctionNode }
                    .hasSize(2)
            }
        }

        context("Edges to calls") {

            it("has edges to all class and function nodes of Class1") {
                val mainNode = requireNotNull(graph.nodeBySimpleName("main"))

                expectThat(graph.outgoingEdges(mainNode)) {
                    hasSize(3)
                    filter { it.target is ConstructorNode }.hasSize(1)
                    filter { it.target is FunctionNode }.hasSize(2)
                }
            }
        }

        context("dead code analysis") {

            it("finds unreachable 'Class2' and 'notCalled' node") {
                expectThat(graph.nodes { !graph.isReachable(it) }.toList()) {
                    hasSize(2)
                    first { it is ClassNode }.get { name }.isEqualTo("test.Class2")
                    first { it is FunctionNode }.get { name }.isEqualTo("test.Class2.notCalled")
                }
            }
        }
    }
})

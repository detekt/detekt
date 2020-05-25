package io.github.detekt.graph

import io.github.detekt.graph.api.Edge
import io.github.detekt.graph.api.Node
import io.github.detekt.test.utils.KtTestCompiler
import io.github.detekt.test.utils.compileContentForTest
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.filter
import strikt.assertions.first
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo

internal class FileToGraphVisitorSpec : Spek({

    val wrapper = KtTestCompiler.createEnvironment()

    afterGroup { wrapper.dispose() }

    describe("graph for a kotlin file") {
        val code = """
            package test

            class Class1 {

                fun funWithUnit() = TODO()
                fun funWithReturn(): Int = 5
            }

            class Class2

            fun main(args: Array<String>) {
                Class1().funWithUnit()
                val r = Class1().funWithReturn()
            }
        """.trimIndent()

        val files = listOf(compileContentForTest(code))
        val context = KtTestCompiler.getContextForPaths(wrapper.env, files)
        val graph = generateGraph(files, context)

        describe("File node") {

            val fileNode = graph.nodesOfType(Node.Type.FILE).first()

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

        describe("Class node") {

            val class1Node = requireNotNull(graph.firstNode { it.name == "Class1" })

            it("has two outgoing edges to functions") {
                expectThat(graph.outgoingEdges(class1Node))
                    .hasSize(2)
                    .all { get { target }.isA<FunctionNode>() }
            }
        }

        describe("Edges to calls") {

            describe("main function") {

                val mainNode = requireNotNull(graph.firstNode { it.name == "main" })

                it("has edges to all class and function nodes of Class1") {
                    expectThat(graph.outgoingEdges(mainNode))
                        .hasSize(3)
                }
            }
        }
    }
})

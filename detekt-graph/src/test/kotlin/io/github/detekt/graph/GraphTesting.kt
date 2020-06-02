package io.github.detekt.graph

import io.github.detekt.graph.api.Graph
import io.github.detekt.test.utils.KtTestCompiler
import io.github.detekt.test.utils.compileContentForTest
import org.spekframework.spek2.dsl.Root
import org.spekframework.spek2.lifecycle.CachingMode

typealias Filename = String
typealias FileContent = String

fun Root.setupGraphEnvironment(entries: String, vararg files: Pair<Filename, FileContent>) {
    val wrapper = KtTestCompiler.createEnvironment()

    afterGroup { wrapper.dispose() }

    val ktFiles = files.map { (filename, content) -> compileContentForTest(content, filename) }
    val context = KtTestCompiler.getContextForPaths(wrapper.env, ktFiles)

    @Suppress("UNUSED_VARIABLE")
    val graph: Graph by memoized(CachingMode.EACH_GROUP) {
        val graph = generateGraph(ktFiles, context)
        computeReachability(graph, entries.splitToSequence(",").toSet())
        return@memoized graph
    }
}

package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class KtFileCountVisitorTest : Spek({
    describe("files") {

        it("twoFiles") {
            val files = arrayOf(
                    compileForTest(path.resolve("Default.kt")),
                    compileForTest(path.resolve("Test.kt"))
            )
            val count = files
                    .map { getData(it) }
                    .sum()
            Assertions.assertThat(count).isEqualTo(2)
        }
    }
})

private fun getData(file: KtFile): Int {
    return with(file) {
        accept(KtFileCountVisitor())
        getUserData(numberOfFilesKey)!!
    }
}

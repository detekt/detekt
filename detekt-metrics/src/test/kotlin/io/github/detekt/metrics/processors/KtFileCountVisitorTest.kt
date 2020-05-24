package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class KtFileCountVisitorTest : Spek({
    describe("files") {

        it("twoFiles") {
            val files = arrayOf(
                compileContentForTest(default),
                compileContentForTest(complexClass)
            )
            val count = files
                .map { getData(it) }
                .sum()
            assertThat(count).isEqualTo(2)
        }
    }
})

private fun getData(file: KtFile): Int {
    return with(file) {
        accept(KtFileCountVisitor())
        checkNotNull(getUserData(numberOfFilesKey))
    }
}

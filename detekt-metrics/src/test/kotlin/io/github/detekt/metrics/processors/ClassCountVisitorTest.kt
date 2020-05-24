package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ClassCountVisitorTest : Spek({
    describe("something") {

        it("twoClassesInSeparateFile") {
            val files = arrayOf(
                compileContentForTest(default),
                compileContentForTest(classWithFields)
            )
            val count = getClassCount(files)
            assertThat(count).isEqualTo(2)
        }

        it("oneClassWithOneNestedClass") {
            val file = compileContentForTest(complexClass)
            val count = getClassCount(arrayOf(file))
            assertThat(count).isEqualTo(2)
        }

        it("testEnumAndInterface") {
            val files = arrayOf(
                compileContentForTest(emptyEnum),
                compileContentForTest(emptyInterface)
            )
            val count = getClassCount(files)
            assertThat(count).isEqualTo(2)
        }
    }
})

private fun getClassCount(files: Array<KtFile>): Int {
    return files
        .map { getData(it) }
        .sum()
}

private fun getData(file: KtFile): Int {
    return with(file) {
        accept(ClassCountVisitor())
        checkNotNull(getUserData(numberOfClassesKey))
    }
}

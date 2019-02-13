package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ClassCountVisitorTest : Spek({
	describe("something") {

        it("twoClassesInSeparateFile") {
            val files = arrayOf(
                    compileForTest(path.resolve("Test.kt")),
                    compileForTest(path.resolve("Default.kt"))
            )
            val count = getClassCount(files)
            assertThat(count).isEqualTo(2)
        }

        it("oneClassWithOneNestedClass") {
            val file = compileForTest(path.resolve("ComplexClass.kt"))
            val count = getClassCount(arrayOf(file))
            assertThat(count).isEqualTo(2)
        }

        it("testEnumAndInterface") {
            val files = arrayOf(
                    compileForTest(path.resolve("../empty/EmptyEnum.kt")),
                    compileForTest(path.resolve("../empty/EmptyInterface.kt"))
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
        getUserData(numberOfClassesKey)!!
    }
}

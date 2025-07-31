package io.github.detekt.metrics.processors

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class ClassCountVisitorSpec {
    @Test
    fun twoClassesInSeparateFile() {
        val files = arrayOf(
            compileContentForTest(default),
            compileContentForTest(classWithFields)
        )
        val count = getClassCount(files)
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun oneClassWithOneNestedClass() {
        val file = compileContentForTest(complexClass)
        val count = getClassCount(arrayOf(file))
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun testEnumAndInterface() {
        val files = arrayOf(
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface)
        )
        val count = getClassCount(files)
        assertThat(count).isEqualTo(2)
    }
}

private fun getClassCount(files: Array<KtFile>): Int = files.sumOf { getData(it) }

private fun getData(file: KtFile): Int =
    with(file) {
        accept(ClassCountVisitor())
        checkNotNull(getUserData(numberOfClassesKey))
    }

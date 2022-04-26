package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class KtFileCountVisitorSpec {

    @Test
    fun `twoFiles`() {
        val files = arrayOf(
            compileContentForTest(default),
            compileContentForTest(complexClass)
        )
        val count = files.sumOf { getData(it) }
        assertThat(count).isEqualTo(2)
    }
}

private fun getData(file: KtFile): Int {
    return with(file) {
        accept(KtFileCountVisitor())
        checkNotNull(getUserData(numberOfFilesKey))
    }
}

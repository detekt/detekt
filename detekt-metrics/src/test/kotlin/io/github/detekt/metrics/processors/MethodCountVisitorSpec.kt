package io.github.detekt.metrics.processors

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class MethodCountVisitorSpec {

    @Test
    fun defaultMethodCount() {
        val file = compileContentForTest(complexClass)
        val count = getMethodCount(file)
        assertThat(count).isEqualTo(6)
    }
}

private fun getMethodCount(file: KtFile): Int =
    with(file) {
        accept(FunctionCountVisitor())
        checkNotNull(getUserData(numberOfFunctionsKey))
    }

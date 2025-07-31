package io.github.detekt.metrics.processors

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class PackageCountVisitorSpec {

    @Test
    fun twoClassesInSeparatePackage() {
        val files = arrayOf(
            compileContentForTest(default),
            compileContentForTest(emptyEnum)
        )
        val distinctFiles = files
            .map { getData(it) }
            .distinct()
        assertThat(distinctFiles).hasSize(2)
    }
}

private fun getData(file: KtFile): String =
    with(file) {
        accept(PackageCountVisitor())
        checkNotNull(getUserData(numberOfPackagesKey))
    }

package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class PackageCountVisitorTest : Spek({
    describe("Package Count Visitor") {

        it("twoClassesInSeparatePackage") {
            val files = arrayOf(
                compileContentForTest(default),
                compileContentForTest(emptyEnum)
            )
            val count = files
                .map { getData(it) }
                .distinct()
                .count()
            assertThat(count).isEqualTo(2)
        }
    }
})

private fun getData(file: KtFile): String {
    return with(file) {
        accept(PackageCountVisitor())
        checkNotNull(getUserData(numberOfPackagesKey))
    }
}

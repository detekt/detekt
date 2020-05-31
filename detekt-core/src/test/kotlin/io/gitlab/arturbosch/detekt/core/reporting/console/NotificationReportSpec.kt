package io.gitlab.arturbosch.detekt.core.reporting.console

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.ModificationNotification
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NotificationReportSpec : Spek({

    val subject by memoized { NotificationReport() }

    describe("notification report") {

        it("reports two notifications") {
            val path = resourceAsPath("/reporting/empty.txt")
            val detektion = object : TestDetektion() {
                override val notifications = listOf(createNotification(), createNotification())
            }
            assertThat(subject.render(detektion)).isEqualTo("File $path was modified.${LN}File $path was modified.")
        }

        it("reports no findings") {
            val detektion = TestDetektion()
            assertThat(subject.render(detektion)).isNull()
        }
    }
})

private val LN = System.lineSeparator()

private fun createNotification() = ModificationNotification(resourceAsPath("/reporting/empty.txt"))

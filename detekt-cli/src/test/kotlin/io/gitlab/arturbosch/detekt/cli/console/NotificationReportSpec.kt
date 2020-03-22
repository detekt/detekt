package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createNotification
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class NotificationReportSpec : Spek({

    val subject by memoized { NotificationReport() }

    describe("notification report") {

        it("reports two notifications") {
            val path = Paths.get(resource("empty.txt"))
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

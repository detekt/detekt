package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.NL
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NotificationReportSpec : Spek({

    val subject by memoized { NotificationReport() }

    describe("notification report") {

        it("reports two notifications") {
            val detektion = object : TestDetektion() {
                override val notifications = listOf(SimpleNotification("test"), SimpleNotification("test"))
            }
            assertThat(subject.render(detektion)).isEqualTo("test${NL}test")
        }

        it("reports no findings") {
            val detektion = TestDetektion()
            assertThat(subject.render(detektion)).isNull()
        }
    }
})

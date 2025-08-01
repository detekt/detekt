package io.gitlab.arturbosch.detekt.core.reporting.console

import dev.detekt.api.testfixtures.TestDetektion
import io.gitlab.arturbosch.detekt.core.NL
import io.gitlab.arturbosch.detekt.core.util.SimpleNotification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NotificationReportSpec {

    private val subject = NotificationReport()

    @Test
    fun `reports two notifications`() {
        val detektion = TestDetektion(notifications = listOf(SimpleNotification("test"), SimpleNotification("test")))
        assertThat(subject.render(detektion)).isEqualTo("test${NL}test")
    }

    @Test
    fun `reports no findings`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }
}

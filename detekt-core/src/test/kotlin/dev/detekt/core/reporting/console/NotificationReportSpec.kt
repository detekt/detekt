package dev.detekt.core.reporting.console

import dev.detekt.api.Notification
import dev.detekt.api.Notification.Level
import dev.detekt.api.testfixtures.TestDetektion
import dev.detekt.core.NL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NotificationReportSpec {

    private val subject = NotificationReport()

    @Test
    fun `reports two notifications`() {
        val detektion = TestDetektion(
            notifications = listOf(Notification("test", Level.Error), Notification("test", Level.Error)),
        )
        assertThat(subject.render(detektion)).isEqualTo("test${NL}test")
    }

    @Test
    fun `reports no findings`() {
        val detektion = TestDetektion()
        assertThat(subject.render(detektion)).isNull()
    }
}

package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createNotification
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import java.nio.file.Paths

class NotificationReportSpec : SubjectSpek<NotificationReport>({

	subject { NotificationReport() }

	given("several notfications") {

		it("reports two notifications") {
			val path = Paths.get(resource("empty.txt"))
			val detektion = object : TestDetektion() {
				override val notifications = listOf(createNotification(), createNotification())
			}
			assertThat(subject.render(detektion)).isEqualTo("File $path was modified.\nFile $path was modified.")
		}
	}
})

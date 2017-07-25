package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertFails

/**
 * @author Artur Bosch
 */
internal class BuildFailureReportSpec : SubjectSpek<BuildFailureReport>({

	subject { BuildFailureReport() }

	describe("build failure threshold is configurable by configuration") {

		describe("empty code smell result") {
			val detektion = object : Detektion {
				override val findings: Map<String, List<Finding>> = mapOf()
				override val notifications: List<Notification> = listOf()
				override fun <V> getData(key: Key<V>) = throw UnsupportedOperationException("not implemented")
				override fun <V> addData(key: Key<V>, value: V) = throw UnsupportedOperationException("not implemented")
			}

			it("should fail because no config is provided for configurable console reporter") {
				assertFails { subject.render(detektion) }
			}

			it("should return no report if build failure not configured") {
				subject.init(Config.empty)
				val report = subject.render(detektion)
				assertThat(report).isNull()
			}

			it("should print a warning if threshold met") {
				subject.init(TestConfig(mapOf("warningThreshold" to "-2")))
				val report = subject.render(detektion)
				assertThat(report).isEqualTo("Warning: 0 weighted code smells found." +
						" Warning threshold is -2 and fail threshold is -1!")
			}

			it("should throw a build failure error") {
				subject.init(TestConfig(mapOf("failThreshold" to "-2")))
				assertFails { subject.render(detektion) }
			}

			it("should throw a build failure error even if warning threshold is also met") {
				subject.init(TestConfig(mapOf("failThreshold" to "-2", "warningThreshold" to "-2")))
				assertFails { subject.render(detektion) }
			}
		}
	}
})

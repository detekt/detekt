package io.gitlab.arturbosch.detekt

import org.gradle.api.Task
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.reporting.internal.TaskGeneratedSingleFileReport
import org.gradle.api.reporting.internal.TaskReportContainer
import javax.inject.Inject

class DetektReportsImpl @Inject constructor(task: Task) :
		TaskReportContainer<SingleFileReport>(SingleFileReport::class.java, task), DetektReports {
	init {
		add(TaskGeneratedSingleFileReport::class.java, "xml", task)
		add(TaskGeneratedSingleFileReport::class.java, "html", task)
	}

	override var xml: SingleFileReport = getByName("xml")
	override var html: SingleFileReport = getByName("html")
}

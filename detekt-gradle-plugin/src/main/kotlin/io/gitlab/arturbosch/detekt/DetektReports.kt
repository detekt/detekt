package io.gitlab.arturbosch.detekt

import org.gradle.api.reporting.ReportContainer
import org.gradle.api.reporting.SingleFileReport

interface DetektReports : ReportContainer<SingleFileReport> {

	var html: SingleFileReport

	var xml: SingleFileReport

}

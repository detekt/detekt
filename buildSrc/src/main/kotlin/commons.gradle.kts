plugins {
    kotlin("jvm") apply false
    jacoco apply false
}

subprojects {
    group = "io.gitlab.arturbosch.detekt"
    version = Versions.currentOrSnapshot()
}

jacoco.toolVersion = Versions.JACOCO

val examplesOrTestUtils = setOf(
    "detekt-bom",
    "detekt-test",
    "detekt-test-utils",
    "detekt-sample-extensions"
)

tasks {
    jacocoTestReport {
        executionData.setFrom(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        subprojects
            .filterNot { it.name in examplesOrTestUtils }
            .forEach {
                this@jacocoTestReport.sourceSets(it.sourceSets.main.get())
                this@jacocoTestReport.dependsOn(it.tasks.test)
            }

        reports {
            xml.isEnabled = true
            xml.destination = file("$buildDir/reports/jacoco/report.xml")
        }
    }
}

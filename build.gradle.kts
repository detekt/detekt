plugins {
    kotlin("jvm") apply false
    jacoco
    packaging
    releasing
    detekt
    id("com.github.ben-manes.versions")
    id("org.sonarqube")
}

allprojects {
    group = "io.gitlab.arturbosch.detekt"
    version = Versions.currentOrSnapshot()
}

jacoco.toolVersion = Versions.JACOCO

tasks {
    jacocoTestReport {
        executionData.setFrom(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        val examplesOrTestUtils = setOf(
            "detekt-bom",
            "detekt-test",
            "detekt-test-utils",
            "detekt-sample-extensions"
        )

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

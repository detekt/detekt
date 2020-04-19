plugins {
    commons
    apps
    releasing
    jacoco
    detekt
    housekeeping
    id("org.jetbrains.dokka") apply false
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

// jacoco code coverage section

val jacocoVersion: String by project
jacoco.toolVersion = jacocoVersion

tasks {
    jacocoTestReport {
        executionData.setFrom(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        subprojects
            .filterNot { it.name in setOf("detekt-test", "detekt-sample-extensions") }
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

subprojects {
    apply {
        plugin("jacoco")
    }
    jacoco.toolVersion = jacocoVersion
}

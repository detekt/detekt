plugins {
    kotlin("jvm") apply false
    jacoco
    packaging
    releasing
    detekt
    id("org.jetbrains.dokka") apply false
    id("com.github.johnrengelman.shadow") apply false
    id("com.github.ben-manes.versions")
    id("org.sonarqube")
    id("binary-compatibility-validator")
}

repositories {
    jcenter()
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
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

apiValidation {
    // We need to perform api validations for external APIs, for :detekt-api and :detekt-psi-utils
    ignoredProjects.addAll(subprojects.filter { it.name !in listOf("detekt-api", "detekt-psi-utils") }.map { it.name })
    ignoredPackages.add("io.gitlab.arturbosch.detekt.api.internal")
}

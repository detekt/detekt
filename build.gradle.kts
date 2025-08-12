import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

plugins {
    id("releasing")
    id("dev.detekt")
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.gradleup.shadow") version "9.0.1" apply false
    id("de.undercouch.download") version "5.6.0" apply false
    id("dev.drewhamilton.poko") version "0.19.3" apply false
}

dependencies {
    dokka(projects.detektApi)
    dokka(projects.detektPsiUtils)
    dokka(projects.detektTest)
    dokka(projects.detektTestUtils)
    dokka(projects.detektTooling)
    dokka("dev.detekt:detekt-gradle-plugin")
}

dokka {
    dokkaPublications.html {
        outputDirectory = layout.projectDirectory.dir("website/static/kdoc")
    }
}

dependencyAnalysis {
    structure {
        // Could potentially remove in future if DAGP starts handling this natively https://github.com/autonomousapps/dependency-analysis-gradle-plugin/issues/1269
        bundle("junit-jupiter") {
            includeDependency("org.junit.jupiter:junit-jupiter")
            includeDependency("org.junit.jupiter:junit-jupiter-api")
            includeDependency("org.junit.jupiter:junit-jupiter-params")
        }
    }
}

val detektReportMergeSarif by tasks.registering(ReportMergeTask::class) {
    output = layout.buildDirectory.file("reports/detekt/merge.sarif.json")
}

allprojects {
    group = "io.gitlab.arturbosch.detekt"
    version = Versions.currentOrSnapshot()

    apply(plugin = "dev.detekt")

    detekt {
        buildUponDefaultConfig = true
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    dependencies {
        detekt(project(":detekt-cli"))
        detektPlugins(project(":detekt-formatting"))
        detektPlugins(project(":detekt-rules-libraries"))
        detektPlugins(project(":detekt-rules-ruleauthors"))
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "1.8"
        reports {
            xml.required = true
            html.required = true
            sarif.required = true
            md.required = true
        }
        basePath = rootDir.absolutePath
    }
    detektReportMergeSarif {
        input.from(tasks.withType<Detekt>().map { it.reports.sarif.outputLocation })
    }
    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = "1.8"
    }
}

setOf(
    "detektMain",
    "detektTest",
    "detektFunctionalTest",
    "detektFunctionalTestMinSupportedGradle",
    "detektTestFixtures",
).forEach { taskName ->
    tasks.register(taskName) {
        dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":$taskName"))
    }
}

tasks.build { dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":build")) }

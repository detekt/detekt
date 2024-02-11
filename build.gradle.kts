import com.gradle.enterprise.gradleplugin.testretry.retry
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    id("releasing")
    id("io.gitlab.arturbosch.detekt")
    alias(libs.plugins.dokka)
}

tasks.withType<DokkaMultiModuleTask>().configureEach {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
    outputDirectory = layout.projectDirectory.dir("website/static/kdoc")
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

val detektReportMergeSarif by tasks.registering(ReportMergeTask::class) {
    output = layout.buildDirectory.file("reports/detekt/merge.sarif")
}

allprojects {
    group = "io.gitlab.arturbosch.detekt"
    version = Versions.currentOrSnapshot()

    apply(plugin = "io.gitlab.arturbosch.detekt")

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
            txt.required = true
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

subprojects {
    tasks.withType<Test>().configureEach {
        retry {
            @Suppress("MagicNumber")
            if (providers.environmentVariable("CI").isPresent) {
                maxRetries = 3
                maxFailures = 20
            }
        }
        predictiveSelection {
            enabled = providers.gradleProperty("enablePTS").map(String::toBooleanStrict)
        }
    }
}

setOf(
    "build",
    "detektMain",
    "detektTest",
    "detektFunctionalTest",
    "detektTestFixtures",
).forEach { taskName ->
    tasks.register(taskName) {
        dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":$taskName"))
    }
}

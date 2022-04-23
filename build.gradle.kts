import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("releasing")
    id("io.gitlab.arturbosch.detekt")
    alias(libs.plugins.gradleVersions)
    alias(libs.plugins.sonarqube)
}

allprojects {
    group = "io.gitlab.arturbosch.detekt"
    version = Versions.currentOrSnapshot()

    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        source = objects.fileCollection().from(
            io.gitlab.arturbosch.detekt.extensions.DetektExtension.DEFAULT_SRC_DIR_JAVA,
            io.gitlab.arturbosch.detekt.extensions.DetektExtension.DEFAULT_TEST_SRC_DIR_JAVA,
            io.gitlab.arturbosch.detekt.extensions.DetektExtension.DEFAULT_SRC_DIR_KOTLIN,
            io.gitlab.arturbosch.detekt.extensions.DetektExtension.DEFAULT_TEST_SRC_DIR_KOTLIN,
        )
        buildUponDefaultConfig = true
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    dependencies {
        detekt(project(":detekt-cli"))
        detektPlugins(project(":detekt-formatting"))
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "1.8"
        reports {
            xml.required.set(true)
            html.required.set(true)
            txt.required.set(true)
            sarif.required.set(true)
        }
    }
    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = "1.8"
    }
}

val analysisDir = file(projectDir)
val baselineFile = file("$rootDir/config/detekt/baseline.xml")
val configFile = file("$rootDir/config/detekt/detekt.yml")
val statisticsConfigFile = file("$rootDir/config/detekt/statistics.yml")

val kotlinFiles = "**/*.kt"
val kotlinScriptFiles = "**/*.kts"
val resourceFiles = "**/resources/**"
val buildFiles = "**/build/**"

val detektFormat by tasks.registering(Detekt::class) {
    description = "Formats whole project."
    parallel = true
    disableDefaultRuleSets = true
    buildUponDefaultConfig = true
    autoCorrect = true
    setSource(analysisDir)
    config.setFrom(listOf(statisticsConfigFile, configFile))
    include(kotlinFiles)
    include(kotlinScriptFiles)
    exclude(resourceFiles)
    exclude(buildFiles)
    baseline.set(baselineFile)
    reports {
        xml.required.set(false)
        html.required.set(false)
        txt.required.set(false)
    }
}

val detektAll by tasks.registering(Detekt::class) {
    description = "Runs the whole project at once."
    parallel = true
    buildUponDefaultConfig = true
    setSource(analysisDir)
    config.setFrom(listOf(statisticsConfigFile, configFile))
    include(kotlinFiles)
    include(kotlinScriptFiles)
    exclude(resourceFiles)
    exclude(buildFiles)
    baseline.set(baselineFile)
    reports {
        xml.required.set(false)
        html.required.set(false)
        txt.required.set(false)
    }
}

val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(analysisDir)
    config.setFrom(listOf(statisticsConfigFile, configFile))
    include(kotlinFiles)
    include(kotlinScriptFiles)
    exclude(resourceFiles)
    exclude(buildFiles)
    baseline.set(baselineFile)
}

tasks.register("build") {
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":build"))
}

tasks.register("publishAllToMavenLocal") {
    description = "Publish all the projects to Maven Local"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishToMavenLocal"))
        }
    }
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishToMavenLocal"))
}

tasks.register("publishAllToSonatypeSnapshot") {
    description = "Publish all the projects to Sonatype Snapshot Repository"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishAllPublicationsToSonatypeSnapshotRepository"))
        }
    }
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishAllPublicationsToSonatypeSnapshotRepository"))
}

tasks.register("publishAllToMavenCentral") {
    description = "Publish all the projects to Sonatype Staging Repository"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishAllPublicationsToMavenCentralRepository"))
        }
    }
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishAllPublicationsToMavenCentralRepository"))
}

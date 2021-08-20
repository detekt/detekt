import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val baselineFile = file("$rootDir/config/detekt/baseline.xml")

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}

detekt {
    source = objects.fileCollection().from(
        DetektExtension.DEFAULT_SRC_DIR_JAVA,
        "src/test/java",
        DetektExtension.DEFAULT_SRC_DIR_KOTLIN,
        "src/test/kotlin"
    )
    buildUponDefaultConfig = true
    baseline = baselineFile

    reports {
        xml.enabled = true
        html.enabled = true
        txt.enabled = true
        sarif.enabled = true
    }
}

dependencies {
    detekt(project(":detekt-cli"))
    detektPlugins(project(":custom-checks"))
    detektPlugins(project(":detekt-formatting"))
}

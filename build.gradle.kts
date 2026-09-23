import dev.detekt.gradle.Detekt
import dev.detekt.gradle.report.ReportMergeTask

plugins {
    id("releasing")
    id("dev.detekt")
    id("org.jetbrains.dokka") version "2.2.0"
}

dependencies {
    dokka(projects.detektApi)
    dokka(projects.detektPsiUtils)
    dokka(projects.detektTest)
    dokka(projects.detektTestAssertj)
    dokka(projects.detektTestUtils)
    dokka(projects.detektTooling)
    dokka("dev.detekt:detekt-gradle-plugin")
}

configurations.detekt {
    attributes {
        // Fixes Caffeine variant ambiguity:
        // 'external' vs 'shadowed'
        attribute(
            Bundling.BUNDLING_ATTRIBUTE,
            named(Bundling::class.java, Bundling.EXTERNAL)
        )

        // Fixes Guava variant mismatch:
        // Selects JRE target (Java 8+) instead of Android
        attribute(
            TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
            named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM)
        )
        attribute(
            Usage.USAGE_ATTRIBUTE,
            named(Usage::class.java, Usage.JAVA_RUNTIME)
        )
        attribute(
            Category.CATEGORY_ATTRIBUTE,
            named(Category::class.java, Category.LIBRARY)
        )
        attribute(
            LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
            named(LibraryElements::class.java, LibraryElements.JAR)
        )
    }
}

configurations.detektPlugins {
    attributes {
        // Fixes Caffeine variant ambiguity:
        // 'external' vs 'shadowed'
        attribute(
            Bundling.BUNDLING_ATTRIBUTE,
            named(Bundling::class.java, Bundling.EXTERNAL)
        )

        // Fixes Guava variant mismatch:
        // Selects JRE target (Java 8+) instead of Android
        attribute(
            TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
            named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM)
        )
        attribute(
            Usage.USAGE_ATTRIBUTE,
            named(Usage::class.java, Usage.JAVA_RUNTIME)
        )
        attribute(
            Category.CATEGORY_ATTRIBUTE,
            named(Category::class.java, Category.LIBRARY)
        )
        attribute(
            LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
            named(LibraryElements::class.java, LibraryElements.JAR)
        )
    }
}

dokka {
    dokkaPublications.html {
        outputDirectory = layout.projectDirectory.dir("website/static/kdoc")
    }
}

dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("fail")
            }
        }
    }
    structure {
        // Could potentially remove in future if DAGP starts handling this natively https://github.com/autonomousapps/dependency-analysis-gradle-plugin/issues/1269
        bundle("junit-jupiter") {
            includeDependency("org.junit.jupiter:junit-jupiter")
            includeDependency("org.junit.jupiter:junit-jupiter-api")
            includeDependency("org.junit.jupiter:junit-jupiter-params")
        }
    }
}

val detektReportMergeSarif = tasks.register<ReportMergeTask>("detektReportMergeSarif") {
    output = layout.buildDirectory.file("reports/detekt/merge.sarif.json")
}

allprojects {
    group = "dev.detekt"
    version = Versions.currentOrSnapshot()

    apply(plugin = "dev.detekt")

    detekt {
        buildUponDefaultConfig = true
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    dependencies {
        detekt(project(":detekt-cli"))
        detektPlugins(project(":detekt-rules-libraries"))
        detektPlugins(project(":detekt-rules-ruleauthors"))
    }

    tasks.withType<Detekt>().configureEach {
        reports {
            checkstyle.required = true
            html.required = true
            sarif.required = true
            markdown.required = true
        }
        basePath = rootDir.absolutePath
    }
    detektReportMergeSarif {
        input.from(tasks.withType<Detekt>().map { it.reports.sarif.outputLocation })
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

tasks.build {
    logger.warn(
        "Executing `build` task from root build will not build the Gradle plugin. " +
            "Use `./gradlew -pdetekt-gradle-plugin build`"
    )
}

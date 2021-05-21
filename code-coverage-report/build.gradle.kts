plugins {
    id("java")
    id("jacoco")
}

jacoco.toolVersion = libs.versions.jacoco.get()

dependencies {
    implementation(projects.customChecks)
    implementation(projects.detektApi)
    implementation(projects.detektCli)
    implementation(projects.detektCore)
    implementation(projects.detektFormatting)
    implementation(projects.detektGenerator)
    implementation(projects.detektMetrics)
    implementation(projects.detektParser)
    implementation(projects.detektPsiUtils)
    implementation(projects.detektReportHtml)
    implementation(projects.detektReportSarif)
    implementation(projects.detektReportTxt)
    implementation(projects.detektReportXml)
    implementation(projects.detektRules)
    implementation(projects.detektRulesComplexity)
    implementation(projects.detektRulesCoroutines)
    implementation(projects.detektRulesDocumentation)
    implementation(projects.detektRulesEmpty)
    implementation(projects.detektRulesErrorprone)
    implementation(projects.detektRulesExceptions)
    implementation(projects.detektRulesNaming)
    implementation(projects.detektRulesPerformance)
    implementation(projects.detektRulesStyle)
    implementation(projects.detektTooling)
}

// A resolvable configuration to collect source code
val jacocoSourceDirs: Configuration by configurations.creating {
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
    isTransitive = false
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("source-folders"))
    }
}

// A resolvable configuration to collect JaCoCo coverage data
val jacocoExecutionData: Configuration by configurations.creating {
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
    isTransitive = false
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-coverage-data"))
    }
}

val jacocoClassDirs: Configuration by configurations.creating {
    extendsFrom(configurations.implementation.get())
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
    isTransitive = false
    attributes {
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.CLASSES))
    }
}

val jacocoMergedReport by tasks.registering(JacocoReport::class) {
    description = "Merge JaCoCo reports from dependencies."
    group = "verification"

    executionData.from(jacocoExecutionData.incoming.artifacts.artifactFiles)
    sourceDirectories.from(jacocoSourceDirs.incoming.artifacts.artifactFiles)
    classDirectories.from(jacocoClassDirs.incoming.artifacts.artifactFiles)

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.check {
    dependsOn(jacocoMergedReport)
}

plugins {
    id("base")
    id("jacoco-report-aggregation")
}

reporting {
    reports {
        create("jacocoMergedReport", JacocoCoverageReport::class) {
            testType.set(TestSuiteType.UNIT_TEST)
        }
    }
}

jacoco.toolVersion = libs.versions.jacoco.get()

dependencies {
    jacocoAggregation(projects.detektApi)
    jacocoAggregation(projects.detektCli)
    jacocoAggregation(projects.detektCore)
    jacocoAggregation(projects.detektFormatting)
    jacocoAggregation(projects.detektGenerator)
    jacocoAggregation(projects.detektMetrics)
    jacocoAggregation(projects.detektParser)
    jacocoAggregation(projects.detektPsiUtils)
    jacocoAggregation(projects.detektReportHtml)
    jacocoAggregation(projects.detektReportSarif)
    jacocoAggregation(projects.detektReportTxt)
    jacocoAggregation(projects.detektReportXml)
    jacocoAggregation(projects.detektRules)
    jacocoAggregation(projects.detektRulesComplexity)
    jacocoAggregation(projects.detektRulesCoroutines)
    jacocoAggregation(projects.detektRulesDocumentation)
    jacocoAggregation(projects.detektRulesEmpty)
    jacocoAggregation(projects.detektRulesErrorprone)
    jacocoAggregation(projects.detektRulesExceptions)
    jacocoAggregation(projects.detektRulesNaming)
    jacocoAggregation(projects.detektRulesPerformance)
    jacocoAggregation(projects.detektRulesStyle)
    jacocoAggregation(projects.detektTooling)
}

tasks.check {
    dependsOn(tasks.named("jacocoMergedReport"))
}

// The `allCodeCoverageReportClassDirectories` configuration provided by the jacoco-report-aggregation plugin actually
// resolves JARs and not class directories as the name suggests. Because the detekt-formatting JAR bundles ktlint and
// other dependencies in its JAR, they are incorrectly displayed on the coverage report even though they're external
// dependencies.
configurations.allCodeCoverageReportClassDirectories.get().attributes {
    attributes.attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class, Category.LIBRARY))
    attributes.attribute(
        LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class, LibraryElements.CLASSES)
    )
}

val customClassDirectories = configurations.allCodeCoverageReportClassDirectories.get().incoming.artifactView {
    componentFilter {
        it is ProjectComponentIdentifier
    }
    lenient(true)
}

tasks.named("jacocoMergedReport", JacocoReport::class).configure {
    this.classDirectories.setFrom(customClassDirectories.files)
}

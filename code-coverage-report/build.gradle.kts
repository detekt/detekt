plugins {
    id("jacoco-report-aggregation")
}

reporting {
    reports {
        create("jacocoMergedReport", JacocoCoverageReport::class) {
            testSuiteName = "test"
            reportTask {
                dependsOn(":detekt-generator:generateDocumentation")
            }
        }
    }
}

jacoco.toolVersion = libs.versions.jacoco.get()

dependencies {
    jacocoAggregation(projects.detektApi)
    jacocoAggregation(projects.detektCli)
    jacocoAggregation(projects.detektCompilerPlugin)
    jacocoAggregation(projects.detektCore)
    jacocoAggregation(projects.detektGenerator)
    jacocoAggregation(projects.detektMetrics)
    jacocoAggregation(projects.detektParser)
    jacocoAggregation(projects.detektPsiUtils)
    jacocoAggregation(projects.detektReportHtml)
    jacocoAggregation(projects.detektReportSarif)
    jacocoAggregation(projects.detektReportCheckstyle)
    jacocoAggregation(projects.detektReportMarkdown)
    jacocoAggregation(projects.detektRules.complexity)
    jacocoAggregation(projects.detektRules.coroutines)
    jacocoAggregation(projects.detektRules.comments)
    jacocoAggregation(projects.detektRules.emptyBlocks)
    jacocoAggregation(projects.detektRules.potentialBugs)
    jacocoAggregation(projects.detektRules.exceptions)
    jacocoAggregation(projects.detektRules.ktlintWrapper)
    jacocoAggregation(projects.detektRules.libraries)
    jacocoAggregation(projects.detektRules.naming)
    jacocoAggregation(projects.detektRules.performance)
    jacocoAggregation(projects.detektRules.ruleauthors)
    jacocoAggregation(projects.detektRules.style)
    jacocoAggregation(projects.detektTestUtils)
    jacocoAggregation(projects.detektTooling)
    jacocoAggregation(projects.detektUtils)
}

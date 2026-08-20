plugins {
    id("jacoco-report-aggregation")
}

reporting {
    reports {
        create<JacocoCoverageReport>("jacocoMergedTestReport") {
            testSuiteName = "test"
            reportTask {
                dependsOn(":detekt-core:generateDefaultDetektConfig", ":detekt-core:generateDeprecationList")
            }
        }
        create<JacocoCoverageReport>("jacocoMergedFunctionalTestReport") {
            testSuiteName = "functionalTest"
            reportTask {
                dependsOn(
                    ":detekt-core:generateDefaultDetektConfig",
                    ":detekt-core:generateDeprecationList",
                    ":detekt-rules-comments:copyConfigToResources",
                    ":detekt-rules-complexity:copyConfigToResources",
                    ":detekt-rules-coroutines:copyConfigToResources",
                    ":detekt-rules-empty-blocks:copyConfigToResources",
                    ":detekt-rules-exceptions:copyConfigToResources",
                    ":detekt-rules-ktlint-wrapper:copyConfigToResources",
                    ":detekt-rules-libraries:copyConfigToResources",
                    ":detekt-rules-naming:copyConfigToResources",
                    ":detekt-rules-performance:copyConfigToResources",
                    ":detekt-rules-potential-bugs:copyConfigToResources",
                    ":detekt-rules-ruleauthors:copyConfigToResources",
                    ":detekt-rules-style:copyConfigToResources",
                )
            }
        }
    }
}

jacoco.toolVersion = libs.versions.jacoco.get()

dependencies {
    jacocoAggregation("dev.detekt:detekt-gradle-plugin")
    jacocoAggregation(projects.detektApi)
    jacocoAggregation(projects.detektCli)
    jacocoAggregation(projects.detektCore)
    jacocoAggregation(projects.detektGenerator)
    jacocoAggregation(projects.detektMetrics)
    jacocoAggregation(projects.detektParser)
    jacocoAggregation(projects.detektPsiUtils)
    jacocoAggregation(projects.detektReportHtml)
    jacocoAggregation(projects.detektReportSarif)
    jacocoAggregation(projects.detektReportStatistics)
    jacocoAggregation(projects.detektReportCheckstyle)
    jacocoAggregation(projects.detektReportComplexity)
    jacocoAggregation(projects.detektReportMarkdown)
    jacocoAggregation(projects.detektRulesComplexity)
    jacocoAggregation(projects.detektRulesCoroutines)
    jacocoAggregation(projects.detektRulesComments)
    jacocoAggregation(projects.detektRulesEmptyBlocks)
    jacocoAggregation(projects.detektRulesPotentialBugs)
    jacocoAggregation(projects.detektRulesExceptions)
    jacocoAggregation(projects.detektRulesKtlintWrapper)
    jacocoAggregation(projects.detektRulesLibraries)
    jacocoAggregation(projects.detektRulesNaming)
    jacocoAggregation(projects.detektRulesPerformance)
    jacocoAggregation(projects.detektRulesRuleauthors)
    jacocoAggregation(projects.detektRulesStyle)
    jacocoAggregation(projects.detektTestUtils)
    jacocoAggregation(projects.detektTooling)
    jacocoAggregation(projects.detektUtils)
}

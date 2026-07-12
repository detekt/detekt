plugins {
    id("jacoco-report-aggregation")
}

reporting {
    reports {
        create<JacocoCoverageReport>("jacocoMergedTestReport") {
            testSuiteName = "test"
            reportTask {
                dependsOn(":detekt-generator:generateDocumentation")
            }
        }
        create<JacocoCoverageReport>("jacocoMergedFunctionalTestReport") {
            testSuiteName = "functionalTest"
            reportTask {
                dependsOn(":detekt-generator:generateDocumentation")
            }
        }
    }
}

jacoco.toolVersion = libs.versions.jacoco.get()

dependencies {
    jacocoAggregation("dev.detekt:detekt-gradle-plugin")
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

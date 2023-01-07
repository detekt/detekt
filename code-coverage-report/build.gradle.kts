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
    jacocoAggregation(projects.detektCompilerPlugin)
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
    jacocoAggregation(projects.detektReportMd)
    jacocoAggregation(projects.detektRulesComplexity)
    jacocoAggregation(projects.detektRulesCoroutines)
    jacocoAggregation(projects.detektRulesDocumentation)
    jacocoAggregation(projects.detektRulesEmpty)
    jacocoAggregation(projects.detektRulesErrorprone)
    jacocoAggregation(projects.detektRulesExceptions)
    jacocoAggregation(projects.detektRulesLibraries)
    jacocoAggregation(projects.detektRulesNaming)
    jacocoAggregation(projects.detektRulesPerformance)
    jacocoAggregation(projects.detektRulesRuleauthors)
    jacocoAggregation(projects.detektRulesStyle)
    jacocoAggregation(projects.detektTestUtils)
    jacocoAggregation(projects.detektTooling)
    jacocoAggregation(projects.detektUtils)
}

tasks.check {
    dependsOn(tasks.named("jacocoMergedReport"))
}

tasks.withType<JacocoReport>().configureEach {
    dependsOn(":detekt-generator:generateDocumentation")
}
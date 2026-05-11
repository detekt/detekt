plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    implementation(testFixtures(projects.detektApi))
    api(projects.detektTestUtils)
    api(libs.kotlin.compiler)
    implementation(libs.kotlin.reflect)
}

dependencyAnalysis {
    issues {
        onUnusedDependencies {
            // All modules have a test suite that uses JUnit Jupiter which adds this dependency.
            // This module has no tests so this dependency is unused and is flagged by DAGP unless excluded.
            exclude("org.junit.jupiter:junit-jupiter")
        }
    }
}

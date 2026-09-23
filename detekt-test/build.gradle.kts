plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    api(projects.detektApi)
    api(projects.detektTestUtils)
    api(libs.kotlin.analysisApiSurface)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.analysisApiImplementation)
    implementation(libs.kotlin.analysisApiIntellijApiSurfaceComponents)
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

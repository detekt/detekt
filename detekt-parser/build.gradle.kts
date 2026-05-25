plugins {
    id("module")
}

dependencies {
    api(libs.kotlin.compiler)
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

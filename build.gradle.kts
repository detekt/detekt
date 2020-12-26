plugins {
    commons
    packaging
    releasing
    detekt
    id("org.jetbrains.dokka") apply false
    id("com.github.johnrengelman.shadow") apply false
    id("com.github.ben-manes.versions")
    id("org.sonarqube")
    id("binary-compatibility-validator")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

apiValidation {
    // We only need to perform api validation for :detekt-api.
    // TODO: rootProject.name is a temporary workaround to exclude api validation of rootProject.
    // We should refactoring our gradle setup to not apply `JavaBasePlugin`
    ignoredProjects.addAll(subprojects.filter { it.name != "detekt-api" }.map { it.name } + rootProject.name)
    ignoredPackages.add("io.gitlab.arturbosch.detekt.api.internal")
}

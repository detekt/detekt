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

repositories {
    jcenter()
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

apiValidation {
    // rootProject.name is a temporary workaround to exclude api validation of rootProject.
    // We should refactoring our gradle setup to not apply `JavaBasePlugin`
    ignoredProjects.add(rootProject.name)
    // We need to perform api validations for external APIs, for :detekt-api and :detekt-psi-utils
    ignoredProjects.addAll(subprojects.filter { it.name !in listOf("detekt-api", "detekt-psi-utils") }.map { it.name })
    ignoredPackages.add("io.gitlab.arturbosch.detekt.api.internal")
}

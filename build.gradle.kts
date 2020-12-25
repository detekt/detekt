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
    // There is also a temporary workaround to exclude api validation of rootProject, otherwise
    // api/detekt.api will be created with a blank line.
    // https://github.com/Kotlin/binary-compatibility-validator/issues/32
    ignoredProjects.addAll(subprojects.filter { it.name != "detekt-api" }.map { it.name } + rootProject.name)
}

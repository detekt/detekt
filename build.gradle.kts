plugins {
    commons
    apps
    detekt
    releasing
    id("org.jetbrains.dokka") apply false
    id("com.github.ben-manes.versions")
    id("org.sonarqube")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.8"
}

dependencies {
    implementation(libs.ktlint.rulesetStandard) {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks.shadowJar {
    relocate("org.jetbrains.kotlin.com.intellij", "com.intellij")
}

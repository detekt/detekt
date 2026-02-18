plugins {
    id("packaging")
    id("com.gradleup.shadow") version "9.3.1"
}

dependencies {
    runtimeOnly(libs.ktlint.rulesetStandard) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
}

tasks.shadowJar {
    relocate("org.jetbrains.kotlin.com.intellij", "com.intellij")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

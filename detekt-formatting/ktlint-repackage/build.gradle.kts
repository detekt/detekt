plugins {
    id("packaging")
    id("com.gradleup.shadow") version "9.0.2"
}

dependencies {
    runtimeOnly(libs.ktlint.rulesetStandard) {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks.shadowJar {
    relocate("org.jetbrains.kotlin.com.intellij", "com.intellij")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}

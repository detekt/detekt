plugins {
    id("packaging")
    id("com.gradleup.shadow") version "9.2.2"
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

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}

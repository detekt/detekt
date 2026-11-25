plugins {
    id("java")
    id("com.gradleup.shadow") version "9.6.1"
}

dependencies {
    runtimeOnly(libs.ktlint.rulesetStandard) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-analysis-api-surface")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-analysis-api-implementation")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-analysis-api-intellij-api-surface-components")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-analysis-api-intellij-implementation-components")
    }
}

tasks.shadowJar {
    relocate("org.jetbrains.kotlin.com.intellij", "com.intellij")
    @Suppress("DEPRECATION") // This flag will be disabled and removed in the next major version of Shadow.
    enableKotlinModuleRemapping = false
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}

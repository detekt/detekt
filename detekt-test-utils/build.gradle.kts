plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.compiler)

    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektParser)
    implementation(libs.kotlinx.coroutinesCore)
    implementation(libs.kotlinx.coroutinesTest)

    testImplementation(libs.assertj.core)
}

apiValidation {
    ignoredPackages.add("dev.detekt.test.utils.internal")
}

//java {
//    targetCompatibility = JavaVersion.VERSION_17
//}
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
//    compilerOptions {
//        jvmTarget = JvmTarget.JVM_17
//    }
//}

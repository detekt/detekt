object Versions {

    const val DETEKT: String = "1.8.0"
    const val JVM_TARGET: String = "1.8"
    const val ASSERTJ: String = "3.15.0"
    const val SPEK: String = "2.0.10"
    const val REFLECTIONS: String = "0.9.12"
    const val MOCKK: String = "1.9.3"
    const val JUNIT: String = "1.6.1"
    const val JCOMMANDER: String = "1.78"
    const val SNAKEYAML: String = "1.26"
    const val KTLINT: String = "0.36.0"
    const val KOTLINX_HTML: String = "0.7.1"
    const val JACOCO: String = "0.8.5"

    fun currentOrSnapshot(): String {
        if (System.getProperty("snapshot")?.toBoolean() == true) {
            return "$DETEKT-SNAPSHOT"
        }
        return DETEKT
    }
}

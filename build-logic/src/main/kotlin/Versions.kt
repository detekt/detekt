object Versions {

    const val DETEKT: String = "1.21.0-RC2"
    const val SNAPSHOT_NAME: String = "main"
    const val JVM_TARGET: String = "1.8"

    fun currentOrSnapshot(): String {
        if (System.getProperty("snapshot")?.toBoolean() == true) {
            return "$SNAPSHOT_NAME-SNAPSHOT"
        }
        return DETEKT
    }
}

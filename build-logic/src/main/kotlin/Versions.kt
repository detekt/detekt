object Versions {

    const val DETEKT: String = "2.0.0-alpha.2"
    const val SNAPSHOT_NAME: String = "main"

    fun currentOrSnapshot(): String {
        if (System.getProperty("snapshot")?.toBoolean() == true) {
            return "$SNAPSHOT_NAME-SNAPSHOT"
        }
        return DETEKT
    }
}

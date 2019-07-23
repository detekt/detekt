package cases

import java.io.IOException

fun noSwallowedException() {
    try {
    } catch (e: Exception) {
        throw IOException(e.message, e)
    } catch (e: Exception) {
        throw IOException(e)
    } catch (e: Exception) {
        throw Exception(e)
    }
}

fun usedException() {
    try {
    } catch (e: Exception) {
        print(e)
    } catch (e: Exception) {
        print(e.message)
    }
}

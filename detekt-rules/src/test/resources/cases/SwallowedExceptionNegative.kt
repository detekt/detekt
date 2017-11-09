package cases

import java.io.IOException

fun noSwallowedException() {
	try {
	} catch (e: Exception) {
		println()
	} catch (e: Exception) {
		throw IOException()
	} catch(e: Exception) {
		throw IOException(e.message, e)
	} catch(e: Exception) {
		throw IOException(e)
	} catch (e: Exception) {
		throw Exception(e)
	}
}

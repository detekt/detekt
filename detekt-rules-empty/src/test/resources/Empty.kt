@file:Suppress("unused", "ConstantConditionIf", "ConvertSecondaryConstructorToPrimary")

package cases

class Empty : Runnable {

    init {

    }

    constructor() {

    }

    override fun run() {

    }

    fun stuff() {
        try {

        } catch (e: Exception) {

        } catch (e: Exception) {
            //no-op
        } catch (e: Exception) {
            println()
        } catch (ignored: Exception) {

        } catch (expected: Exception) {

        } catch (_: Exception) {

        } finally {

        }
        if (true) {

        } else {

        }
        when (true) {

        }
        for (i in 1..10) {

        }
        while (true) {

        }
        do {

        } while (true)
    }
}

class EmptyClass() {}

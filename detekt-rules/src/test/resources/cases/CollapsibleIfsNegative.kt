package cases

@Suppress("unused", "ConstantConditionIf", "SimplifyBooleanWithConstants", "RedundantSemicolon")
fun collapsibleIfsNegative() {

    if (true) {
    } else if (1 == 1) {
        if (true) {
        }
    }

    if (true) {
        if (1 == 1) {
        }
    } else {
    }

    if (true) {
        if (1 == 1) {
        }
    } else if (false) {
    } else {
    }


    if (true) {
        if (1 == 1);
        println()
    }

    if (true) {
        if (1 == 1) {
        } else {
        }
    }

    if (true) {
        if (1 == 1) {
        } else if (2 == 2) {
        }
    }
}

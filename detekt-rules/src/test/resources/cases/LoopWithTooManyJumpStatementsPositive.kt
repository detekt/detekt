package cases

@Suppress("unused", "ConstantConditionIf")
fun tooManyJumpStatements() {
    val i = 0

    // reports 1 - too many jump statements
    for (j in 1..2) {
        if (i > 1) {
            break
        } else {
            continue
        }
    }

    // reports 1 - too many jump statements
    while (i < 2) {
        if (i > 1) break else continue
    }

    // reports 1 - too many jump statements
    do {
        if (i > 1) break else continue
    } while (i < 2)
}



fun main(args: Array<String>) {
//    val w = "racer"
//    val tries = (::productionSolver).singleRound(w)
//    println("$w $tries")
//    return

    val results = ALL_TARGETS.map { w ->
        // println("$w")
        val tries = (::productionSolver).singleRound(w)
        println("$w $tries")
        w to tries
    }

    results
        .sortedBy { (w, tries) -> tries.size }
        .forEachIndexed { idx, (w, tries) ->
            println("${(idx + 1).toString().padStart(4, ' ')}) $w ${tries.size}")
        }

    results.groupBy { it.second.size }
        .entries
        .sortedBy { it.key }
        .forEach {
            println("${it.key} -> ${it.value.size}")
        }
}


fun main(args: Array<String>) {
//    val w = "racer"
//    val tries = (::productionSolver).singleRound(w)
//    println("$w $tries")
//    return

    ALL_TARGETS.map { w ->
        // println("$w")
        val tries = (::productionSolver).singleRound(w)
        println("$w $tries")
        w to tries
    }
    .sortedBy { (w, tries) -> tries.size }
    .forEachIndexed { idx, (w, tries) ->
        println("${(idx + 1).toString().padStart(4, ' ')}) $w ${tries.size}")
    }
}
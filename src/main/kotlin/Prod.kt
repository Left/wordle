fun main() {
    println(productionSolver(
        listOf("trace"),
        Masks(
            GreenMask.betweenWords("aaala", "ccclc").also {
              println(it)
            },
            YellowMask.fromWord("cu"),
            YellowMask.fromWord("traemis")
        )
    ))

}
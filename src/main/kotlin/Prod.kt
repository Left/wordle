fun main() {
    /*
    println(productionSolver(
        listOf("trace", "claim", "abase", "ionic"),
        Masks(
            GreenMask.betweenWords("___ic", "aaaic").also {
              print("$it ")
            },
            YellowMask.fromWord("cio").also {
                print("$'it' ")
            },
            YellowMask.fromWord("traelmbsn").also {
                print("'$it' ")
            }
        )
    ))
    */
    var solved = false
    var words = mutableListOf<String>()
    var mask = Masks.EMPTY

    while (!solved) {
        val newWord = productionSolver(words, mask)
        words.add(newWord)
        println(words.joinToString(" "))
        println("Yellow (f.e. 1 2 3 4 5): ")
        val yellows = readln().split(" ")
            .map { it.toIntOrNull() }
            .filterNotNull()
            .map { it  - 1}
        println("Greens (f.e. 1 2 3 4 5): ")
        val greens = readln().split(" ")
            .map { it.toIntOrNull() }
            .filterNotNull()
            .map { it  - 1}

        mask = Masks(
            mask.greenMask or GreenMask.fromIndexes(newWord, greens),
            mask.yellowMask or (YellowMask.fromIndexes(newWord, yellows)),
            mask.blackMask or
                    (YellowMask.fromWord(newWord) and YellowMask.fromIndexes(newWord, yellows).inverted() and mask.yellowMask.inverted()),
        )

        println(mask)

        solved = mask.isSolved()
    }
}
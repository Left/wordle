import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SolverKtTest {

//    @Test
//    fun testSolver() {
//        val tries = (::productionSolver).singleRound("inert")
//
//        println(tries.println())
//    }

    @Test
    fun toLetterMask() {
        assertEquals(YellowMask.fromWord("perky").asCharSet(), "ekpry")
        assertEquals(YellowMask.fromWord("perky").inverted().asCharSet(), "abcdfghijlmnoqstuvwxz")
        assertEquals(YellowMask.fromWord("perky").inverted().inverted().asCharSet(), "ekpry")
        assertEquals(YellowMask.fromWord("perky").or(YellowMask.fromWord("aaaab")).asCharSet(), "abekpry")
    }

    @Test
    fun greenMask() {
        assertEquals(GreenMask.betweenWords("aerky", "akpry").toString(), "a___y")
        assertEquals(GreenMask.betweenWords("perky", "ekpry").toString(), "____y")
        assertEquals(GreenMask.betweenWords("aerky", "aerky").toString(), "aerky")
        assertEquals(GreenMask.betweenWords("aaaaa", "bbbbb").toString(), "_____")
    }

    @Test
    fun masksPlay() {
        val target = "acxyz"
        println(Masks.EMPTY
            .applyWord("aaaaa", target)
            .applyWord("bcdef", target)
            .applyWord("mcmmz", target)
        )
    }

    @Test
    fun y() {
        println(YellowMask.fromIndexes("raise", listOf(0)))
    }

}

package cases

@Suppress("unused")
class MaxLineLength {
    companion object {
        val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."

        val A_VERY_LONG_MULTI_LINE = """
            This is anotehr very very very very very very very very, very long multiline String that will break the MaxLineLength"
        """.trimIndent()
    }

    val loremIpsumField = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."

    val longMultiLineField = """
            This is anotehr very very very very very very very very
            very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very
            very long multiline String that will break the MaxLineLength
        """.trimIndent()

    val longMultiLineFieldWithLineBreaks =
        """
            This is anotehr very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very
            very long multiline String with Line Break that will break the MaxLineLength
        """.trimIndent()

    fun main() {
        val thisIsAVeryLongValName = "This is a very, very long String that will break the MaxLineLength"

        if (thisIsAVeryLongValName.length > "This is not quite as long of a String".length) {
            println("It's indeed a very long String")
        }

        val hello = anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot()
        val loremIpsum = getLoremIpsum()

        println(hello)
        println(loremIpsum)

    }

    // https://longurlmaker.com/go?id=z4Is.gd8c11rangy50farawayRedirx1drawn%2Bout60c3protractedTinyLinkstringylingeringfar%2BreachinglMyURLSHurl86018lengthy7frunningoutstretched1111ilengthy2xSimURLSmallr800xSimURL361juEasyURLSimURL0022faraway1095xhighfar%2Boff1618sustained0Shima8961toutstretchedexpanded0stretch611220drawn%2BoutdwkTightURL8kDoioplongish10Xil14b101ShredURLTraceURLbptoweringB6512TinyURL6towering0rGetShorty004bm5301URLprotracted0prolonged61MooURLy1948jspread%2Bout428u0t3stretchingfarawaylasting11ShredURLc2bDigBigexpandedX.se90a20TinyURL26WapURLr1cprolongedkelongatedc1f2c01loftylengthycontinuede7WapURLgGetShorty2NutshellURLcontinued6a2lastingr5protracted1expandeddistantspread%2BoutURl.iersustainedNotLongSHurl3w2SimURL011xSnipURL02GetShorty2prolonged0f02f60blingeringIs.gd301URLTinyLinktowering3d200t01osustained2WapURL90ShortURL11spread%2Boute02URLPieFly2toweringDwarfurl70elongated9s070SnipURL6Is.gd7spread%2Boutc0hy210vtcnf43Redirxb9148n1lingering6PiURL16URLcutaspread%2BoutYATUCoutstretchede70lUlimita1e610ShortenURL1lnk.inenduringUlimit0U760l8m72011793v7020TightURLelongatedYATUCt6UrlTeaetc91e5kspun%2Bout010d1e1b1Dwarfurl6Shortlinksb0sustained0enlarged6great1187e5e690URLCutter1spun%2Bout10drawn%2Bouttall4EasyURLDecentURLenduringd1eTraceURL5yGetShortyTinyLinkfar%2Boff1prolonged4cc0stretcheddeepprotracted3f001elongate9018ystretchinglastingi7TinyURL7expanded910continuedremotef8sustainedz175lingeringcbloftyprolonged10079running0UlimitB6515Shrinkr00LiteURL1loftyoutstretchedclnk.in3farawayg5runningTinyLinkspread%2Bout1stringy11c036greatfarawaystretchingefar%2Boff31spread%2Bout4kDoiopMooURL53m19Beam.tolastingShredURL1s25ShimBeam.to8nstretchtowering80StartURLShortURL4lengthened018Is.gdNotLongzWapURLNutshellURLe2spun%2Bout119elongated7elongated5outstretchedh8k1stringyloftyShredURL84running06308d071Minilien3wg3UrlTealoftystretchedwCanURLfar%2Boff7atf104083towering820ganglingw35m1a063LiteURLt081NanoRef361lnk.in0deep0Shrinkr6e80far%2Boff9170Redirxy6btspread%2Boutsustained10UlimitShortlinks2toweringGetShorty3ShrinkrDecentURLsustaineddbg1nfShortURL331a001enlargedB65RedirxelongatedMinilien809UrlT
    fun anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot(): String {
        return "Hello"
    }

    fun getLoremIpsum() = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
}

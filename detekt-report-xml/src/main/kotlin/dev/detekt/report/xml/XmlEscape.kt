/*
 * =============================================================================
 *
 *   Copyright (c) 2014, The UNBESCAPE team (http://www.unbescape.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
@file:Suppress("ALL")

package dev.detekt.report.xml

/**
 * Adapted from Unbescape - https://github.com/unbescape/unbescape/
 *
 * Utility class for performing XML escape/unescape operations.
 */
object XmlEscape {

    private val REFERENCE_HEXA_PREFIX = "&#x".toCharArray()
    private const val REFERENCE_SUFFIX = ';'

    /**
     * Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) **escape** operation
     * on a <tt>String</tt> input.
     *
     * *Level 2* means this method will escape:
     *  * The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     * <tt>&quot;</tt> and <tt>&#39;</tt>
     *  * All non ASCII characters.
     *
     * This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     * (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     * character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     *
     * This method is **thread-safe**.

     * @param text the <tt>String</tt> to be escaped.
     * *
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     * *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     * *         no additional <tt>String</tt> objects will be created during processing). Will
     * *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    fun escapeXml(text: String): String {
        val symbols = Xml10EscapeSymbolsInitializer.initializeXml10()
        val level = 2
        var strBuilder: StringBuilder? = null
        val offset = 0
        val max = text.length
        var readOffset = offset

        var i = offset
        while (i < max) {
            val codepoint = Character.codePointAt(text, i)
            val codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint)

            /*
             * Shortcut: most characters will be ASCII/Alphanumeric, and we won't need to do anything at
             * all for them
             */
            if (codepoint <= Xml10EscapeSymbolsInitializer.XmlEscapeSymbols.LEVELS_LEN - 2 &&
                level < symbols.ESCAPE_LEVELS[codepoint] &&
                codepointValid
            ) {
                i++
                continue
            }

            /*
             * Shortcut: we might not want to escape non-ASCII chars at all either.
             */
            if (codepoint > Xml10EscapeSymbolsInitializer.XmlEscapeSymbols.LEVELS_LEN - 2 &&
                level < symbols.ESCAPE_LEVELS[Xml10EscapeSymbolsInitializer.XmlEscapeSymbols.LEVELS_LEN - 1] &&
                codepointValid
            ) {
                if (Character.charCount(codepoint) > 1) {
                    // This is to compensate that we are actually escaping two char[] positions with a single codepoint.
                    i++
                }
                i++
                continue
            }

            /*
             * At this point we know for sure we will need some kind of escape, so we
             * can increase the offset and initialize the string builder if needed, along with
             * copying to it all the contents pending up to this point.
             */
            if (strBuilder == null) {
                strBuilder = StringBuilder(max + 20)
            }
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i)
            }
            if (Character.charCount(codepoint) > 1) {
                // This is to compensate that we are actually reading two char[] positions with a single codepoint.
                i++
            }
            readOffset = i + 1

            /*
             * If the char is invalid, there is nothing to write, simply skip it (which we already did by
             * incrementing the readOffset.
             */
            if (!codepointValid) {
                i++
                continue
            }

            /*
             * -----------------------------------------------------------------------------------------
             *
             * Perform the real escape, attending the different combinations of NCR, DCR and HCR needs.
             *
             * -----------------------------------------------------------------------------------------
             */
            // We will try to use a CER
            val codepointIndex = symbols.SORTED_CODEPOINTS.binarySearch(codepoint)
            if (codepointIndex >= 0) {
                // CER found! just write it and go for the next char
                strBuilder.append(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex])
                i++
                continue
            }
            /*
             * No NCR-escape was possible (or allowed), so we need decimal/hexa escape.
             */
            strBuilder.append(REFERENCE_HEXA_PREFIX)
            strBuilder.append(Integer.toHexString(codepoint))
            strBuilder.append(REFERENCE_SUFFIX)
            i++
        }

        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no escape was actually needed. Otherwise
         *                 append the remaining unescaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */
        if (strBuilder == null) {
            return text
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max)
        }
        return strBuilder.toString()
    }
}

/**
 * This class initializes the XML10_SYMBOLS structure.
 */
@Suppress("ALL")
private object Xml10EscapeSymbolsInitializer {

    class XmlCodepointValidator {

        /*
         * XML 1.0 does not allow many control characters, nor unpaired surrogate chars
         * (characters used for composing two-char codepoints, but appearing on their own).
         */
        fun isValid(codepoint: Int): Boolean {
            if (codepoint < 0x20) {
                return codepoint == 0x9 || codepoint == 0xA || codepoint == 0xD
            }
            if (codepoint <= 0xD7FF) { // U+D800 - U+DFFF are reserved for low + high surrogates
                return true
            }
            if (codepoint < 0xE000) {
                return false
            }
            if (codepoint <= 0xFFFD) { // U+FFFE and U+FFFF are non-characters, and therefore not valid
                return true
            }
            if (codepoint < 0x10000) {
                return false
            }
            return true
        }
    }

    fun initializeXml10(): XmlEscapeSymbols {
        val xml10References = XmlEscapeSymbols.References()

        /*
         * --------------------------------------------------------------------------------------------------
         *   XML 1.0 CHARACTER ENTITY REFERENCES
         *   See: http://www.w3.org/TR/xml
         * --------------------------------------------------------------------------------------------------
         */
        xml10References.addReference(34, "&quot;")
        xml10References.addReference(38, "&amp;")
        xml10References.addReference(39, "&apos;")
        xml10References.addReference(60, "&lt;")
        xml10References.addReference(62, "&gt;")

        /*
         * Initialization of escape markup-significant characters plus all non-ASCII
         */
        val escapeLevels = ByteArray(XmlEscapeSymbols.LEVELS_LEN)
        /*
         * Everything is level 3 unless contrary indication.
         */
        escapeLevels.fill(3.toByte())
        /*
         * Everything non-ASCII is level 2 unless contrary indication.
         */
        for (c in 0x80..<XmlEscapeSymbols.LEVELS_LEN) {
            escapeLevels[c] = 2
        }

        /*
         * Alphanumeric characters are level 4.
         */
        run {
            var c = 'A'
            while (c <= 'Z') {
                escapeLevels[c] = 4
                c++
            }
        }
        run {
            var c = 'a'
            while (c <= 'z') {
                escapeLevels[c] = 4
                c++
            }
        }
        run {
            var c = '0'
            while (c <= '9') {
                escapeLevels[c] = 4
                c++
            }
        }

        /*
         * The five XML predefined entities will be escaped always (level 1)
         */
        escapeLevels['\''] = 1
        escapeLevels['"'] = 1
        escapeLevels['<'] = 1
        escapeLevels['>'] = 1
        escapeLevels['&'] = 1

        /*
         * XML 1.0 allows a series of control characters, but they should appear
         * escaped: [#x7F-#x84] | [#x86-#x9F]
         */
        for (c in 0x7F..0x84) {
            escapeLevels[c] = 1
        }
        for (c in 0x86..0x9F) {
            escapeLevels[c] = 1
        }

        /*
         * Create the new symbols structure
         */
        return XmlEscapeSymbols(xml10References, escapeLevels, XmlCodepointValidator())
    }

    private operator fun ByteArray.set(c: Char, value: Byte) {
        set(c.code, value)
    }

    /**
     * Instances of this class group all the complex data structures needed to support escape and unescape
     * operations for XML.
     *
     * In contrast with HTML escape operations, the entity references to be used for XML escape/unescape operations
     * can be defined by the user by manually creating an instance of this class containing all the entities he/she
     * wants to escape.
     *
     * It is **not** recommended to use this XML class for HTML escape/unescape operations. Use the methods
     * in [org.unbescape.html.HtmlEscape] instead, as HTML escape rules include a series of tweaks not allowed in
     * XML, as well as being less lenient with regard to aspects such as case-sensitivity. Besides, the HTML escape
     * infrastructure is able to apply a series of performance optimizations not possible in XML due to the fact that
     * the number of HTML Character Entity References (*Named Character References* in HTML5 jargon) is fixed
     * and known in advance.
     *
     * Objects of this class are **thread-safe**.
     */
    class XmlEscapeSymbols
    /*
     * Create a new XmlEscapeSymbols structure. This will initialize all the structures needed to cover the
     * specified references and escape levels, including sorted arrays, overflow maps, etc.
     */
    internal constructor(
        references: References,
        escapeLevels: ByteArray,
        /*
         * This object will be in charge of validating each codepoint in input, in order to determine
         * whether such codepoint will be allowed in escaped output (escaped or not). Invalid codepoints
         * will be simply discarded.
         */
        val CODEPOINT_VALIDATOR: XmlCodepointValidator
    ) {

        /*
         * This array will hold the 'escape level' assigned to chars (not codepoints) up to LEVELS_LEN.
         * - The last position of this array will be used for determining the level of all codepoints >= (LEVELS_LEN - 1)
         */
        val ESCAPE_LEVELS = ByteArray(LEVELS_LEN)

        /*
         * This array will contain all the codepoints that might be escaped, numerically ordered.
         * - Positions in this array will correspond to positions in the SORTED_CERS_BY_CODEPOINT array, so that one array
         *   (this one) holds the codepoints while the other one holds the CERs such codepoints refer to.
         * - Gives the opportunity to store all codepoints in numerical order and therefore be able to perform
         *   binary search operations in order to quickly find codepoints (and translate to CERs) when escaping.
         */
        val SORTED_CODEPOINTS: IntArray

        /*
         * This array contains all the CERs corresponding to the codepoints stored in SORTED_CODEPOINTS. This array is
         * ordered so that each index in SORTED_CODEPOINTS can also be used to retrieve the corresponding CER when used
         * on this array.
         */
        val SORTED_CERS_BY_CODEPOINT: Array<CharArray?>

        /*
         * This array will contain all the CERs that might be unescaped, alphabetically ordered.
         * - Positions in this array will correspond to positions in the SORTED_CODEPOINTS_BY_CER array, so that one array
         *   (this one) holds the CERs while the other one holds the codepoint(s) such CERs refer to.
         * - Gives the opportunity to store all CERs in alphabetical order and therefore be able to perform
         *   binary search operations in order to quickly find CERs (and translate to codepoints) when unescaping.
         */
        val SORTED_CERS: Array<CharArray?>

        /*
         * This array contains all the codepoints corresponding to the CERs stored in SORTED_CERS. This array is
         * ordered so that each index in SORTED_CERS can also be used to retrieve the corresponding CODEPOINT when used
         * on this array.
         */
        val SORTED_CODEPOINTS_BY_CER: IntArray

        init {

            // Initialize escape levels: just copy the array
            System.arraycopy(escapeLevels, 0, ESCAPE_LEVELS, 0, LEVELS_LEN)

            // Initialize the length of the escaping structures
            val structureLen = references.references.size

            // Initialize some auxiliary structures
            val cers = ArrayList<CharArray>(structureLen + 5)
            val codepoints = ArrayList<Int>(structureLen + 5)

            // For each reference, initialize its corresponding codepoint -> CER and CER -> codepoint structures
            for (reference in references.references) {
                cers.add(reference.cer) // can be null
                codepoints.add(Integer.valueOf(reference.codepoint))
            }

            // We can initialize now the arrays
            SORTED_CODEPOINTS = IntArray(structureLen)
            SORTED_CERS_BY_CODEPOINT = arrayOfNulls(structureLen)
            SORTED_CERS = arrayOfNulls(structureLen)
            SORTED_CODEPOINTS_BY_CER = IntArray(structureLen)

            val cersOrdered = ArrayList(cers)
            cersOrdered.sortWith { o1, o2 -> String(o1).compareTo(String(o2)) }

            val codepointsOrdered = ArrayList(codepoints)
            codepointsOrdered.sort()

            // Order the CODEPOINT -> CERs (escape)structures
            for (i in 0..<structureLen) {
                val codepoint = codepointsOrdered[i]
                SORTED_CODEPOINTS[i] = codepoint
                for (j in 0..<structureLen) {
                    if (codepoint == codepoints[j]) {
                        SORTED_CERS_BY_CODEPOINT[i] = cers[j]
                        break
                    }
                }
            }

            // Order the CERs -> CODEPOINT (unescape)structures
            for (i in 0..<structureLen) {
                val cer = cersOrdered[i]
                SORTED_CERS[i] = cer
                for (j in 0..<structureLen) {
                    if (cer.contentEquals(cers[j])) {
                        SORTED_CODEPOINTS_BY_CER[i] = codepoints[j]
                        break
                    }
                }
            }
        }

        /*
         * Inner utility classes that model the named character references to be included in an initialized
         * instance of the XmlEscapeSymbols class.
         */
        class References {

            internal val references = ArrayList<Reference>(200)

            fun addReference(codepoint: Int, cer: String) {
                this.references.add(Reference(cer, codepoint))
            }
        }

        class Reference internal constructor(cer: String, internal val codepoint: Int) {
            // cer CAN be null -> codepoint should be removed from escaped output.
            internal val cer: CharArray = cer.toCharArray()
        }

        companion object {
            /*
             * Size of the array specifying the escape levels.
             */
            const val LEVELS_LEN = (0x9f + 2)
        }
    }
}

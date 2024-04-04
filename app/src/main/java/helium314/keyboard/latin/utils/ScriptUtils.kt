/*
 * Copyright (C) 2012 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */
package helium314.keyboard.latin.utils

import java.util.Locale

/**
 * A class to help with handling different writing scripts.
 */
object ScriptUtils {
    // Unicode scripts (ISO 15924), incomplete
    const val SCRIPT_UNKNOWN = "" // Used for hardware keyboards
    const val SCRIPT_BENGALI = "Beng"
    const val SCRIPT_LATIN = "Latn"

    @JvmStatic
    fun scriptSupportsUppercase(locale: Locale): Boolean {
        // only Latin, Cyrillic, Greek and Armenian have upper/lower case
        // https://unicode.org/faq/casemap_charprop.html#3
        return when (locale.script()) {
            SCRIPT_LATIN -> true
            else -> false
        }
    }

    /*
     * Returns whether the code point is a letter that makes sense for the specified
     * locale for this spell checker.
     * The dictionaries supported by Latin IME are described in res/xml/spellchecker.xml
     * and is limited to EFIGS languages and Russian.
     * Hence at the moment this explicitly tests for Cyrillic characters or Latin characters
     * as appropriate, and explicitly excludes CJK, Arabic and Hebrew characters.
     */
    @JvmStatic
    fun isLetterPartOfScript(codePoint: Int, script: String): Boolean {
        return when (script) {
            SCRIPT_BENGALI ->
                // Bengali unicode block is U+0980..U+09FF
                codePoint in 0x980..0x9FF
            SCRIPT_LATIN ->
                // Our supported latin script dictionaries (EFIGS) at the moment only include
                // characters in the C0, C1, Latin Extended A and B, IPA extensions unicode
                // blocks. As it happens, those are back-to-back in the code range 0x40 to 0x2AF,
                // so the below is a very efficient way to test for it. As for the 0-0x3F, it's
                // excluded from isLetter anyway.
                codePoint <= 0x2AF && Character.isLetter(codePoint)
            SCRIPT_UNKNOWN -> true
            else -> throw RuntimeException("Unknown value of script: $script")
        }
    }

    /**
     * returns the locale script with fallback to default scripts
     */
    @JvmStatic
    fun Locale.script(): String {
        if (script.isNotEmpty()) return script
        if (country.equals("ZZ", true)) {
            Log.w("ScriptUtils", "old _ZZ locale found: $this")
            return SCRIPT_LATIN
        }
        return when (language) {
            "bn" -> SCRIPT_BENGALI
            else -> SCRIPT_LATIN // use as fallback
        }
    }
}

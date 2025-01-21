package com.vardemin.hels.utils

internal fun String.utf8Size(beginIndex: Int = 0, endIndex: Int = length): Long {
    require(beginIndex >= 0) { "beginIndex < 0: $beginIndex" }
    require(endIndex >= beginIndex) { "endIndex < beginIndex: $endIndex < $beginIndex" }
    require(endIndex <= length) { "endIndex > string.length: $endIndex > $length" }

    var result = 0L
    var i = beginIndex
    while (i < endIndex) {
        val c = this[i].code

        if (c < 0x80) {
            // A 7-bit character with 1 byte.
            result++
            i++
        } else if (c < 0x800) {
            // An 11-bit character with 2 bytes.
            result += 2
            i++
        } else if (c < 0xd800 || c > 0xdfff) {
            // A 16-bit character with 3 bytes.
            result += 3
            i++
        } else {
            val low = if (i + 1 < endIndex) this[i + 1].code else 0
            if (c > 0xdbff || low < 0xdc00 || low > 0xdfff) {
                // A malformed surrogate, which yields '?'.
                result++
                i++
            } else {
                // A 21-bit character with 4 bytes.
                result += 4
                i += 2
            }
        }
    }

    return result
}
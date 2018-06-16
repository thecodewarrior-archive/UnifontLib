package com.thecodewarrior.unifontlib.utils

import kotlin.math.min

/**
 * Allows easily splitting a string into sections based on delimiters
 */
class Tokenizer(val string: String) {
    var index = 0
        private set(value) {
            field = min(value, string.length)
        }

    /**
     * Seeks the tokenizer to the specified position.
     *
     * @throws IllegalArgumentException if the index is larger than the length of [string]
     */
    fun seek(index: Int) {
        if(index > string.length)
            throw IllegalArgumentException("Index ($index) is larger than the length of the string (${string.length})")
        this.index = index
    }

    /**
     * Gets the portion of the string until [delimiter], or the rest of the string if [delimiter] wasn't found.
     * Advances the tokenizer to the end of the returned string
     *
     * @return the substring or null if the tokenizer is already at the end of the string
     */
    fun untilOrNull(delimiter: Char): String? {
        if(index >= string.length) return null
        val i = string.indexOf(delimiter, startIndex = index)
        val substr: String
        if(i == -1) {
            substr = string.substring(index, string.length)
            index = string.length
        } else {
            substr = string.substring(index, i)
            index = i+1
        }
        return substr
    }

    /**
     * Gets the portion of the string until [delimiter], or an empty string if [delimiter] wasn't found.
     * Advances the tokenizer to the end of the returned string if [delimiter] was found
     *
     * @return the substring or null if the tokenizer is already at the end of the string
     */
    fun untilOrNullIf(delimiter: Char): String? {
        if(index >= string.length) return null
        val i = string.indexOf(delimiter, startIndex = index)
        val substr: String
        if(i == -1) {
            substr = ""
        } else {
            substr = string.substring(index, i)
            index = i+1
        }
        return substr
    }

    /**
     * Gets the portion of the string until [delimiter], or the rest of the string if [delimiter] wasn't found.
     * Advances the tokenizer to the end of the returned string
     *
     * @throws IllegalArgumentException if the tokenizer is at the end of the string
     */
    fun until(delimiter: Char) = untilOrNull(delimiter) ?: throw IllegalArgumentException("Tokenizer already empty")

    /**
     * Gets the portion of the string until [delimiter], or an empty string if [delimiter] wasn't found.
     * Advances the tokenizer to the end of the returned string if [delimiter] was found
     *
     * @throws IllegalArgumentException if the tokenizer is at the end of the string
     */
    fun untilIf(delimiter: Char) = untilOrNullIf(delimiter) ?: throw IllegalArgumentException("Tokenizer already empty")

    /**
     * Returns the remaining portion of the string and advances the tokenizer to the end of the string.
     * Returns an empty string if the tokenizer is already at the end of the string.
     */
    fun remaining(): String {
        if(index >= string.length) return ""
        val substr = string.substring(index, string.length)
        index = string.length
        return substr
    }
}

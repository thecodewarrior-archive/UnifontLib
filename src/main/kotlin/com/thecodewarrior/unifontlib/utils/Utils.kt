package com.thecodewarrior.unifontlib.utils

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.min

private object Utils

fun resource(name: String) = Utils::class.java.getResource("/$name")
fun resourceStream(name: String) = Utils::class.java.getResourceAsStream("/$name")


fun CharSequence.splitOnce(delimiter: String, ignoreCase: Boolean = false): Pair<String, String> {
    val list = this.split(delimiter, ignoreCase = ignoreCase, limit = 2)
    return list[0] to list.getOrElse(1) { "" }
}

fun CharSequence.splitOnce(delimiter: Char, ignoreCase: Boolean = false): Pair<String, String> {
    val list = this.split(delimiter, ignoreCase = ignoreCase, limit = 2)
    return list[0] to list.getOrElse(1) { "" }
}

fun CharSequence.splitOnce(delimiter: Regex): Pair<String, String> {
    val list = this.split(regex = delimiter, limit = 2)
    return list[0] to list.getOrElse(1) { "" }
}

operator fun IntRange.minus(other: IntRange): List<IntRange> {
    if(this.step != 1 || other.step != 1)
        throw IllegalArgumentException("Cannot subtract ranges if either of them have a non-1 step")
    if(other.first in this && other.last !in this)
        return listOf(IntRange(this.first, other.first-1))
    if(other.first !in this && other.last in this)
        return listOf(IntRange(other.last+1, this.last))
    if(other.first < this.first && other.last > this.last)
        return listOf()
    if(other.first > this.first && other.last < this.last)
        return listOf(
                IntRange(this.first, other.first-1),
                IntRange(other.last+1, this.last)
        )
    return listOf(this)
}

fun byteArrayOf(vararg ints: Int): ByteArray {
    return ints.map { it.toByte() }.toByteArray()
}

fun BufferedImage.isColor(x: Int, y: Int, color: Color): Boolean {
    return this.getRGB(x, y) == color.rgb
}

fun BufferedImage.isColor(startX: Int, startY: Int, width: Int, height: Int, color: Color): Boolean {
    val array = IntArray(width*height)
    this.getRGB(startX, startY, width, height, array, 0, width)
    return array.all { it == color.rgb }
}

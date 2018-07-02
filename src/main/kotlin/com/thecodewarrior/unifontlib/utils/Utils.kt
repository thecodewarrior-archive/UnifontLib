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

fun IntRange.overlaps(other: IntRange): Boolean {
    if(this.step != 1 || other.step != 1)
        throw IllegalArgumentException("Cannot test for range overlap if either range has a non-1 step")
    if(other.first in this || other.last in this)
        return true
    if(this.first in other || this.last in other)
        return true
    if(other.first < this.first && this.last < other.last)
        return true
    if(this.first < other.first && other.last < this.last)
        return true
    return false
}

operator fun IntRange.minus(other: IntRange): List<IntRange> {
    if(this.step != 1 || other.step != 1)
        throw IllegalArgumentException("Cannot subtract ranges if either of them have a non-1 step")
    if(other.first in this && other.last !in this)
        return listOf(IntRange(this.first, other.first-1))
    if(other.first !in this && other.last in this)
        return listOf(IntRange(other.last+1, this.last))
    if(other.first < this.first && this.last < other.last)
        return listOf()
    if(this.first < other.first && other.last < this.last)
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

fun Int.codepointHex(): String {
    val digits = if(this > 0xFFFF) 6 else 4
    return "%0${digits}X".format(this)
}


val DISTINCT_COLORS = listOf(
        Color(0xe6194b),
        Color(0x3cb44b),
        Color(0xffe119),
        Color(0x0082c8),
        Color(0xf58231),
        Color(0x911eb4),
        Color(0x46f0f0),
        Color(0xf032e6),
        Color(0xd2f53c),
        Color(0xfabebe),
        Color(0x008080),
        Color(0xe6beff),
        Color(0xaa6e28),
        Color(0xfffac8),
        Color(0x800000),
        Color(0xaaffc3),
        Color(0x808000),
        Color(0xffd8b1),
        Color(0x000080),
        Color(0x808080),
        Color(0xFFFFFF),
        Color(0x000000)
)

val DISTINCT_COLOR_NAMES = listOf(
        "Red",
        "Green",
        "Yellow",
        "Blue",
        "Orange",
        "Purple",
        "Cyan",
        "Magenta",
        "Lime",
        "Pink",
        "Teal",
        "Lavender",
        "Brown",
        "Beige",
        "Maroon",
        "Mint",
        "Olive",
        "Coral",
        "Navy",
        "Grey",
        "White",
        "Black"
)

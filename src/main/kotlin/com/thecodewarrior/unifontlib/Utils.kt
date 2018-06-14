package com.thecodewarrior.unifontlib

import java.net.URL

private object Utils

fun resource(name: String) = Utils::class.java.getResource("/$name")
fun resourceStream(name: String) = Utils::class.java.getResourceAsStream("/$name")

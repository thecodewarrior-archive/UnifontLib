package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.core.CliktCommand
import java.net.URL
import java.nio.file.Paths
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.close
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.zip.GZIPInputStream
import java.io.ByteArrayInputStream



class Download: CliktCommand() {

    private val regex = """http://unifoundry\.com/pub/unifont-\d*\.\d*\.\d*/font-builds/unifont-\d*\.\d*\.\d*\.hex\.gz""".toRegex()
    override fun run() {
        val unifontHomepage = URL("http://unifoundry.com/unifont.html").readText()
        val hexURL = URL(regex.find(unifontHomepage)?.value
                ?: throw IllegalArgumentException("Couldn't find hex download on http://unifoundry.com/unifont.html")
        )
        val hexGzip = hexURL.readBytes()
        GZIPInputStream(ByteArrayInputStream(hexGzip)).bufferedReader().useLines {
            Paths.get("unifont.hex").toFile().writeText(it.joinToString("\n"))
        }
    }
}
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
import java.io.IOException
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.close
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.io.File
import org.apache.commons.net.ftp.FTP
import com.apple.eio.FileManager.setFileType
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import sun.security.jgss.GSSUtil.login
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.apache.commons.net.io.CopyStreamEvent
import org.apache.commons.net.io.CopyStreamListener
import org.rauschig.jarchivelib.ArchiverFactory
import kotlin.math.max

class Download: CliktCommand(
        name="download",
        epilog = "Downloads unifont from the GNU FTP server"
) {

    val outputDir by option("-o", "--output").file()

    override fun run() {
        val outputFile = outputDir ?: File(".")
        val serverAddress = "ftp.gnu.org" // ftp server address
        val port = 21 // ftp uses default port Number 21

        val ftp = FTPClient()
        try {
            println("Connecting to ftp.gnu.org.")
            ftp.connect(serverAddress, port)

            val reply = ftp.replyCode
            if(!FTPReply.isPositiveCompletion(reply)) {
                throw PrintMessage("ftp.gnu.org connection failed. Reply code $reply")
            }
            println("Connected. Logging in as anonymous")

            if(!ftp.login("anonymous", "password")) {
                throw PrintMessage("Failed to log in with credentials 'anonymous:password'")
            }
            println("Logged in. Listing unifont versions")

            ftp.enterLocalPassiveMode()
            ftp.setFileType(FTP.BINARY_FILE_TYPE)

            ftp.changeWorkingDirectory("gnu/unifont")
            val directories = ftp.listDirectories()
            val versions = directories.map {
                Version(it.name.removePrefix("unifont-"))
            }.sortedDescending()
            println("Found versions [${versions.joinToString(", ")}]")

            val newest = versions.max()
                    ?: throw PrintMessage("Unable to find the newest unifont version in gnu/unifont/")
            println("Highest unifont version is $newest")

            if(outputFile.resolve("unifont-$newest").exists()) {
                if(outputFile.resolve("unifont-$newest").isFile) {
                    throw PrintMessage("unifont-$newest is a file. Please delete or move it in order to download unifont.")
                } else {
                    throw PrintMessage("unifont-$newest/ already exists. Downloading unifont would overwrite any local " +
                            "changes, please delete or move it if you want to download a fresh version of unifont.")
                }
            }

            ftp.changeWorkingDirectory("unifont-$newest")
            val fileName = "unifont-$newest.tar.gz"
            val unifontFiles = ftp.listFiles()
            val remoteFile = unifontFiles.find { it.name == fileName }
                    ?: throw PrintMessage("Unable to find $fileName in file listing " +
                            "[${unifontFiles.joinToString(", ") { it.name }}]")
            println("Downloading $fileName...")

            val outputTar = outputFile.resolve(fileName)
            val outputStream = BufferedOutputStream(FileOutputStream(outputTar))

            ftp.copyStreamListener = ProgressCopyStreamListener(remoteFile.size)
            val success = ftp.retrieveFile(fileName, outputStream)
            println()

            outputStream.close()

            if (success) {
                println("$fileName successfully download.")
            } else {
                throw PrintMessage("Download failed: ${ftp.replyString.trim()}")
            }

            println("Expanding $fileName")

            val archiver = ArchiverFactory.createArchiver("tar", "gz")
            archiver.extract(outputTar, outputFile)
        } catch (ex: IOException) {
            println("Error occurred downloading files from ftp Server : " + ex.message)
            throw ex
        } finally {
                if (ftp.isConnected) {
                    ftp.logout()
                    ftp.disconnect()
                }
        }
    }

    private data class Version(val version: List<String>): Comparable<Version> {
        constructor(versionString: String): this(versionString.split("."))

        override fun compareTo(other: Version): Int {
            for(i in 0 until max(version.size, other.version.size)) {
                val thisVersion = version.getOrNull(i)?.toIntOrNull() ?: 0
                val otherVersion = other.version.getOrNull(i)?.toIntOrNull() ?: 0

                if(thisVersion > otherVersion) return 1
                if(thisVersion < otherVersion) return -1
            }
            return 0
        }

        override fun toString(): String {
            return version.joinToString(".")
        }
    }

    private class ProgressCopyStreamListener(val fileSize: Long): CopyStreamListener{
        override fun bytesTransferred(event: CopyStreamEvent?) {}
        override fun bytesTransferred(totalBytesTransferred: Long, bytesTransferred: Int, streamSize: Long) {
            val fileSizeKb = fileSize/1000
            val totalBytesKb = totalBytesTransferred/1000
            val fileSizeString = fileSizeKb.toString()
            val totalBytesString = totalBytesKb.toString().padStart(fileSizeString.length, ' ')
            print("\r\u001B[K$totalBytesString/$fileSizeString KB")
        }
    }
}
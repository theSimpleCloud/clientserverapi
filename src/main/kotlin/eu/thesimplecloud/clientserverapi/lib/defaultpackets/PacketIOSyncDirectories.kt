/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

class PacketIOSyncDirectories() : JsonPacket() {

    constructor(dirPathOnOtherSide: String, directoriesOnOtherSide: List<String>) : this() {
        this.jsonLib.append("dirPathOnOtherSide", dirPathOnOtherSide)
                .append("directoriesOnOtherSide", directoriesOnOtherSide)
    }

    override suspend fun handle(sender: IPacketSender): ICommunicationPromise<Any> {
        val dirPathOnOtherSide = this.jsonLib.getString("dirPathOnOtherSide")
                ?: return contentException("dirPathOnOtherSide")
        val fileInfoList = this.jsonLib.getObject("directoriesOnOtherSide", Array<String>::class.java)
                ?: return contentException("directoriesOnOtherSide")
        val otherSideAllDirs = fileInfoList.map { File(it) }
        val thisSideAllDirectories = getAllDirectories(File(dirPathOnOtherSide))

        val createMutableList = otherSideAllDirs.toMutableList()
        createMutableList.removeAll(thisSideAllDirectories)
        createMutableList.forEach { it.mkdirs() }

        val deleteMutableList = thisSideAllDirectories.toMutableList()
        deleteMutableList.removeAll(otherSideAllDirs)
        deleteMutableList.forEach { FileUtils.deleteDirectory(it) }
        return unit()
    }

    private fun getAllDirectories(directory: File): Collection<File> {
        if (!directory.exists()) return emptyList()
        val acceptAllFilter = object : IOFileFilter {
            override fun accept(file: File?): Boolean {
                return true
            }

            override fun accept(dir: File?, name: String?): Boolean {
                return true
            }
        }
        return FileUtils.listFilesAndDirs(directory, acceptAllFilter, acceptAllFilter)
                .filter { it.isDirectory }.filter { it != directory }
    }
}
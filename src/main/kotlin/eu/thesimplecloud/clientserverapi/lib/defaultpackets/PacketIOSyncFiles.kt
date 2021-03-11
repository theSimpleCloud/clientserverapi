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

import eu.thesimplecloud.clientserverapi.lib.filetransfer.FileInfo
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

class PacketIOSyncFiles() : JsonPacket() {

    constructor(dirPathOnOtherSide: String, currentFiles: List<FileInfo>): this() {
        this.jsonLib.append("dirPathOnOtherSide", dirPathOnOtherSide).append("currentFiles", currentFiles)
    }

    override suspend fun handle(sender: IPacketSender): ICommunicationPromise<Any> {
        val dirPathOnOtherSide = this.jsonLib.getString("dirPathOnOtherSide")
                ?: return contentException("dirPathOnOtherSide")
        val fileInfoList = this.jsonLib.getObject("currentFiles", Array<FileInfo>::class.java)
                ?: return contentException("currentFiles")
        val allFiles = fileInfoList.map { File(it.relativePath) }
        val neededFiles = allFiles.filterIndexed { index, file -> !file.exists() || file.lastModified() != fileInfoList[index].lastModified }
        removeDeletedFiles(File(dirPathOnOtherSide), allFiles)
        return success(neededFiles.map { FileInfo.fromFile(it) }.toTypedArray())
    }

    private fun removeDeletedFiles(directory: File, syncFiles: List<File>) {
        if (!directory.isDirectory) return
        val allFiles = getAllFiles(directory).toMutableList()
        allFiles.removeAll(syncFiles)
        allFiles.forEach { file -> if (file.isDirectory) FileUtils.deleteDirectory(file) else file.delete() }
    }

    private fun getAllFiles(directory: File): Collection<File> {
        val acceptAllFilter = object : IOFileFilter {
            override fun accept(file: File?): Boolean {
                return true
            }

            override fun accept(dir: File?, name: String?): Boolean {
                return true
            }
        }
        return FileUtils.listFiles(directory, acceptAllFilter, acceptAllFilter)
    }
}
package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.filetransfer.FileInfo
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

class PacketIOSyncFiles() : JsonPacket() {

    constructor(dirPathOnOtherSide: String, currentFiles: List<FileInfo>): this() {
        this.jsonData.append("dirPathOnOtherSide", dirPathOnOtherSide).append("currentFiles", currentFiles)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val dirPathOnOtherSide = this.jsonData.getString("dirPathOnOtherSide")
                ?: return contentException("dirPathOnOtherSide")
        val fileInfoList = this.jsonData.getObject("currentFiles", Array<FileInfo>::class.java)
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
        return FileUtils.listFilesAndDirs(directory, acceptAllFilter, acceptAllFilter)
    }
}
package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

class PacketIOSyncDirectories() : JsonPacket() {

    constructor(dirPathOnOtherSide: String, directoriesOnOtherSide: List<String>) : this() {
        this.jsonData.append("dirPathOnOtherSide", dirPathOnOtherSide)
                .append("directoriesOnOtherSide", directoriesOnOtherSide)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val dirPathOnOtherSide = this.jsonData.getString("dirPathOnOtherSide")
                ?: return contentException("dirPathOnOtherSide")
        val fileInfoList = this.jsonData.getObject("directoriesOnOtherSide", Array<String>::class.java)
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
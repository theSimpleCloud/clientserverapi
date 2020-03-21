package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.ZipUtils
import java.io.File

class PacketIOUnzipZipFile() : JsonPacket() {

    constructor(pathToZipFile: String, dirToUnzipPath: String) : this() {
        this.jsonData.append("pathToZipFile", pathToZipFile).append("dirToUnzipPath", dirToUnzipPath)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val pathToZipFile = this.jsonData.getString("pathToZipFile") ?: return contentException("pathToZipFile")
        val dirToUnzipPath = this.jsonData.getString("dirToUnzipPath") ?: return contentException("dirToUnzipPath")
        val zipFile = File(pathToZipFile)
        if (!zipFile.exists()) return failure(NoSuchFileException(zipFile))
        ZipUtils.unzipDir(zipFile, dirToUnzipPath)
        zipFile.delete()
        return unit()
    }
}
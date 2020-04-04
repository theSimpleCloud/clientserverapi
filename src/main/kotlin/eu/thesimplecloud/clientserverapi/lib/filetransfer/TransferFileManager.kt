package eu.thesimplecloud.clientserverapi.lib.filetransfer

import java.io.File
import java.util.*
import kotlin.collections.HashMap

class TransferFileManager : ITransferFileManager {

    private val transferFiles = HashMap<UUID, TransferFile>()

    override fun creteFileTransfer(uuid: UUID, savePath: String, lastModified: Long) {
        val transferFile = TransferFile(uuid, lastModified, savePath)
        this.transferFiles[uuid] = transferFile
    }

    override fun addBytesToTransferFile(uuid: UUID, byteArray: Array<Byte>) {
        val transferFile = transferFiles[uuid] ?: return
        transferFile.addBytes(byteArray)
    }

    override fun fileTransferComplete(uuid: UUID) {
        transferFiles.remove(uuid)?.setLasModified()
    }




}
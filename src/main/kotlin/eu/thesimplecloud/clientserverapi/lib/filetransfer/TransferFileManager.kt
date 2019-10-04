package eu.thesimplecloud.clientserverapi.lib.filetransfer

import java.io.File
import java.util.*
import kotlin.collections.HashMap

class TransferFileManager : ITransferFileManager {

    private val transferFiles = HashMap<UUID, TransferFile>()

    private fun getTransferFile(uuid: UUID): TransferFile{
        val transferFile = transferFiles[uuid] ?: TransferFile()
        if (!transferFiles.containsKey(uuid)) transferFiles[uuid] = transferFile
        return transferFile
    }

    override fun addBytesToTransferFile(uuid: UUID, byteArray: Array<Byte>) {
        getTransferFile(uuid).addBytes(byteArray)
    }

    override fun fileTransferComplete(uuid: UUID, savePath: String){
        getTransferFile(uuid).buildToFile(File(savePath))
        transferFiles.remove(uuid)
    }


}
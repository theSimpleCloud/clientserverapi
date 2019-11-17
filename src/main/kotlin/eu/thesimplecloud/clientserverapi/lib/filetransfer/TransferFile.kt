package eu.thesimplecloud.clientserverapi.lib.filetransfer

import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.collections.ArrayList

class TransferFile {

    private val byteList = ArrayList<Byte>()

    fun addBytes(byteArray: Array<Byte>) {
        byteList.addAll(byteArray)
    }

    fun buildToFile(file: File) {
        if (file.isDirectory)
            file.mkdirs()
        else
            FileUtils.writeByteArrayToFile(file, byteList.toByteArray())
    }

}
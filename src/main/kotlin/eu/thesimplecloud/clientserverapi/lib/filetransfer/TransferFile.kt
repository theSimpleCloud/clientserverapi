package eu.thesimplecloud.clientserverapi.lib.filetransfer

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TransferFile(val uuid: UUID, savePath: String) {

    val file = File(savePath)

    init {
        file.delete()
        file.parentFile?.mkdirs()
    }

    fun addBytes(byteArray: Array<Byte>) {
        while(true) {
            try {
                FileUtils.writeByteArrayToFile(file, byteArray.toByteArray(), true)
                break
            } catch (e: FileNotFoundException) {
                Thread.sleep(3)
            }
        }
    }

}
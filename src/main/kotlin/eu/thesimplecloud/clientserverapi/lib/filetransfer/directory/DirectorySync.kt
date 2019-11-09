package eu.thesimplecloud.clientserverapi.lib.filetransfer.directory

import eu.thesimplecloud.clientserverapi.lib.filetransfer.packets.PacketIODeleteFile
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File

class DirectorySync(private val directory: File, private val toDirectory: String) : IDirectorySync {

    private var allFilesLastUpdate: Collection<File> = emptyList()

    private var lastUpdate: Long = 0

    private val receivers = ArrayList<IConnection>()

    init {
        require(this.directory.isDirectory) { "Specified file must be a directory." }
        updateFiles()
    }

    override fun getDirectory(): File = this.directory

    override fun syncDirectory(connection: IConnection): ICommunicationPromise<Unit> {
        val returnPromise = CommunicationPromise<Unit>()
        this.receivers.add(connection)
        val promises = getAllFiles().map { file -> connection.sendFile(file, getFilePathOnOtherSide(file)) }
        GlobalScope.launch {
            promises.forEach {
                it.syncUninterruptibly()
            }
            returnPromise.trySuccess(Unit)
        }
        return returnPromise
    }

    override fun syncNoLonger(connection: IConnection) {
        this.receivers.remove(connection)
    }


    fun updateFiles(){
        this.lastUpdate = System.currentTimeMillis()
        this.allFilesLastUpdate = getAllFiles()
    }

    fun syncChanges(){
        for (file in getChangedFiles()){
            val otherSidePath = getFilePathOnOtherSide(file)
            if (!file.exists()){
                this.receivers.forEach { connection -> connection.sendQuery(PacketIODeleteFile(otherSidePath)) }
                continue
            }
            this.receivers.forEach { connection -> connection.sendFile(file, otherSidePath) }
        }
    }

    fun syncChangesAndUpdateFiles(){
        syncChanges()
        updateFiles()
    }

    fun getChangedFiles(): Collection<File> {
        val updatedFiles = getAllFiles().filter { file -> file.lastModified() > this.lastUpdate || !this.allFilesLastUpdate.contains(file) }
        val deletedFiles = this.allFilesLastUpdate.filter { file -> !file.exists() }
        return updatedFiles.union(deletedFiles)
    }

    private fun getAllFiles(): Collection<File> {
        return FileUtils.listFiles(directory, null, true)
    }

    private fun getFilePathOnOtherSide(file: File) = toDirectory + file.path.replace(this.directory.path, "")


}